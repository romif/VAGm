package com.vagm.vagmdroid.activities;

import java.lang.Thread.UncaughtExceptionHandler;

import junit.framework.Assert;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.vagm.vagmdroid.R;
import com.vagm.vagmdroid.exceptions.ExceptionHandler;
import com.vagm.vagmdroid.service.BluetoothService;

/**
 * The Class CustomAbstractActivity.
 * @author Roman_Konovalov
 */
public abstract class CustomAbstractActivity extends Activity {

	/**
	 * defaultUEH.
	 */
	private UncaughtExceptionHandler defaultUEH;

	/**
	 * alertDialog.
	 */
	private AlertDialog alertDialog;

	/**
	 * @return the defaultUEH
	 */
	public UncaughtExceptionHandler getDefaultUEH() {
		return defaultUEH;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onBackPressed() {
	}

	/**
	 * @param defaultUEH the defaultUEH to set
	 */
	public void setDefaultUEH(final UncaughtExceptionHandler defaultUEH) {
		this.defaultUEH = defaultUEH;
	}

	/**
	 * Disables/Enables Controls.
	 * @param enable enable/disable
	 * @param vg ViewGroup
	 */
	protected void disableEnableControls(final boolean enable, final ViewGroup vg) {
		for (int i = 0; i < vg.getChildCount(); i++) {
			final View child = vg.getChildAt(i);
			if (child instanceof ViewGroup) {
				disableEnableControls(enable, (ViewGroup) child);
			} else {
				child.setEnabled(enable);
			}
		}
	}

	/**
	 * @return the bluetoothService
	 */
	protected BluetoothService getBluetoothService() {
		BluetoothService bluetoothService = getIntent().getParcelableExtra(BluetoothService.BLUETOOTH_SERVICE_INSTANCE);
		Assert.assertTrue("bluetoothService must not be null, implement getBluetoothService()", bluetoothService != null);
		return bluetoothService;
	}

	/**
	 * getControllerNotAnswerAlert.
	 * @return AlertDialog
	 */
	protected AlertDialog getControllerNotAnswerAlert() {
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(getString(R.string.controller_not_answer)).setTitle(getString(R.string.error)).setCancelable(false)
				.setNeutralButton(getString(R.string.back), new DialogInterface.OnClickListener() {
					public void onClick(final DialogInterface dialog, final int id) {
						finish();
					}
				});
		alertDialog = builder.create();
		return alertDialog;
	}

	/**
	 * getHandler.
	 * @return Handler
	 */
	protected abstract Handler getHandler();

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setDefaultUEH(Thread.getDefaultUncaughtExceptionHandler());
		Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
	}

	@Override
	protected void onDestroy() {
		if (alertDialog != null) {
			alertDialog.dismiss();
		}
		super.onDestroy();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onResume() {
		super.onResume();
		getBluetoothService().setmHandler(getHandler());
	}

	/**
	 * setButtonOnClickListner.
	 * @param vg ViewGroup
	 * @param clickListener clickListener
	 */
	protected void setButtonOnClickListner(final ViewGroup vg, final OnClickListener clickListener) {
		for (int i = 0; i < vg.getChildCount(); i++) {
			final View child = vg.getChildAt(i);
			if (child instanceof ViewGroup) {
				setButtonOnClickListner((ViewGroup) child, clickListener);
			} else {
				if (child instanceof Button) {
					child.setOnClickListener(clickListener);
				}
			}

		}
	}


}
