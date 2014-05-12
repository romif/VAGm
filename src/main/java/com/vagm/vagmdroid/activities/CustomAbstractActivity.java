package com.vagm.vagmdroid.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.vagm.vagmdroid.R;
import com.vagm.vagmdroid.service.BluetoothService;

/**
 * The Class CustomAbstractActivity.
 * @author Roman_Konovalov
 */
public abstract class CustomAbstractActivity extends Activity {

	/**
	 * @return the bluetoothService
	 */
	abstract BluetoothService getBluetoothService();

	/**
	 * getHandler.
	 * @return Handler
	 */
	protected abstract Handler getHandler();

	/**
	 * alertDialog.
	 */
	private AlertDialog alertDialog;

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onResume() {
		super.onResume();
		getBluetoothService().setmHandler(getHandler());
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onBackPressed() {
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

	@Override
	protected void onDestroy() {
		if (alertDialog != null) {
			alertDialog.dismiss();
		}
		super.onDestroy();
	}
}
