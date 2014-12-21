package com.vagm.vagmdroid.activities;

import java.io.File;
import java.io.IOException;
import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import roboguice.inject.InjectView;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;

import com.google.inject.Inject;
import com.vagm.vagmdroid.R;
import com.vagm.vagmdroid.dto.HttpPostDTO;
import com.vagm.vagmdroid.service.FileService;
import com.vagm.vagmdroid.service.HttpPostService;
import com.vagm.vagmdroid.service.HttpPostService.MediaType;
import com.vagm.vagmdroid.service.LogService;
import com.vagm.vagmdroid.tasks.CustomBackgroundTask;

/**
 * @author Roman_Konovalov
 */
public class SendLogActivity extends CustomAbstractActivity implements OnClickListener {
	
	/**
	 * LOG_TEXT.
	 */
	public static final String LOG_TEXT = "logText";

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
	 * file.
	 */
	private File file = null;

	/**
	 * zipFile.
	 */
	private File zipFile = null;

	/**
	 * bViewMobileLog.
	 */
	@InjectView(R.id.bViewMobileLog)
	private Button bViewMobileLog;

	/**
	 * bViewAdapterLog.
	 */
	@InjectView(R.id.bViewAdapterLog)
	private Button bViewAdapterLog;

	/**
	 * bSendLog.
	 */
	@InjectView(R.id.bSendLog)
	private Button bSendLog;

	@Override
	public void onClick(final View v) {
		Intent showLogIntent;
		switch (v.getId()) {
		case R.id.bViewMobileLog:
			showLogIntent = new Intent(this, ShowLogActivity.class);
			showLogIntent.putExtra(LOG_TEXT, file);
			
			startActivity(showLogIntent);
			break;

		case R.id.bViewAdapterLog:
			showLogIntent = new Intent(this, ShowLogActivity.class);
			showLogIntent.putExtra(LOG_TEXT, "");
			startActivity(showLogIntent);
			break;

		case R.id.bSendLog:
			if (zipFile != null) {
				sendLogFile(zipFile);
			}
			break;

		default:
			break;
		}

	}

	private void sendLogFile(File zipFile) {
		new CustomBackgroundTask<HttpPostDTO, String>(this, getString(R.string.sendingLog)) {

			@Override
			protected String doBackgroundJob(HttpPostDTO httpPostDTO) {
				return httpPostService.doMultipartRequest(httpPostDTO);
			}
			
		}.execute(new HttpPostDTO(SERVER_URL, Collections.<String, String> emptyMap(), zipFile, MediaType.MULTIPART_FORM_DATA));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.send_log);

		setResult(Activity.RESULT_CANCELED);

		setButtonOnClickListner((ViewGroup) findViewById(R.id.send_log), this);

		file = logService.getLogFile();
		if (file != null) {
			try {
				zipFile = fileService.zip(file);
				bSendLog.setText(bSendLog.getText() + " (" + String.valueOf(zipFile.length() / 1024) + " kB)");
			} catch (final IOException e) {
				LOG.error(e.getMessage());
				bSendLog.setVisibility(View.GONE);
			}
		} else {
			bSendLog.setVisibility(View.GONE);
		}

	}

	protected void setButtonOnClickListner(final ViewGroup vg, final OnClickListener clickListener) {
		for (int i = 0; i < vg.getChildCount(); i++) {
			final View child = vg.getChildAt(i);
			if (child instanceof ViewGroup) {
				setButtonOnClickListner((ViewGroup) child, clickListener);
			} else {
				if (child instanceof Button) {
					child.setOnClickListener(clickListener);
				}
			}

		}
	}
	
	@Override
	public void onBackPressed() {
		finish();
	}

}
