package com.vagm.vagmdroid.activities;

import static com.vagm.vagmdroid.service.TestConstatnts.*;
import static com.vagm.vagmdroid.service.TestConstatnts.BUFFER_FAULT_CODES2;
import static com.vagm.vagmdroid.service.TestConstatnts.BUFFER_FAULT_CODES_NO_ERRORS;
import static com.vagm.vagmdroid.service.TestConstatnts.FAULT_CODES_STRING1;
import static com.vagm.vagmdroid.service.TestConstatnts.FAULT_CODES_STRING2;
import static com.vagm.vagmdroid.service.TestConstatnts.hexStringToByteArray;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowAlertDialog;
import org.robolectric.shadows.ShadowLog;
import org.robolectric.shadows.ShadowToast;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.inject.Inject;
import com.vagm.vagmdroid.R;
import com.vagm.vagmdroid.service.BluetoothService.ServiceCommand;
import com.vagm.vagmdroid.service.ControllerInfoService;
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
	 * controllerInfoService.
	 */
	@Inject
	private ControllerInfoService controllerInfoService;

	/**
	 * setUp.
	 */
	@Before
	public void setUp() {
		ShadowLog.stream = System.out;
		controllerInfoService.setVagNumber(LabelServiceTest.ECU);
		Intent intent = new Intent(Robolectric.getShadowApplication().getApplicationContext(), ControllerActivity.class);
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
	 * testHandleMessageConnectionLost.
	 */
	@Test
	public final void testHandleMessageConnectionLost() {
		activity.getHandler().obtainMessage(ServiceCommand.CONNECTION_LOST.ordinal(), -1, -1).sendToTarget();

		// postcondition
		Toast toast = ShadowToast.getLatestToast();
		assertNotNull("Should be error dialog", toast);
		assertThat(ShadowToast.getTextOfLatestToast(), equalTo(activity.getString(R.string.connection_lost)));
	}

	/**
	 * testHandleMessageControllerNotAnswer.
	 */
	@Test
	public final void testHandleMessageControllerNotAnswer() {
		byte[] buffer = hexStringToByteArray(BUFFER_STRING_ARRAY_NEGATIVE[1]);
		activity.getHandler().obtainMessage(ServiceCommand.MESSAGE_READ.ordinal(), buffer.length, -1, buffer).sendToTarget();

		// postcondition
		AlertDialog alert = ShadowAlertDialog.getLatestAlertDialog();
		assertNotNull("Should be error dialog", alert);
		ShadowAlertDialog sAlert = Robolectric.shadowOf(alert);
		assertThat(sAlert.getMessage().toString(), equalTo(activity.getString(R.string.controller_not_answer)));
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
