package com.vagm.vagmdroid.activities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import roboguice.inject.InjectView;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.inject.Inject;
import com.vagm.vagmdroid.R;
import com.vagm.vagmdroid.enums.FunctionCode;
import com.vagm.vagmdroid.exceptions.ControllerWrongResponseException;
import com.vagm.vagmdroid.service.BluetoothService;
import com.vagm.vagmdroid.service.BufferService;

/**
 * The Class FaultCodesActivity.
 * @author roman_konovalov
 */
public class FaultCodesActivity extends CustomAbstractActivity implements OnClickListener {

	/**
	 * LOG.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(FaultCodesActivity.class);

	/**
	 * bufferService.
	 */
	@Inject
	private BufferService bufferService;
	
	/**
	 * bluetoothService.
	 */
	@Inject
	private BluetoothService bluetoothService;

	/**
	 * faultCodes.
	 */
	@InjectView(R.id.faultCodes)
	private TextView faultCodes;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onClick(final View v) {
		switch (v.getId()) {
		case R.id.bFaultCodesBack:
			LOG.debug("Exiting FaultCodes Activity");
			finish();
			break;

		case R.id.bClearCodes:
			faultCodes.setText("");
			bluetoothService.write(FunctionCode.CLEAR_CODES.getCode());
			break;

		default:
			break;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LOG.debug("onCreate");
		setContentView(R.layout.activity_fault_codes);
		setButtonOnClickListner((ViewGroup) findViewById(R.id.faultCodesLayout), this);

	}

	/**
	 * proceedMessage.
	 * @param message
	 *            message
	 * @throws ControllerWrongResponseException
	 *             if wrong response from controller occurs
	 */
	protected void proceedMessage(final byte[] message) throws ControllerWrongResponseException {
		String error = bufferService.getFaultCodesInfo(message);
		if (error.equals(getText(R.string.no_errors))) {
			faultCodes.setText(error);
			return;
		}
		if (faultCodes.getText().length() == 0) {
			faultCodes.append(error);
		} else {
			faultCodes.append("\r\n\r\n" + error);
		}
	}

}
