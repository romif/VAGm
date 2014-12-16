package com.vagm.vagmdroid.activities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.google.inject.Inject;
import com.vagm.vagmdroid.R;
import com.vagm.vagmdroid.service.FileService;
import com.vagm.vagmdroid.service.HttpPostService;
import com.vagm.vagmdroid.service.HttpPostService.MediaType;
import com.vagm.vagmdroid.service.LogService;

/**
 * @author Roman_Konovalov
 */
public class SendLogActivity extends RoboActivity {

	/**
	 * LOG.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(SendLogActivity.class);

	/**
	 * SERVER_URL.
	 */
	private static final String SERVER_URL = "http://vagmlog-romif.rhcloud.com/rest/log/11111111111/vagmdroidlog";

	/**
	 * propertyService.
	 */
	@Inject
	private LogService logService;

	/**
	 * httpPostService.
	 */
	@Inject
	private HttpPostService httpPostService;

	/**
	 * fileService.
	 */
	@Inject
	private FileService fileService;

	/**
	 * logText.
	 */
	@InjectView(R.id.log_text)
	private TextView logText;

	/**
	 * EXTRA_DEVICE_ADDRESS.
	 */
	public static final String EXTRA_DEVICE_ADDRESS = "device_address";

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Setup the window
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.send_log);

		// Set result CANCELED incase the user backs out
		setResult(Activity.RESULT_CANCELED);

		File file = logService.getLogFile();

		if (file != null) {
			try {
				//logText.append(fileService.convertStreamToString(new FileInputStream(file)));

				File zipFile = fileService.zip(file);

				httpPostService.doMultipartRequest(SERVER_URL, Collections.EMPTY_MAP, zipFile, MediaType.MULTIPART_FORM_DATA);
			} catch (IOException e) {
				LOG.error(e.getMessage());
			}

		}

		// Initialize the button to perform device discovery
		final Button scanButton = (Button) findViewById(R.id.button_scan);
		scanButton.setOnClickListener(new OnClickListener() {
			public void onClick(final View v) {
				v.setVisibility(View.GONE);
			}
		});

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();

	}

	/**
	 * The on-click listener for all devices in the ListViews.
	 */
	private final OnItemClickListener mDeviceClickListener = new OnItemClickListener() {
		public void onItemClick(final AdapterView<?> av, final View v, final int arg2, final long arg3) {

			// Get the device MAC address, which is the last 17 chars in the
			// View
			final String info = ((TextView) v).getText().toString();
			final String address = info.substring(info.length() - 17);

			// Create the result Intent and include the MAC address
			final Intent intent = new Intent();
			intent.putExtra(EXTRA_DEVICE_ADDRESS, address);

			// Set result and finish this Activity
			setResult(Activity.RESULT_OK, intent);
			finish();
		}
	};


}
