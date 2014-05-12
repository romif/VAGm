package com.vagm.vagmdroid.activities;

import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
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
import android.widget.Toast;

import com.vagm.vagmdroid.R;
import com.vagm.vagmdroid.enums.FunctionCode;
import com.vagm.vagmdroid.enums.VAGmConstans;
import com.vagm.vagmdroid.exceptions.ControllerCommunicationException;
import com.vagm.vagmdroid.exceptions.ControllerWrongResponseException;
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
	 * boudRate.
	 */
	private static final String BOUD_RATE = "boudRate";

	/**
	 * VAGnumber.
	 */
	private static final String VAG_NUMBER = "VAGnumber";

	/**
	 * component.
	 */
	private static final String COMPONENT = "component";

	/**
	 * buffer.
	 */
	private byte[] message = new byte[0];

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
	 * bluetoothService.
	 */
	private BluetoothService bluetoothService;

	/**
	 * ecu.
	 */
	private String ecu;

	/**
	 * ECU.
	 */
	public static final String ECU = "ecu";

	/**
	 * controllerInfo.
	 */
	private String[] controllerInfo = {"", "", "" };

	/**
	 * The Handler that gets information back from the BluetoothService.
	 */
	@SuppressLint("HandlerLeak")
	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(final Message msg)  {
			super.handleMessage(msg);
			final ServiceCommand serviceCommand = ServiceCommand.values()[msg.what];
			if (serviceCommand == ServiceCommand.MESSAGE_READ) {
				message = (byte[]) msg.obj;
				if (D) {
					Log.d(TAG, "Recieved message from conroller: " + BufferService.bytesToHex(message));
				}
				try {
					proceedMessage(message);
				} catch (ControllerCommunicationException e) {
					getControllerNotAnswerAlert().show();
				} catch (ControllerWrongResponseException e) {
					Log.w(TAG, e.getMessage());
				}
			} else if (serviceCommand == ServiceCommand.CONNECTION_LOST) {
				Toast.makeText(getApplicationContext(), getText(R.string.connection_lost), Toast.LENGTH_SHORT).show();
				stopTimer();
				finish();
			}
		}
	};

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onClick(final View v) {
		switch (v.getId()) {
		case R.id.bCloseController:
			getBluetoothService().write(VAGmConstans.STOP_CONTROLLER_COMMUNICATION);
			stopTimer();
			finish();
			break;

		case R.id.bFaultCodes:
			//getmCommandService().write(FunctionCode.FAULT_CODES.getCode());
			// final Intent faultCodeIntent = new Intent(this,
			// FaultCodeActivity.class);
			// startActivityForResult(faultCodeIntent, -1);
			break;

		case R.id.bMeasBlocks:
			getBluetoothService().write(FunctionCode.MEAS_BLOCKS.getCode());
			final Intent measBlocksIntent = new Intent(this, MeasBlocksActivity.class);
			measBlocksIntent.putExtra(BluetoothService.BLUETOOTH_SERVICE_INSTANCE, getBluetoothService());
			measBlocksIntent.putExtra(ECU, ecu);
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_controller);
		/*if (BluetoothService.getInstance().getState() != ConnectionState.CONNECTED) {
			Toast.makeText(getApplicationContext(), getText(R.string.connection_lost), Toast.LENGTH_SHORT).show();
			stopTimer();
			finish();
		}*/
		if (savedInstanceState == null) {
			int controllerCode = getIntent().getExtras().getInt(MainActivity.CONTROLLER_CODE);
			bluetoothService = getIntent().getParcelableExtra(BluetoothService.BLUETOOTH_SERVICE_INSTANCE);
			if (D) {
				Log.d(TAG, "Sending controller code: " + controllerCode);
			}
			getBluetoothService().write(controllerCode);
			disableEnableControls(false, (ViewGroup) findViewById(R.id.controllerLayout));
			setButtonOnClickListner((ViewGroup) findViewById(R.id.controllerLayout), this);
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
							if (!ControllerActivity.this.isFinishing()) {
								getControllerNotAnswerAlert().show();
							}
						}
					});
				}
			}, 15 * 1000);
		} else {
			((TextView) findViewById(R.id.boudRate)).setText(savedInstanceState.getString(BOUD_RATE));
			((TextView) findViewById(R.id.VAGnumber)).setText(savedInstanceState.getString(VAG_NUMBER));
			((TextView) findViewById(R.id.component)).setText(savedInstanceState.getString(COMPONENT));
		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onSaveInstanceState(final Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString(BOUD_RATE, ((TextView) findViewById(R.id.boudRate)).getText().toString());
		outState.putString(VAG_NUMBER, ((TextView) findViewById(R.id.VAGnumber)).getText().toString());
		outState.putString(COMPONENT, ((TextView) findViewById(R.id.component)).getText().toString());
	}

	@Override
	protected BluetoothService getBluetoothService() {
		return bluetoothService;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Handler getHandler() {
		return mHandler;
	}

	/**
	 * proceedMessage.
	 * @param array
	 *            array
	 * @throws ControllerCommunicationException
	 *             if some communication error occurs
	 * @throws ControllerWrongResponseException if wrong response from controller occurs
	 */
	private void proceedMessage(final byte[] array) throws ControllerCommunicationException, ControllerWrongResponseException {
		controllerInfo = BufferService.getControllerInfo(array, controllerInfo);
		((TextView) findViewById(R.id.boudRate)).setText(controllerInfo[0]);
		((TextView) findViewById(R.id.VAGnumber)).setText(controllerInfo[1]);
		((TextView) findViewById(R.id.component)).setText(controllerInfo[2]);

		if (controllerInfo[1].length() == VAGmConstans.ECU_LENGTH) {
			ecu = controllerInfo[1];
			disableEnableControls(true, (ViewGroup) findViewById(R.id.controllerLayout));
			stopTimer();
		}
	}

	/**
	 * stopTimer.
	 */
	private void stopTimer() {
		if (longTimer != null) {
			longTimer.cancel();
			longTimer = null;
		}
	}

}
