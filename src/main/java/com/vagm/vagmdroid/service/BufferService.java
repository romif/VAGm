package com.vagm.vagmdroid.service;

import static com.vagm.vagmdroid.enums.VAGmConstans.CONTROLLER_NO_ANSWER;

import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
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
		final String[] result = controllerInfo;
		if (buffer.length == 1) {
			if (buffer[0] == CONTROLLER_NO_ANSWER) {
				throw new ControllerCommunicationException();
			}
			if (buffer[0] > 1) {
				result[0] = String.valueOf(1000000 / byteToInt(buffer[0]));
			}

		} else {

			int response = byteToInt(buffer[0]);
			int dataStartPosition = 1;
			if (response <= 0x01) {
				response = byteToInt(buffer[1]);
				dataStartPosition = 2;
			}

			if (response != VAGmConstans.VAG_BTI_INFO_RES) {
				throw new ControllerWrongResponseException("Wrong response from controller: expected "
						+ VAGmConstans.VAG_BTI_INFO_RES + ", but was: " + response);
			}

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
		if (buffer.length == 1) {
			if (buffer[0] == CONTROLLER_NO_ANSWER) {
				throw new ControllerCommunicationException();
			}
		}

		int responseCode = byteToInt(buffer[0]);
		if (responseCode == VAGmConstans.VAG_BTI_ERROR) {
			LOG.debug("No data for current group");
			return new DataStreamDTO[]{DataStreamDTO.getDefault(), DataStreamDTO.getDefault(), DataStreamDTO.getDefault(), DataStreamDTO.getDefault()};
		}
		if (responseCode != VAGmConstans.VAG_BTI_GROUP_RES) {
			throw new ControllerWrongResponseException("Wrong response code from controller: expected " + VAGmConstans.VAG_BTI_GROUP_RES
					+ ", but was: " + responseCode);
		}
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
		for (int i = groupsCount - 1; i < 4; i++) {
			dtos[i] = DataStreamDTO.getDefault();
		}

		return dtos;
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

}

