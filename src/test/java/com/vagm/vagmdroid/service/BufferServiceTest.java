package com.vagm.vagmdroid.service;

import static com.vagm.vagmdroid.service.TestConstatnts.BUFFER_MEAS_BLOCKS_1GROUPS;
import static com.vagm.vagmdroid.service.TestConstatnts.BUFFER_MEAS_BLOCKS_2GROUPS;
import static com.vagm.vagmdroid.service.TestConstatnts.BUFFER_MEAS_BLOCKS_3GROUPS;
import static com.vagm.vagmdroid.service.TestConstatnts.BUFFER_MEAS_BLOCKS_4GROUPS;
import static com.vagm.vagmdroid.service.TestConstatnts.BUFFER_STRING_ARRAY;
import static com.vagm.vagmdroid.service.TestConstatnts.BUFFER_STRING_ARRAY1;
import static com.vagm.vagmdroid.service.TestConstatnts.BUFFER_STRING_ARRAY2;
import static com.vagm.vagmdroid.service.TestConstatnts.BUFFER_STRING_ARRAY3;
import static com.vagm.vagmdroid.service.TestConstatnts.BUFFER_STRING_ARRAY_NEGATIVE;
import static com.vagm.vagmdroid.service.TestConstatnts.ECU_INFO;
import static com.vagm.vagmdroid.service.TestConstatnts.ECU_INFO1;
import static com.vagm.vagmdroid.service.TestConstatnts.ECU_INFO2;
import static com.vagm.vagmdroid.service.TestConstatnts.ECU_INFO3;
import static com.vagm.vagmdroid.service.TestConstatnts.hexStringToByteArray;
import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import roboguice.RoboGuice;
import roboguice.activity.RoboActivity;
import android.app.Application;
import android.content.Context;
import android.os.Vibrator;

import com.google.inject.AbstractModule;
import com.google.inject.util.Modules;
import com.vagm.vagmdroid.exceptions.ControllerCommunicationException;
import com.vagm.vagmdroid.exceptions.ControllerWrongResponseException;

/**
 * A testcase that swaps in a TestVibrator to verify that
 * Astroboy's {@link org.roboguice.astroboy.controller.Astroboy#brushTeeth()} method
 * works properly.
 */
public class BufferServiceTest  {

    BufferService bufferService ;


    
    @After
    public void teardown() {
        // Don't forget to tear down our custom injector to avoid polluting other test classes
        RoboGuice.util.reset();
    }
    
    /**
	 * testGetControllerInfo.
	 * @throws ControllerCommunicationException if some communication error occurs
	 * @throws ControllerWrongResponseException if wrong response from controller occurs
	 */
    @Test
	public final void testGetControllerInfo() throws ControllerCommunicationException, ControllerWrongResponseException {
		String[] result = {"", "", "" };
		for (String message : BUFFER_STRING_ARRAY) {
			result = bufferService.getControllerInfo(hexStringToByteArray(message), result);
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
    @Test
	public final void testGetControllerInfo1() throws ControllerCommunicationException, ControllerWrongResponseException {
		String[] result = {"", "", "" };
		for (String message : BUFFER_STRING_ARRAY1) {
			result = bufferService.getControllerInfo(hexStringToByteArray(message), result);
		}
		Assert.assertEquals(ECU_INFO1[0], result[0]);
		Assert.assertEquals(ECU_INFO1[1], result[1]);
		Assert.assertEquals(ECU_INFO1[2], result[2]);
	}

	/**
	 * testGetControllerInfo2.
	 * @throws ControllerCommunicationException if some communication error occurs
	 * @throws ControllerWrongResponseException if wrong response from controller occurs
	 */
    @Test
	public final void testGetControllerInfo2() throws ControllerCommunicationException, ControllerWrongResponseException {
		String[] result = {"", "", "" };
		for (String message : BUFFER_STRING_ARRAY2) {
			result = bufferService.getControllerInfo(hexStringToByteArray(message), result);
		}
		Assert.assertEquals(ECU_INFO2[0], result[0]);
		Assert.assertEquals(ECU_INFO2[1], result[1]);
		Assert.assertEquals(ECU_INFO2[2], result[2]);
	}

	/**
	 * testGetControllerInfo3.
	 * @throws ControllerCommunicationException if some communication error occurs
	 * @throws ControllerWrongResponseException if wrong response from controller occurs
	 */
    @Test
	public final void testGetControllerInfo3() throws ControllerCommunicationException, ControllerWrongResponseException {
		String[] result = {"", "", "" };
		for (String message : BUFFER_STRING_ARRAY3) {
			result = bufferService.getControllerInfo(hexStringToByteArray(message), result);
		}
		Assert.assertEquals(ECU_INFO3[0], result[0]);
		Assert.assertEquals(ECU_INFO3[1], result[1]);
		Assert.assertEquals(ECU_INFO3[2], result[2]);
	}

	/**
	 * testGetControllerInfoNegative.
	 * @throws ControllerCommunicationException if some communication error occurs
	 * @throws ControllerWrongResponseException if wrong response from controller occurs
	 */
    @Test
	public final void testGetControllerInfoNegative() throws ControllerCommunicationException, ControllerWrongResponseException {
		String[] result = {"", "", "" };

		result = bufferService.getControllerInfo(hexStringToByteArray(BUFFER_STRING_ARRAY_NEGATIVE[0]), result);
		Assert.assertEquals(ECU_INFO1[0], result[0]);

		try {
			result = bufferService.getControllerInfo(hexStringToByteArray(BUFFER_STRING_ARRAY_NEGATIVE[1]), result);
			Assert.fail("Should have thrown ControllerCommunication Exception");
		} catch (ControllerCommunicationException e) {
			// success
		}
	}

	/**
	 * testGetMeasBlocksInfo.
	 * @throws ControllerCommunicationException if some communication error occurs
	 * @throws ControllerWrongResponseException if wrong response from controller occurs
	 */
    @Test
	public final void testGetMeasBlocksInfo() throws ControllerCommunicationException, ControllerWrongResponseException {
		bufferService.getMeasBlocksInfo(hexStringToByteArray(BUFFER_MEAS_BLOCKS_1GROUPS));
		bufferService.getMeasBlocksInfo(hexStringToByteArray(BUFFER_MEAS_BLOCKS_2GROUPS));
		bufferService.getMeasBlocksInfo(hexStringToByteArray(BUFFER_MEAS_BLOCKS_3GROUPS));
		bufferService.getMeasBlocksInfo(hexStringToByteArray(BUFFER_MEAS_BLOCKS_4GROUPS));
	}



    public class MyTestModule extends AbstractModule {
        @Override
        protected void configure() {
            //bind(Vibrator.class).toInstance(vibratorMock);
        }
    }
}