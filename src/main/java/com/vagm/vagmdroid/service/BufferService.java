package com.vagm.vagmdroid.service;

import static com.vagm.vagmdroid.enums.VAGmConstans.CONTROLLER_NO_ANSWER;

import java.util.ArrayList;
import java.util.List;

import com.vagm.vagmdroid.exceptions.ControllerCommunicationException;

/**
 * The Class BufferService.
 * @author Roman_Konovalov
 */
public final class BufferService {

	/**
	 * Constructor.
	 */
	private BufferService() {
	}

	/**
	 * Gets ControllerInfo.
	 * @param buffer
	 *            buffer
	 * @return List<String>
	 * @throws ControllerCommunicationException if some communication error occurs
	 */
	public static List<String> getControllerInfo(final byte[] buffer) throws ControllerCommunicationException {
		int countPos = 0;
		while (getCount(countPos, buffer) != 0) {
			if ((buffer[countPos] == 0x01) && (buffer.length > countPos)) {
				if (buffer[countPos + 1] == CONTROLLER_NO_ANSWER) {
					throw new ControllerCommunicationException();
				}
			}
			int newCountPos = countPos + getCount(countPos, buffer) + 1;
			buffer[countPos] = 0;
			countPos = newCountPos;
		}
		return proceedArray(buffer);
	}

	/**
	 * getCount.
	 * @param countPos
	 *            countPos
	 * @param buffer
	 *            buffer
	 * @return Count
	 */
	private static int getCount(final int countPos, final byte[] buffer) {
		if (countPos >= buffer.length) {
			return 0;
		}
		if (buffer[countPos] < 0) {
			return buffer[countPos] + 256;
		} else {
			return buffer[countPos];
		}
	}

	/**
	 * byteToInt.
	 * @param b
	 *            byte
	 * @return int
	 */
	private static int byteToInt(final byte b) {
		if (b < 0) {
			return b + 256;
		} else {
			return b;
		}
	}

	/**
	 * proceedArray.
	 * @param array
	 *            array
	 * @return List<String>
	 */
	private static List<String> proceedArray(final byte[] array) {
		final List<String> result = new ArrayList<String>(3);

		if ((array.length > 1) && (array[1] != 0)) {
			result.add(String.valueOf(1000000 / byteToInt(array[1])));
		}
		/*
		 * if (array[0] < 0) { array[0] = (byte) (array[0] + 0x80); }
		 */
		final StringBuilder builder = new StringBuilder();
		for (int i = 1; i < array.length; i++) {
			if ((array[i] >= 0x20) && (array[i] <= 0x7E)) {
				builder.append((char) array[i]);
			}
		}

		String resultString = builder.toString();
		if ((resultString.length() > 13)) {
			result.add(resultString.substring(1, 12));
			result.add(resultString.substring(13));
		}
		return result;
	}

	/**
	 * bytesToHex.
	 * @param in
	 *            in
	 * @return String
	 */
	public static String bytesToHex(final byte[] in) {
		final StringBuilder builder = new StringBuilder();
		for (final byte b : in) {
			builder.append(String.format("%02x", b));
		}
		return builder.toString();
	}

	/**
	 * hexStringToByteArray.
	 * @param s
	 *            String
	 * @return byte[]
	 */
	public static byte[] hexStringToByteArray(final String s) {
		int len = s.length();
		byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			data[i / 2] = (byte) Integer.parseInt(s.substring(i, i + 2), 16);
		}
		return data;
	}

}

