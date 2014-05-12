package com.vagm.vagmdroid.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.util.SparseArray;

import com.vagm.vagmdroid.R;
import com.vagm.vagmdroid.dto.LabelDTO;
import com.vagm.vagmdroid.service.LabelService;
import com.vagm.vagmdroid.service.PropertyService;

/**
 * The Class GetLabelsTask.
 * @author Roman_Konovalov
 */
public class GetLabelsTask extends AsyncTask<String, Integer, SparseArray<LabelDTO>> {

	/**
	 * TAG constant.
	 */
	private static final String TAG = "VAGm_GetLabelsTask";

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
	public GetLabelsTask(final Context context) {
		this.context = context;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		progressBar = new ProgressDialog(context);
		progressBar.setMessage(context.getString(R.string.copying_labels));
		progressBar.setCancelable(false);
		progressBar.show();
	}

	@Override
	protected SparseArray<LabelDTO> doInBackground(final String... ecu) {
		if (D) {
			Log.d(TAG, "Begin getting labels");
		}
		return LabelService.getLabels(ecu[0]);
	}

	@Override
	protected void onPostExecute(final SparseArray<LabelDTO> unused) {
		progressBar.dismiss();
	}

}
