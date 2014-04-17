package com.vagm.vagmdroid.enums;

import android.util.Log;

import com.vagm.vagmdroid.R;

/**
 * The Class FunctionCode.
 * @author Roman_Konovalov
 */
public enum FunctionCode {

	/**
	 * FAULT_CODES.
	 */
	FAULT_CODES(0x02, R.id.bFaultCodes),

	/**
	 * MEAS_BLOCKS.
	 */
	MEAS_BLOCKS(0x08, R.id.bMeasBlocks),

	/**
	 * OUTPUT_TESTS.
	 */
	OUTPUT_TESTS(0x02, R.id.bOuputTests);

	/**
	 * TAG constant.
	 */
	private static final String TAG = "VAGm_FunctionCode";

	/**
	 * code.
	 */
	private int code;

	/**
	 * id.
	 */
	private int id;

	/**
	 * constructor.
	 * @param code code
	 * @param id id
	 */
	private FunctionCode(final int code, final int id) {
		this.code = code;
		this.id = id;
	}

	/**
	 * Gets Code.
	 * @return code
	 */
	public int getCode() {
		return code;
	}

	/**
	 * Gets FunctionCode.
	 * @param id id
	 * @return ControllerCode
	 */
	public static FunctionCode getFunctionCode(final int id) {
		for (final FunctionCode code : FunctionCode.values()) {
			if (code.id == id) {
				return code;
			}
		}
		Log.e(TAG, "Controller with id: " + id + "doesn't exist");
		return null;
	}

}
