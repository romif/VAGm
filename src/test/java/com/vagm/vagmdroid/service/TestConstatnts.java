package com.vagm.vagmdroid.service;

import com.vagm.vagmdroid.dto.DataStreamDTO;

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
	public static final String[] BUFFER_STRING_ARRAY = {"0341018A", 
			"01f6303238393036303231474c20312c396c20523420454443200303030303030303030303030303030303030303"
					+ "0303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303"
					+ "03030303030303030303030303030303030303030303030303030303030303030303030303030303030303", "f620203030",
			"f65347202031343135", "f60000040370" };

	/**
	 * ECU_INFO.
	 */
	public static final String[] ECU_INFO = {"9603", "028906021GL", " 1,9l R4 EDC   00SG  1415p" };

	/**
	 * BUFFER_STRING_ARRAY1.
	 */
	public static final String[] BUFFER_STRING_ARRAY1 = {"02F9018A", "f63845303631343131", "f631502020",
			"f64142532f45445320352e3320", "f646524f4e5420202044313100", "f6303237333030343133332020", "f64242203234333034" };

	/**
	 * ECU_INFO1.
	 */
	public static final String[] ECU_INFO1 = {"10512", "8E0614111P ", " ABS/EDS 5.3 FRONT   D110273004133  BB 24304" };

	/**
	 * BUFFER_STRING_ARRAY2.
	 */
	public static final String[] BUFFER_STRING_ARRAY2 = {"02F9018A", "f6b34230393139383830442020", "f642352d4b4f4d4249494e5354",
			"f6522e2056444f2056303920", "f60001180000" };

	/**
	 * ECU_INFO2.
	 */
	public static final String[] ECU_INFO2 = {"10512", "3B0919880D ", " B5-KOMBIINSTR. VDO V09 " };

	/**
	 * BUFFER_STRING_ARRAY3.
	 */
	public static final String[] BUFFER_STRING_ARRAY3 = {
			"0341018A",
			"01f6b34230393539373936204b200303030303030303030303030303030303030303030303030303030303030303030303030303030303"
					+ "03030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303"
					+ "0303030303030303030303030303030303030303030303030303", "f65a656e7472616c2d5347204b", "f66f6d666f72742020563431",
			"f600200214bf" };

	/**
	 * BUFFER_MEAS_BLOCKS_4GROUPS.
	 */
	public static final String BUFFER_MEAS_BLOCKS_4GROUPS = "e701690027330015142405097c";

	/**
	 * DATA_STREAM_DTOS4.
	 */
	public static final DataStreamDTO[] DATA_STREAM_DTOS4 = {new DataStreamDTO("0.0", " rpm", 0.0F),
			new DataStreamDTO("0.0", " mg/h", 0.0F), new DataStreamDTO("0.72", " V", 0.72F), new DataStreamDTO("21.6", " °C", 21.6F) };

	/**
	 * BUFFER_MEAS_BLOCKS_3GROUPS.
	 */
	public static final String BUFFER_MEAS_BLOCKS_3GROUPS = "e7108000102010101010";

	/**
	 * BUFFER_MEAS_BLOCKS_2GROUPS.
	 */
	public static final String BUFFER_MEAS_BLOCKS_2GROUPS = "e7245b81136409";

	/**
	 * BUFFER_MEAS_BLOCKS_1GROUPS.
	 */
	public static final String BUFFER_MEAS_BLOCKS_1GROUPS = "e7050a82";

	/**
	 * BUFFER_FAULT_CODES1.
	 */
	public static final String BUFFER_FAULT_CODES1 = "fc049807";

	/**
	 * FAULT_CODES_STRING1.
	 */
	public static final String FAULT_CODES_STRING1 = "1176 Ключ - слишком низкий уровень сигнала";

	/**
	 * BUFFER_FAULT_CODES2.
	 */
	public static final String BUFFER_FAULT_CODES2 = "fc02729f";

	/**
	 * FAULT_CODES_STRING2.
	 */
	public static final String FAULT_CODES_STRING2 = "626 Контрольная лампа времени прогрева свечами накаливания-K29 - обрыв цепи/короткое замыкание на массу";

	/**
	 * BUFFER_FAULT_CODES_NO_ERRORS.
	 */
	public static final String BUFFER_FAULT_CODES_NO_ERRORS = "fcffff88";

	/**
	 * BUFFER_OUTPUTTESTS_CODES1.
	 */
	public static final String BUFFER_OUTPUTTESTS_CODES1 = "f504f1";

	/**
	 * OUTPUTTESTS_STRING1.
	 */
	public static final String OUTPUTTESTS_STRING1 = "Клапан рециркуляции ОГ-N18";

	/**
	 * BUFFER_OUTPUTTESTS_CODES2.
	 */
	public static final String BUFFER_OUTPUTTESTS_CODES2 = "f50272";

	/**
	 * OUTPUTTESTS_STRING2.
	 */
	public static final String OUTPUTTESTS_STRING2 = "Контрольная лампа времени прогрева свечами накаливания-K29";

	/**
	 * BUFFER_OUTPUTTESTS_END.
	 */
	public static final String BUFFER_OUTPUTTESTS_END = "03";

	/**
	 * ECU_INFO3.
	 */
	public static final String[] ECU_INFO3 = {"9603", "3B0959796 K", " Zentral-SG Komfort  V41 " };

	/**
	 * BUFFER_STRING_ARRAY_NEGATIVE.
	 */
	public static final String[] BUFFER_STRING_ARRAY_NEGATIVE = {"02F9018A", "50" };

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
