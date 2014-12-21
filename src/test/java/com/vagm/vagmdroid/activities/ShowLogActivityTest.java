package com.vagm.vagmdroid.activities;

import static com.vagm.vagmdroid.service.TestConstatnts.ADAPTER_LOG;
import static com.vagm.vagmdroid.service.TestConstatnts.ADAPTER_LOG_DECODED;
import static com.vagm.vagmdroid.service.TestConstatnts.hexStringToByteArray;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLog;

import android.content.Intent;
import android.widget.TextView;

import com.vagm.vagmdroid.R;
import com.vagm.vagmdroid.service.BluetoothService.ServiceCommand;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = "AndroidManifest.xml")
public class ShowLogActivityTest extends TestCase {
	
	/**
	 * activity.
	 */
	private ShowLogActivity activity;
	
	private TextView logText;
	
	/**
	 * setUp.
	 */
	@Before
	public void setUp() {
		ShadowLog.stream = System.out;
		Intent intent = new Intent(Robolectric.getShadowApplication().getApplicationContext(), ShowLogActivity.class);
		intent.putExtra(SendLogActivity.LOG_TEXT, "");
		activity = Robolectric.buildActivity(ShowLogActivity.class).withIntent(intent).create().get();
		logText = (TextView) activity.findViewById(R.id.log_text);

	}
	
	/**
	 * testHandleMessage.
	 * @throws InterruptedException 
	 */
	@Test
	public final void testHandleMessage() throws InterruptedException {
		byte[] buffer = hexStringToByteArray(ADAPTER_LOG);
		activity.getHandler().obtainMessage(ServiceCommand.MESSAGE_READ.ordinal(), buffer.length, -1, buffer).sendToTarget();
		
		Thread.sleep(100);
		
		assertThat(logText.getText().toString(), equalTo(ADAPTER_LOG_DECODED));
	}

}
