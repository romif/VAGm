package com.vagm.vagmdroid.service;

import static com.vagm.vagmdroid.service.TestConstatnts.BUFFER_STRING_ARRAY;
import static com.vagm.vagmdroid.service.TestConstatnts.BUFFER_STRING_ARRAY1;
import static com.vagm.vagmdroid.service.TestConstatnts.BUFFER_STRING_ARRAY_NEGATIVE;
import static com.vagm.vagmdroid.service.TestConstatnts.ECU_INFO;
import static com.vagm.vagmdroid.service.TestConstatnts.ECU_INFO1;
import static com.vagm.vagmdroid.service.TestConstatnts.hexStringToByteArray;

import java.util.List;

import junit.framework.Assert;
import android.test.AndroidTestCase;
import android.util.Log;

import com.vagm.vagmdroid.exceptions.ControllerCommunicationException;
import com.vagm.vagmdroid.exceptions.ControllerWrongResponseException;

/**
 * The Class BufferServiceTest.
 * @author Roman_Konovalov
 */
public class BufferServiceTest extends AndroidTestCase {

	/**
	 * testGetControllerInfo.
	 * @throws ControllerCommunicationException if some communication error occurs
	 * @throws ControllerWrongResponseException if wrong response from controller occurs
	 */
	public final void testGetControllerInfo() throws ControllerCommunicationException, ControllerWrongResponseException {
		String[] result = {"", "", "" };
		for (String message : BUFFER_STRING_ARRAY) {
			result = BufferService.getControllerInfo(hexStringToByteArray(message), result);
		}
		Assert.assertEquals(ECU_INFO[0], result[0]);
		Assert.assertEquals(ECU_INFO[1], result[1]);
		Assert.assertEquals(ECU_INFO[2], result[2]);
	}

	/**
	 * testGetControllerInfo1.
	 * @throws ControllerCommunicationException if some communication error occurs
	 * @throws ControllerWrongResponseException if wrong response from controller occurs
	 */
	public final void testGetControllerInfo1() throws ControllerCommunicationException, ControllerWrongResponseException {
		String[] result = {"", "", "" };
		for (String message : BUFFER_STRING_ARRAY1) {
			result = BufferService.getControllerInfo(hexStringToByteArray(message), result);
		}
		Assert.assertEquals(ECU_INFO1[0], result[0]);
		Assert.assertEquals(ECU_INFO1[1], result[1]);
		Assert.assertEquals(ECU_INFO1[2], result[2]);
	}

	/**
	 * testGetControllerInfoNegative.
	 * @throws ControllerCommunicationException if some communication error occurs
	 * @throws ControllerWrongResponseException if wrong response from controller occurs
	 */
	public final void testGetControllerInfoNegative() throws ControllerCommunicationException, ControllerWrongResponseException {
		String[] result = {"", "", "" };

		result = BufferService.getControllerInfo(hexStringToByteArray(BUFFER_STRING_ARRAY_NEGATIVE[0]), result);
		Assert.assertEquals(ECU_INFO1[0], result[0]);

		try {
			result = BufferService.getControllerInfo(hexStringToByteArray(BUFFER_STRING_ARRAY_NEGATIVE[1]), result);
			Assert.fail("Should have thrown ControllerCommunication Exception");
		} catch (ControllerCommunicationException e) {
			// success
		}
	}


}
