package com.vagm.vagmdroid.enums;


/**
 * The Class FunctionCode.
 * @author Roman_Konovalov
 */
public enum FunctionCode {

	/**
	 * FAULT_CODES.
	 */
	FAULT_CODES(0x02),

	/**
	 * MEAS_BLOCKS.
	 */
	MEAS_BLOCKS(0x08),

	/**
	 * OUTPUT_TESTS.
	 */
	OUTPUT_TESTS(0x02),

	/**
	 * CLEAR_CODES.
	 */
	CLEAR_CODES(0x05);


	/**
	 * code.
	 */
	private int code;

	/**
	 * constructor.
	 * @param code code
	 */
	private FunctionCode(final int code) {
		this.code = code;
	}

	/**
	 * Gets Code.
	 * @return code
	 */
	public int getCode() {
		return code;
	}

}
