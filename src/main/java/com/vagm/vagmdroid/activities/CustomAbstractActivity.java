package com.vagm.vagmdroid.activities;

import java.lang.Thread.UncaughtExceptionHandler;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.inject.Inject;
import com.vagm.vagmdroid.exceptions.ExceptionHandler;
import com.vagm.vagmdroid.service.BluetoothService;

/**
 * The Class CustomAbstractActivity.
 * @author Roman_Konovalov
 */
public abstract class CustomAbstractActivity extends DispatcherHandler {

	/**
	 * defaultUEH.
	 */
	private UncaughtExceptionHandler defaultUEH;

	/**
	 * alertDialog.
	 */
	private AlertDialog alertDialog;

	/**
	 * bluetoothService.
	 */
	@Inject
	private BluetoothService bluetoothService;

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
		bluetoothService.setmHandler(getHandler());
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
