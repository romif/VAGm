package com.vagm.vagmdroid.activities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore.Files;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.google.inject.Inject;
import com.vagm.vagmdroid.R;
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
		
		logText.append(readFileAsString(file));

		/*try {
		    HttpClient httpclient = new DefaultHttpClient();

		    HttpPost httppost = new HttpPost(SERVER_URL);

		    InputStreamEntity reqEntity = new InputStreamEntity(
		            new FileInputStream(file), -1);
		    reqEntity.setContentType("multipart/form-data");
		    //reqEntity.setChunked(true); // Send in multiple parts if needed
		    httppost.setEntity(reqEntity);
		    HttpResponse response = httpclient.execute(httppost);
		    LOG.debug(response.getStatusLine().getReasonPhrase());

		} catch (Exception e) {
		    LOG.error(e.getMessage());
		}*/

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

	public static String readFileAsString(File file) {
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        BufferedReader in = null;

        try {
            in = new BufferedReader(new FileReader(file));
            while ((line = in.readLine()) != null) stringBuilder.append(line);

        } catch (IOException e) {
        	LOG.error(e.getMessage());
        } 

        return stringBuilder.toString();
    }

}
