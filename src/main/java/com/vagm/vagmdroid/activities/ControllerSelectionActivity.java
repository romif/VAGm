package com.vagm.vagmdroid.activities;

import static com.vagm.vagmdroid.enums.ControllerCode.ABS;
import static com.vagm.vagmdroid.enums.ControllerCode.COMFORT;
import static com.vagm.vagmdroid.enums.ControllerCode.ENGINE;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.vagm.vagmdroid.R;
import com.vagm.vagmdroid.enums.ControllerCode;
import com.vagm.vagmdroid.service.BluetoothCommandService;

public class ControllerSelectionActivity extends Activity {

	private static final String TAG = "VAGm_ControllerSelectionActivity";

	public static final String CONTROLLER_CODE = "controllerCode";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_controller_selection);
		BluetoothCommandService.getInstance().setmHandler(getmHandler());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.controller_selection, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			// NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	// The Handler that gets information back from the BluetoothChatService
	@SuppressLint("HandlerLeak")
	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {

			case BluetoothCommandService.MESSAGE_READ:
				Toast.makeText(getApplicationContext(),
						"received message in activity..!", Toast.LENGTH_SHORT)
						.show();
				break;
			}

		}
	};

	private void startActivity(int controllerCode) {
		Intent controller = new Intent(this, ControllerActivity.class);
		controller.putExtra(CONTROLLER_CODE, controllerCode);
		startActivityForResult(controller, -1);
	}

	private Handler getmHandler() {
		return mHandler;
	}

	public void bEngineClick(View view) {
		startActivity(ENGINE.getCode());
		logCommand(ENGINE);
	}
	
	public void bABSClick(View view) {
		startActivity(ABS.getCode());
		logCommand(ABS);
	}
	
	public void bComfortClick(View view) {
		startActivity(COMFORT.getCode());
		logCommand(COMFORT);
	}
	
	private void logCommand(ControllerCode controller) {
		Log.d(TAG, "Request for " + controller + " controller");
	}
}
