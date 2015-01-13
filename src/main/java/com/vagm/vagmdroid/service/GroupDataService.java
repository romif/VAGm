package com.vagm.vagmdroid.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Singleton;
import com.vagm.vagmdroid.dto.DataStreamDTO;

@Singleton
public class GroupDataService {

    private static final String SECOND = " s";

    private static final String MILLI_GRAMM_PER_HOUR = " mg/h";

    private static final String KILLO_WATT = " kW";

    private static final String MILLI_BAR = " mbar";

    private static final String MILLI_SECOND = " ms";

    private static final String VOLT = " V";

    private static final String DEGREE = " Deg";

    private static final String PERCENT = " %";

    private static final Logger LOG = LoggerFactory.getLogger(GroupDataService.class);

    private Map<Integer, GroupDataFunction> groupDataMap = null;
    
    private String[] nA37Strings = null;

    public DataStreamDTO encodeGroupData(final int blockCode, final int blockData1, final int blockData2) {
        GroupDataFunction function = getgroupDataMap().get(blockCode);
        if (function == null) {
            throw new IllegalArgumentException("Function for blockCode = " + blockCode + "not found");
        }
        return function.getDataStreamDTO(blockData1, blockData2);
    }

    private Map<Integer, GroupDataFunction> getgroupDataMap() {
        if (groupDataMap != null) {
            return groupDataMap;
        }

        groupDataMap = new HashMap<>();

        fillFirstPart(groupDataMap);

        fillSecondPart(groupDataMap);
        
        return groupDataMap;
    }

