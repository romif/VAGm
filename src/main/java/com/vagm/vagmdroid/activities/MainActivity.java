package com.vagm.vagmdroid.activities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.inject.Inject;
import com.vagm.vagmdroid.R;
import com.vagm.vagmdroid.enums.ControllerCode;
import com.vagm.vagmdroid.enums.VAGmConstans;
import com.vagm.vagmdroid.service.BluetoothService.ConnectionState;
import com.vagm.vagmdroid.service.BluetoothService.ServiceCommand;
import com.vagm.vagmdroid.service.PropertyService;
import com.vagm.vagmdroid.util.CopyLabelsTask;

/**
 * The Class MainActivity.
 * @author Roman_Konovalov
 */
public class MainActivity extends CustomAbstractActivity implements OnClickListener {

	/**
	 * LOG.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(MainActivity.class);

	/**
	 * CONTROLLER_CODE constant.
	 */
	public static final String CONTROLLER_CODE = "controllerCode";

	/**
	 * REQUEST_SELECT_DEVICE constant.
	 */
	private static final int REQUEST_SELECT_DEVICE = 1;

	/**
	 * REQUEST_ENABLE_BT.
	 */
	private static final int REQUEST_ENABLE_BT = 2;

	/**
	 * DEVICE_NAME constant.
	 */
	public static final String DEVICE_NAME = "device_name";

	/**
	 * TOAST constant.
	 */
	public static final String TOAST = "toast";

	/**
	 * PREFERENCE_FIRST_RUN.
	 */
	private static final String PREFERENCE_FIRST_RUN = "firstRun";

	/**
	 * mTitle.
	 */
	private TextView mTitle;

	/**
	 * mConnectedDeviceName.
	 */
	private String mConnectedDeviceName = null;

	/**
	 * connectButton.
	 */
	private Button connectButton;

	/**
	 * bluetoothAdapter.
	 */
	private BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	
	/**
	 * propertyService.
	 */
	@Inject
	private PropertyService propertyService;

