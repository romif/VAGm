package com.vagm.vagmdroid.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.content.Intent;

import com.vagm.vagmdroid.activities.CustomAbstractActivity;
import com.vagm.vagmdroid.activities.SendLogActivity;

/**
 * The Class ExceptionHandler.
 * 
 * @author Roman_Konovalov
 */
public class ExceptionHandler implements Thread.UncaughtExceptionHandler {

    /**
     * LOG.
     */
    private static final Logger LOG = LoggerFactory.getLogger(ExceptionHandler.class);

    /**
     * activity.
     */
    private final CustomAbstractActivity activity;

    /**
     * constructor.
     * 
     * @param activity
     *            activity
     */
    public ExceptionHandler(final CustomAbstractActivity activity) {
        this.activity = activity;
    }

    @Override
    public void uncaughtException(final Thread thread, final Throwable ex) {
        LOG.error("Uncaught Exception", ex);
        
        Intent crashedIntent = new Intent(activity, SendLogActivity.class);
        crashedIntent.putExtra(SendLogActivity.EXTRA_CRASHED_FLAG,  "Unexpected Error occurred.");
        crashedIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        crashedIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivity(crashedIntent);
        
        System.exit(0);
    }

}
