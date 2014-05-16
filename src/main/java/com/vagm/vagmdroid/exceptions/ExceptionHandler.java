package com.vagm.vagmdroid.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

import com.vagm.vagmdroid.R;
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
		/*final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setMessage(activity.getString(R.string.controller_not_answer)).setTitle(activity.getString(R.string.error)).setCancelable(false)
		.setNeutralButton(activity.getString(R.string.back), new DialogInterface.OnClickListener() {
			public void onClick(final DialogInterface dialog, final int id) {
				activity.finish();
			}
		}).create().show();*/
		((CustomAbstractActivity) activity).getDefaultUEH().uncaughtException(thread, ex);
	}

}