	/**
	 * The Handler that gets information back from the BluetoothCommandService.
	 */
	@SuppressLint("HandlerLeak")
	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(final Message msg) {
			switch (ServiceCommand.values()[msg.what]) {
			case MESSAGE_STATE_CHANGE:
				switch (ConnectionState.values()[msg.arg1]) {
				case CONNECTED:
					mTitle.setText(R.string.title_connected_to);
					mTitle.append(mConnectedDeviceName);
					bluetoothService.write(VAGmConstans.START_CONTROLLER_COMMUNICATION);
					disableEnableControls(true, (ViewGroup) findViewById(R.id.mainLayout));
					connectButton.setEnabled(false);
					break;
				case CONNECTING:
					mTitle.setText(R.string.title_connecting);
					break;
				case LISTEN:
				case NONE:
					mTitle.setText(R.string.title_not_connected);
					break;
				default:
					break;
				}
				break;
			case MESSAGE_DEVICE_NAME:
				// save the connected device's name
				mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
				Toast.makeText(getApplicationContext(), "Connected to " + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
				break;
			case UNABLE_CONNECT:
				Toast.makeText(getApplicationContext(), getText(R.string.unable_connect), Toast.LENGTH_SHORT).show();
				connectButton.setEnabled(true);
				break;
			case CONNECTION_LOST:
				Toast.makeText(getApplicationContext(), getText(R.string.connection_lost), Toast.LENGTH_SHORT).show();
				connectButton.setEnabled(true);
				break;
			case MESSAGE_TOAST:
				Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST), Toast.LENGTH_SHORT).show();
				break;
			default:
				break;
			}
		}
	};

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onClick(final View v) {
		if (v.getId() == R.id.bConnectAdapter) {
			final SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
			final String address = sharedPref.getString(getString(R.string.savedDevice), null);
			if (address != null) {
				connectButton.setEnabled(false);
				final BluetoothDevice device = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(address);
				bluetoothService.setmHandler(mHandler);
				bluetoothService.connect(device);
			} else {
				Toast.makeText(MainActivity.this, getString(R.string.btn_not_selected), Toast.LENGTH_LONG).show();
			}
		}
		if (v.getId() == R.id.bDirectEntry) {
			EditText text = (EditText) findViewById(R.id.controllerCode);
			int controllerCode;
			try {
				controllerCode = Integer.parseInt(text.getText().toString(), 16);
			} catch (NumberFormatException e) {
				Toast.makeText(MainActivity.this, getString(R.string.wrong_number), Toast.LENGTH_LONG).show();
				return;
			}
			selectController(controllerCode);
		} else {
			selectController(ControllerCode.getControllerCode(v.getId()));
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		final MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean onKeyDown(final int keyCode, final KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
			//getmCommandService().write(BluetoothCommandService.VOL_UP);
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
			//getmCommandService().write(BluetoothCommandService.VOL_DOWN);
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		final int itemId = item.getItemId();
		if (itemId == R.id.settings) {
			// Launch the DeviceListActivity to see devices and do scan
			final Intent serverIntent = new Intent(this, DeviceListActivity.class);
			startActivityForResult(serverIntent, REQUEST_SELECT_DEVICE);
			return true;
		}
		if (itemId == R.id.exit) {
			finish();
			return true;
		}
		return false;
	}

	/*@Override
	protected BluetoothService getBluetoothService() {
		return BluetoothService.getInstance();
	}*/

	@Override
	protected Handler getHandler() {
		return mHandler;
	}

	/**
	 * {@inheritDoc}
	 */
	protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
		if (requestCode == REQUEST_SELECT_DEVICE) {
			if (resultCode == Activity.RESULT_OK) {
				// Get the device MAC address
				final String address = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
				final SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
				final SharedPreferences.Editor editor = sharedPref.edit();
				editor.putString(getString(R.string.savedDevice), address);
				editor.commit();
			}
		} else if (requestCode == REQUEST_ENABLE_BT) {
			// When the request to enable Bluetooth returns
			if (resultCode != Activity.RESULT_OK) {
				Toast.makeText(this, R.string.bt_not_enabled_leaving, Toast.LENGTH_SHORT).show();
				finish();
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LOG.debug("onCreate");

		// Set up the window layout
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.main);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title);

		// Set up the custom title
		mTitle = (TextView) findViewById(R.id.title_left_text);
		mTitle.setText(R.string.app_name);
		mTitle = (TextView) findViewById(R.id.title_right_text);

		disableEnableControls(false, (ViewGroup) findViewById(R.id.mainLayout));
		setButtonOnClickListner((ViewGroup) findViewById(R.id.mainLayout), this);
		connectButton = (Button) findViewById(R.id.bConnectAdapter);
		connectButton.setEnabled(true);

		// If the adapter is null, then Bluetooth is not supported
		if (propertyService.isProduction() && bluetoothAdapter == null) {
			getControllerNotAnswerAlert().show();
			return;
		}

		if (isFirstRun()) {
			new CopyLabelsTask(this).execute();
		}

		/*final Intent faultCodesIntent = new Intent(this, GraphicActivity.class);
		startActivityForResult(faultCodesIntent, -1);*/
		/*String url = "http://vagm-romif.rhcloud.com/uploadFile";
		File file = new File(Environment.getExternalStorageDirectory() + File.separator + PropertyService.getAppName(),
		        "VAGm.log");
		try {
		    HttpClient httpclient = new DefaultHttpClient();

		    HttpPost httppost = new HttpPost(url);

		    InputStreamEntity reqEntity = new InputStreamEntity(
		            new FileInputStream(file), -1);
		    reqEntity.setContentType("multipart/form-data");
		    reqEntity.setChunked(true); // Send in multiple parts if needed
		    httppost.setEntity(reqEntity);
		    HttpResponse response = httpclient.execute(httppost);
		    //Do something with response...

		} catch (Exception e) {
		    LOG.error("Error", e);
		}*/
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onDestroy() {
		LOG.debug("MainActivity onDestroy()");
		super.onDestroy();
		bluetoothService.stop();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onResume() {
		super.onResume();
		// Performing this check in onResume() covers the case in which BT was
		// not enabled during onStart(), so we were paused to enable it...
		// onResume() will be called when ACTION_REQUEST_ENABLE activity
		// returns.
		if (bluetoothService.getState() == ConnectionState.NONE) {
			bluetoothService.start();
			connectButton.setEnabled(true);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onStart() {
		super.onStart();
		// If BT is not on, request that it be enabled.
		// setupCommand() will then be called during onActivityResult
		if (bluetoothAdapter != null && !bluetoothAdapter.isEnabled()) {
			final Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
		}
	}

	/**
	 * isFirstRun.
	 * @return isFirstRun
	 */
	private boolean isFirstRun() {
		SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(this);
		boolean firstRun = p.getBoolean(PREFERENCE_FIRST_RUN, true);
		p.edit().putBoolean(PREFERENCE_FIRST_RUN, false).commit();
		return firstRun;
	}

	/**
	 * Selects Controller.
	 * @param controllerCode
	 *            controllerCode
	 */
	private void selectController(final ControllerCode controllerCode) {
		if (controllerCode == null) {
			return;
		}
		LOG.debug("Request for {} controller", controllerCode);
		final Intent controller = new Intent(this, ControllerActivity.class);
		controller.putExtra(CONTROLLER_CODE, controllerCode.getCode());
		startActivityForResult(controller, -1);
	}

	/**
	 * Selects Controller.
	 * @param controllerCode
	 *            controllerCode
	 */
	private void selectController(final int controllerCode) {
		LOG.debug("Request for controller with number: {}", controllerCode);
		final Intent controller = new Intent(this, ControllerActivity.class);
		controller.putExtra(CONTROLLER_CODE, controllerCode);
		startActivityForResult(controller, -1);
	}

}
