package com.vagm.vagmdroid.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.os.Environment;
import android.util.SparseArray;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.vagm.vagmdroid.dto.LabelDTO;
import com.vagm.vagmdroid.dto.LabelDTO.Group;

/**
 * The Class LabelService.
 * @author Roman_Konovalov
 */
@Singleton
public class LabelService {

	/**
	 * COMMENT_SYMBOl.
	 */
	private static final String COMMENT_SYMBOL = ";";

	/**
	 * LOG.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(LabelService.class);

	/**
	 * propertyService.
	 */
	@Inject
	private PropertyService propertyService;

	/**
	 * constructor.
	 */
	public LabelService() {
	}

	/**
	 * getLabelFile.
	 * @param fullEcu
	 *            fullEcu
	 * @return LabelFileName
	 */
	public String getLabelFileName(final String fullEcu) {
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
			inputStream = DataStreamService.class.getClassLoader().getResourceAsStream(
					"assets" + File.separator + "labels" + File.separator + "Redirect.txt");
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
								LOG.debug("Found label file for ECU: {}, label: {}", fullEcu, tokens[2]);
								return tokens[2];
							}
						}
					} else {
						result = tokens[2];
					}
				}
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

		if (result != null) {
			LOG.debug("Found label file for ECU: {}, label: {}", fullEcu, result);
		} else {
			result = ecu.substring(0, 3) + "-" + ecu.substring(3, 6) + "-" + ecu.substring(6, 9) + ".lbl";
			LOG.debug("Cannot find label file for ECU: {}, build default file name: ", fullEcu, result);
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
	 * @param fileName fileName
	 * @return Labels
	 */
	public SparseArray<LabelDTO> getLabels(final String fileName) {
		//LOG.debug("Begin getting labels for ecu: " + ecu);
		//String fileName = getLabelFileName(ecu);
		SparseArray<LabelDTO> result = new SparseArray<LabelDTO>();
		BufferedReader reader = null;
		Set<Integer> recordsCount = new HashSet<Integer>();
		try {
			File file = new File(Environment.getExternalStorageDirectory() + File.separator + propertyService.getAppName() + File.separator
					+ "labels" + File.separator + fileName);
			if (!file.exists()) {
				LOG.debug("Label file: " + fileName + " doesn't exist");
				return result;
			}
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "utf-8"));

			String st;
			int lineCount = 0;
			int groupNumber;
			int prevGroupNumber = -1;
			int blockNumber;
			boolean firstCycle = true;
			LabelDTO labelDTO = null;
			while (((st = reader.readLine()) != null)) {
				lineCount++;
				//skip empty string and comments
				if (st.equals("") || st.startsWith(COMMENT_SYMBOL)) {
					continue;
				}

				String[] tokens = st.split(",");
				if (tokens.length < 2) {
					LOG.debug("Wrong syntax in line " + lineCount + ": expected tokens length > 2, but was: " + tokens.length);
					continue;
				}

				//skip adaptation, basic and coding labels
				if (tokens[0].toUpperCase(Locale.ENGLISH).startsWith("A") || tokens[0].toUpperCase(Locale.ENGLISH).startsWith("B")
						|| tokens[0].toUpperCase(Locale.ENGLISH).startsWith("C")) {
					continue;
				}

				try {
					groupNumber = Integer.parseInt(tokens[0]);
					blockNumber = Integer.parseInt(tokens[1]);
				} catch (NumberFormatException e) {
					LOG.debug("Wrong syntax in line " + lineCount + ": expected number, but was: " + tokens[0] + "," + tokens[1]);
					continue;
				}

				if ((blockNumber < 0) || (blockNumber > 4)) {
					LOG.debug("Wrong block number in line " + lineCount + ": expected 0..4, but was: " + blockNumber);
					continue;
				}

				//skip 0 group
				if (groupNumber == 0) {
					continue;
				}

				recordsCount.add(groupNumber);

				if (firstCycle) {
					prevGroupNumber = groupNumber;
					labelDTO = new LabelDTO();
					firstCycle = false;
				}

				if (groupNumber != prevGroupNumber) {
					result.put(prevGroupNumber, labelDTO);
					labelDTO = new LabelDTO();
				}

				prevGroupNumber = groupNumber;
				fillLabel(labelDTO, tokens, blockNumber);

			}
			if (labelDTO != null) {
				result.put(prevGroupNumber, labelDTO);
			}

		} catch (final IOException ex) {
			LOG.error("Cannot label file", ex);
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					LOG.error("Cannot close BufferedReader", e);
				}
			}
		}
		Assert.assertEquals("Wrong records count", recordsCount.size(), result.size());
		LOG.debug("Found group records: " + result.size());
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
			if (tokens.length > 2) {
				label.setTitle(tokens[2]);
			} else {
				label.setTitle("");
			}
		} else {
			label.getGroup()[blockNumber - 1] = getFilledGoup(tokens);
		}
	}

}
