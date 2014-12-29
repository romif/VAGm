package com.vagm.vagmdroid.tasks;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import roboguice.util.RoboAsyncTask;
import android.app.ProgressDialog;

import com.vagm.vagmdroid.activities.DispatcherHandlerActivity;
import com.vagm.vagmdroid.service.BluetoothService.ServiceCommand;
import com.vagm.vagmdroid.service.TimeOutJob;

/**
 * The Class SendLogTask.
 * 
 * @author roman_konovalov
 * @param <Param>
 * @param <Result>
 */
public abstract class CustomBackgroundTask<Param, Result> extends RoboAsyncTask<Result> implements TimeOutJob {
    
    private static final Logger LOG = LoggerFactory.getLogger(CustomBackgroundTask.class);

    /**
     * DEFAULT_TIMEOUT.
     */
    private static final int DEFAULT_TIMEOUT = 60000;

    /**
     * message
     */
    private final String message;

    /**
	 * 
	 */
    private final DispatcherHandlerActivity context;

    /**
     * timeToWait.
     */
    private final long timeToWait;

    /**
     * progressBar.
     */
    private final ProgressDialog progressBar;

    private long time;

    /**
     * constructor.
     * @param context
     * @param message
     */
    public CustomBackgroundTask(final DispatcherHandlerActivity context, final String message) {
        this(context, message, DEFAULT_TIMEOUT);
    }

    /**
     * constructor.
     * @param context
     * @param message
     * @param timeToWait
     */
    public CustomBackgroundTask(final DispatcherHandlerActivity context, final String message, long timeToWait) {
        super(context);
        this.message = message;
        this.context = context;
        this.timeToWait = timeToWait;
        progressBar = new ProgressDialog(context);
        LOG.debug("Start task: " + message);
        time = System.currentTimeMillis();
    }

    @Override
    protected void onPreExecute() {
        progressBar.setMessage(message);
        progressBar.setCancelable(false);
        progressBar.show();
    }

    @Override
    public Result call() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    CustomBackgroundTask.this.future.get(timeToWait, TimeUnit.MILLISECONDS);
                } catch (InterruptedException | ExecutionException | TimeoutException e) {
                    if (CustomBackgroundTask.this.progressBar.isShowing()) {
                        CustomBackgroundTask.this.progressBar.dismiss();
                    }
                    context.getHandler().obtainMessage(ServiceCommand.TASK_TIMEOUT.ordinal(), CustomBackgroundTask.this).sendToTarget();
                }
            }
        }).start();

        return doBackgroundJob();
    }

    @Override
    protected void onSuccess(final Result result) {
        if (this.progressBar.isShowing()) {
            this.progressBar.dismiss();
        }
        LOG.debug("Finish task: " + message + " in " + (System.currentTimeMillis() - time) + " ms");
        onJobDone(result);
    }

    protected abstract Result doBackgroundJob();

    protected void onJobDone(Result result) {
    }

    /**
     * {@inheritDoc}
     */
    public void onTimeout() {
        context.showControllerTaskNotCompleteAlert();
    }
}
