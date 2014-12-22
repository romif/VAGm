package com.vagm.vagmdroid.enums;

/**
 * The Class FunctionCode.
 * 
 * @author Roman_Konovalov
 */
public enum FunctionCode {

    /**
     * FAULT_CODES.
     */
    FAULT_CODES(0x07),

    /**
     * MEAS_BLOCKS.
     */
    MEAS_BLOCKS(0x29),

    /**
     * OUTPUT_TESTS.
     */
    OUTPUT_TESTS(0x04),

    /**
     * CLEAR_CODES.
     */
    CLEAR_CODES(0x05),

    /**
     * GET_LOG.
     */
    GET_LOG(0x11);

    /**
     * code.
     */
    private int code;

    /**
     * constructor.
     * 
     * @param code
     *            code
     */
    private FunctionCode(final int code) {
        this.code = code;
    }

    /**
     * Gets Code.
     * 
     * @return code
     */
    public int getCode() {
        return code;
    }

}
