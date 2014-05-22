package com.vagm.vagmdroid.activities;

import static com.vagm.vagmdroid.service.TestConstatnts.*;
import static com.vagm.vagmdroid.service.TestConstatnts.BUFFER_FAULT_CODES2;
import static com.vagm.vagmdroid.service.TestConstatnts.BUFFER_FAULT_CODES_NO_ERRORS;
import static com.vagm.vagmdroid.service.TestConstatnts.FAULT_CODES_STRING1;
import static com.vagm.vagmdroid.service.TestConstatnts.FAULT_CODES_STRING2;
import static com.vagm.vagmdroid.service.TestConstatnts.hexStringToByteArray;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

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
import com.vagm.vagmdroid.service.LabelServiceTest;

/**
 * The Class FaultCodesActivityTest.
 * @author roman_konovalov
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest = "AndroidManifest.xml")
public class FaultCodesActivityTest {

	/**
	 * activity.
	 */
	private FaultCodesActivity activity;

	/**
	 * faultCodes.
	 */
	private TextView faultCodes;

	/**
	 * bFaultCodesBack.
	 */
	private Button bFaultCodesBack;

	/**
	 * bClearCodes.
	 */
	private Button bClearCodes;

	/**
	 * setUp.
	 */
	@Before
	public void setUp() {
		ShadowLog.stream = System.out;
		Intent intent = new Intent(Robolectric.getShadowApplication().getApplicationContext(), ControllerActivity.class);
		intent.putExtra(ControllerActivity.ECU, LabelServiceTest.ECU);
		activity = Robolectric.buildActivity(FaultCodesActivity.class).withIntent(intent).create().get();
		bFaultCodesBack = (Button) activity.findViewById(R.id.bFaultCodesBack);
		bClearCodes = (Button) activity.findViewById(R.id.bClearCodes);
		faultCodes = (TextView) activity.findViewById(R.id.faultCodes);
	}

	/**
	 * testHandleMessage.
	 */
	@Test
	public final void testHandleMessage() {
		byte[] buffer = hexStringToByteArray(BUFFER_FAULT_CODES1);
		activity.getHandler().obtainMessage(ServiceCommand.MESSAGE_READ.ordinal(), buffer.length, -1, buffer).sendToTarget();
		assertThat(faultCodes.getText().toString(), equalTo(FAULT_CODES_STRING1));

		buffer = hexStringToByteArray(BUFFER_FAULT_CODES2);
		activity.getHandler().obtainMessage(ServiceCommand.MESSAGE_READ.ordinal(), buffer.length, -1, buffer).sendToTarget();
		assertThat(faultCodes.getText().toString(), equalTo(FAULT_CODES_STRING1 + "\r\n\r\n" + FAULT_CODES_STRING2));

		buffer = hexStringToByteArray(BUFFER_FAULT_CODES_NO_ERRORS);
		activity.getHandler().obtainMessage(ServiceCommand.MESSAGE_READ.ordinal(), buffer.length, -1, buffer).sendToTarget();
		assertThat(faultCodes.getText().toString(), equalTo(activity.getString(R.string.no_errors)));
	}
	
	/**
	 * testHandleMessageNegative.
	 */
	@Test
	public final void testHandleMessageNegative() {
		CharSequence expected = faultCodes.getText();
		byte[] buffer = hexStringToByteArray(BUFFER_STRING_ARRAY1[1]);
		activity.getHandler().obtainMessage(ServiceCommand.MESSAGE_READ.ordinal(), buffer.length, -1, buffer).sendToTarget();
		assertEquals(expected, faultCodes.getText());
	}

	/**
	 * testClearCodesClick.
	 */
	@SuppressLint("NewApi")
	@Test
	public final void testClearCodesClick() {
		byte[] buffer = hexStringToByteArray(BUFFER_FAULT_CODES1);
		activity.getHandler().obtainMessage(ServiceCommand.MESSAGE_READ.ordinal(), buffer.length, -1, buffer).sendToTarget();
		assertThat(faultCodes.getText().toString(), equalTo(FAULT_CODES_STRING1));

		bClearCodes.callOnClick();
		assertThat(faultCodes.getText().toString(), equalTo(""));
	}

	/**
	 * testFaultCodesBackClick.
	 */
	@SuppressLint("NewApi")
	@Test
	public final void testFaultCodesBackClick() {
		bFaultCodesBack.callOnClick();
		assertTrue(activity.isFinishing());
	}

}
