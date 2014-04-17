package com.vagm.vagmdroid.activities;

import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.vagm.vagmdroid.R;
import com.vagm.vagmdroid.enums.FunctionCode;
import com.vagm.vagmdroid.enums.VAGmConstans;
import com.vagm.vagmdroid.exceptions.ControllerCommunicationException;
import com.vagm.vagmdroid.service.BluetoothService;
import com.vagm.vagmdroid.service.BluetoothService.ServiceCommand;
import com.vagm.vagmdroid.service.BufferService;

/**
 * The Class ControllerActivity.
 * @author Roman_Konovalov
 */
public class ControllerActivity extends CustomAbstractActivity implements OnClickListener {

	/**
	 * TAG constant.
	 */
	private static final String TAG = "VAGm_ControllerActivity";

	/**
	 * D.
	 */
	private static final boolean D = true;

	/**
	 * buffer.
	 */
	private byte[] buffer = new byte[0];

	// Layout view
	/**
	 * VAGnumberText.
	 */
	private TextView VAGnumberText;

	/**
	 * componentText.
	 */
	private TextView componentText;

	/**
	 * boudRateText.
	 */
	private TextView boudRateText;

	/**
	 * progressBar.
	 */
	private ProgressBar progressBar;

	/**
	 * longTimer.
	 */
	private Timer longTimer;

	/**
	 * h.
	 */
	private Handler h;

	/**
	 * The Handler that gets information back from the BluetoothService.
	 */
	@SuppressLint("HandlerLeak")
	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(final Message msg) {
			final ServiceCommand serviceCommand = ServiceCommand.values()[msg.what];
			if (serviceCommand == ServiceCommand.MESSAGE_READ) {
				byte[] tempArray = Arrays.copyOf(buffer, buffer.length + msg.arg1);
				System.arraycopy((byte[]) msg.obj, 0, tempArray, buffer.length, msg.arg1);
				buffer = tempArray;
				if (D) {
					Log.d(TAG, "Recieved message from conroller: " + BufferService.bytesToHex((byte[]) msg.obj));
				}
				try {
					proceedMessage(buffer);
				} catch (ControllerCommunicationException e) {
					getAlert().show();
				}
			}
		}
	};

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_controller);
		Bundle extras = getIntent().getExtras();
		int controllerCode = 0;
		if (extras != null) {
			controllerCode = extras.getInt(MainActivity.CONTROLLER_CODE);
		}
		if (D) {
			Log.d(TAG, "Sending controller code: " + controllerCode);
		}
		BluetoothService.getInstance().write(controllerCode);
		disableEnableControls(false, (ViewGroup) findViewById(R.id.controllerLayout));
		setButtonOnClickListner((ViewGroup) findViewById(R.id.controllerLayout), this);
		VAGnumberText = (TextView) findViewById(R.id.VAGnumber);
		componentText = (TextView) findViewById(R.id.component);
		boudRateText = (TextView) findViewById(R.id.boudRate);
		progressBar = (ProgressBar) findViewById(R.id.progressBar1);
		progressBar.setVisibility(View.VISIBLE);
		h = new Handler();
		longTimer = new Timer();
		longTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				h.post(new Runnable() {

					public void run() {
						Log.w(TAG, "No answer from controller");
						longTimer.cancel();
						longTimer = null;
						getAlert().show();
					}
				});
			}
		}, 15 * 1000);

	}

	/**
	 * proceedMessage.
	 * @param array array
	 * @throws ControllerCommunicationException if some communication error occurs
	 */
	private void proceedMessage(final byte[] array) throws ControllerCommunicationException {
		List<String> controllerInfo = BufferService.getControllerInfo(array);
		if (controllerInfo.size() == 1) {
			boudRateText.setText(controllerInfo.get(0));
		}
		if (controllerInfo.size() == 3) {
			boudRateText.setText(controllerInfo.get(0));
			VAGnumberText.setText(controllerInfo.get(1));
			componentText.setText(controllerInfo.get(2));
			progressBar.setVisibility(View.GONE);
			disableEnableControls(true, (ViewGroup) findViewById(R.id.controllerLayout));
			if (longTimer != null) {
				longTimer.cancel();
				longTimer = null;
			}
		}
	}

	/**
	 * getAlert.
	 * @return AlertDialog
	 */
	private AlertDialog getAlert() {
		final AlertDialog.Builder builder = new AlertDialog.Builder(ControllerActivity.this);
		builder.setMessage(getString(R.string.controller_not_answer)).setTitle(getString(R.string.error)).setCancelable(false)
				.setNeutralButton(getString(R.string.back), new DialogInterface.OnClickListener() {
					public void onClick(final DialogInterface dialog, final int id) {
						finish();
					}
				});
		return builder.create();
	}



	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Handler getHandler() {
		return mHandler;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onClick(final View v) {
		switch (v.getId()) {
		case R.id.bCloseController:
			getmCommandService().write(VAGmConstans.STOP_CONTROLLER_COMMUNICATION);
			if (longTimer != null) {
				longTimer.cancel();
				longTimer = null;
			}
			finish();
			break;

		case R.id.bFaultCodes:
			//getmCommandService().write(FunctionCode.FAULT_CODES.getCode());
			// final Intent faultCodeIntent = new Intent(this,
			// FaultCodeActivity.class);
			// startActivityForResult(faultCodeIntent, -1);
			break;

		case R.id.bMeasBlocks:
			getmCommandService().write(FunctionCode.MEAS_BLOCKS.getCode());
			final Intent measBlocksIntent = new Intent(this, MeasBlocksActivity.class);
			startActivityForResult(measBlocksIntent, -1);
			break;

		case R.id.bOuputTests:
			//getmCommandService().write(FunctionCode.OUTPUT_TESTS.getCode());
			// final Intent outputTestsIntent = new Intent(this,
			// OutputTestsActivity.class);
			// startActivityForResult(outputTestsIntent, -1);
			break;

		default:
			break;
		}
	}

}
