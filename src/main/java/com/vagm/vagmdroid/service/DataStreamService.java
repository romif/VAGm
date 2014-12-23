package com.vagm.vagmdroid.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Singleton;
import com.vagm.vagmdroid.dto.DataStreamDTO;

/**
 * The Class DataStreamService.
 * 
 * @author Roman_Konovalov
 */
@Singleton
public class DataStreamService {

    private static final String SECOND = " s";

    private static final String MILLI_GRAMM_PER_HOUR = " mg/h";

    private static final String KILO_WATT = " kW";

    private static final String MILLI_BAR = " mbar";

    private static final String MILLI_SEC = " ms";

    private static final String VOLT = " V";

    private static final String DEGREE = " Deg";

    private static final String PERCENT = " %";

    /**
     * LOG.
     */
    private static final Logger LOG = LoggerFactory.getLogger(DataStreamService.class);

    /**
     * NA37Strings.
     */
    private String[] nA37Strings = null;

    /**
     * constructor.
     */
    public DataStreamService() {
    }

    /**
     * round.
     * 
     * @param num
     *            num
     * @param numDecim
     *            numDecim
     * @return round
     */
    private double round(final double num, final int numDecim) {
        long p = 1;
        // next line – calculate pow(10,brDecim)
        for (int i = 0; i < numDecim; i++) {
            p *= 10;
        }
        return (double) (int) (p * num + 0.5) / (double) p;
    }

