package com.vagm.vagmdroid.service;

import static com.vagm.vagmdroid.enums.VAGmConstans.CONTROLLER_NO_ANSWER;

import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.content.Context;

import com.google.inject.Inject;
import com.vagm.vagmdroid.R;
import com.vagm.vagmdroid.dto.DataStreamDTO;
import com.vagm.vagmdroid.enums.VAGmConstans;
import com.vagm.vagmdroid.exceptions.ControllerCommunicationException;
import com.vagm.vagmdroid.exceptions.ControllerWrongResponseException;

/**
 * The Class BufferService.
 * @author Roman_Konovalov
 */
@Singleton
public class BufferService {

	/**
	 * LOG.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(BufferService.class);

	/**
	 * dataStreamService.
	 */
	@Inject
	private DataStreamService dataStreamService;

	/**
	 * context.
	 */
	@Inject
	private Context context;

	/**
	 * faultCodesService.
	 */
	@Inject
	private FaultCodesService faultCodesService;

	/**
	 * Constructor.
	 */
	public BufferService() {
	}

	/**
	 * bytesToHex.
	 * @param in
	 *            in
	 * @return String
	 */
	public String bytesToHex(final byte[] in) {
		final StringBuilder builder = new StringBuilder();
		for (final byte b : in) {
			builder.append(String.format("%02x", b));
		}
		return builder.toString();
	}

	/**
	 * gets ControllerInfo.
	 * @param buffer buffer
	 * @param controllerInfo controllerInfo
	 * @return ControllerInfo
	 * @throws ControllerCommunicationException if some communication error occurs
	 * @throws ControllerWrongResponseException if wrong response from controller occurs
	 */
	public String[] getControllerInfo(final byte[] buffer, final String[] controllerInfo)
			throws ControllerCommunicationException, ControllerWrongResponseException {
		checkAdapterErrors(buffer);
		final String[] result = controllerInfo;
		if ((buffer.length == 1) && (buffer[0] > 1)) {
			result[0] = String.valueOf(1000000 / byteToInt(buffer[0]));
		} else {

			int response = byteToInt(buffer[0]);
			int dataStartPosition = 1;
			if (response <= 0x01) {
				response = byteToInt(buffer[1]);
				dataStartPosition = 2;
			}

			checkResponseCode(VAGmConstans.VAG_BTI_INFO_RES, response);

			if (result[1].length() == 0) {
				if (byteToInt(buffer[dataStartPosition]) > 0x80) {
					buffer[dataStartPosition] = (byte) (buffer[dataStartPosition] + 0x80);
				}
			}

			final StringBuilder builder = new StringBuilder();
			for (int i = dataStartPosition; i < buffer.length; i++) {
				if ((buffer[i] >= 0x20) && (buffer[i] <= 0x7E)) {
					builder.append((char) buffer[i]);
				}
			}

			String resultString = builder.toString();
			int requaredEcuLenth = (VAGmConstans.ECU_LENGTH - result[1].length()) > resultString.length() ? resultString.length()
					: VAGmConstans.ECU_LENGTH - result[1].length();
			result[1] = result[1] + resultString.substring(0, requaredEcuLenth);
			result[2] = result[2] + resultString.substring(requaredEcuLenth);

		}

		return result;
	}

	/**
	 * getMeasBlocksInfo.
	 * @param buffer buffer
	 * @return DataStreamDTO array
	 * @throws ControllerCommunicationException if some communication error occurs
	 * @throws ControllerWrongResponseException if wrong response from controller occurs
	 */
	public DataStreamDTO[] getMeasBlocksInfo(final byte[] buffer) throws ControllerCommunicationException, ControllerWrongResponseException {
		checkAdapterErrors(buffer);

		int responseCode = byteToInt(buffer[0]);
		if (responseCode == VAGmConstans.VAG_BTI_ERROR) {
			LOG.trace("No data for current group");
			return new DataStreamDTO[] {DataStreamDTO.getDefault(context), DataStreamDTO.getDefault(context),
					DataStreamDTO.getDefault(context), DataStreamDTO.getDefault(context) };
		}

		checkResponseCode(VAGmConstans.VAG_BTI_GROUP_RES, responseCode);
		if ((buffer.length - 1) % 3 != 0) {
			throw new ControllerWrongResponseException("Wrong response length from controller: expected multiplicity of three, but was: "
					+ (buffer.length - 1));
		}

		int groupsCount = (buffer.length - 1) / 3;

		DataStreamDTO[] dtos = new DataStreamDTO[4];
		for (int i = 0; i < groupsCount; i++) {
			dtos[i] = dataStreamService.encodeGroupData(byteToInt(buffer[i * 3 + 1]), byteToInt(buffer[i * 3 + 2]),
					byteToInt(buffer[i * 3 + 3]));
		}
		for (int i = groupsCount; i < 4; i++) {
			dtos[i] = DataStreamDTO.getDefault(context);
		}

		return dtos;
	}

