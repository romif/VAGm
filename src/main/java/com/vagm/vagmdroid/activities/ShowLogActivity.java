package com.vagm.vagmdroid.activities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import com.vagm.vagmdroid.enums.AdapterLogKey;
import com.vagm.vagmdroid.service.BufferService;
import com.vagm.vagmdroid.service.FileService;

/**
 * @author Roman_Konovalov
 */
public class ShowLogActivity extends CustomAbstractActivity implements OnClickListener {

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
	 * bufferService.
	 */
	@Inject
	private BufferService bufferService;

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
			try {
				new ReadFileTask().execute().get(10000, TimeUnit.MILLISECONDS);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (TimeoutException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (getIntent().getExtras().get(SendLogActivity.LOG_TEXT) instanceof String) {
			//logText.setText(encodeAdapterLog((String)getIntent().getExtras().get(SendLogActivity.LOG_TEXT)));
		}

	}

	private String encodeAdapterLog(byte[] adapterLog) {
		StringBuilder stringBuilder = new StringBuilder();
		
		for (int i = 0; i < adapterLog.length; i++) {
			byte st = adapterLog[i];
			AdapterLogKey logKey = AdapterLogKey.getAdapterLogKey(st);
			stringBuilder.append(logKey.getValue());
			stringBuilder.append(" - ");
			stringBuilder.append(adapterLog[i++]);
			if (logKey == AdapterLogKey.BAUDRATE_TICKS) {
				stringBuilder.append(", ");
				stringBuilder.append(adapterLog[i++]);
				stringBuilder.append(", ");
				stringBuilder.append(adapterLog[i++]);
				stringBuilder.append(", ");
				stringBuilder.append(adapterLog[i++]);
			} 
		}
		
		return stringBuilder.toString();
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
