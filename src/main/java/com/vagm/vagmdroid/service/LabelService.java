package com.vagm.vagmdroid.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.os.Environment;
import android.util.Log;
import android.util.SparseArray;

import com.vagm.vagmdroid.dto.LabelDTO;
import com.vagm.vagmdroid.dto.LabelDTO.Group;

/**
 * The Class LabelService.
 * @author Roman_Konovalov
 */
public final class LabelService {

	/**
	 * TAG constant.
	 */
	private static final String TAG = "VAGm_LabelService";

	/**
	 * D.
	 */
	private static final boolean D = PropertyService.isDebug();

	/**
	 * constructor.
	 */
	private LabelService() {
	}

	/**
	 * getLabelFile.
	 * @param fullEcu
	 *            fullEcu
	 * @return LabelFileName
	 */
	public static String getLabelFileName(final String fullEcu) {
		String ecu = fullEcu;
		String ecuLit = "";
		String result = null;
		if (ecu.length() > 9) {
			ecuLit = ecu.substring(9);
			ecu = ecu.substring(0, 9);
		}

		InputStream inputStream = null;
		BufferedReader reader = null;
		try {
			inputStream = DataStreamService.class.getClassLoader().getResourceAsStream("labels/Redirect.txt");
			reader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"));

			String st;
			while (((st = reader.readLine()) != null)) {
				if (matchStrings(ecu, st.substring(4, 13))) {
					String[] tokens = st.split(",");
					if (tokens.length > 3) {
						String[] liters = new String[tokens.length - 3];
						System.arraycopy(tokens, 3, liters, 0, liters.length);
						for (String lit : liters) {
							if (ecuLit.equals(lit)) {
								if (D) {
									Log.d(TAG, "Found label file for ECU: " + fullEcu + ", label: " + tokens[2]);
								}
								return tokens[2];
							}
						}
					} else {
						result = tokens[2];
					}
				}
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
		if (D) {
			if (result != null) {
				Log.d(TAG, "Found label file for ECU: " + fullEcu + ", label: " + result);
			} else {
				result = ecu.substring(0, 3) + "-" + ecu.substring(3, 6) + "-" + ecu.substring(6, 9) + ".lbl";
				Log.d(TAG, "Cannot find label file for ECU: " + fullEcu + ", build default file name: " + result);
			}
		}
		return result;
	}

	/**
	 * matchStrings.
	 * @param ecu
	 *            ecu
	 * @param pattern
	 *            pattern
	 * @return matching
	 */
	private static boolean matchStrings(final String ecu, final String pattern) {
		String regex = pattern.replaceAll("\\?", ".");
		return ecu.matches(regex);
	}

	/**
	 * getLabels.
	 * @param ecu ecu
	 * @return Labels
	 */
	public static SparseArray<LabelDTO> getLabels(final String ecu) {
		if (D) {
			Log.d(TAG, "Begin getting labels for ecu: " + ecu);
		}
		String fileName = getLabelFileName(ecu);
		SparseArray<LabelDTO> result = new SparseArray<LabelDTO>();
		BufferedReader reader = null;
		try {
			File file = new File(Environment.getExternalStorageDirectory() + "/" + PropertyService.getAppName() + "/labels/" + fileName);
			if (!file.exists()) {
				if (D) {
					Log.d(TAG, "Label file: " + fileName + " doesn't exist");
				}
				return result;
			}
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "utf-8"));

			String st;
			int lineCount = 0;
			int groupNumber;
			int prevGroupNumber = -1;
			int blockNumber;
			boolean newGroup = true;
			LabelDTO labelDTO = new LabelDTO();
			while (((st = reader.readLine()) != null)) {
				lineCount++;
				if (st.equals("") || st.startsWith(";")) {
					continue;
				}

				String[] tokens = st.split(",");
				if (tokens.length < 3) {
					if (D) {
						Log.d(TAG, "Wrong syntax in line " + lineCount + ": expected tokens length > 3, but was: " + tokens.length);
					}
					continue;
				}

				try {
					groupNumber = Integer.parseInt(tokens[0]);
					blockNumber = Integer.parseInt(tokens[1]);
				} catch (NumberFormatException e) {
					if (D) {
						Log.d(TAG, "Wrong syntax in line " + lineCount + ": expected number, but was: " + tokens[0] + "," + tokens[1]);
					}
					continue;
				}

				if ((blockNumber < 0) || (blockNumber > 4)) {
					if (D) {
						Log.d(TAG, "Wrong block number in line " + lineCount + ": expected 0..4, but was: " + blockNumber);
					}
					continue;
				}

				if (groupNumber == 0) {
					continue;
				}

				if (newGroup) {
					prevGroupNumber = groupNumber;
					newGroup = false;
					fillLabel(labelDTO, tokens, blockNumber);
				} else {
					if (groupNumber != prevGroupNumber) {
						result.put(prevGroupNumber, labelDTO);
						newGroup = true;
						labelDTO = new LabelDTO();
					}
					fillLabel(labelDTO, tokens, blockNumber);
				}

			}

		} catch (final IOException ex) {
			Log.e(TAG, "Cannot label file", ex);
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					Log.e(TAG, "Cannot close BufferedReader", e);
				}
			}
		}
		if (D) {
			Log.d(TAG, "Found group records: " + result.size());
		}
		return result;
	}

	/**
	 * getFilledGoup.
	 * @param tokens tokens
	 * @return FilledGoup
	 */
	private static Group getFilledGoup(final String[] tokens) {
		switch (tokens.length) {
		case 3:
			return new Group(tokens[2], "", "");

		case 4:
			return new Group(tokens[2], tokens[3], "");

		case 5:
			return new Group(tokens[2], tokens[3], tokens[4]);

		default:
			return new Group("", "", "");
		}
	}

	/**
	 * fillLabel.
	 * @param label label
	 * @param tokens tokens
	 * @param blockNumber blockNumber
	 */
	private static void fillLabel(final LabelDTO label, final String[] tokens, final int blockNumber) {
		if (blockNumber == 0) {
			label.setTitle(tokens[2]);
		} else {
			label.getGroup()[blockNumber - 1] = getFilledGoup(tokens);
		}
	}

}
