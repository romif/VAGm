package com.vagm.vagmdroid.enums;

/**
 * The Class AdapterConstans.
 * 
 * @author Roman_Konovalov
 */
public enum AdapterLogKey {

    /**
     * RECEIVED_CHAR
     */
    RECEIVED_CHAR(0x01, "Received char"),

    /**
     * ECU_BAUDRATE
     */
    ECU_BAUDRATE(0x02, "Ecu baudrate"),

    /**
     * GETBAUDRATE_TIMEOUT_ERROR
     */
    GETBAUDRATE_TIMEOUT_ERROR(0x03, "Get baudrate time out error"),

    /**
     * BAUDRATE_TICKS
     */
    BAUDRATE_TICKS(0x04, "Baudrate ticks"),

    /**
     * GETCHAR_TIMEOUT_ERROR
     */
    GETCHAR_TIMEOUT_ERROR(0x05, "Get char time out error");

    /**
     * key.
     */
    private int key;

    /**
     * value.
     */
    private String value;

    AdapterLogKey(final int key, final String value) {
        this.key = key;
        this.value = value;
    }

    /**
     * getValue
     * 
     * @return value
     */
    public String getValue() {
        return value;
    }

    /**
     * getAdapterLogKey.
     * @param key
     * @return
     */
    public static AdapterLogKey getAdapterLogKey(final int key) {
        for (final AdapterLogKey adapterLogKey : AdapterLogKey.values()) {
            if (adapterLogKey.key == key) {
                return adapterLogKey;
            }
        }
        return null;
    }
}
