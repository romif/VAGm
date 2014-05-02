package com.vagm.vagmdroid.service;

import static com.vagm.vagmdroid.service.TestConstatnts.BUFFER_STRING_ARRAY;
import static com.vagm.vagmdroid.service.TestConstatnts.BUFFER_STRING_ARRAY1;
import static com.vagm.vagmdroid.service.TestConstatnts.BUFFER_STRING_ARRAY_NEGATIVE;
import static com.vagm.vagmdroid.service.TestConstatnts.ECU_INFO;
import static com.vagm.vagmdroid.service.TestConstatnts.ECU_INFO1;
import static com.vagm.vagmdroid.service.TestConstatnts.hexStringToByteArray;

import java.util.List;

import junit.framework.Assert;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;

import com.vagm.vagmdroid.activities.ControllerActivity;
import com.vagm.vagmdroid.exceptions.ControllerCommunicationException;

/**
 * The Class BufferServiceTest.
 * @author Roman_Konovalov
 */
public class BufferServiceTest extends ActivityInstrumentationTestCase2<ControllerActivity> {

	/**
	 * constructor.
	 */
	public BufferServiceTest() {
		super(ControllerActivity.class);
	}

	/**
	 * testGetControllerInfo.
	 * @throws ControllerCommunicationException if some communication error occurs
	 */
	public final void testGetControllerInfo() throws ControllerCommunicationException {
		for (int i = 0; i < BUFFER_STRING_ARRAY.length; i++) {
			final StringBuilder builder = new StringBuilder();
			for (int j = 0; j < i + 1; j++) {
				builder.append(BUFFER_STRING_ARRAY[j]);
			}
			BufferService.getControllerInfo(hexStringToByteArray(builder.toString()));
		}
		final StringBuilder builder = new StringBuilder();
		for (int j = 0; j < BUFFER_STRING_ARRAY.length; j++) {
			builder.append(BUFFER_STRING_ARRAY[j]);
		}
		List<String> list = BufferService.getControllerInfo(hexStringToByteArray(builder.toString()));
		Assert.assertEquals(3, list.size());
		Assert.assertEquals(ECU_INFO[0], list.get(0));
		Assert.assertEquals(ECU_INFO[1], list.get(1));
		Assert.assertEquals(ECU_INFO[2], list.get(2));
	}

	/**
	 * testGetControllerInfo1.
	 * @throws ControllerCommunicationException if some communication error occurs
	 */
	public final void testGetControllerInfo1() throws ControllerCommunicationException {

		final StringBuilder builder = new StringBuilder();
		for (int j = 0; j < BUFFER_STRING_ARRAY1.length; j++) {
			builder.append(BUFFER_STRING_ARRAY1[j]);
		}
		List<String> list = BufferService.getControllerInfo(hexStringToByteArray(builder.toString()));
		Log.d("TEST", list.get(2));
		Assert.assertEquals(3, list.size());
		Assert.assertEquals(ECU_INFO1[0], list.get(0));
		Assert.assertEquals(ECU_INFO1[1], list.get(1));
		Assert.assertEquals(ECU_INFO1[2], list.get(2));
	}

	/**
	 * testGetControllerInfoNegative.
	 * @throws ControllerCommunicationException if some communication error occurs
	 */
	public final void testGetControllerInfoNegative() throws ControllerCommunicationException {
		List<String> list = BufferService.getControllerInfo(hexStringToByteArray(BUFFER_STRING_ARRAY_NEGATIVE[0]));
		Assert.assertEquals(0, list.size());

		list = BufferService.getControllerInfo(hexStringToByteArray(BUFFER_STRING_ARRAY_NEGATIVE[0] + BUFFER_STRING_ARRAY_NEGATIVE[1]));
		Assert.assertEquals(1, list.size());
		Assert.assertEquals(ECU_INFO1[0], list.get(0));

		try {
			list = BufferService.getControllerInfo(hexStringToByteArray(BUFFER_STRING_ARRAY_NEGATIVE[0] + BUFFER_STRING_ARRAY_NEGATIVE[1]
					+ BUFFER_STRING_ARRAY_NEGATIVE[2]));
			Assert.fail("Should have thrown ControllerCommunication Exception");
		} catch (ControllerCommunicationException e) {
			// success
		}
	}


}
