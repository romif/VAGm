package com.vagm.vagmdroid.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Singleton;

/**
 * The Class PropertyService.
 * @author Roman_Konovalov
 */
@Singleton
public class PropertyService {

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
	private static final String LABELS_FOLDER_PREFIX_KEY = "vagm.folder.labels";

	/**
	 * SAVED_CHARTS_FOLDER_PREFIX_KEY.
	 */
	private static final String SAVED_CHARTS_FOLDER_PREFIX_KEY = "vagm.folder.savedcharts";
	
	/**
	 * LOG_FOLDER_PREFIX_KEY.
	 */
	private static final String LOG_FILE_NAME = "vagm.folder.logfilename";

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
	 * DEFAULT_SAVED_CHARTS_FOLDER.
	 */
	private static final String DEFAULT_SAVED_CHARTS_FOLDER = "charts";
	
	/**
	 * DEFAULT_LOG_CHARTS_FOLDER.
	 */
	private static final String DEFAULT_LOG_FILE_NAME = "vagm.log";

	/**
	 * propertiesFileName.
	 */
	private static String propertiesFileName = null;


	/**
	 * properties.
	 */
	private static Properties properties;
	
	private boolean connectedToAdapter = false;
	
	private boolean connectedToController = false;	

	/**
	 * constructor.
	 * @throws IOException if an error occurs
	 */
	public PropertyService() throws IOException {
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
	 * isDebug.
	 * @return isDebug
	 */
	public boolean isProduction() {
		return properties.getProperty(PRODUCTION_PREFIX_KEY, DEFAULT_PRODUCTION).equals("true");
	}

	/**
	 * getAppName.
	 * @return AppName
	 */
	public String getAppName() {
		return properties.getProperty(NAME_PREFIX_KEY, DEFAULT_NAME);
	}

	/**
	 * getLabelsFolder.
	 * @return LabelsFolder
	 */
	public String getLabelsFolder() {
		return properties.getProperty(LABELS_FOLDER_PREFIX_KEY, DEFAULT_LABELS_FOLDER);
	}

	/**
	 * getSavedChartsFolder.
	 * @return SavedChartsFolder
	 */
	public String getSavedChartsFolder() {
		return properties.getProperty(SAVED_CHARTS_FOLDER_PREFIX_KEY, DEFAULT_SAVED_CHARTS_FOLDER);
	}
	
	public String getLogFileName() {
		return properties.getProperty(LOG_FILE_NAME, DEFAULT_LOG_FILE_NAME);
	}

	/**
	 * @return the connectedToAdapter
	 */
	public boolean isConnectedToAdapter() {
		return connectedToAdapter;
	}

	/**
	 * @param connectedToAdapter the connectedToAdapter to set
	 */
	public void setConnectedToAdapter(boolean connectedToAdapter) {
		this.connectedToAdapter = connectedToAdapter;
	}

	/**
	 * @return the connectedToController
	 */
	public boolean isConnectedToController() {
		return connectedToController;
	}

	/**
	 * @param connectedToController the connectedToController to set
	 */
	public void setConnectedToController(boolean connectedToController) {
		this.connectedToController = connectedToController;
	}

}
