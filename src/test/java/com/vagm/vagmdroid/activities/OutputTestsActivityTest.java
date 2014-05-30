/**
 * 
 */
package com.vagm.vagmdroid.activities;

import static com.vagm.vagmdroid.service.TestConstatnts.BUFFER_OUTPUTTESTS_CODES1;
import static com.vagm.vagmdroid.service.TestConstatnts.BUFFER_OUTPUTTESTS_END;
import static com.vagm.vagmdroid.service.TestConstatnts.OUTPUTTESTS_STRING1;
import static com.vagm.vagmdroid.service.TestConstatnts.hexStringToByteArray;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLog;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.widget.Button;
import android.widget.TextView;

import com.vagm.vagmdroid.R;
import com.vagm.vagmdroid.service.BluetoothService.ServiceCommand;

/**
 * The Class OutputTestsActivityTest.
 * @author Roman_Konovalov
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest = "AndroidManifest.xml")
public class OutputTestsActivityTest {

	/**
	 * activity.
	 */
	private OutputTestsActivity activity;

	/**
	 * activatedOutput.
	 */
	private TextView activatedOutput;

	/**
	 * bStart.
	 */
	private Button bStart;

	/**
	 * setUp.
	 */
	@Before
	public void setUp() {
		ShadowLog.stream = System.out;
		Intent intent = new Intent(Robolectric.getShadowApplication().getApplicationContext(), ControllerActivity.class);
		activity = Robolectric.buildActivity(OutputTestsActivity.class).withIntent(intent).create().get();
		bStart = (Button) activity.findViewById(R.id.bStart);
		activatedOutput = (TextView) activity.findViewById(R.id.activatedOutput);
	}

	/**
	 * Test method for {@link com.vagm.vagmdroid.activities.OutputTestsActivity#getHandler()}.
	 */
	@SuppressLint("NewApi")
	@Test
	public final void testHandleMessage() {
		assertThat(bStart.getText().toString(), equalTo(activity.getString(R.string.bStart)));
		bStart.callOnClick();
		assertThat(bStart.getText().toString(), equalTo(activity.getString(R.string.bStart)));

		byte[] buffer = hexStringToByteArray(BUFFER_OUTPUTTESTS_CODES1);
		activity.getHandler().obtainMessage(ServiceCommand.MESSAGE_READ.ordinal(), buffer.length, -1, buffer).sendToTarget();

		assertThat(activatedOutput.getText().toString(), equalTo(OUTPUTTESTS_STRING1));
		assertThat(bStart.getText().toString(), equalTo(activity.getString(R.string.bNext)));

		buffer = hexStringToByteArray(BUFFER_OUTPUTTESTS_END);
		activity.getHandler().obtainMessage(ServiceCommand.MESSAGE_READ.ordinal(), buffer.length, -1, buffer).sendToTarget();

		assertThat(activatedOutput.getText().toString(), equalTo(activity.getString(R.string.test_ended)));
		assertThat(bStart.getText().toString(), equalTo(activity.getString(R.string.bStart)));
	}

}
