package com.vagm.vagmdroid.activities;

import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import roboguice.inject.InjectView;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.inject.Inject;
import com.vagm.vagmdroid.R;
import com.vagm.vagmdroid.enums.FunctionCode;
import com.vagm.vagmdroid.enums.VAGmConstans;
import com.vagm.vagmdroid.exceptions.ControllerCommunicationException;
import com.vagm.vagmdroid.exceptions.ControllerWrongResponseException;
import com.vagm.vagmdroid.service.BluetoothService.ServiceCommand;
import com.vagm.vagmdroid.service.BufferService;
import com.vagm.vagmdroid.service.PropertyService;

/**
 * The Class ControllerActivity.
 * @author Roman_Konovalov
 */
public class ControllerActivity extends CustomAbstractActivity implements OnClickListener {

	/**
	 * LOG.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(ControllerActivity.class);

	/**
	 * boudRate.
	 */
	@InjectView(R.id.boudRate)
	private TextView boudRate;

	/**
	 * VAGnumber.
	 */
	@InjectView(R.id.VAGnumber)
	private TextView VAGnumber;

	/**
	 * component.
	 */
	@InjectView(R.id.component)
	private TextView component;

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
	private ProgressDialog progressBar;

	/**
	 * longTimer.
	 */
	private Timer longTimer;

	/**
	 * h.
	 */
	private Handler h;

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
	 * propertyService.
	 */
	@Inject
	private PropertyService propertyService;

	/**
	 * bufferService.
	 */
	@Inject
	private BufferService bufferService;

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
				LOG.trace("Recieved message from conroller: {}", bufferService.bytesToHex(message));
				try {
					proceedMessage(message);
				} catch (ControllerCommunicationException e) {
					getControllerNotAnswerAlert().show();
				} catch (ControllerWrongResponseException e) {
					LOG.warn("Wrong response", e);
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
			LOG.debug("Exiting Controller Activity, writing exit command: {}", VAGmConstans.EXIT_COMMAND);
			bluetoothService.write(VAGmConstans.EXIT_COMMAND);
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
			bluetoothService.write(FunctionCode.MEAS_BLOCKS.getCode());
			final Intent measBlocksIntent = new Intent(this, MeasBlocksActivity.class);
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
	protected Handler getHandler() {
		return mHandler;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_controller);
		/*if (BluetoothService.getInstance().getState() != ConnectionState.CONNECTED) {
			Toast.makeText(getApplicationContext(), getText(R.string.connection_lost), Toast.LENGTH_SHORT).show();
			stopTimer();
			finish();
		}*/
		if (savedInstanceState == null) {
			int controllerCode = getIntent().getExtras().getInt(MainActivity.CONTROLLER_CODE);
			LOG.debug("Sending controller code: {}", controllerCode);
			bluetoothService.write(controllerCode);
			disableEnableControls(false, (ViewGroup) findViewById(R.id.controllerLayout));
			setButtonOnClickListner((ViewGroup) findViewById(R.id.controllerLayout), this);
			progressBar = new ProgressDialog(this);
			progressBar.setMessage(getString(R.string.connecting_to_controller));
			progressBar.setCancelable(false);
			progressBar.show();
			h = new Handler();
			longTimer = new Timer();
			longTimer.schedule(new TimerTask() {
				@Override
				public void run() {
					h.post(new Runnable() {

						public void run() {
							LOG.warn("No answer from controller");
							longTimer.cancel();
							longTimer = null;
							if (!ControllerActivity.this.isFinishing()) {
								progressBar.dismiss();
								getControllerNotAnswerAlert().show();
							}
						}
					});
				}
			}, 15 * 1000);
		} else {
			boudRate.setText(savedInstanceState.getString(BOUD_RATE));
			VAGnumber.setText(savedInstanceState.getString(VAG_NUMBER));
			component.setText(savedInstanceState.getString(COMPONENT));
		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onSaveInstanceState(final Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString(BOUD_RATE, boudRate.getText().toString());
		outState.putString(VAG_NUMBER, VAGnumber.getText().toString());
		outState.putString(COMPONENT, component.getText().toString());
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
		controllerInfo = bufferService.getControllerInfo(array, controllerInfo);
		boudRate.setText(controllerInfo[0]);
		VAGnumber.setText(controllerInfo[1]);
		component.setText(controllerInfo[2]);

		if (controllerInfo[1].length() == VAGmConstans.ECU_LENGTH) {
			ecu = controllerInfo[1];
			progressBar.dismiss();
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