    private void fillSecondPart(Map<Integer, GroupDataFunction> groupDataMap) {
        groupDataMap.put(36, new GroupDataFunction() {

            @Override
            public DataStreamDTO getDataStreamDTO(final int blockData1, final int blockData2) {
                final DataStreamDTO dto = new DataStreamDTO();
                dto.setValue(String.valueOf(round(blockData2 * 10 + blockData1 * 2560, 0)));
                dto.setUnit(" km");
                dto.setValueFloat((float) (blockData2 * 10 + blockData1 * 2560));
                return dto;
            }
        });

        groupDataMap.put(37, new GroupDataFunction() {

            @Override
            public DataStreamDTO getDataStreamDTO(final int blockData1, final int blockData2) {
                final DataStreamDTO dto = new DataStreamDTO();
                // temp=Integer.toHexString(a);
                // temp1=" "+Integer.toHexString(b));
                // if (na37==null)na37=new NA37());
                // temp=na37.mas[b]);
                dto.setValue(getNA37Strings()[blockData2 - 1]);
                return dto;
            }
        });

        groupDataMap.put(38, new GroupDataFunction() {

            @Override
            public DataStreamDTO getDataStreamDTO(final int blockData1, final int blockData2) {
                final DataStreamDTO dto = new DataStreamDTO();
                dto.setValue(String.valueOf(round(0.001 * blockData1 * (blockData2 - 128), 4)));
                dto.setUnit(KILLO_WATT);
                dto.setValueFloat((float) (0.001 * blockData1 * (blockData2 - 128)));
                return dto;
            }
        });

        groupDataMap.put(39, new GroupDataFunction() {

            @Override
            public DataStreamDTO getDataStreamDTO(final int blockData1, final int blockData2) {
                final DataStreamDTO dto = new DataStreamDTO();
                // temp=String.valueOf(round(Math.abs(b-128)*0.01*a,4)));
                // temp1=" ° ATDC");
                // temp=String.valueOf(round((b-128)/255*a,4)); temp1=" mg/h");
                dto.setValue(String.valueOf(round(blockData1 * blockData2 * 0.00390625, 1)));
                dto.setUnit(MILLI_GRAMM_PER_HOUR);
                dto.setValueFloat((float) (blockData1 * blockData2 * 0.00390625));
                // temp=Integer.toString(a); temp1=" "+Integer.toString(b));
                return dto;
            }
        });

        groupDataMap.put(40, new GroupDataFunction() {

            @Override
            public DataStreamDTO getDataStreamDTO(final int blockData1, final int blockData2) {
                final DataStreamDTO dto = new DataStreamDTO();
                dto.setValue(String.valueOf(round(0.1 * blockData2 + 25.5 * blockData2 - 400, 4)));
                dto.setUnit(" A");
                dto.setValueFloat((float) (0.1 * blockData2 + 25.5 * blockData2 - 400));
                return dto;
            }
        });

        groupDataMap.put(41, new GroupDataFunction() {

            @Override
            public DataStreamDTO getDataStreamDTO(final int blockData1, final int blockData2) {
                final DataStreamDTO dto = new DataStreamDTO();
                dto.setValue(String.valueOf(round(blockData2 + blockData2 * 255, 4)));
                dto.setUnit(" Ah");
                dto.setValueFloat((float) (blockData2 + blockData2 * 255));
                return dto;
            }
        });

        groupDataMap.put(42, new GroupDataFunction() {

            @Override
            public DataStreamDTO getDataStreamDTO(final int blockData1, final int blockData2) {
                final DataStreamDTO dto = new DataStreamDTO();
                dto.setValue(String.valueOf(round(0.1 * blockData2 + 25.5 * blockData2 - 400, 4)));
                dto.setUnit(KILLO_WATT);
                dto.setValueFloat((float) (0.1 * blockData2 + 25.5 * blockData2 - 400));
                return dto;
            }
        });

        groupDataMap.put(43, new GroupDataFunction() {

            @Override
            public DataStreamDTO getDataStreamDTO(final int blockData1, final int blockData2) {
                final DataStreamDTO dto = new DataStreamDTO();
                dto.setValue(String.valueOf(round(0.1 * blockData2 + 25.5 * blockData2, 4)));
                dto.setUnit(VOLT);
                dto.setValueFloat((float) (0.1 * blockData2 + 25.5 * blockData2));
                return dto;
            }
        });

        groupDataMap.put(44, new GroupDataFunction() {

            @Override
            public DataStreamDTO getDataStreamDTO(final int blockData1, final int blockData2) {
                final DataStreamDTO dto = new DataStreamDTO();
                // temp=(char)a+":"+(char)b+" h");
                dto.setValue(Integer.toString(blockData1) + ":" + Integer.toString(blockData2));
                dto.setUnit(" h");
                return dto;
            }
        });

        groupDataMap.put(45, new GroupDataFunction() {

            @Override
            public DataStreamDTO getDataStreamDTO(final int blockData1, final int blockData2) {
                final DataStreamDTO dto = new DataStreamDTO();
                dto.setValue(String.valueOf(round(0.1 * blockData1 * blockData2 / 100, 4)));
                dto.setValueFloat((float) (0.1 * blockData1 * blockData2 / 100));
                return dto;
            }
        });

        groupDataMap.put(46, new GroupDataFunction() {

            @Override
            public DataStreamDTO getDataStreamDTO(final int blockData1, final int blockData2) {
                final DataStreamDTO dto = new DataStreamDTO();
                dto.setValue(String.valueOf(round((blockData1 * (blockData2 - 3200) * 0.0027), 4)));
                dto.setUnit(KILLO_WATT);
                dto.setValueFloat((float) (blockData1 * (blockData2 - 3200) * 0.0027));
                return dto;
            }
        });

        groupDataMap.put(47, new GroupDataFunction() {

            @Override
            public DataStreamDTO getDataStreamDTO(final int blockData1, final int blockData2) {
                final DataStreamDTO dto = new DataStreamDTO();
                dto.setValue(String.valueOf(round(blockData1 * (blockData2 - 128), 4)));
                dto.setUnit(MILLI_SECOND);
                dto.setValueFloat((float) (blockData1 * (blockData2 - 128)));
                return dto;
            }
        });

        groupDataMap.put(48, new GroupDataFunction() {

            @Override
            public DataStreamDTO getDataStreamDTO(final int blockData1, final int blockData2) {
                final DataStreamDTO dto = new DataStreamDTO();
                dto.setValue(String.valueOf(round(blockData2 + blockData1 * 255, 4)));
                dto.setValueFloat((float) (blockData2 + blockData1 * 255));
                return dto;
            }
        });

        groupDataMap.put(49, new GroupDataFunction() {

            @Override
            public DataStreamDTO getDataStreamDTO(final int blockData1, final int blockData2) {
                final DataStreamDTO dto = new DataStreamDTO();
                dto.setValue(String.valueOf(round((blockData2 * 0.25) * blockData1 * 0.1, 4)));
                dto.setUnit(MILLI_GRAMM_PER_HOUR);
                dto.setValueFloat((float) ((blockData2 * 0.25) * blockData1 * 0.1));
                return dto;
            }
        });

        groupDataMap.put(50, new GroupDataFunction() {

            @Override
            public DataStreamDTO getDataStreamDTO(final int blockData1, final int blockData2) {
                final DataStreamDTO dto = new DataStreamDTO();
                if (blockData1 == 0) {
                    dto.setValue(String.valueOf(round((blockData2 - 128) * 100, 4)));
                    dto.setUnit(MILLI_BAR);
                    dto.setValueFloat((float) ((blockData2 - 128) * 100));
                } else {
                    dto.setValue(String.valueOf(round((blockData2 - 128) * 100 / (float) blockData1, 4)));
                    dto.setUnit(MILLI_BAR);
                    dto.setValueFloat((float) ((blockData2 - 128) * 100 / (float) blockData1));
                }
                return dto;
            }
        });

        groupDataMap.put(51, new GroupDataFunction() {

            @Override
            public DataStreamDTO getDataStreamDTO(final int blockData1, final int blockData2) {
                final DataStreamDTO dto = new DataStreamDTO();
                dto.setValue(String.valueOf(round((blockData2 - 128) * 0.00390625 * blockData1, 4)));
                dto.setUnit(MILLI_GRAMM_PER_HOUR);
                dto.setValueFloat((float) ((blockData2 - 128) * 0.00390625 * blockData1));
                return dto;
            }
        });

        groupDataMap.put(52, new GroupDataFunction() {

            @Override
            public DataStreamDTO getDataStreamDTO(final int blockData1, final int blockData2) {
                final DataStreamDTO dto = new DataStreamDTO();
                dto.setValue(String.valueOf(round(blockData2 * 0.02 * blockData1 - blockData1, 4)));
                dto.setUnit(" Nm");
                dto.setValueFloat((float) (blockData2 * 0.02 * blockData1 - blockData1));
                return dto;
            }
        });

        groupDataMap.put(53, new GroupDataFunction() {

            @Override
            public DataStreamDTO getDataStreamDTO(final int blockData1, final int blockData2) {
                final DataStreamDTO dto = new DataStreamDTO();
                dto.setValue(String.valueOf(round((blockData2 - 128) * 1.4222 + 0.006 * blockData1, 4)));
                dto.setUnit(" g/s");
                dto.setValueFloat((float) ((blockData2 - 128) * 1.4222 + 0.006 * blockData1));
                return dto;
            }
        });

        groupDataMap.put(54, new GroupDataFunction() {

            @Override
            public DataStreamDTO getDataStreamDTO(final int blockData1, final int blockData2) {
                final DataStreamDTO dto = new DataStreamDTO();
                dto.setValue(String.valueOf(round(blockData1 * 256 + blockData2, 4)));
                dto.setUnit(" Count");
                dto.setValueFloat((float) (blockData1 * 256 + blockData2));
                return dto;
            }
        });

        groupDataMap.put(55, new GroupDataFunction() {

            @Override
            public DataStreamDTO getDataStreamDTO(final int blockData1, final int blockData2) {
                final DataStreamDTO dto = new DataStreamDTO();
                dto.setValue(String.valueOf(round(blockData1 * blockData2 * 0.005, 4)));
                dto.setUnit(SECOND);
                dto.setValueFloat((float) (blockData1 * blockData2 * 0.005));
                return dto;
            }
        });

        groupDataMap.put(56, new GroupDataFunction() {

            @Override
            public DataStreamDTO getDataStreamDTO(final int blockData1, final int blockData2) {
                final DataStreamDTO dto = new DataStreamDTO();
                dto.setValue(String.valueOf(round(blockData1 * 256 + blockData2, 4)));
                dto.setUnit(" WSC");
                dto.setValueFloat((float) (blockData1 * 256 + blockData2));
                return dto;
            }
        });

        groupDataMap.put(57, new GroupDataFunction() {

            @Override
            public DataStreamDTO getDataStreamDTO(final int blockData1, final int blockData2) {
                final DataStreamDTO dto = new DataStreamDTO();
                dto.setValue(String.valueOf(round(blockData1 * 256 + blockData2 + 65536, 4)));
                dto.setUnit(" WSC");
                dto.setValueFloat((float) (blockData1 * 256 + blockData2 + 65536));
                return dto;
            }
        });

        groupDataMap.put(58, new GroupDataFunction() {

            @Override
            public DataStreamDTO getDataStreamDTO(final int blockData1, final int blockData2) {
                final DataStreamDTO dto = new DataStreamDTO();
                if (blockData2 > 128) {
                    dto.setValue(String.valueOf(round(1.0225 * (256 - blockData2), 4)));
                    dto.setValueFloat((float) (1.0225 * (256 - blockData2)));
                } else {
                    dto.setValue(String.valueOf(round(1.0225 * blockData2, 4)));
                    dto.setValueFloat((float) (1.0225 * blockData2));
                }
                return dto;
            }
        });

        groupDataMap.put(59, new GroupDataFunction() {

            @Override
            public DataStreamDTO getDataStreamDTO(final int blockData1, final int blockData2) {
                final DataStreamDTO dto = new DataStreamDTO();
                dto.setValue(String.valueOf(round((blockData1 * 256 + blockData2) * 0.000030517578125, 4)));
                dto.setValueFloat((float) ((blockData1 * 256 + blockData2) * 0.000030517578125));
                return dto;
            }
        });

        groupDataMap.put(60, new GroupDataFunction() {

            @Override
            public DataStreamDTO getDataStreamDTO(final int blockData1, final int blockData2) {
                final DataStreamDTO dto = new DataStreamDTO();
                dto.setValue(String.valueOf(round((blockData1 * 256 + blockData2) * 0.01, 4)));
                dto.setUnit(SECOND);
                dto.setValueFloat((float) ((blockData1 * 256 + blockData2) * 0.01));
                return dto;
            }
        });

        groupDataMap.put(61, new GroupDataFunction() {

            @Override
            public DataStreamDTO getDataStreamDTO(final int blockData1, final int blockData2) {
                final DataStreamDTO dto = new DataStreamDTO();
                if (blockData1 == 0) {
                    dto.setValue(String.valueOf(round(blockData2 - 128, 4)));
                    dto.setValueFloat((float) (blockData2 - 128));
                } else {
                    dto.setValue(String.valueOf(round((blockData2 - 128) / (float) blockData1, 4)));
                    dto.setValueFloat((float) ((blockData2 - 128) / (float) blockData1));
                }
                return dto;
            }
        });

        groupDataMap.put(62, new GroupDataFunction() {

            @Override
            public DataStreamDTO getDataStreamDTO(final int blockData1, final int blockData2) {
                final DataStreamDTO dto = new DataStreamDTO();
                dto.setValue(String.valueOf(round(0.256 * blockData1 * blockData2, 4)));
                dto.setUnit(SECOND);
                dto.setValueFloat((float) (0.256 * blockData1 * blockData2));
                return dto;
            }
        });

        groupDataMap.put(63, new GroupDataFunction() {

            @Override
            public DataStreamDTO getDataStreamDTO(final int blockData1, final int blockData2) {
                final DataStreamDTO dto = new DataStreamDTO();
                dto.setValue((char) blockData1 + "+" + (char) blockData2);
                return dto;
            }
        });

        groupDataMap.put(64, new GroupDataFunction() {

            @Override
            public DataStreamDTO getDataStreamDTO(final int blockData1, final int blockData2) {
                final DataStreamDTO dto = new DataStreamDTO();
                dto.setValue(String.valueOf(round(blockData1 + blockData2, 4)));
                dto.setUnit(" Ohm");
                dto.setValueFloat((float) (blockData1 + blockData2));
                return dto;
            }
        });

        groupDataMap.put(65, new GroupDataFunction() {

            @Override
            public DataStreamDTO getDataStreamDTO(final int blockData1, final int blockData2) {
                final DataStreamDTO dto = new DataStreamDTO();
                dto.setValue(String.valueOf(round(0.01 * blockData1 * (blockData2 - 127), 4)));
                dto.setUnit(" mm");
                dto.setValueFloat((float) (0.01 * blockData1 * (blockData2 - 127)));
                return dto;
            }
        });

        groupDataMap.put(66, new GroupDataFunction() {

            @Override
            public DataStreamDTO getDataStreamDTO(final int blockData1, final int blockData2) {
                final DataStreamDTO dto = new DataStreamDTO();
                dto.setValue(String.valueOf(round((blockData1 * blockData2) * 0.001956487, 4)));
                dto.setUnit(VOLT);
                dto.setValueFloat((float) ((blockData1 * blockData2) * 0.001956487));
                return dto;
            }
        });

        groupDataMap.put(67, new GroupDataFunction() {

            @Override
            public DataStreamDTO getDataStreamDTO(final int blockData1, final int blockData2) {
                final DataStreamDTO dto = new DataStreamDTO();
                dto.setValue(String.valueOf(round((640 * blockData1) + blockData2 * 2.5, 4)));
                dto.setUnit(DEGREE);
                dto.setValueFloat((float) ((640 * blockData1) + blockData2 * 2.5));
                return dto;
            }
        });

        groupDataMap.put(68, new GroupDataFunction() {

            @Override
            public DataStreamDTO getDataStreamDTO(final int blockData1, final int blockData2) {
                final DataStreamDTO dto = new DataStreamDTO();
                dto.setValue(String.valueOf(round((256 * blockData1 + blockData2) * 0.13577732518, 4)));
                dto.setUnit(" deg/s");
                dto.setValueFloat((float) ((256 * blockData1 + blockData2) * 0.13577732518));
                return dto;
            }
        });

        groupDataMap.put(69, new GroupDataFunction() {

            @Override
            public DataStreamDTO getDataStreamDTO(final int blockData1, final int blockData2) {
                final DataStreamDTO dto = new DataStreamDTO();
                dto.setValue(String.valueOf(round((256 * blockData1 + blockData2) * 0.3254, 4)));
                dto.setUnit(" Bar");
                dto.setValueFloat((float) ((256 * blockData1 + blockData2) * 0.3254));
                return dto;
            }
        });

        groupDataMap.put(70, new GroupDataFunction() {

            @Override
            public DataStreamDTO getDataStreamDTO(final int blockData1, final int blockData2) {
                final DataStreamDTO dto = new DataStreamDTO();
                dto.setValue(String.valueOf(round((256 * blockData1 + blockData2) * 0.192, 4)));
                dto.setUnit(" m/s^2");
                dto.setValueFloat((float) ((256 * blockData1 + blockData2) * 0.192));
                return dto;
            }
        });
    }

