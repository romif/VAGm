package com.vagm.vagmdroid.activities;

import static com.vagm.vagmdroid.service.TestConstatnts.BUFFER_STRING_ARRAY;
import static com.vagm.vagmdroid.service.TestConstatnts.BUFFER_STRING_ARRAY_NEGATIVE;
import static com.vagm.vagmdroid.service.TestConstatnts.ECU_INFO;
import static com.vagm.vagmdroid.service.TestConstatnts.ECU_INFO1;
import static com.vagm.vagmdroid.service.TestConstatnts.hexStringToByteArray;

import com.robotium.solo.Solo;
import com.vagm.vagmdroid.R;
import com.vagm.vagmdroid.service.BluetoothService.ServiceCommand;

/**
 * The Class ControllerActivityTest.
 * @author Roman_Konovalov
 */
public class ControllerActivityTest extends AbstractActivityTest<ControllerActivity> {

	/**
	 * constructor.
	 */
	public ControllerActivityTest() {
		super(ControllerActivity.class);
	}

	/**
	 * testHandleMessage.
	 */
	public final void testHandleMessage() {
		// precondition
		for (int i = 0; i < 4; i++) {
			assertTrue("Button '" + solo.getButton(i).getText() + "' should be desabled", !solo.getButton(i).isEnabled());
		}

		for (String s : BUFFER_STRING_ARRAY) {
			byte[] buffer = hexStringToByteArray(s);
			getActivity().getHandler().obtainMessage(ServiceCommand.MESSAGE_READ.ordinal(), buffer.length, -1, buffer).sendToTarget();
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
	 * testHandleMessageRotation.
	 */
	public final void testHandleMessageRotation() {
		// precondition
		for (int i = 0; i < 4; i++) {
			assertTrue("Button '" + solo.getButton(i).getText() + "' should be desabled", !solo.getButton(i).isEnabled());
		}

		for (String s : BUFFER_STRING_ARRAY) {
			byte[] buffer = hexStringToByteArray(s);
			getActivity().getHandler().obtainMessage(ServiceCommand.MESSAGE_READ.ordinal(), buffer.length, -1, buffer).sendToTarget();
		}
		solo.setActivityOrientation(Solo.LANDSCAPE);

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
		byte[] buffer = hexStringToByteArray(BUFFER_STRING_ARRAY_NEGATIVE[0] + BUFFER_STRING_ARRAY_NEGATIVE[1]);
		getActivity().getHandler().obtainMessage(ServiceCommand.MESSAGE_READ.ordinal(), buffer.length, -1, buffer).sendToTarget();

		// postcondition
		assertTrue("Cannot find TextView with text: " + ECU_INFO1[0], solo.searchText(ECU_INFO1[0]));
		for (int i = 0; i < 4; i++) {
			assertTrue("Button '" + solo.getButton(i).getText() + "' should be desabled", !solo.getButton(i).isEnabled());
		}

		buffer = hexStringToByteArray(BUFFER_STRING_ARRAY_NEGATIVE[0] + BUFFER_STRING_ARRAY_NEGATIVE[1]  + BUFFER_STRING_ARRAY_NEGATIVE[2]);
		getActivity().getHandler().obtainMessage(ServiceCommand.MESSAGE_READ.ordinal(), buffer.length, -1, buffer).sendToTarget();

		// postcondition
		assertTrue("Should be error dialog", solo.waitForDialogToOpen());
	}

	/**
	 * testHandleMessageNegative1.
	 */
	public final void testHandleMessageNegative1() {
		// postcondition
		assertTrue("Should be error dialog", solo.waitForDialogToOpen());
	}

	/**
	 * testHandleMessageConnectionLost.
	 */
	public final void testHandleMessageConnectionLost() {
		getActivity().getHandler().obtainMessage(ServiceCommand.CONNECTION_LOST.ordinal(), -1, -1).sendToTarget();

		// postcondition
		assertTrue("Should be a message: " + getActivity().getText(R.string.connection_lost),
				solo.waitForText(getActivity().getText(R.string.connection_lost).toString()));
	}

}
