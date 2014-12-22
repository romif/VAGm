package com.vagm.vagmdroid.activities;

import java.io.File;
import java.io.IOException;
import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import roboguice.inject.InjectView;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;

import com.google.inject.Inject;
import com.vagm.vagmdroid.R;
import com.vagm.vagmdroid.service.FileService;
import com.vagm.vagmdroid.service.HttpPostService;
import com.vagm.vagmdroid.service.HttpPostService.MediaType;
import com.vagm.vagmdroid.service.LogService;
import com.vagm.vagmdroid.service.PropertyService;
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
    private static final String SERVER_URL = "http://vagmlog-romif.rhcloud.com/rest/log/001204017530/vagmdroidlog";

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
     * propertyService.
     */
    @Inject
    private PropertyService propertyService;

    /**
     * file.
     */
    private File file = null;

    /**
     * zipFile.
     */
    private File zipFile = null;

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
    
            case R.id.bBack:
                finish();
                break;
    
            default:
                break;
        }

    }

    private void sendLogFile(final File zipFile) {
        new CustomBackgroundTask<Void, String>(this, getString(R.string.sendingLog)) {

            @Override
            protected String doBackgroundJob() {
                return httpPostService.doMultipartRequest(SERVER_URL, Collections.<String, String> emptyMap(), zipFile,
                        MediaType.MULTIPART_FORM_DATA);
            }

        }.execute();
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

        if (!propertyService.isConnectedToAdapter()) {
            bViewAdapterLog.setVisibility(View.GONE);
        }

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

}
