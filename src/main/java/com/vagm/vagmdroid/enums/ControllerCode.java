/**
 * 
 */
package com.vagm.vagmdroid.enums;

import android.util.Log;

import com.vagm.vagmdroid.R;

/**
 * The Class ControllerCode.
 * 
 * @author Roman_Konovalov
 * 
 */
public enum ControllerCode {
	/**
	 * ENGINE.
	 */
	ENGINE(0x01, R.id.bEngine),

	/**
	 * ABS.
	 */
	ABS(0x03, R.id.bABS),

	AIRBAGS(0x15, R.id.bAirbags),

	INSTRUMENTS(0x17, R.id.bInstruments),

	IMMOBILIZER(0x25, R.id.bImmobilizer),

	/**
	 * COMFORT.
	 */
	COMFORT(0x46, R.id.bComfort);

	/**
	 * code.
	 */
	private int code;

	private int id;

	private static final String TAG = "VAGm_ControllerCode";

	/**
	 * @param code
	 */
	ControllerCode(int code, int id) {
		this.code = code;
		this.id = id;
	}

	/**
	 * Gets Code.
	 * 
	 * @return
	 */
	public int getCode() {
		return code;
	}

	public static ControllerCode getControllerCode(int id) {
		for (ControllerCode controllerCode : ControllerCode.values()) {
			if (controllerCode.id == id) {
				return controllerCode;
			}
		}
		Log.e(TAG, "Controller with id: " + id + "doesn't exist");
		return null;
	}

}
