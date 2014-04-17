/**
 * 
 */
package com.vagm.vagmdroid.service;

/**
 * The Class TestConstatnts.
 * @author Roman_Konovalov
 */
public final class TestConstatnts {
	
	/**
	 * constructor.
	 */
	private TestConstatnts() { }

	/**
	 * BUFFER_STRING_ARRAY.
	 */
	public final static String[] BUFFER_STRING_ARRAY = { "01668801f6303238393036303231474c", "20312c396c2052342045444320030303",
			"03030303030303030303030303030303", "03030303030303030303030303030303", "03030303030303030303030303030303",
			"03030303030303030303030303030303", "03030303030303030303030303030303", "03030303030303030303030303030303",
			"030303030303030303030305f6202030", "3009f6534720203134313506f6000004", "0370" };

	/**
	 * ECU_INFO.
	 */
	public final static String[] ECU_INFO = { "9803", "028906021GL", "1,9l R4 EDC   00SG  1415p" };

	/**
	 * BUFFER_STRING_ARRAY1.
	 */
	public final static String[] BUFFER_STRING_ARRAY1 = { "015f0df6384530363134313131502020", "0df64142532f45445320352e33200df6",
			"46524f4e54202020443131000df63032", "3733303034313333202009f642422032", "34333034" };
	
	/**
	 * ECU_INFO1.
	 */
	public final static String[] ECU_INFO1 = { "10526", "8E0614111P ", "ABS/EDS 5.3 FRONT   D110273004133  BB 24304" };

	/**
	 * BUFFER_STRING_ARRAY_NEGATIVE.
	 */
	public final static String[] BUFFER_STRING_ARRAY_NEGATIVE = { "015f", "0101" };
	
	/**
	 * hexStringToByteArray.
	 * @param s
	 *            String
	 * @return byte[]
	 */
	public 	static byte[] hexStringToByteArray(final String s) {
		int len = s.length();
		byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			data[i / 2] = (byte) Integer.parseInt(s.substring(i, i + 2), 16);
		}
		return data;
	}

}
