package com.vagm.vagmdroid.service;

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
	 * PROPERTY_FILE_NAME.
	 */
	private static final String PROPERTY_FILE_NAME = "VAGm.properties";


	/**
	 * DEBUG.
	 */
	private static final String DEBUG = "debug";


	/**
	 * APP_NAME.
	 */
	private static final String APP_NAME = "appName";


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
		InputStream inputStream = PropertyService.class.getClassLoader().getResourceAsStream(PROPERTY_FILE_NAME);
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
	 * isDebug.
	 * @return isDebug
	 */
	public static boolean isDebug() {
		Assert.assertNotNull("First init", instance);
		return properties.getProperty(DEBUG).equals("true");
	}

	/**
	 * getAppName.
	 * @return AppName
	 */
	public static String getAppName() {
		Assert.assertNotNull("First init", instance);
		return properties.getProperty(APP_NAME);
	}

}