	/**
	 * getFaultCodesInfo.
	 * @param buffer buffer
	 * @return FaultCodesInfo
	 * @throws ControllerCommunicationException if some communication error occurs
	 * @throws ControllerWrongResponseException if wrong response from controller occurs
	 */
	public String getFaultCodesInfo(final byte[] buffer) throws ControllerCommunicationException, ControllerWrongResponseException {
		checkAdapterErrors(buffer);

		int responseCode = byteToInt(buffer[0]);
		if (responseCode == VAGmConstans.VAG_BTI_ERROR) {
			LOG.trace("Error");
			//TODO
			return "ERROR";
		}
		checkResponseCode(VAGmConstans.VAG_BTI_DTC_RES, responseCode);
		if ((buffer.length - 1) % 3 != 0) {
			throw new ControllerWrongResponseException("Wrong response length from controller: expected multiplicity of three, but was: "
					+ (buffer.length - 1));
		}
		if ((byteToInt(buffer[1]) == 0xFF) && (byteToInt(buffer[2]) == 0xFF)) {
			return context.getString(R.string.no_errors);
		}

		String result = "";
		for (int i = 0; i < buffer.length / 3; i++) {
			int errorCode = Integer.parseInt(
					Integer.toHexString(byteToInt(buffer[i * 3 + 1])) + Integer.toHexString(byteToInt(buffer[i * 3 + 2])), 16);
			String errorString = faultCodesService.getDTC(errorCode);
			int errorTypeInt = byteToInt(buffer[i * 3 + 3]);
			if (errorTypeInt > 0x80) {
				errorTypeInt = errorTypeInt - 0x80;
			}
			String errorType = faultCodesService.getErrorType(errorTypeInt);
			result += "" + errorCode + " " + errorString + " - " + errorType;
		}
		return result;
	}

	/**
	 * hexStringToByteArray.
	 * @param s
	 *            String
	 * @return byte[]
	 */
	public byte[] hexStringToByteArray(final String s) {
		int len = s.length();
		byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			data[i / 2] = (byte) Integer.parseInt(s.substring(i, i + 2), 16);
		}
		return data;
	}

	/**
	 * byteToInt.
	 * @param b
	 *            byte
	 * @return int
	 */
	private static int byteToInt(final byte b) {
		return b < 0 ? b + 256 : b;
	}

	/**
	 * Checks AdapterErrors.
	 * @param buffer buffer
	 * @throws ControllerCommunicationException if some communication error occurs
	 */
	private void checkAdapterErrors(final byte[] buffer) throws ControllerCommunicationException {
		if (buffer.length == 1) {
			if (buffer[0] == CONTROLLER_NO_ANSWER) {
				throw new ControllerCommunicationException();
			}
		}
	}

	/**
	 * checkResponseCode.
	 * @param expectedCode expectedCode
	 * @param actualCode actualCode
	 * @throws ControllerWrongResponseException if wrong response from controller occurs
	 */
	private void checkResponseCode(final int expectedCode, final int actualCode) throws ControllerWrongResponseException {
		if (expectedCode != actualCode) {
			throw new ControllerWrongResponseException("Wrong response from controller: expected 0x"
					+ String.format("%02x", expectedCode) + ", but was: 0x" + String.format("%02x", actualCode));
		}
	}

}

