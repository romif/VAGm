package com.vagm.vagmdroid.activities;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.concurrent.CountDownLatch;

import org.apache.commons.io.FileUtils;
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
import com.vagm.vagmdroid.constants.VAGmConstans;
import com.vagm.vagmdroid.service.BluetoothService;
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
    public static final String LOG_TEXT = "logTest";
    
    public static final String EXTRA_CRASHED_FLAG = "crashed";
    
    private static final CountDownLatch LATCH = new CountDownLatch(1);

    /**
     * LOG.
     */
    private static final Logger LOG = LoggerFactory.getLogger(SendLogActivity.class);

    /**
     * SERVER_URL.
     */
    private static final String SERVER_URL = "http://vagmlog-romif.rhcloud.com/rest/log/001204017530";

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
    
    @Inject
    private BluetoothService bluetoothService;

    /**
     * file.
     */
    private File file = null;

    /**
     * zipFile.
     */
    private File zipFile = null;

    /**
     * bSendLog.
     */
    @InjectView(R.id.bSendLog)
    private Button bSendLog;

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.bViewLog:
                LOG.debug("Request for log");
                showLog();
                break;
    
            case R.id.bSendLog:
                LOG.debug("Request for sending log");
                if (zipFile != null) {
                    sendLogFile(zipFile);
                }
                break;
    
            case R.id.bExit:
                finish();
                break;
    
            default:
                break;
        }

    }

    private void sendLogFile(final File zipFile) {
        new CustomBackgroundTask<Void, Void>(this, getString(R.string.sendingLog)) {

            @Override
            protected Void doBackgroundJob() {
                httpPostService.doMultipartRequest(SERVER_URL, Collections.<String, String> emptyMap(), zipFile, MediaType.MULTIPART_FORM_DATA);
                
                return null;
            }

        }.execute();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        boolean isApplicationCrashed = getIntent().getExtras() != null && getIntent().getExtras().get(SendLogActivity.EXTRA_CRASHED_FLAG) != null;
        
        if (isApplicationCrashed) {
            this.setTitle(getString(R.string.apllicationCrashed));
        }

        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.send_log);

        setResult(Activity.RESULT_CANCELED);

        setButtonOnClickListner((ViewGroup) findViewById(R.id.send_log), this);

        file = logService.getLogFile();
        try {
            zipFile = fileService.zip(file);
            bSendLog.setText(bSendLog.getText() + " " + FileUtils.byteCountToDisplaySize(zipFile.length()));
        } catch (final IOException e) {
            LOG.error(e.getMessage(), e);
            bSendLog.setVisibility(View.GONE);
        }
    }
    
    private void showLog() {
        new CustomBackgroundTask<Void, Void>(this, getString(R.string.readingLogFile), 8000) {

            @Override
            protected Void doBackgroundJob() {
                if (propertyService.isConnectedToAdapter()) {
                    bluetoothService.write(VAGmConstans.ADAPTER_LOG_REQ);
                    try {
                        LATCH.await();
                    } catch (InterruptedException e) {
                        LOG.error(e.getMessage());
                    }
                }
                String logString = logService.getTodayLog(file);
                
                Intent showLogIntent = new Intent(SendLogActivity.this, ShowLogActivity.class);
                showLogIntent.putExtra(LOG_TEXT, logString);
    
                startActivity(showLogIntent);

                return null;
            }

        }.execute();
    }
    
    @Override
    protected void proceedMessage(byte[] message) {
        LATCH.countDown();
    }

}
