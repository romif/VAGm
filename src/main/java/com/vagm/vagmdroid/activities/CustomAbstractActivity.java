package com.vagm.vagmdroid.activities;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.vagm.vagmdroid.service.BluetoothService;

/**
 * The Class CustomAbstractActivity.
 * @author Roman_Konovalov
 */
public abstract class CustomAbstractActivity extends Activity {

	/**
	 * mCommandService.
	 */
	private BluetoothService mCommandService = (BluetoothService) BluetoothService.getInstance();

	/**
	 * @return the mCommandService
	 */
	protected BluetoothService getmCommandService() {
		return mCommandService;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mCommandService.setmHandler(getHandler());
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
	 * Gets Handler.
	 * @return Handler
	 */
	protected abstract Handler getHandler();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onBackPressed() {
	}

}
