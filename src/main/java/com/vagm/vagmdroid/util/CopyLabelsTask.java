package com.vagm.vagmdroid.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.vagm.vagmdroid.R;
import com.vagm.vagmdroid.service.PropertyService;

/**
 * The Class LabelCopyTask.
 * @author Roman_Konovalov
 */
public class CopyLabelsTask extends AsyncTask<Void, Integer, Boolean> {

	/**
	 * TAG constant.
	 */
	private static final String TAG = "VAGm_LabelUtil";

	/**
	 * D.
	 */
	private static final boolean D = PropertyService.isDebug();

	/**
	 * context.
	 */
	private final Context context;

	/**
	 * progressDialog.
	 */
	private ProgressDialog progressBar;

	/**
	 * constructor.
	 * @param context
	 *            context
	 */
	public CopyLabelsTask(final Context context) {
		this.context = context;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		progressBar = new ProgressDialog(context);
		progressBar.setCancelable(true);
		progressBar.setMessage(context.getString(R.string.copying_labels));
		progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		progressBar.setProgress(0);
		progressBar.setCancelable(false);
		progressBar.show();
	}

	@Override
	protected Boolean doInBackground(final Void... unused) {
		if (D) {
			Log.d(TAG, "Begin copying label files");
		}
		InputStream in = null;
		OutputStream out = null;
		try {
			File labelDir = new File(Environment.getExternalStorageDirectory(), PropertyService.getAppName() + "/labels");
			if (!labelDir.exists()) {
				labelDir.mkdirs();
				String[] labelFiles = context.getAssets().list("labels");
				progressBar.setMax(labelFiles.length);
				int filesCopied = 0;
				for (String file : labelFiles) {
					File dst = new File(Environment.getExternalStorageDirectory(), PropertyService.getAppName() + "/labels/" + file);
					in = context.getAssets().open("labels/" + file);
					out = new FileOutputStream(dst);

					// Transfer bytes from in to out
					byte[] buf = new byte[1024];
					int len;
					while ((len = in.read(buf)) > 0) {
						out.write(buf, 0, len);
					}
					filesCopied++;
					if (filesCopied % 10 == 0) {
						progressBar.setProgress(filesCopied);
					}
					in.close();
					out.close();
				}
				if (D) {
					Log.d(TAG, filesCopied + " files copied");
				}
			}
		} catch (IOException e) {
			Log.e(TAG, "Error copying label files", e);
			return false;
		} finally {
			try {
				if ((in != null) && (out != null)) {
					in.close();
					out.close();
				}
			} catch (IOException e) {
				Log.e(TAG, "Cannot close InputStream / OutputStream", e);
				return false;
			}
		}
		return true;
	}

	@Override
	protected void onPostExecute(final Boolean unused) {
		progressBar.dismiss();
	}

}
