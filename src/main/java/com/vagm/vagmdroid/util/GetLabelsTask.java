package com.vagm.vagmdroid.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.SparseArray;

import com.vagm.vagmdroid.R;
import com.vagm.vagmdroid.dto.LabelDTO;
import com.vagm.vagmdroid.service.LabelService;

/**
 * The Class GetLabelsTask.
 * @author Roman_Konovalov
 */
public class GetLabelsTask extends AsyncTask<String, Integer, SparseArray<LabelDTO>> {

	/**
	 * LOG.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(GetLabelsTask.class);

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
		LOG.debug("Begin GetLabelsTask");
		progressBar = new ProgressDialog(context);
		progressBar.setMessage(context.getString(R.string.copying_labels));
		progressBar.setCancelable(false);
		progressBar.show();
	}

	@Override
	protected SparseArray<LabelDTO> doInBackground(final String... ecu) {
		String fileName = LabelService.getLabelFileName(ecu[0]);
		return LabelService.getLabels(fileName);
	}

	@Override
	protected void onPostExecute(final SparseArray<LabelDTO> unused) {
		progressBar.dismiss();
		LOG.debug("End GetLabelsTask");
	}

}
