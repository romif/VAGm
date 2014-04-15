package com.vagm.vagmdroid.activities;

import static com.vagm.vagmdroid.service.BluetoothCommandService.MESSAGE_DEVICE_NAME;
import static com.vagm.vagmdroid.service.BluetoothCommandService.MESSAGE_STATE_CHANGE;
import static com.vagm.vagmdroid.service.BluetoothCommandService.MESSAGE_TOAST;
import static com.vagm.vagmdroid.service.BluetoothCommandService.STATE_CONNECTED;
import static com.vagm.vagmdroid.service.BluetoothCommandService.STATE_CONNECTING;
import static com.vagm.vagmdroid.service.BluetoothCommandService.STATE_LISTEN;
import static com.vagm.vagmdroid.service.BluetoothCommandService.STATE_NONE;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.vagm.vagmdroid.R;
import com.vagm.vagmdroid.enums.ControllerCode;
import com.vagm.vagmdroid.enums.VAGmConstans;
import com.vagm.vagmdroid.service.BluetoothCommandService;

public class MainActivity extends CustomActivity implements OnClickListener {

	private static final String TAG = "VAGm_RemoteBluetooth";

	public static final String CONTROLLER_CODE = "controllerCode";

	// Layout view
	private TextView mTitle;

	// Intent request codes
	private static final int REQUEST_SELECT_DEVICE = 1;
	private static final int REQUEST_ENABLE_BT = 2;

	// Message types sent from the BluetoothChatService Handler

	// Key names received from the BluetoothCommandService Handler
	public static final String DEVICE_NAME = "device_name";
	public static final String TOAST = "toast";

	// Name of the connected device
	private String mConnectedDeviceName = null;

	private Button connectButton;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Set up the window layout
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.main);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title);

		// Set up the custom title
		mTitle = (TextView) findViewById(R.id.title_left_text);
		mTitle.setText(R.string.app_name);
		mTitle = (TextView) findViewById(R.id.title_right_text);

		disableEnableControls(false, (LinearLayout) findViewById(R.id.mainLayout));
		setButtonOnClickListner((LinearLayout) findViewById(R.id.mainLayout), this);
		connectButton = (Button) findViewById(R.id.bConnectAdapter);
		connectButton.setEnabled(true);

		// If the adapter is null, then Bluetooth is not supported
		if (BluetoothAdapter.getDefaultAdapter() == null) {
			Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
			finish();
			return;
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		// If BT is not on, request that it be enabled.
		// setupCommand() will then be called during onActivityResult

		if (!BluetoothAdapter.getDefaultAdapter().isEnabled()) {
			Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		// Performing this check in onResume() covers the case in which BT was
		// not enabled during onStart(), so we were paused to enable it...
		// onResume() will be called when ACTION_REQUEST_ENABLE activity
		// returns.
		if (mCommandService != null) {
			if (mCommandService.getState() == BluetoothCommandService.STATE_NONE) {
				mCommandService.start();
			}
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		if (mCommandService != null)
			mCommandService.stop();
	}

	// The Handler that gets information back from the BluetoothChatService
	@SuppressLint("HandlerLeak")
	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MESSAGE_STATE_CHANGE:
				switch (msg.arg1) {
				case STATE_CONNECTED:
					mTitle.setText(R.string.title_connected_to);
					mTitle.append(mConnectedDeviceName);
					mCommandService.write(VAGmConstans.START_CONTROLLER_COMMUNICATION);
					disableEnableControls(false, (LinearLayout) findViewById(R.id.mainLayout));
					break;
				case STATE_CONNECTING:
					mTitle.setText(R.string.title_connecting);
					break;
				case STATE_LISTEN:
				case STATE_NONE:
					mTitle.setText(R.string.title_not_connected);
					break;
				}
				break;
			case MESSAGE_DEVICE_NAME:
				// save the connected device's name
				mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
				Toast.makeText(getApplicationContext(), "Connected to " + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
				break;
			case MESSAGE_TOAST:
				Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST), Toast.LENGTH_SHORT).show();
				break;
			}

		}
	};

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_SELECT_DEVICE) {
			if (resultCode == Activity.RESULT_OK) {
				// Get the device MAC address
				String address = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
				SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
				SharedPreferences.Editor editor = sharedPref.edit();
				editor.putString(getString(R.string.savedDevice), address);
				editor.commit();
			}
		} else if (requestCode == REQUEST_ENABLE_BT)
			// When the request to enable Bluetooth returns
			if (resultCode != Activity.RESULT_OK) {
				Toast.makeText(this, R.string.bt_not_enabled_leaving, Toast.LENGTH_SHORT).show();
				finish();
			}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.option_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int itemId = item.getItemId();
		if (itemId == R.id.scan) {
			// Launch the DeviceListActivity to see devices and do scan
			Intent serverIntent = new Intent(this, DeviceListActivity.class);
			startActivityForResult(serverIntent, REQUEST_SELECT_DEVICE);
			return true;
		} else if (itemId == R.id.discoverable) {
			// Ensure this device is discoverable by others
			return true;
		}
		return false;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
			mCommandService.write(BluetoothCommandService.VOL_UP);
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
			mCommandService.write(BluetoothCommandService.VOL_DOWN);
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}

	public void onClick(View v) {
		if (v.getId() == R.id.bConnectAdapter) {
			SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
			String address = sharedPref.getString(getString(R.string.savedDevice), null);
			if (address != null) {
				BluetoothDevice device = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(address);
				mCommandService.setmHandler(mHandler);
				mCommandService.connect(device);
			} else {
				Toast.makeText(MainActivity.this, "Bluetooth adapter is not selected, first select", Toast.LENGTH_LONG).show();
			}
		} else {
			selectController(ControllerCode.getControllerCode(v.getId()));
		}
	}

	private void selectController(ControllerCode controllerCode) {
		if (controllerCode == null) {
			return;
		}
		Log.d(TAG, "Request for " + controllerCode + " controller");
		Intent controller = new Intent(this, ControllerActivity.class);
		controller.putExtra(CONTROLLER_CODE, controllerCode.getCode());
		startActivityForResult(controller, -1);

	}

	@Override
	protected Handler getHandler() {
		return mHandler;
	}

}