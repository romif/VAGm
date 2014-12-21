package com.vagm.vagmdroid.activities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import roboguice.inject.InjectView;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;

import com.google.inject.Inject;
import com.vagm.vagmdroid.R;
import com.vagm.vagmdroid.constants.VAGmConstans;
import com.vagm.vagmdroid.exceptions.ControllerCommunicationException;
import com.vagm.vagmdroid.exceptions.ControllerNotFoundException;
import com.vagm.vagmdroid.exceptions.ControllerWrongResponseException;
import com.vagm.vagmdroid.service.BluetoothService;
import com.vagm.vagmdroid.service.BufferService;
import com.vagm.vagmdroid.service.FileService;
import com.vagm.vagmdroid.tasks.CustomBackgroundTask;

/**
 * @author Roman_Konovalov
 */
public class ShowLogActivity extends CustomAbstractActivity implements OnClickListener {

	/**
	 * LOG.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(ShowLogActivity.class);
	
	private static final CountDownLatch LATCH = new CountDownLatch(1);

	/**
	 * fileService.
	 */
	@Inject
	private FileService fileService;
	
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
	 * logText.
	 */
	@InjectView(R.id.log_text)
	private TextView logText;

	@Override
	public void onClick(final View v) {
		switch (v.getId()) {
		case R.id.bViewMobileLog:
			startActivityForResult(new Intent(this, FaultCodesActivity.class), -1);
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

		// Setup the window
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.show_log);

		// Set result CANCELED incase the user backs out
		setResult(Activity.RESULT_CANCELED);
		
		if (getIntent().getExtras().get(SendLogActivity.LOG_TEXT) instanceof File) {
			readPhoneLogFile();
		} else {
			getAdapterLog();
		}

	}

	private void getAdapterLog() {
		new CustomBackgroundTask<Void, Void>(this, getString(R.string.readingLogFile), 5000) {

			@Override
			protected Void doBackgroundJob() {
				LOG.debug("Request for adapter log");
				bluetoothService.write(VAGmConstans.ADAPTER_LOG_REQ);
				try {
					LATCH.await();
				} catch (InterruptedException e) {
					LOG.error(e.getMessage());
				}	
				
				return null;
			}

		}.execute();
		
	}

	private void readPhoneLogFile() {
		new CustomBackgroundTask<File, String>(this, getString(R.string.readingLogFile)) {
			@Override
			protected String doBackgroundJob() {
				try {
					return fileService.convertStreamToString(new FileInputStream((File) getIntent().getExtras().get(SendLogActivity.LOG_TEXT)));
				} catch (FileNotFoundException e) {
					LOG.error(e.getMessage());
					return "";
				}
			}
			
			@Override
			public void onJobDone(String result) {
				logText.setText(result);
			}
			
		}.execute();

	}
	
	@Override
	public void onBackPressed() {
		finish();
	}
	
	@Override
	protected void proceedMessage(byte[] message)
			throws ControllerCommunicationException,
			ControllerWrongResponseException, ControllerNotFoundException {
		super.proceedMessage(message);
		
		logText.setText(bufferService.encodeAdapterLog(message));
		
		LATCH.countDown();
	}

}
