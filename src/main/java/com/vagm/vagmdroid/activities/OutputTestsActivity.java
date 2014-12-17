package com.vagm.vagmdroid.activities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import roboguice.inject.InjectView;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.inject.Inject;
import com.vagm.vagmdroid.R;
import com.vagm.vagmdroid.enums.FunctionCode;
import com.vagm.vagmdroid.exceptions.ControllerCommunicationException;
import com.vagm.vagmdroid.exceptions.ControllerNotFoundException;
import com.vagm.vagmdroid.exceptions.ControllerWrongResponseException;
import com.vagm.vagmdroid.service.BluetoothService;
import com.vagm.vagmdroid.service.BufferService;

/**
 * The Class OutputTestsActivity.
 * @author Roman_Konovalov
 */
public class OutputTestsActivity extends CustomAbstractActivity implements OnClickListener {

	/**
	 * LOG.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(OutputTestsActivity.class);

	/**
	 * bufferService.
	 */
	@Inject
	private BufferService bufferService;

	/**
	 * activatedOutput.
	 */
	@InjectView(R.id.activatedOutput)
	private TextView activatedOutput;

	/**
	 * bStart.
	 */
	@InjectView(R.id.bStart)
	private Button bStart;
	
	/**
	 * bluetoothService.
	 */
	@Inject
	private BluetoothService bluetoothService;

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LOG.debug("onCreate");
		setContentView(R.layout.activity_output_tests);
		setButtonOnClickListner((ViewGroup) findViewById(R.id.outputTestsLayout), this);

	}

	/**
	 * proceedMessage.
	 * @param message buffer
	 * @throws ControllerCommunicationException if some communication error occurs
	 * @throws ControllerWrongResponseException if wrong response from controller occurs
	 * @throws ControllerNotFoundException 
	 */
	protected void proceedMessage(final byte[] message) throws ControllerCommunicationException, ControllerWrongResponseException, ControllerNotFoundException {
		String activatedOutputText = bufferService.getOutputTestsInfo(message);
		if (activatedOutputText == getString(R.string.test_ended)) {
			bStart.setText(getString(R.string.bStart));
		} else {
			bStart.setText(getString(R.string.bNext));
		}
		activatedOutput.setText(activatedOutputText);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onClick(final View v) {
		switch (v.getId()) {
		case R.id.bStart:
			bluetoothService.write(FunctionCode.OUTPUT_TESTS.getCode());
			break;

		case R.id.bOutputTestsBack:
			LOG.debug("Exiting OutputTests Activity");
			finish();
			break;

		default:
			break;
		}

	}

}
