package com.vagm.vagmdroid.activities;

import static com.vagm.vagmdroid.service.TestConstatnts.BUFFER_FAULT_CODES1;
import static com.vagm.vagmdroid.service.TestConstatnts.BUFFER_STRING_ARRAY;
import static com.vagm.vagmdroid.service.TestConstatnts.BUFFER_STRING_ARRAY_NEGATIVE;
import static com.vagm.vagmdroid.service.TestConstatnts.ECU_INFO;
import static com.vagm.vagmdroid.service.TestConstatnts.ECU_INFO1;
import static com.vagm.vagmdroid.service.TestConstatnts.hexStringToByteArray;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowAlertDialog;
import org.robolectric.shadows.ShadowLog;
import org.robolectric.shadows.ShadowToast;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.inject.Inject;
import com.vagm.vagmdroid.R;
import com.vagm.vagmdroid.service.BluetoothService.ServiceCommand;
import com.vagm.vagmdroid.service.ControllerInfoService;
import com.vagm.vagmdroid.service.LabelServiceTest;

/**
 * The Class ControllerActivityTest.
 * @author Roman_Konovalov
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest = "AndroidManifest.xml")
public class ControllerActivityTest {

	/**
	 * activity.
	 */
	private ControllerActivity activity;

	/**
	 * bFaultCodes.
	 */
	private Button bFaultCodes;

	/**
	 * bMeasBlocks.
	 */
	private Button bMeasBlocks;

	/**
	 * bOuputTests.
	 */
	private Button bOuputTests;

	/**
	 * bCloseController.
	 */
	private Button bCloseController;

	/**
	 * boudRate.
	 */
	private TextView boudRate;

	/**
	 * VAGnumber.
	 */
	private TextView VAGnumber;

	/**
	 * component.
	 */
	private TextView component;

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
		intent.putExtra(MainActivity.CONTROLLER_CODE, 1);
		activity = Robolectric.buildActivity(ControllerActivity.class).withIntent(intent).create().get();
		bFaultCodes = (Button) activity.findViewById(R.id.bFaultCodes);
		bMeasBlocks = (Button) activity.findViewById(R.id.bMeasBlocks);
		bOuputTests = (Button) activity.findViewById(R.id.bOuputTests);
		bCloseController = (Button) activity.findViewById(R.id.bCloseController);
		boudRate = (TextView) activity.findViewById(R.id.boudRate);
		VAGnumber = (TextView) activity.findViewById(R.id.VAGnumber);
		component = (TextView) activity.findViewById(R.id.component);
	}

	/**
	 * testHandleMessage.
	 */
	@Test
	public final void testHandleMessage() {
		// precondition
		assertTrue("Button FaultCodes should be desabled", !bFaultCodes.isEnabled());
		assertTrue("Button MeasBlocks should be desabled", !bMeasBlocks.isEnabled());
		assertTrue("Button OuputTests should be desabled", !bOuputTests.isEnabled());
		assertTrue("Button CloseController should be desabled", !bCloseController.isEnabled());

		for (String s : BUFFER_STRING_ARRAY) {
			byte[] buffer = hexStringToByteArray(s);
			activity.getHandler().obtainMessage(ServiceCommand.MESSAGE_READ.ordinal(), buffer.length, -1, buffer).sendToTarget();
		}

		// postcondition
		assertThat(boudRate.getText().toString(), equalTo(ECU_INFO[0]));
		assertThat(VAGnumber.getText().toString(), equalTo(ECU_INFO[1]));
		assertThat(component.getText().toString(), equalTo(ECU_INFO[2]));

		assertTrue("Button FaultCodes should be enabled", bFaultCodes.isEnabled());
		assertTrue("Button MeasBlocks should be enabled", bMeasBlocks.isEnabled());
		assertTrue("Button OuputTests should be enabled", bOuputTests.isEnabled());
		assertTrue("Button CloseController should be enabled", bCloseController.isEnabled());
	}

	/**
	 * testHandleMessageRotation.
	 */
	@Test
	public final void testHandleMessageRotation() {
		// precondition
		assertTrue("Button FaultCodes should be desabled", !bFaultCodes.isEnabled());
		assertTrue("Button MeasBlocks should be desabled", !bMeasBlocks.isEnabled());
		assertTrue("Button OuputTests should be desabled", !bOuputTests.isEnabled());
		assertTrue("Button CloseController should be desabled", !bCloseController.isEnabled());

		for (String s : BUFFER_STRING_ARRAY) {
			byte[] buffer = hexStringToByteArray(s);
			activity.getHandler().obtainMessage(ServiceCommand.MESSAGE_READ.ordinal(), buffer.length, -1, buffer).sendToTarget();
		}
		activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

		// postcondition
		assertThat(boudRate.getText().toString(), equalTo(ECU_INFO[0]));
		assertThat(VAGnumber.getText().toString(), equalTo(ECU_INFO[1]));
		assertThat(component.getText().toString(), equalTo(ECU_INFO[2]));

		assertTrue("Button FaultCodes should be enabled", bFaultCodes.isEnabled());
		assertTrue("Button MeasBlocks should be enabled", bMeasBlocks.isEnabled());
		assertTrue("Button OuputTests should be enabled", bOuputTests.isEnabled());
		assertTrue("Button CloseController should be enabled", bCloseController.isEnabled());
	}

	/**
	 * testHandleMessageControllerNotAnswer.
	 */
	@Test
	public final void testHandleMessageControllerNotAnswer() {
		byte[] buffer = hexStringToByteArray(BUFFER_STRING_ARRAY_NEGATIVE[0]);
		activity.getHandler().obtainMessage(ServiceCommand.MESSAGE_READ.ordinal(), buffer.length, -1, buffer).sendToTarget();

		// postcondition
		assertThat(boudRate.getText().toString(), equalTo(ECU_INFO1[0]));
		assertTrue("Button FaultCodes should be desabled", !bFaultCodes.isEnabled());
		assertTrue("Button MeasBlocks should be desabled", !bMeasBlocks.isEnabled());
		assertTrue("Button OuputTests should be desabled", !bOuputTests.isEnabled());
		assertTrue("Button CloseController should be desabled", !bCloseController.isEnabled());

		buffer = hexStringToByteArray(BUFFER_STRING_ARRAY_NEGATIVE[2]);
		activity.getHandler().obtainMessage(ServiceCommand.MESSAGE_READ.ordinal(), buffer.length, -1, buffer).sendToTarget();

		// postcondition
		AlertDialog alert = ShadowAlertDialog.getLatestAlertDialog();
		assertNotNull("Should be error dialog", alert);
		ShadowAlertDialog sAlert = Robolectric.shadowOf(alert);
		assertThat(sAlert.getMessage().toString(), equalTo(activity.getString(R.string.controller_not_answer)));
	}
	
	@Test
	public final void testHandleMessageControllerNotFound() {
		byte[] buffer = hexStringToByteArray(BUFFER_STRING_ARRAY_NEGATIVE[1]);
		activity.getHandler().obtainMessage(ServiceCommand.MESSAGE_READ.ordinal(), buffer.length, -1, buffer).sendToTarget();

		// postcondition
		AlertDialog alert = ShadowAlertDialog.getLatestAlertDialog();
		assertNotNull("Should be error dialog", alert);
		ShadowAlertDialog sAlert = Robolectric.shadowOf(alert);
		assertThat(sAlert.getMessage().toString(), equalTo(activity.getString(R.string.controller_not_found)));
	}

	/**
	 * testHandleMessageNegative1.
	 * @throws InterruptedException
	 */
	@Test
	@Ignore
	public final void testHandleMessageNegative1() throws InterruptedException {
		CountDownLatch latch = new CountDownLatch(1);
		latch.await(20, TimeUnit.SECONDS);

		Robolectric.runUiThreadTasksIncludingDelayedTasks();
		// postcondition
		AlertDialog alert = ShadowAlertDialog.getLatestAlertDialog();
		assertNotNull("Should be error dialog", alert);
		ShadowAlertDialog sAlert = Robolectric.shadowOf(alert);
		//assertThat(sAlert.getMessage().toString(), equalTo(activity.getString(R.string.adapter_not_answer)));
	}

	/**
	 * testHandleMessageNegative2.
	 */
	@Test
	public final void testHandleMessageNegative2() {
		CharSequence expected = VAGnumber.getText();
		byte[] buffer = hexStringToByteArray(BUFFER_FAULT_CODES1);
		activity.getHandler().obtainMessage(ServiceCommand.MESSAGE_READ.ordinal(), buffer.length, -1, buffer).sendToTarget();
		assertEquals(expected, VAGnumber.getText());
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

}
