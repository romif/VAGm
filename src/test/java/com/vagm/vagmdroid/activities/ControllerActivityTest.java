package com.vagm.vagmdroid.activities;

import static com.vagm.vagmdroid.service.TestConstatnts.BUFFER_STRING_ARRAY;
import static com.vagm.vagmdroid.service.TestConstatnts.BUFFER_STRING_ARRAY_NEGATIVE;
import static com.vagm.vagmdroid.service.TestConstatnts.ECU_INFO;
import static com.vagm.vagmdroid.service.TestConstatnts.ECU_INFO1;
import static com.vagm.vagmdroid.service.TestConstatnts.hexStringToByteArray;
import android.test.ActivityInstrumentationTestCase2;

import com.robotium.solo.Solo;
import com.vagm.vagmdroid.service.BluetoothService.ServiceCommand;

/**
 * The Class ControllerActivityTest.
 * @author Roman_Konovalov
 */
public class ControllerActivityTest extends ActivityInstrumentationTestCase2<ControllerActivity> {

	/**
	 * solo.
	 */
	private Solo solo;

	/**
	 * constructor.
	 */
	public ControllerActivityTest() {
		super(ControllerActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	/**
	 * testHandleMessage.
	 */
	public final void testHandleMessage() {
		ControllerActivity controllerActivity = getActivity();
		solo = new Solo(getInstrumentation(), controllerActivity);
		getInstrumentation().waitForIdleSync();

		// precondition
		for (int i = 0; i < 4; i++) {
			assertTrue("Button '" + solo.getButton(i).getText() + "' should be desabled", !solo.getButton(i).isEnabled());
		}

		for (String s : BUFFER_STRING_ARRAY) {
			byte[] buffer = hexStringToByteArray(s);
			controllerActivity.getHandler().obtainMessage(ServiceCommand.MESSAGE_READ.ordinal(), buffer.length, -1, buffer).sendToTarget();
		}

		// postcondition
		for (String s : ECU_INFO) {
			assertTrue("Cannot find TextView with text: " + s, solo.searchText(s));
		}
		for (int i = 0; i < 4; i++) {
			assertTrue("Button '" + solo.getButton(i).getText() + "' should be enabled", solo.getButton(i).isEnabled());
		}

	}

	/**
	 * testHandleMessageNegative.
	 */
	public final void testHandleMessageNegative() {
		ControllerActivity controllerActivity = getActivity();
		solo = new Solo(getInstrumentation(), controllerActivity);
		getInstrumentation().waitForIdleSync();

		byte[] buffer = hexStringToByteArray(BUFFER_STRING_ARRAY_NEGATIVE[0]);
		controllerActivity.getHandler().obtainMessage(ServiceCommand.MESSAGE_READ.ordinal(), buffer.length, -1, buffer).sendToTarget();

		// postcondition
		assertTrue("Cannot find TextView with text: " + ECU_INFO1[0], solo.searchText(ECU_INFO1[0]));
		for (int i = 0; i < 4; i++) {
			assertTrue("Button '" + solo.getButton(i).getText() + "' should be desabled", !solo.getButton(i).isEnabled());
		}

		buffer = hexStringToByteArray(BUFFER_STRING_ARRAY_NEGATIVE[0] + BUFFER_STRING_ARRAY_NEGATIVE[1]);
		controllerActivity.getHandler().obtainMessage(ServiceCommand.MESSAGE_READ.ordinal(), buffer.length, -1, buffer).sendToTarget();

		// postcondition
		assertTrue("Should be error dialog", solo.waitForDialogToOpen());
	}

	/**
	 * testHandleMessageNegative1.
	 */
	public final void testHandleMessageNegative1() {
		ControllerActivity controllerActivity = getActivity();
		solo = new Solo(getInstrumentation(), controllerActivity);
		getInstrumentation().waitForIdleSync();

		// postcondition
		assertTrue("Should be error dialog", solo.waitForDialogToOpen());
	}

}
