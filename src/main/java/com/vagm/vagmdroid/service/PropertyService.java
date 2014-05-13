package com.vagm.vagmdroid.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import junit.framework.Assert;

/**
 * The Class PropertyService.
 * @author Roman_Konovalov
 */
public final class PropertyService {

	/**
	 * DEFAULT_PROPERTIES_FILENAME.
	 */
	private static final String DEFAULT_PROPERTIES_FILENAME = "VAGm.properties";


	/**
	 * DEBUG.
	 */
	private static final String PRODUCTION_PREFIX_KEY = "vagm.production";


	/**
	 * APP_NAME.
	 */
	private static final String NAME_PREFIX_KEY = "vagm.name";

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
	 * @throws IOException
	 */
	private PropertyService() throws IOException {
		String fileName = propertiesFileName == null ? DEFAULT_PROPERTIES_FILENAME : propertiesFileName;
		InputStream inputStream = PropertyService.class.getClassLoader().getResourceAsStream("assets" + File.separator + fileName);
		properties = new Properties();
		properties.load(inputStream);
	}

	/**
	 * init.
	 * @throws IOException
	 */
	public static void init() throws IOException {
		if (instance == null) {
			instance = new PropertyService();
		}
	}

	/**
	 * init.
	 * @param fileName fileName
	 * @throws IOException
	 */
	public static void init(String fileName) throws IOException {
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
		return properties.getProperty(PRODUCTION_PREFIX_KEY, "dev").equals("prod");
	}

	/**
	 * getAppName.
	 * @return AppName
	 */
	public static String getAppName() {
		Assert.assertNotNull("First init", instance);
		return properties.getProperty(NAME_PREFIX_KEY);
	}

}
