package com.vagm.vagmdroid.activities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import roboguice.inject.InjectView;
import android.annotation.SuppressLint;
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
import com.vagm.vagmdroid.exceptions.ControllerCommunicationException;
import com.vagm.vagmdroid.exceptions.ControllerWrongResponseException;
import com.vagm.vagmdroid.service.BluetoothService.ServiceCommand;
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
	 * The Handler that gets information back from the BluetoothService.
	 */
	@SuppressLint("HandlerLeak")
	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(final Message msg) {
			super.handleMessage(msg);
			final ServiceCommand serviceCommand = ServiceCommand.values()[msg.what];
			if (serviceCommand == ServiceCommand.MESSAGE_READ) {
			byte[] message = (byte[]) msg.obj;
				LOG.trace("Recieved message from conroller: {}", bufferService.bytesToHex(message));
				try {
					proceedMessage(message);
				} catch (final ControllerCommunicationException e) {
					LOG.error("No answer from controller", e);
					getControllerNotAnswerAlert().show();
				} catch (ControllerWrongResponseException e) {
					LOG.info(e.getMessage(), e);
				}
			} else if (serviceCommand == ServiceCommand.CONNECTION_LOST) {
				Toast.makeText(getApplicationContext(), getText(R.string.connection_lost), Toast.LENGTH_SHORT).show();
				finish();
			}
		}
	};

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LOG.debug("onCreate");
		setContentView(R.layout.activity_output_tests);
		setButtonOnClickListner((ViewGroup) findViewById(R.id.measBlocksLayout), this);

	}

	/**
	 * proceedMessage.
	 * @param message buffer
	 * @throws ControllerCommunicationException if some communication error occurs
	 * @throws ControllerWrongResponseException if wrong response from controller occurs
	 */
	private void proceedMessage(final byte[] message) throws ControllerCommunicationException, ControllerWrongResponseException {
		activatedOutput.setText(bufferService.getOutputTestsInfo(message));
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Handler getHandler() {
		return mHandler;
	}

}