    private void fillFirstPart(Map<Integer, GroupDataFunction> groupDataMap) {
        groupDataMap.put(1, new GroupDataFunction() {

            @Override
            public DataStreamDTO getDataStreamDTO(final int blockData1, final int blockData2) {
                final DataStreamDTO dto = new DataStreamDTO();
                dto.setValue(String.valueOf(round(0.2 * blockData1 * blockData2, 4)));
                dto.setUnit(" rpm");
                dto.setValueFloat((float) (0.2 * blockData1 * blockData2));
                return dto;
            }
        });

        groupDataMap.put(2, new GroupDataFunction() {

            @Override
            public DataStreamDTO getDataStreamDTO(final int blockData1, final int blockData2) {
                final DataStreamDTO dto = new DataStreamDTO();
                dto.setValue(String.valueOf(round(blockData1 * blockData2 * 0.002, 4)));
                dto.setUnit(PERCENT);
                dto.setValueFloat((float) (blockData1 * blockData2 * 0.002));
                return dto;
            }
        });

        groupDataMap.put(3, new GroupDataFunction() {

            @Override
            public DataStreamDTO getDataStreamDTO(final int blockData1, final int blockData2) {
                final DataStreamDTO dto = new DataStreamDTO();
                dto.setValue(String.valueOf(round(blockData1 * blockData2 * 0.002, 4)));
                dto.setUnit(DEGREE);
                dto.setValueFloat((float) (blockData1 * blockData2 * 0.002));
                return dto;
            }
        });

        groupDataMap.put(4, new GroupDataFunction() {

            @Override
            public DataStreamDTO getDataStreamDTO(final int blockData1, final int blockData2) {
                final DataStreamDTO dto = new DataStreamDTO();
                dto.setValue(String.valueOf(round(Math.abs(blockData2 - 127) * 0.01 * blockData1, 4)));
                dto.setUnit(" ° ATDC");
                dto.setValueFloat((float) (Math.abs(blockData2 - 127) * 0.01 * blockData1));
                return dto;
            }
        });

        groupDataMap.put(5, new GroupDataFunction() {

            @Override
            public DataStreamDTO getDataStreamDTO(final int blockData1, final int blockData2) {
                final DataStreamDTO dto = new DataStreamDTO();
                dto.setValue(String.valueOf(round(blockData1 * (blockData2 - 100) * 0.1, 4)));
                dto.setUnit(" °C");
                dto.setValueFloat((float) (blockData1 * (blockData2 - 100) * 0.1));
                return dto;
            }
        });

        groupDataMap.put(6, new GroupDataFunction() {

            @Override
            public DataStreamDTO getDataStreamDTO(final int blockData1, final int blockData2) {
                final DataStreamDTO dto = new DataStreamDTO();
                dto.setValue(String.valueOf(round(blockData1 * blockData2 * 0.001, 4)));
                dto.setUnit(VOLT);
                dto.setValueFloat((float) (blockData1 * blockData2 * 0.001));
                return dto;
            }
        });

        groupDataMap.put(7, new GroupDataFunction() {

            @Override
            public DataStreamDTO getDataStreamDTO(final int blockData1, final int blockData2) {
                final DataStreamDTO dto = new DataStreamDTO();
                dto.setValue(String.valueOf(round(blockData1 * blockData2 * 0.01, 4)));
                dto.setUnit(" km/h");
                dto.setValueFloat((float) (blockData1 * blockData2 * 0.01));
                return dto;
            }
        });

        groupDataMap.put(8, new GroupDataFunction() {

            @Override
            public DataStreamDTO getDataStreamDTO(final int blockData1, final int blockData2) {
                final DataStreamDTO dto = new DataStreamDTO();
                dto.setValue(String.valueOf(round(blockData1 * blockData2 * 0.1, 4)));
                dto.setValueFloat((float) (blockData1 * blockData2 * 0.1));
                return dto;
            }
        });

        groupDataMap.put(9, new GroupDataFunction() {

            @Override
            public DataStreamDTO getDataStreamDTO(final int blockData1, final int blockData2) {
                final DataStreamDTO dto = new DataStreamDTO();
                dto.setValue(String.valueOf(round(Math.abs(blockData2 - 127) * 0.02 * blockData1, 4)));
                dto.setUnit(DEGREE);
                dto.setValueFloat((float) (Math.abs(blockData2 - 127) * 0.02 * blockData1));
                return dto;
            }
        });

        groupDataMap.put(10, new GroupDataFunction() {

            @Override
            public DataStreamDTO getDataStreamDTO(final int blockData1, final int blockData2) {
                final DataStreamDTO dto = new DataStreamDTO();
                if (blockData2 == 0) {
                    dto.setValue("COLD");
                } else {
                    dto.setValue("HOT");
                }
                return dto;
            }
        });

        groupDataMap.put(11, new GroupDataFunction() {

            @Override
            public DataStreamDTO getDataStreamDTO(final int blockData1, final int blockData2) {
                final DataStreamDTO dto = new DataStreamDTO();
                dto.setValue(String.valueOf(round(0.0001 * blockData1 * (blockData2 - 128) + 1, 4)));
                dto.setValueFloat((float) (0.0001 * blockData1 * (blockData2 - 128) + 1));
                return dto;
            }
        });

        groupDataMap.put(12, new GroupDataFunction() {

            @Override
            public DataStreamDTO getDataStreamDTO(final int blockData1, final int blockData2) {
                final DataStreamDTO dto = new DataStreamDTO();
                dto.setValue(String.valueOf(round(0.01 * blockData1 * blockData2, 4)));
                dto.setUnit(" Ohm");
                dto.setValueFloat((float) (0.01 * blockData1 * blockData2));
                return dto;
            }
        });

        groupDataMap.put(13, new GroupDataFunction() {

            @Override
            public DataStreamDTO getDataStreamDTO(final int blockData1, final int blockData2) {
                final DataStreamDTO dto = new DataStreamDTO();
                dto.setValue(String.valueOf(round(0.001 * blockData1 * (blockData2 - 127) + 1, 4)));
                dto.setUnit(" mm");
                dto.setValueFloat((float) (0.001 * blockData1 * (blockData2 - 127) + 1));
                return dto;
            }
        });

        groupDataMap.put(14, new GroupDataFunction() {

            @Override
            public DataStreamDTO getDataStreamDTO(final int blockData1, final int blockData2) {
                final DataStreamDTO dto = new DataStreamDTO();
                dto.setValue(String.valueOf(round(0.005 * blockData1 * blockData2, 4)));
                dto.setUnit(" bar");
                dto.setValueFloat((float) (0.005 * blockData1 * blockData2));
                return dto;
            }
        });

        groupDataMap.put(15, new GroupDataFunction() {

            @Override
            public DataStreamDTO getDataStreamDTO(final int blockData1, final int blockData2) {
                final DataStreamDTO dto = new DataStreamDTO();
                dto.setValue(String.valueOf(round(0.01 * blockData1 * blockData2, 4)));
                dto.setUnit(MILLI_SECOND);
                dto.setValueFloat((float) (0.01 * blockData1 * blockData2));
                return dto;
            }
        });

        groupDataMap.put(16, new GroupDataFunction() {

            @Override
            public DataStreamDTO getDataStreamDTO(final int blockData1, final int blockData2) {
                final DataStreamDTO dto = new DataStreamDTO();
                dto.setValue(binToStr(blockData2));
                dto.setValueFloat((float) (blockData2));
                return dto;
            }
        });

        groupDataMap.put(17, new GroupDataFunction() {

            @Override
            public DataStreamDTO getDataStreamDTO(final int blockData1, final int blockData2) {
                final DataStreamDTO dto = new DataStreamDTO();
                dto.setValue((char) blockData1 + " " + (char) blockData2);
                return dto;
            }
        });

        groupDataMap.put(18, new GroupDataFunction() {

            @Override
            public DataStreamDTO getDataStreamDTO(final int blockData1, final int blockData2) {
                final DataStreamDTO dto = new DataStreamDTO();
                dto.setValue(String.valueOf(round(0.04 * blockData1 * blockData2, 4)));
                dto.setUnit(MILLI_BAR);
                dto.setValueFloat((float) (0.04 * blockData1 * blockData2));
                return dto;
            }
        });

        groupDataMap.put(19, new GroupDataFunction() {

            @Override
            public DataStreamDTO getDataStreamDTO(final int blockData1, final int blockData2) {
                final DataStreamDTO dto = new DataStreamDTO();
                dto.setValue(String.valueOf(round(blockData1 * blockData2 * 0.01, 4)));
                dto.setUnit(" l");
                dto.setValueFloat((float) (blockData1 * blockData2 * 0.01));
                return dto;
            }
        });

        groupDataMap.put(20, new GroupDataFunction() {

            @Override
            public DataStreamDTO getDataStreamDTO(final int blockData1, final int blockData2) {
                final DataStreamDTO dto = new DataStreamDTO();
                dto.setValue(String.valueOf(round((blockData2 - 128) * 0.0078125 * blockData1, 4)));
                dto.setUnit(PERCENT);
                dto.setValueFloat((float) ((blockData2 - 128) * 0.0078125 * blockData1));
                return dto;
            }
        });

        groupDataMap.put(21, new GroupDataFunction() {

            @Override
            public DataStreamDTO getDataStreamDTO(final int blockData1, final int blockData2) {
                final DataStreamDTO dto = new DataStreamDTO();
                dto.setValue(String.valueOf(round(0.001 * blockData1 * blockData2, 4)));
                dto.setUnit(VOLT);
                dto.setValueFloat((float) (0.001 * blockData1 * blockData2));
                return dto;
            }
        });

        groupDataMap.put(22, new GroupDataFunction() {

            @Override
            public DataStreamDTO getDataStreamDTO(final int blockData1, final int blockData2) {
                final DataStreamDTO dto = new DataStreamDTO();
                dto.setValue(String.valueOf(round(0.001 * blockData1 * blockData2, 4)));
                dto.setUnit(MILLI_SECOND);
                dto.setValueFloat((float) (0.001 * blockData1 * blockData2));
                return dto;
            }
        });

        groupDataMap.put(23, new GroupDataFunction() {

            @Override
            public DataStreamDTO getDataStreamDTO(final int blockData1, final int blockData2) {
                final DataStreamDTO dto = new DataStreamDTO();
                dto.setValue(String.valueOf(round(blockData1 * blockData2 * 0.00390625, 1)));
                dto.setUnit(PERCENT);
                dto.setValueFloat((float) (blockData1 * blockData2 * 0.00390625));
                // temp=Integer.toString(a); temp1=" "+Integer.toString(b));
                return dto;
            }
        });

        groupDataMap.put(24, new GroupDataFunction() {

            @Override
            public DataStreamDTO getDataStreamDTO(final int blockData1, final int blockData2) {
                final DataStreamDTO dto = new DataStreamDTO();
                dto.setValue(String.valueOf(round(0.001 * blockData1 * blockData2, 4)));
                dto.setUnit(" A");
                dto.setValueFloat((float) (0.001 * blockData1 * blockData2));
                return dto;
            }
        });

        groupDataMap.put(25, new GroupDataFunction() {

            @Override
            public DataStreamDTO getDataStreamDTO(final int blockData1, final int blockData2) {
                final DataStreamDTO dto = new DataStreamDTO();
                dto.setValue(String.valueOf(round(blockData2 * 1.421 + blockData1 * 0.0054945054, 4)));
                dto.setUnit(" g/s");
                dto.setValueFloat((float) (blockData2 * 1.421 + blockData1 * 0.0054945054));
                return dto;
            }
        });

        groupDataMap.put(26, new GroupDataFunction() {

            @Override
            public DataStreamDTO getDataStreamDTO(final int blockData1, final int blockData2) {
                final DataStreamDTO dto = new DataStreamDTO();
                dto.setValue(String.valueOf(round(blockData1 * blockData2, 4)));
                dto.setUnit(" C");
                dto.setValueFloat((float) (blockData1 * blockData2));
                return dto;
            }
        });

        groupDataMap.put(27, new GroupDataFunction() {

            @Override
            public DataStreamDTO getDataStreamDTO(final int blockData1, final int blockData2) {
                final DataStreamDTO dto = new DataStreamDTO();
                dto.setValue(String.valueOf(round(Math.abs(blockData2 - 128) * 0.01 * blockData1, 1)));
                dto.setUnit(" ° ATDC");
                dto.setValueFloat((float) (Math.abs(blockData2 - 128) * 0.01 * blockData1));
                return dto;
            }
        });

        groupDataMap.put(28, new GroupDataFunction() {

            @Override
            public DataStreamDTO getDataStreamDTO(final int blockData1, final int blockData2) {
                final DataStreamDTO dto = new DataStreamDTO();
                dto.setValue(String.valueOf(round(blockData1 * blockData2, 4)));
                dto.setValueFloat((float) (blockData1 * blockData2));
                return dto;
            }
        });

        groupDataMap.put(29, new GroupDataFunction() {

            @Override
            public DataStreamDTO getDataStreamDTO(final int blockData1, final int blockData2) {
                final DataStreamDTO dto = new DataStreamDTO();
                dto.setValue("1.Kennfeld");
                return dto;
            }
        });

        groupDataMap.put(30, new GroupDataFunction() {

            @Override
            public DataStreamDTO getDataStreamDTO(final int blockData1, final int blockData2) {
                final DataStreamDTO dto = new DataStreamDTO();
                dto.setValue(String.valueOf(round(blockData2 / 12 * blockData1, 4)));
                dto.setUnit("k/w");
                dto.setValueFloat((float) (blockData2 / 12 * blockData1));
                return dto;
            }
        });

        groupDataMap.put(31, new GroupDataFunction() {

            @Override
            public DataStreamDTO getDataStreamDTO(final int blockData1, final int blockData2) {
                final DataStreamDTO dto = new DataStreamDTO();
                dto.setValue(String.valueOf(round(blockData2 * 0.003906250 * blockData1, 4)));
                dto.setUnit(" °C");
                dto.setValueFloat((float) (blockData2 * 0.003906250 * blockData1));
                return dto;
            }
        });

        groupDataMap.put(32, new GroupDataFunction() {

            @Override
            public DataStreamDTO getDataStreamDTO(final int blockData1, final int blockData2) {
                final DataStreamDTO dto = new DataStreamDTO();
                if (blockData2 > 128) {
                    dto.setValue(String.valueOf(round(blockData2 - 256, 4)));
                    dto.setValueFloat((float) (blockData2 - 256));
                } else {
                    dto.setValue(String.valueOf(round(blockData2, 4)));
                    dto.setValueFloat((float) (blockData2));
                }
                return dto;
            }
        });

        groupDataMap.put(33, new GroupDataFunction() {

            @Override
            public DataStreamDTO getDataStreamDTO(final int blockData1, final int blockData2) {
                final DataStreamDTO dto = new DataStreamDTO();
                if (blockData1 == 0) {
                    dto.setValue(String.valueOf(round(100 * blockData2, 3)));
                    dto.setUnit(PERCENT);
                    dto.setValueFloat((float) (100 * blockData2));
                } else {
                    dto.setValue(String.valueOf(round(100 * blockData2 / (float) blockData1, 3)));
                    dto.setUnit(PERCENT);
                    dto.setValueFloat((float) (100 * blockData2 / (float) blockData1));
                }
                return dto;
            }
        });

        groupDataMap.put(34, new GroupDataFunction() {

            @Override
            public DataStreamDTO getDataStreamDTO(final int blockData1, final int blockData2) {
                final DataStreamDTO dto = new DataStreamDTO();
                dto.setValue(String.valueOf(round(0.01 * blockData1 * (blockData2 - 128), 4)));
                dto.setUnit(KILLO_WATT);
                dto.setValueFloat((float) (0.01 * blockData1 * (blockData2 - 128)));
                return dto;
            }
        });

        groupDataMap.put(35, new GroupDataFunction() {

            @Override
            public DataStreamDTO getDataStreamDTO(final int blockData1, final int blockData2) {
                final DataStreamDTO dto = new DataStreamDTO();
                dto.setValue(String.valueOf(round(0.01 * blockData1 * blockData2, 4)));
                dto.setUnit(" l/h");
                dto.setValueFloat((float) (0.01 * blockData1 * blockData2));
                return dto;
            }
        });
    }

    private static double round(final double num, final int numDecim) {
        long p = 1;
        // next line – calculate pow(10,brDecim)
        for (int i = 0; i < numDecim; i++) {
            p *= 10;
        }
        return (double) (int) (p * num + 0.5) / (double) p;
    }

    private static String binToStr(final int binary) {
        int b = binary;
        final StringBuilder builder = new StringBuilder();
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

    private String[] getNA37Strings() {
        if (nA37Strings == null) {
            nA37Strings = new String[1288];
            InputStream inputStream = null;
            BufferedReader reader = null;
            try {
                inputStream = GroupDataService.class.getClassLoader().getResourceAsStream(
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
                IOUtils.closeQuietly(reader);
            }
        }
        return nA37Strings;
    }

}
