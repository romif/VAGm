package com.vagm.vagmdroid.service;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.os.Environment;

import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * The Class LogService.
 * @author roman_konovalov
 */
@Singleton
public class LogService {

	/**
	 * LOG.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(LogService.class);

	/**
	 * propertyService.
	 */
	@Inject
	private PropertyService propertyService;

	/**
	 * Default constructor.
	 */
	public LogService() {
	}

	/**
	 * getLogFile.
	 * @return LogFile
	 */
	public File getLogFile() {

		File file = new File(Environment.getExternalStorageDirectory() + File.separator + propertyService.getAppName() + File.separator
				+ propertyService.getLogFileName());

		if (!file.exists()) {
			LOG.error("File not found:" + Environment.getExternalStorageDirectory() + File.separator + propertyService.getAppName()
					+ File.separator + propertyService.getLogFileName());
		}
		return file;
	}

}
