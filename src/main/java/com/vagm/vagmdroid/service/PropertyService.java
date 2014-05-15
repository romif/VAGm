package com.vagm.vagmdroid.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import junit.framework.Assert;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class PropertyService.
 * @author Roman_Konovalov
 */
public final class PropertyService {

	/**
	 * LOG.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(PropertyService.class);

	/**
	 * DEFAULT_PROPERTIES_FILENAME.
	 */
	private static final String DEFAULT_PROPERTIES_FILENAME = "VAGm.properties";


	/**
	 * PRODUCTION_PREFIX_KEY.
	 */
	private static final String PRODUCTION_PREFIX_KEY = "vagm.production";


	/**
	 * NAME_PREFIX_KEY.
	 */
	private static final String NAME_PREFIX_KEY = "vagm.name";

	/**
	 * LABELS_FOLDER_PREFIX_KEY.
	 */
	private static final String LABELS_FOLDER_PREFIX_KEY = "vagm.labels.folder";

	/**
	 * DEFAULT_NAME.
	 */
	private static final String DEFAULT_NAME = "VAGm";

	/**
	 * DEFAULT_PRODUCTION.
	 */
	private static final String DEFAULT_PRODUCTION = "true";

	/**
	 * DEFAULT_LABELS_FOLDER.
	 */
	private static final String DEFAULT_LABELS_FOLDER = "labels";

	/**
	 * propertiesFileName.
	 */
	private static String propertiesFileName = null;


	/**
	 * properties.
	 */
	private static Properties properties;

	/**
	 * instance.
	 */
	private static PropertyService instance;

	/**
	 * constructor.
	 * @throws IOException if an error occurs
	 */
	private PropertyService() throws IOException {
		InputStream inputStream = null;
		try {
		String fileName = propertiesFileName == null ? DEFAULT_PROPERTIES_FILENAME : propertiesFileName;
		inputStream = PropertyService.class.getClassLoader().getResourceAsStream("assets" + File.separator + fileName);
		properties = new Properties();
		properties.load(inputStream);
		} finally {
			if (inputStream != null) {
				try {
				inputStream.close();
				} catch (IOException ex) {
					LOG.error("Cannot close inputStream", ex);
				}
			}
		}
	}

	/**
	 * init.
	 * @throws IOException if an error occurs
	 */
	public static void init() throws IOException {
		if (instance == null) {
			instance = new PropertyService();
		}
	}

	/**
	 * init.
	 * @param fileName fileName
	 * @throws IOException if an error occurs
	 */
	public static void init(final String fileName) throws IOException {
		if (instance == null) {
			propertiesFileName = fileName;
			instance = new PropertyService();
		}
	}

	/**
	 * isDebug.
	 * @return isDebug
	 */
	public static boolean isProduction() {
		Assert.assertNotNull("First init", instance);
		return properties.getProperty(PRODUCTION_PREFIX_KEY, DEFAULT_PRODUCTION).equals("true");
	}

	/**
	 * getAppName.
	 * @return AppName
	 */
	public static String getAppName() {
		Assert.assertNotNull("First init", instance);
		return properties.getProperty(NAME_PREFIX_KEY, DEFAULT_NAME);
	}

	/**
	 * getLabelsFolder.
	 * @return LabelsFolder
	 */
	public static String getLabelsFolder() {
		Assert.assertNotNull("First init", instance);
		return properties.getProperty(LABELS_FOLDER_PREFIX_KEY, DEFAULT_LABELS_FOLDER);
	}

}