    /**
     * BinToStr.
     * 
     * @param binary
     *            binary
     * @return String
     */
    private String binToStr(final int binary) {
        int b = binary;
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 8; ++i) {
            if ((b & 0x01) == 1) {
                builder.append("1");
            } else {
                builder.append("0");
            }
            b >>= 1;
        }
        return builder.toString();
    }

    /**
     * getNA37Strings.
     * 
     * @return String array
     */
    private String[] getNA37Strings() {
        if (nA37Strings == null) {
            nA37Strings = new String[1288];
            InputStream inputStream = null;
            BufferedReader reader = null;
            try {
                inputStream = DataStreamService.class.getClassLoader().getResourceAsStream(
                        "assets" + File.separator + "NA37Strings" + File.separator + "NA37.txt");
                reader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"));
                String st;
                int i = 0;
                while ((st = reader.readLine()) != null) {
                    nA37Strings[i] = st;
                    i++;
                }

            } catch (final IOException ex) {
                LOG.error("Cannot read NA37.txt", ex);
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        LOG.error("Cannot close BufferedReader", e);
                    }
                }
            }
        }
        return nA37Strings;
    }

    /**
     * encodeGroupData.
     * 
     * @param blockData1
     *            blockData1
     * @param blockData2
     *            blockData2
     * @param blockCode
     *            blockCode
     * @return DataStreamDTO
     */
    public DataStreamDTO encodeGroupData(final int blockCode, final int blockData1, final int blockData2) {
        final DataStreamDTO dto = new DataStreamDTO();
        String value = "";
        String unit = "";
        float valueFloat = 0;

        switch (blockCode) {
            case 1:
                value = String.valueOf(round(0.2 * blockData1 * blockData2, 4));
                unit = " rpm";
                valueFloat = (float) (0.2 * blockData1 * blockData2);
                break;
    
            case 2:
                value = String.valueOf(round(blockData1 * blockData2 * 0.002, 4));
                unit = PERCENT;
                valueFloat = (float) (blockData1 * blockData2 * 0.002);
                break;
    
            case 3:
                value = String.valueOf(round(blockData1 * blockData2 * 0.002, 4));
                unit = DEGREE;
                valueFloat = (float) (blockData1 * blockData2 * 0.002);
                break;
    
            case 4:
                value = String.valueOf(round(Math.abs(blockData2 - 127) * 0.01 * blockData1, 4));
                unit = " ° ATDC";
                valueFloat = (float) (Math.abs(blockData2 - 127) * 0.01 * blockData1);
                break;
    
            case 5:
                value = String.valueOf(round(blockData1 * (blockData2 - 100) * 0.1, 4));
                unit = " °C";
                valueFloat = (float) (blockData1 * (blockData2 - 100) * 0.1);
                break;
    
            case 6:
                value = String.valueOf(round(blockData1 * blockData2 * 0.001, 4));
                unit = VOLT;
                valueFloat = (float) (blockData1 * blockData2 * 0.001);
                break;
    
            case 7:
                value = String.valueOf(round(blockData1 * blockData2 * 0.01, 4));
                unit = " km/h";
                valueFloat = (float) (blockData1 * blockData2 * 0.01);
                break;
    
            case 8:
                value = String.valueOf(round(blockData1 * blockData2 * 0.1, 4));
                valueFloat = (float) (blockData1 * blockData2 * 0.1);
                break;
    
            case 9:
                value = String.valueOf(round(Math.abs(blockData2 - 127) * 0.02 * blockData1, 4));
                unit = DEGREE;
                valueFloat = (float) (Math.abs(blockData2 - 127) * 0.02 * blockData1);
                break;
    
            case 10:
                if (blockData2 == 0) {
                    value = "COLD";
                } else {
                    value = "HOT";
                }
                break;
    
            case 11:
                value = String.valueOf(round(0.0001 * blockData1 * (blockData2 - 128) + 1, 4));
                valueFloat = (float) (0.0001 * blockData1 * (blockData2 - 128) + 1);
                break;
    
            case 12:
                value = String.valueOf(round(0.01 * blockData1 * blockData2, 4));
                unit = " Ohm";
                valueFloat = (float) (0.01 * blockData1 * blockData2);
                break;
    
            case 13:
                value = String.valueOf(round(0.001 * blockData1 * (blockData2 - 127) + 1, 4));
                unit = " mm";
                valueFloat = (float) (0.001 * blockData1 * (blockData2 - 127) + 1);
                break;
    
            case 14:
                value = String.valueOf(round(0.005 * blockData1 * blockData2, 4));
                unit = " bar";
                valueFloat = (float) (0.005 * blockData1 * blockData2);
                break;
    
            case 15:
                value = String.valueOf(round(0.01 * blockData1 * blockData2, 4));
                unit = MILLI_SEC;
                valueFloat = (float) (0.01 * blockData1 * blockData2);
                break;
    
            case 16:
                value = /*
                         * Integer.toBinaryString(a); temp1=" "+
                         */binToStr(blockData2);
                valueFloat = (float) (blockData2);
                break;
    
            case 17:
                value = (char) blockData1 + " " + (char) blockData2;
                break;
    
            case 18:
                value = String.valueOf(round(0.04 * blockData1 * blockData2, 4));
                unit = MILLI_BAR;
                valueFloat = (float) (0.04 * blockData1 * blockData2);
                break;
    
            case 19:
                value = String.valueOf(round(blockData1 * blockData2 * 0.01, 4));
                unit = " l";
                valueFloat = (float) (blockData1 * blockData2 * 0.01);
                break;
    
            case 20:
                value = String.valueOf(round((blockData2 - 128) * 0.0078125 * blockData1, 4));
                unit = PERCENT;
                valueFloat = (float) ((blockData2 - 128) * 0.0078125 * blockData1);
                break;
    
            case 21:
                value = String.valueOf(round(0.001 * blockData1 * blockData2, 4));
                unit = VOLT;
                valueFloat = (float) (0.001 * blockData1 * blockData2);
                break;
    
            case 22:
                value = String.valueOf(round(0.001 * blockData1 * blockData2, 4));
                unit = MILLI_SEC;
                valueFloat = (float) (0.001 * blockData1 * blockData2);
                break;
    
            case 23:
                value = String.valueOf(round(blockData1 * blockData2 * 0.00390625, 1));
                unit = PERCENT;
                valueFloat = (float) (blockData1 * blockData2 * 0.00390625);
                // temp=Integer.toString(a); temp1=" "+Integer.toString(b);
                break;
    
            case 24:
                value = String.valueOf(round(0.001 * blockData1 * blockData2, 4));
                unit = " A";
                valueFloat = (float) (0.001 * blockData1 * blockData2);
                break;
    
            case 25:
                value = String.valueOf(round(blockData2 * 1.421 + blockData1 * 0.0054945054, 4));
                unit = " g/s";
                valueFloat = (float) (blockData2 * 1.421 + blockData1 * 0.0054945054);
                break;
    
            case 26:
                value = String.valueOf(round(blockData1 * blockData2, 4));
                unit = " C";
                valueFloat = (float) (blockData1 * blockData2);
                break;
    
            case 27:
                value = String.valueOf(round(Math.abs(blockData2 - 128) * 0.01 * blockData1, 1));
                unit = " ° ATDC";
                valueFloat = (float) (Math.abs(blockData2 - 128) * 0.01 * blockData1);
                break;
    
            case 28:
                value = String.valueOf(round(blockData1 * blockData2, 4));
                valueFloat = (float) (blockData1 * blockData2);
                break;
    
            case 29:
                if (blockData2 < blockData1) {
                    value = "1.Kennfeld";
                } else {
                    value = "1.Kennfeld";
                }
                break;
    
            case 30:
                value = String.valueOf(round(blockData2 / 12 * blockData1, 4));
                unit = "k/w";
                valueFloat = (float) (blockData2 / 12 * blockData1);
                break;
    
            case 31:
                value = String.valueOf(round(blockData2 * 0.003906250 * blockData1, 4));
                unit = " °C";
                valueFloat = (float) (blockData2 * 0.003906250 * blockData1);
                break;
    
            case 32:
                if (blockData2 > 128) {
                    value = String.valueOf(round(blockData2 - 256, 4));
                    valueFloat = (float) (blockData2 - 256);
                } else {
                    value = String.valueOf(round(blockData2, 4));
                    valueFloat = (float) (blockData2);
                }
                break;
    
            case 33:
                if (blockData1 == 0) {
                    value = String.valueOf(round(100 * blockData2, 3));
                    unit = PERCENT;
                    valueFloat = (float) (100 * blockData2);
                } else {
                    value = String.valueOf(round(100 * blockData2 / (float) blockData1, 3));
                    unit = PERCENT;
                    valueFloat = (float) (100 * blockData2 / (float) blockData1);
                }
                break;
    
            case 34:
                value = String.valueOf(round(0.01 * blockData1 * (blockData2 - 128), 4));
                unit = KILO_WATT;
                valueFloat = (float) (0.01 * blockData1 * (blockData2 - 128));
                break;
    
            case 35:
                value = String.valueOf(round(0.01 * blockData1 * blockData2, 4));
                unit = " l/h";
                valueFloat = (float) (0.01 * blockData1 * blockData2);
                break;
    
            case 36:
                value = String.valueOf(round(blockData2 * 10 + blockData1 * 2560, 0));
                unit = " km";
                valueFloat = (float) (blockData2 * 10 + blockData1 * 2560);
                break;
    
            case 37:
                // temp=Integer.toHexString(a); temp1=" "+Integer.toHexString(b);
                // if (na37==null)na37=new NA37();
                // temp=na37.mas[b];
                value = getNA37Strings()[blockData2 - 1];
                /*
                 * if (b==0x1e){temp="Oil presure below norm";} else {temp="Oil
                 * presure norm"; }
                 */
                break;
    
            case 38:
                value = String.valueOf(round(0.001 * blockData1 * (blockData2 - 128), 4));
                unit = KILO_WATT;
                valueFloat = (float) (0.001 * blockData1 * (blockData2 - 128));
                break;
    
            case 39:
                // temp=String.valueOf(round(Math.abs(b-128)*0.01*a,4));
                // temp1=" ° ATDC";
                // temp=String.valueOf(round((b-128)/255*a,4)); temp1=" mg/h";
                value = String.valueOf(round(blockData1 * blockData2 * 0.00390625, 1));
                unit = MILLI_GRAMM_PER_HOUR;
                valueFloat = (float) (blockData1 * blockData2 * 0.00390625);
                // temp=Integer.toString(a); temp1=" "+Integer.toString(b);
                break;
    
            case 40:
                value = String.valueOf(round(0.1 * blockData2 + 25.5 * blockData2 - 400, 4));
                unit = " A";
                valueFloat = (float) (0.1 * blockData2 + 25.5 * blockData2 - 400);
                break;
    
            case 41:
                value = String.valueOf(round(blockData2 + blockData2 * 255, 4));
                unit = " Ah";
                valueFloat = (float) (blockData2 + blockData2 * 255);
                break;
    
            case 42:
                value = String.valueOf(round(0.1 * blockData2 + 25.5 * blockData2 - 400, 4));
                unit = KILO_WATT;
                valueFloat = (float) (0.1 * blockData2 + 25.5 * blockData2 - 400);
                break;
    
            case 43:
                value = String.valueOf(round(0.1 * blockData2 + 25.5 * blockData2, 4));
                unit = VOLT;
                valueFloat = (float) (0.1 * blockData2 + 25.5 * blockData2);
                break;
    
            case 44:
                // temp=(char)a+":"+(char)b+" h";
                value = Integer.toString(blockData1) + ":" + Integer.toString(blockData2);
                unit = " h";
                break;
    
            case 45:
                value = String.valueOf(round(0.1 * blockData1 * blockData2 / 100, 4));
                valueFloat = (float) (0.1 * blockData1 * blockData2 / 100);
                break;
    
            case 46:
                value = String.valueOf(round((blockData1 * (blockData2 - 3200) * 0.0027), 4));
                unit = KILO_WATT;
                valueFloat = (float) (blockData1 * (blockData2 - 3200) * 0.0027);
                break;
    
            case 47:
                value = String.valueOf(round(blockData1 * (blockData2 - 128), 4));
                unit = MILLI_SEC;
                valueFloat = (float) (blockData1 * (blockData2 - 128));
                break;
    
            case 48:
                value = String.valueOf(round(blockData2 + blockData1 * 255, 4));
                valueFloat = (float) (blockData2 + blockData1 * 255);
                break;
    
            case 49:
                value = String.valueOf(round((blockData2 * 0.25) * blockData1 * 0.1, 4));
                unit = MILLI_GRAMM_PER_HOUR;
                valueFloat = (float) ((blockData2 * 0.25) * blockData1 * 0.1);
                break;
    
            case 50:
                if (blockData1 == 0) {
                    value = String.valueOf(round((blockData2 - 128) * 100, 4));
                    unit = MILLI_BAR;
                    valueFloat = (float) ((blockData2 - 128) * 100);
                } else {
                    value = String.valueOf(round((blockData2 - 128) * 100 / (float) blockData1, 4));
                    unit = MILLI_BAR;
                    valueFloat = (float) ((blockData2 - 128) * 100 / (float) blockData1);
                }
                break;
    
            case 51:
                value = String.valueOf(round((blockData2 - 128) * 0.00390625 * blockData1, 4));
                unit = MILLI_GRAMM_PER_HOUR;
                valueFloat = (float) ((blockData2 - 128) * 0.00390625 * blockData1);
                break;
    
            case 52:
                value = String.valueOf(round(blockData2 * 0.02 * blockData1 - blockData1, 4));
                unit = " Nm";
                valueFloat = (float) (blockData2 * 0.02 * blockData1 - blockData1);
                break;
    
            case 53:
                value = String.valueOf(round((blockData2 - 128) * 1.4222 + 0.006 * blockData1, 4));
                unit = " g/s";
                valueFloat = (float) ((blockData2 - 128) * 1.4222 + 0.006 * blockData1);
                break;
    
            case 54:
                value = String.valueOf(round(blockData1 * 256 + blockData2, 4));
                unit = " Count";
                valueFloat = (float) (blockData1 * 256 + blockData2);
                break;
    
            case 55:
                value = String.valueOf(round(blockData1 * blockData2 * 0.005, 4));
                unit = SECOND;
                valueFloat = (float) (blockData1 * blockData2 * 0.005);
                break;
    
            case 56:
                value = String.valueOf(round(blockData1 * 256 + blockData2, 4));
                unit = " WSC";
                valueFloat = (float) (blockData1 * 256 + blockData2);
                break;
    
            case 57:
                value = String.valueOf(round(blockData1 * 256 + blockData2 + 65536, 4));
                unit = " WSC";
                valueFloat = (float) (blockData1 * 256 + blockData2 + 65536);
                break;
    
            case 58:
                if (blockData2 > 128) {
                    value = String.valueOf(round(1.0225 * (256 - blockData2), 4));
                    valueFloat = (float) (1.0225 * (256 - blockData2));
                } else {
                    value = String.valueOf(round(1.0225 * blockData2, 4));
                    valueFloat = (float) (1.0225 * blockData2);
                }
                break;
    
            case 59:
                value = String.valueOf(round((blockData1 * 256 + blockData2) * 0.000030517578125, 4));
                valueFloat = (float) ((blockData1 * 256 + blockData2) * 0.000030517578125);
                break;
    
            case 60:
                value = String.valueOf(round((blockData1 * 256 + blockData2) * 0.01, 4));
                unit = SECOND;
                valueFloat = (float) ((blockData1 * 256 + blockData2) * 0.01);
                break;
    
            case 61:
                if (blockData1 == 0) {
                    value = String.valueOf(round(blockData2 - 128, 4));
                    valueFloat = (float) (blockData2 - 128);
                } else {
                    value = String.valueOf(round((blockData2 - 128) / (float) blockData1, 4));
                    valueFloat = (float) ((blockData2 - 128) / (float) blockData1);
                }
                break;
    
            case 62:
                value = String.valueOf(round(0.256 * blockData1 * blockData2, 4));
                unit = SECOND;
                valueFloat = (float) (0.256 * blockData1 * blockData2);
                break;
    
            case 63:
                value = (char) blockData1 + "+" + (char) blockData2;
                break;
    
            case 64:
                value = String.valueOf(round(blockData1 + blockData2, 4));
                unit = " Ohm";
                valueFloat = (float) (blockData1 + blockData2);
                break;
    
            case 65:
                value = String.valueOf(round(0.01 * blockData1 * (blockData2 - 127), 4));
                unit = " mm";
                valueFloat = (float) (0.01 * blockData1 * (blockData2 - 127));
                break;
    
            case 66:
                value = String.valueOf(round((blockData1 * blockData2) * 0.001956487, 4));
                unit = VOLT;
                valueFloat = (float) ((blockData1 * blockData2) * 0.001956487);
                break;
    
            case 67:
                value = String.valueOf(round((640 * blockData1) + blockData2 * 2.5, 4));
                unit = DEGREE;
                valueFloat = (float) ((640 * blockData1) + blockData2 * 2.5);
                break;
    
            case 68:
                value = String.valueOf(round((256 * blockData1 + blockData2) * 0.13577732518, 4));
                unit = " deg/s";
                valueFloat = (float) ((256 * blockData1 + blockData2) * 0.13577732518);
                break;
    
            case 69:
                value = String.valueOf(round((256 * blockData1 + blockData2) * 0.3254, 4));
                unit = " Bar";
                valueFloat = (float) ((256 * blockData1 + blockData2) * 0.3254);
                break;
    
            case 70:
                value = String.valueOf(round((256 * blockData1 + blockData2) * 0.192, 4));
                unit = " m/s^2";
                valueFloat = (float) ((256 * blockData1 + blockData2) * 0.192);
                break;
    
            default:
                value = "Нет данных";
                break;
        }
        dto.setValue(value);
        dto.setUnit(unit);
        dto.setValueFloat(valueFloat);
        return dto;
    }
}
