package com.vagm.vagmdroid.service;

/**
 * The Class TestConstatnts.
 * @author Roman_Konovalov
 */
public final class TestConstatnts {

	/**
	 * constructor.
	 */
	private TestConstatnts() {
	}

	/**
	 * BUFFER_STRING_ARRAY.
	 */
	public static final String[] BUFFER_STRING_ARRAY = {"66",
			"01f6303238393036303231474c20312c396c20523420454443200303030303030303030303030303030303030303"
					+ "0303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303"
					+ "03030303030303030303030303030303030303030303030303030303030303030303030303030303030303", "f620203030",
			"f65347202031343135", "f60000040370" };

	/**
	 * ECU_INFO.
	 */
	public static final String[] ECU_INFO = {"9803", "028906021GL", " 1,9l R4 EDC   00SG  1415p" };

	/**
	 * BUFFER_STRING_ARRAY1.
	 */
	public static final String[] BUFFER_STRING_ARRAY1 = {"5f", "f63845303631343131", "f631502020",
			"f64142532f45445320352e3320", "f646524f4e5420202044313100", "f6303237333030343133332020", "f64242203234333034" };

	/**
	 * ECU_INFO1.
	 */
	public static final String[] ECU_INFO1 = {"10526", "8E0614111P ", " ABS/EDS 5.3 FRONT   D110273004133  BB 24304" };

	/**
	 * BUFFER_STRING_ARRAY_NEGATIVE.
	 */
	public static final String[] BUFFER_STRING_ARRAY_NEGATIVE = {"5f", "01" };

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
