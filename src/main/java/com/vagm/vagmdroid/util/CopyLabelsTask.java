package com.vagm.vagmdroid.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;

import com.google.inject.Inject;
import com.vagm.vagmdroid.R;
import com.vagm.vagmdroid.service.PropertyService;

/**
 * The Class LabelCopyTask.
 * 
 * @author Roman_Konovalov
 */
public class CopyLabelsTask extends AsyncTask<Void, Integer, Boolean> {

    /**
     * LOG.
     */
    private static final Logger LOG = LoggerFactory.getLogger(CopyLabelsTask.class);

    /**
     * context.
     */
    private final Context context;

    /**
     * progressDialog.
     */
    private ProgressDialog progressBar;

    /**
     * propertyService.
     */
    @Inject
    private PropertyService propertyService;

    /**
     * constructor.
     * 
     * @param context
     *            context
     */
    public CopyLabelsTask(final Context context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        LOG.debug("Begin CopyLabelsTask");
        progressBar = new ProgressDialog(context);
        progressBar.setMessage(context.getString(R.string.copying_labels));
        progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressBar.setProgress(0);
        progressBar.setCancelable(false);
        progressBar.show();
    }

    @Override
    protected Boolean doInBackground(final Void... unused) {
        InputStream in = null;
        OutputStream out = null;
        try {
            File labelDir = new File(Environment.getExternalStorageDirectory(), propertyService.getAppName() + "/labels");
            if (!labelDir.exists()) {
                labelDir.mkdirs();
                String[] labelFiles = context.getAssets().list("labels");
                progressBar.setMax(labelFiles.length);
                int filesCopied = 0;
                for (String file : labelFiles) {
                    File dst = new File(Environment.getExternalStorageDirectory(), propertyService.getAppName() + "/labels/" + file);
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
                LOG.debug("{} files copied", filesCopied);
            }
        } catch (IOException e) {
            LOG.error("Error copying label files", e);
            return false;
        } finally {
            try {
                if ((in != null) && (out != null)) {
                    in.close();
                    out.close();
                }
            } catch (IOException e) {
                LOG.error("Cannot close InputStream / OutputStream", e);
            }
        }
        return true;
    }

    @Override
    protected void onPostExecute(final Boolean unused) {
        progressBar.dismiss();
        LOG.debug("End CopyLabelsTask");
    }

}
