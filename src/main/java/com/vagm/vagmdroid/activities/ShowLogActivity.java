package com.vagm.vagmdroid.activities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;

import com.google.inject.Inject;
import com.vagm.vagmdroid.R;
import com.vagm.vagmdroid.service.FileService;

/**
 * @author Roman_Konovalov
 */
public class ShowLogActivity extends RoboActivity implements OnClickListener {

	/**
	 * LOG.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(ShowLogActivity.class);

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

		new ReadFileTask().execute();

	}

	/**
	 * The Class ReadFileTask.
	 * @author roman_konovalov
	 */
	private class ReadFileTask extends AsyncTask<Void, Void, Void> {

		/**
		 * progressBar.
		 */
		private final ProgressDialog progressBar = new ProgressDialog(ShowLogActivity.this);

		/**
		 * textLog.
		 */
		private String textLog = null;

		@Override
		protected void onPreExecute() {
			progressBar.setMessage(getString(R.string.readingLogFile));
			progressBar.setCancelable(false);
			progressBar.show();
		}

		@Override
		protected Void doInBackground(final Void... arg0) {
			File file = (File) getIntent().getExtras().get(SendLogActivity.LOG_TEXT);
			try {
				textLog = fileService.convertStreamToString(new FileInputStream(file));
			} catch (FileNotFoundException e) {
				LOG.error(e.getMessage());
			}
			return null;
		}

		@Override
		protected void onPostExecute(final Void unused) {
			logText.setText(textLog);
			if (this.progressBar.isShowing()) {
				this.progressBar.dismiss();

			}
		}
	}

}
