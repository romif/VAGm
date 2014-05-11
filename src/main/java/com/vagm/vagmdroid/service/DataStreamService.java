package com.vagm.vagmdroid.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.util.Log;

import com.vagm.vagmdroid.dto.DataStreamDTO;

/**
 * The Class DataStreamService.
 * @author Roman_Konovalov
 */
public final class DataStreamService {

	/**
	 * TAG constant.
	 */
	private static final String TAG = "VAGm_DataStreamService";

	/**
	 * D.
	 */
	private static final boolean D = true;

	/**
	 * NA37Strings.
	 */
	private static String[] NA37Strings = null;

	/**
	 * constructor.
	 */
	private DataStreamService() {
	}

	/**
	 * round.
	 * @param num num
	 * @param numDecim numDecim
	 * @return round
	 */
	private static double round(final double num, final int numDecim) {
		long p = 1;
		// next line – calculate pow(10,brDecim)
		for (int i = 0; i < numDecim; i++) {
			p *= 10;
		}
		return (double) (int) (p * num + 0.5) / p;
	}

	/**
	 * BinToStr.
	 * @param binary binary
	 * @return String
	 */
	private static String binToStr(final int binary) {
		int b = binary;
		String s = "";
		for (int i = 0; i < 8; ++i) {
			if ((b & 0x01) == 1) {
				s += "1";
			} else {
				s += "0";
			}
			b >>= 1;
		}
		return s;
	}

	/**
	 * getNA37Strings.
	 * @return String array
	 */
	private static String[] getNA37Strings() {
		if (NA37Strings == null) {
			NA37Strings = new String[1288];
			InputStream inputStream = null;
			BufferedReader reader = null;
			try {
				inputStream = DataStreamService.class.getClassLoader().getResourceAsStream("NA37Strings/NA37.txt");
				reader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"));
				String st;
				int i = 0;
				while ((st = reader.readLine()) != null) {
					NA37Strings[i] = st;
					i++;
				}

			} catch (final IOException ex) {
				Log.e(TAG, "Cannot read NA37.txt", ex);
			} finally {
				if (reader != null) {
					try {
						reader.close();
					} catch (IOException e) {
						Log.e(TAG, "Cannot close BufferedReader", e);
					}
				}
			}
		}
		return NA37Strings;
	}

	/**
	 * encodeGroupData.
	 * @param blockData1 blockData1
	 * @param blockData2 blockData2
	 * @param blockCode blockCode
	 * @return DataStreamDTO
	 */
	public static DataStreamDTO encodeGroupData( final int blockCode, final int blockData1, final int blockData2) {
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
			unit = " %";
			valueFloat = (float) (blockData1 * blockData2 * 0.002);
			break;

		case 3:
			value = String.valueOf(round(blockData1 * blockData2 * 0.002, 4));
			unit = " Deg";
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
			unit = " V";
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
			unit = " Deg";
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
			unit = " ms";
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
			unit = " mbar";
			valueFloat = (float) (0.04 * blockData1 * blockData2);
			break;

		case 19:
			value = String.valueOf(round(blockData1 * blockData2 * 0.01, 4));
			unit = " l";
			valueFloat = (float) (blockData1 * blockData2 * 0.01);
			break;

		case 20:
			value = String.valueOf(round((blockData2 - 128) * 0.0078125 * blockData1, 4));
			unit = " %";
			valueFloat = (float) ((blockData2 - 128) * 0.0078125 * blockData1);
			break;

		case 21:
			value = String.valueOf(round(0.001 * blockData1 * blockData2, 4));
			unit = " V";
			valueFloat = (float) (0.001 * blockData1 * blockData2);
			break;

		case 22:
			value = String.valueOf(round(0.001 * blockData1 * blockData2, 4));
			unit = " ms";
			valueFloat = (float) (0.001 * blockData1 * blockData2);
			break;

		case 23:
			value = String.valueOf(round(blockData1 * blockData2 * 0.00390625, 1));
			unit = " %";
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
				unit = " %";
				valueFloat = (float) (100 * blockData2);
			} else {
				value = String.valueOf(round(100 * blockData2 / blockData1, 3));
				unit = " %";
				valueFloat = (float) (100 * blockData2 / blockData1);
			}
			break;

		case 34:
			value = String.valueOf(round(0.01 * blockData1 * (blockData2 - 128), 4));
			unit = " kW";
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
			unit = " kW";
			valueFloat = (float) (0.001 * blockData1 * (blockData2 - 128));
			break;

		case 39:
			// temp=String.valueOf(round(Math.abs(b-128)*0.01*a,4));
			// temp1=" ° ATDC";
			// temp=String.valueOf(round((b-128)/255*a,4)); temp1=" mg/h";
			value = String.valueOf(round(blockData1 * blockData2 * 0.00390625, 1));
			unit = " mg/h";
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
			unit = " kW";
			valueFloat = (float) (0.1 * blockData2 + 25.5 * blockData2 - 400);
			break;

		case 43:
			value = String.valueOf(round(0.1 * blockData2 + 25.5 * blockData2, 4));
			unit = " V";
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
			unit = " kW";
			valueFloat = (float) (blockData1 * (blockData2 - 3200) * 0.0027);
			break;

		case 47:
			value = String.valueOf(round(blockData1 * (blockData2 - 128), 4));
			unit = " ms";
			valueFloat = (float) (blockData1 * (blockData2 - 128));
			break;

		case 48:
			value = String.valueOf(round(blockData2 + blockData1 * 255, 4));
			valueFloat = (float) (blockData2 + blockData1 * 255);
			break;

		case 49:
			value = String.valueOf(round((blockData2 * 0.25) * blockData1 * 0.1, 4));
			unit = " mg/h";
			valueFloat = (float) ((blockData2 * 0.25) * blockData1 * 0.1);
			break;

		case 50:
			if (blockData1 == 0) {
				value = String.valueOf(round((blockData2 - 128) * 100, 4));
				unit = " mbar";
				valueFloat = (float) ((blockData2 - 128) * 100);
			} else {
				value = String.valueOf(round((blockData2 - 128) * 100 / blockData1, 4));
				unit = " mbar";
				valueFloat = (float) ((blockData2 - 128) * 100 / blockData1);
			}
			break;

		case 51:
			value = String.valueOf(round((blockData2 - 128) * 0.00390625 * blockData1, 4));
			unit = " mg/h";
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
			unit = " s";
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
			unit = " s";
			valueFloat = (float) ((blockData1 * 256 + blockData2) * 0.01);
			break;

		case 61:
			if (blockData1 == 0) {
				value = String.valueOf(round(blockData2 - 128, 4));
				valueFloat = (float) (blockData2 - 128);
			} else {
				value = String.valueOf(round((blockData2 - 128) / blockData1, 4));
				valueFloat = (float) ((blockData2 - 128) / blockData1);
			}
			break;

		case 62:
			value = String.valueOf(round(0.256 * blockData1 * blockData2, 4));
			unit = " s";
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
			unit = " V";
			valueFloat = (float) ((blockData1 * blockData2) * 0.001956487);
			break;

		case 67:
			value = String.valueOf(round((640 * blockData1) + blockData2 * 2.5, 4));
			unit = " Deg";
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
