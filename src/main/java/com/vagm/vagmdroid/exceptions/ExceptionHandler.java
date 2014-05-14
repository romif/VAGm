package com.vagm.vagmdroid.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.Activity;

import com.vagm.vagmdroid.activities.CustomAbstractActivity;

/**
 * The Class ExceptionHandler.
 * @author Roman_Konovalov
 */
public class ExceptionHandler implements Thread.UncaughtExceptionHandler {

	/**
	 * LOG.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(ExceptionHandler.class);

	/**
	 * activity.
	 */
	private final Activity activity;

	/**
	 * constructor.
	 * @param activity activity
	 */
	public ExceptionHandler(final Activity activity) {
		this.activity = activity;
	}

	@Override
	public void uncaughtException(final Thread thread, final Throwable ex) {
		LOG.error("Uncaught Exception", ex);
		((CustomAbstractActivity) activity).getDefaultUEH().uncaughtException(thread, ex);
	}

}
