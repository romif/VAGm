package com.vagm.vagmdroid.util;

import javax.inject.Singleton;

@Singleton
public class NumberUtil {
    
    /**
     * byteToInt.
     * 
     * @param b
     *            byte
     * @return int
     */
    public int byteToInt(final byte b) {
        return b < 0 ? b + 256 : b;
    }
    
    /**
     * hexStringToByteArray.
     * 
     * @param s
     *            String
     * @return byte[]
     */
    public byte[] hexStringToByteArray(final String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) Integer.parseInt(s.substring(i, i + 2), 16);
        }
        return data;
    }

}
