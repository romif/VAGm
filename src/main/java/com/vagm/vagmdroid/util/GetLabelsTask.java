package com.vagm.vagmdroid.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import roboguice.util.RoboAsyncTask;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.SparseArray;

import com.google.inject.Inject;
import com.vagm.vagmdroid.R;
import com.vagm.vagmdroid.dto.LabelDTO;
import com.vagm.vagmdroid.service.LabelService;

/**
 * The Class GetLabelsTask.
 * 
 * @author Roman_Konovalov
 */
public class GetLabelsTask extends RoboAsyncTask<SparseArray<LabelDTO>> {

    /**
     * LOG.
     */
    private static final Logger LOG = LoggerFactory.getLogger(GetLabelsTask.class);

    /**
     * progressDialog.
     */
    private ProgressDialog progressBar;

    /**
     * ecu.
     */
    private String ecu;

    /**
     * labelService.
     */
    @Inject
    private LabelService labelService;

    /**
     * constructor.
     * 
     * @param context
     *            context
     * @param ecu
     *            ecu
     */
    public GetLabelsTask(final Context context, final String ecu) {
        super(context);
        this.ecu = ecu;
    }

    @Override
    protected void onPreExecute() {
        LOG.debug("Begin GetLabelsTask");
        progressBar = new ProgressDialog(context);
        progressBar.setMessage(context.getString(R.string.copying_labels));
        progressBar.setCancelable(false);
        progressBar.show();
    }

    @Override
    protected void onFinally() {
        progressBar.dismiss();
        LOG.debug("End GetLabelsTask");
    }

    @Override
    public SparseArray<LabelDTO> call() throws Exception {
        String fileName = labelService.getLabelFileName(ecu);
        return labelService.getLabels(fileName);
    }

}
