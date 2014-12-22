package com.vagm.vagmdroid.activities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import roboguice.activity.RoboActivity;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.google.inject.Inject;
import com.vagm.vagmdroid.R;
import com.vagm.vagmdroid.exceptions.ControllerCommunicationException;
import com.vagm.vagmdroid.exceptions.ControllerNotFoundException;
import com.vagm.vagmdroid.exceptions.ControllerWrongResponseException;
import com.vagm.vagmdroid.service.BluetoothService.ServiceCommand;
import com.vagm.vagmdroid.service.BufferService;
import com.vagm.vagmdroid.service.TimeOutJob;

/**
 * The Class DispatcherHandlerActivity.
 * @author Roman_Konovalov
 */
public class DispatcherHandlerActivity extends RoboActivity {

    /**
     * bufferService.
     */
    @Inject
    private BufferService bufferService;

    /**
     * LOG.
     */
    private static final Logger LOG = LoggerFactory.getLogger(DispatcherHandlerActivity.class);

    /**
     * The Handler that gets information back from the BluetoothService.
     */
    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(final Message msg) {
            super.handleMessage(msg);
            final ServiceCommand serviceCommand = ServiceCommand.values()[msg.what];
            if (serviceCommand == ServiceCommand.MESSAGE_READ) {
                byte[] message = (byte[]) msg.obj;
                LOG.debug("Recieved message from conroller: {}", bufferService.bytesToHex(message));
                try {
                    bufferService.checkAdapterErrors(message);
                } catch (ControllerNotFoundException e) {
                    LOG.error("Controller wasn't found", e);
                    getControllerNotFoundAlert().show();
                } catch (ControllerCommunicationException e) {
                    LOG.error("No answer from controller", e);
                    getControllerNotAnswerAlert().show();
                }

                try {
                    proceedMessage(message);
                } catch (ControllerWrongResponseException e) {
                    LOG.info(e.getMessage());
                }
            } else if (serviceCommand == ServiceCommand.CONNECTION_LOST) {
                Toast.makeText(getApplicationContext(), getText(R.string.connection_lost), Toast.LENGTH_SHORT).show();
                onConnectionLost();
                finish();
            } else if (serviceCommand == ServiceCommand.TASK_TIMEOUT) {
                ((TimeOutJob) msg.obj).onTimeout();
            }
        }
    };

    /**
     * getHandler.
     * 
     * @return Handler
     */
    public Handler getHandler() {
        return mHandler;
    }

    /**
     * showControllerTaskNotCompleteAlert.
     */
    public void showControllerTaskNotCompleteAlert() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.taskNotComlete)).setTitle(getString(R.string.error)).setCancelable(false)
                .setNeutralButton(getString(R.string.back), new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        finish();
                    }
                });
        builder.create().show();
    }

    protected void proceedMessage(byte[] message) throws ControllerWrongResponseException {
    }

    protected void onConnectionLost() {
    }

    /**
     * getControllerNotAnswerAlert.
     * 
     * @return AlertDialog
     */
    protected AlertDialog getControllerNotAnswerAlert() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.controller_not_answer)).setTitle(getString(R.string.error)).setCancelable(false)
                .setNeutralButton(getString(R.string.back), new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        finish();
                    }
                });
        AlertDialog alertDialog = builder.create();
        return alertDialog;
    }

    /**
     * getControllerNotFoundAlert.
     * 
     * @return AlertDialog
     */
    protected AlertDialog getControllerNotFoundAlert() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.controller_not_found)).setTitle(getString(R.string.error)).setCancelable(false)
                .setNeutralButton(getString(R.string.back), new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        finish();
                    }
                });
        AlertDialog alertDialog = builder.create();
        return alertDialog;
    }

    protected void showtAlert(String message) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message).setTitle(getString(R.string.error)).setCancelable(false)
                .setNeutralButton(getString(R.string.back), new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        finish();
                    }
                });
        builder.create().show();
    }

}
