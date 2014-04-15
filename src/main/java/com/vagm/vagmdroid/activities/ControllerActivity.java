package com.vagm.vagmdroid.activities;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.vagm.vagmdroid.R;
import com.vagm.vagmdroid.service.BluetoothCommandService;

public class ControllerActivity extends Activity {

	private static final String TAG = "VAGm_ControllerActivity";
	private static final boolean D = true;

	// Layout view
	private TextView ECUText;

	private TextView ECUInfoText;

	private Button okButton;

	private String controllerInfo = "";

	private ProgressBar progressBar;

	private Timer longTimer;

	private Handler h;

	// The Handler that gets information back from the BluetoothChatService
	@SuppressLint("HandlerLeak")
	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case BluetoothCommandService.MESSAGE_READ:
				byte[] array = Arrays.copyOfRange((byte[]) msg.obj, 0, msg.arg1);
				if (D)
					Log.d(TAG, "Recieved message from conroller: " + bytesToHex(array));
				if (msg.arg1 > 1) {
					if (array[0] != msg.arg1) {
						Log.w(TAG, "Message length: " + msg.arg1 + ", but should be: " + array[0]);
					}
					proceedMessage(Arrays.copyOfRange(array, 1, array.length));
				}
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_controller);
		BluetoothCommandService.getInstance().setmHandler(mHandler);
		int controllerCode = getIntent().getExtras().getInt(ControllerSelectionActivity.CONTROLLER_CODE);
		if (D)
			Log.d(TAG, "Sending controller code: " + controllerCode);
		BluetoothCommandService.getInstance().write(controllerCode);
		ECUText = (TextView) findViewById(R.id.VAGnumber);
		ECUInfoText = (TextView) findViewById(R.id.component);
		okButton = (Button) findViewById(R.id.okButton);
		okButton.setVisibility(View.GONE);
		progressBar = (ProgressBar) findViewById(R.id.progressBar1);
		progressBar.setVisibility(View.VISIBLE);
		h = new Handler();
		longTimer = new Timer();
		longTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				h.post(new Runnable() {
					
					public void run() {
						Log.w(TAG, "No answer from controller");
						longTimer.cancel();
						longTimer = null;
						getAlert().show();
					}
				});
			}
		}, 15 * 1000);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.controller, menu);
		return true;
	}

	private void proceedMessage(byte[] array) {
		if (array[0] > 0x80)
			array[0] = (byte) (array[0] - 0x80);
		try {
			controllerInfo = controllerInfo + new String(array, "ISO-8859-1");
		} catch (UnsupportedEncodingException e) {
			controllerInfo = controllerInfo + new String(array);
		}
		if (controllerInfo.length() > 14) {
			ECUText.setText(getString(R.string.VAGnumber) + controllerInfo.substring(0, 12));
			ECUInfoText.setText(getString(R.string.component) + controllerInfo.substring(12));
			progressBar.setVisibility(View.GONE);
			okButton.setVisibility(View.VISIBLE);
			if (longTimer != null) {
				longTimer.cancel();
				longTimer = null;
			}
		}
	}
	
	private void proceedArray(byte[] array) {
		byte marker = array[0];
		
	}

	private AlertDialog getAlert() {
		AlertDialog.Builder builder = new AlertDialog.Builder(ControllerActivity.this);
		builder.setMessage("Нет ответа от контроллера.").setTitle("Ошибка").setCancelable(false)
				.setNeutralButton("Назад", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						Intent controllerSelection = new Intent(ControllerActivity.this,
								ControllerSelectionActivity.class);
						startActivityForResult(controllerSelection, -1);
					}
				});
		return builder.create();
	}
	
	private static String bytesToHex(byte[] in) {
	    final StringBuilder builder = new StringBuilder();
	    for(byte b : in) {
	        builder.append(String.format("%02x", b));
	    }
	    return builder.toString();
	}

}
