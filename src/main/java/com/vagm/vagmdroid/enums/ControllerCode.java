package com.vagm.vagmdroid.enums;

import android.util.Log;

import com.vagm.vagmdroid.R;

/**
 * The Class ControllerCode.
 * 
 * @author Roman_Konovalov
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

    /**
     * AIRBAGS.
     */
    AIRBAGS(0x15, R.id.bAirbags),

    /**
     * INSTRUMENTS.
     */
    INSTRUMENTS(0x17, R.id.bInstruments),

    /**
     * IMMOBILIZER.
     */
    IMMOBILIZER(0x25, R.id.bImmobilizer),

    /**
     * COMFORT.
     */
    COMFORT(0x46, R.id.bComfort);

    /**
     * code.
     */
    private int code;

    /**
     * id.
     */
    private int id;

    /**
     * TAG constant.
     */
    private static final String TAG = "VAGm_ControllerCode";

    /**
     * Constructor.
     * 
     * @param code
     *            code
     * @param id
     *            id
     */
    ControllerCode(final int code, final int id) {
        this.code = code;
        this.id = id;
    }

    /**
     * Gets Code.
     * 
     * @return code
     */
    public int getCode() {
        return code;
    }

    /**
     * Gets ControllerCode.
     * 
     * @param id
     *            id
     * @return ControllerCode
     */
    public static ControllerCode getControllerCode(final int id) {
        for (final ControllerCode controllerCode : ControllerCode.values()) {
            if (controllerCode.id == id) {
                return controllerCode;
            }
        }
        Log.e(TAG, "Controller with id: " + id + "doesn't exist");
        return null;
    }

}
