package com.vagm.vagmdroid.activities;

import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import roboguice.inject.InjectView;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.inject.Inject;
import com.vagm.vagmdroid.R;
import com.vagm.vagmdroid.constants.VAGmConstans;
import com.vagm.vagmdroid.enums.FunctionCode;
import com.vagm.vagmdroid.exceptions.ControllerCommunicationException;
import com.vagm.vagmdroid.exceptions.ControllerNotFoundException;
import com.vagm.vagmdroid.exceptions.ControllerWrongResponseException;
import com.vagm.vagmdroid.service.BluetoothService;
import com.vagm.vagmdroid.service.BufferService;
import com.vagm.vagmdroid.service.ControllerInfoService;
import com.vagm.vagmdroid.tasks.CustomBackgroundTask;

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
	private TextView vagNumber;

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
	
	private static final CountDownLatch LATCH = new CountDownLatch(1);

	/**
	 * controllerInfo.
	 */
	private String[] controllerInfo = {"", "", "" };

	/**
	 * bufferService.
	 */
	@Inject
	private BufferService bufferService;

	/**
	 * controllerInfoService.
	 */
	@Inject
	private ControllerInfoService controllerInfoService;
	
	/**
	 * bluetoothService.
	 */
	@Inject
	private BluetoothService bluetoothService;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onClick(final View v) {
		switch (v.getId()) {
		case R.id.bCloseController:
			LOG.debug("Exiting Controller Activity, writing exit command: {}", VAGmConstans.EXIT_COMMAND);
			bluetoothService.write(0xAA);
			controllerInfoService.clear();
			finish();
			break;

		case R.id.bFaultCodes:
			bluetoothService.write(FunctionCode.FAULT_CODES.getCode());
			startActivityForResult(new Intent(this, FaultCodesActivity.class), -1);
			fillControllerInfo();
			break;

		case R.id.bMeasBlocks:
			bluetoothService.write(FunctionCode.MEAS_BLOCKS.getCode());
			startActivityForResult(new Intent(this, MeasBlocksActivity.class), -1);
			fillControllerInfo();
			break;

		case R.id.bOuputTests:
			startActivityForResult(new Intent(this, OutputTestsActivity.class), -1);
			fillControllerInfo();
			break;

		default:
			break;
		}
	}

	/**
	 * fillControllerInfo.
	 */
	private void fillControllerInfo() {
		controllerInfoService.setBoudRate(boudRate.getText().toString());
		controllerInfoService.setComponent(component.getText().toString());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LOG.debug("onCreate");
		setContentView(R.layout.activity_controller);
		/*if (BluetoothService.getInstance().getState() != ConnectionState.CONNECTED) {
			Toast.makeText(getApplicationContext(), getText(R.string.connection_lost), Toast.LENGTH_SHORT).show();
			stopTimer();
			finish();
		}*/
		if (savedInstanceState == null) {
			
			disableEnableControls(false, (ViewGroup) findViewById(R.id.controllerLayout));
			setButtonOnClickListner((ViewGroup) findViewById(R.id.controllerLayout), this);
			
			connect();
			
		} else {
			boudRate.setText(savedInstanceState.getString(BOUD_RATE));
			vagNumber.setText(savedInstanceState.getString(VAG_NUMBER));
			component.setText(savedInstanceState.getString(COMPONENT));
		}

	}

	private void connect() {
		new CustomBackgroundTask<Void, Void>(this,getString(R.string.connecting_to_controller), 15 * 1000) {

			@Override
			protected Void doBackgroundJob() {
				int controllerCode = getIntent().getExtras().getInt(MainActivity.CONTROLLER_CODE);
				LOG.debug("Sending controller code: {}", controllerCode);
				bluetoothService.write(VAGmConstans.CONNECT_ECU);
				bluetoothService.write(controllerCode);
				try {
					LATCH.await();
				} catch (InterruptedException e) {
					LOG.error(e.getMessage());
				}
				return null;
			}
			
			@Override
			public void onTimeout() {
				showtAlert(getString(R.string.adapter_not_answer));
			}
			
		}.execute();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onSaveInstanceState(final Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString(BOUD_RATE, boudRate.getText().toString());
		outState.putString(VAG_NUMBER, vagNumber.getText().toString());
		outState.putString(COMPONENT, component.getText().toString());
	}

	/**
	 * proceedMessage.
	 * @param array
	 *            array
	 * @throws ControllerCommunicationException
	 *             if some communication error occurs
	 * @throws ControllerWrongResponseException if wrong response from controller occurs
	 * @throws ControllerNotFoundException 
	 */
	protected void proceedMessage(final byte[] array) throws ControllerCommunicationException, ControllerWrongResponseException, ControllerNotFoundException {
		controllerInfo = bufferService.getControllerInfo(array, controllerInfo);
		boudRate.setText(controllerInfo[0]);
		vagNumber.setText(controllerInfo[1]);
		component.setText(controllerInfo[2]);

		if (controllerInfo[1].length() == VAGmConstans.ECU_LENGTH) {
			controllerInfoService.setVagNumber(controllerInfo[1]);
			LOG.debug("Found ecu: {}", controllerInfo[1]);
			disableEnableControls(true, (ViewGroup) findViewById(R.id.controllerLayout));
			LATCH.countDown();
		}
	}

}
