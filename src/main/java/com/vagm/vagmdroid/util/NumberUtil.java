package com.vagm.vagmdroid.util;


public final class NumberUtil {
    
    private NumberUtil() { }
    
    private static final String TWO_DIGIT_FORMAT = "%02x";
    
    /**
     * byteToInt.
     * 
     * @param b
     *            byte
     * @return int
     */
    public static int byteToInt(final byte b) {
        return b < 0 ? b + 256 : b;
    }
    
    /**
     * hexStringToByteArray.
     * 
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
    
    /**
     * bytesToHex.
     * 
     * @param in
     *            in
     * @return String
     */
    public static String bytesToHex(final byte[] in) {
        final StringBuilder builder = new StringBuilder();
        for (final byte b : in) {
            builder.append(String.format(TWO_DIGIT_FORMAT, b));
        }
        return builder.toString();
    }

}
