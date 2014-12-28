package com.vagm.vagmdroid.service;

import static com.vagm.vagmdroid.service.TestConstatnts.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static com.vagm.vagmdroid.util.NumberUtil.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowLog;

import android.content.Context;

import com.google.inject.Inject;
import com.vagm.vagmdroid.R;
import com.vagm.vagmdroid.dto.DataStreamDTO;
import com.vagm.vagmdroid.exceptions.ControllerCommunicationException;
import com.vagm.vagmdroid.exceptions.ControllerNotFoundException;
import com.vagm.vagmdroid.exceptions.ControllerWrongResponseException;

/**
 * The Class BufferServiceTest.
 * 
 * @author roman_konovalov
 */
@RunWith(RobolectricTestRunner.class)
public class BufferServiceTest {

    /**
     * bufferService.
     */
    @Inject
    private BufferService bufferService;

    /**
     * context.
     */
    @Inject
    private Context context;

    {
        ShadowLog.stream = System.out;
    }

    /**
     * testGetControllerInfo.
     * 
     * @throws ControllerCommunicationException
     *             if some communication error occurs
     * @throws ControllerWrongResponseException
     *             if wrong response from controller occurs
     * @throws ControllerNotFoundException
     */
    @Test
    public final void testGetControllerInfo() throws ControllerCommunicationException, ControllerWrongResponseException,
            ControllerNotFoundException {
        String[] result = { "", "", "" };
        for (String message : BUFFER_STRING_ARRAY) {
            result = bufferService.getControllerInfo(hexStringToByteArray(message), result);
        }
        assertEquals(ECU_INFO[0], result[0]);
        assertEquals(ECU_INFO[1], result[1]);
        assertEquals(ECU_INFO[2], result[2]);
    }

    /**
     * testGetControllerInfo1.
     * 
     * @throws ControllerCommunicationException
     *             if some communication error occurs
     * @throws ControllerWrongResponseException
     *             if wrong response from controller occurs
     * @throws ControllerNotFoundException
     */
    @Test
    public final void testGetControllerInfo1() throws ControllerCommunicationException, ControllerWrongResponseException,
            ControllerNotFoundException {
        String[] result = { "", "", "" };
        for (String message : BUFFER_STRING_ARRAY1) {
            result = bufferService.getControllerInfo(hexStringToByteArray(message), result);
        }
        assertEquals(ECU_INFO1[0], result[0]);
        assertEquals(ECU_INFO1[1], result[1]);
        assertEquals(ECU_INFO1[2], result[2]);
    }

    /**
     * testGetControllerInfo2.
     * 
     * @throws ControllerCommunicationException
     *             if some communication error occurs
     * @throws ControllerWrongResponseException
     *             if wrong response from controller occurs
     * @throws ControllerNotFoundException
     */
    @Test
    public final void testGetControllerInfo2() throws ControllerCommunicationException, ControllerWrongResponseException,
            ControllerNotFoundException {
        String[] result = { "", "", "" };
        for (String message : BUFFER_STRING_ARRAY2) {
            result = bufferService.getControllerInfo(hexStringToByteArray(message), result);
        }
        assertEquals(ECU_INFO2[0], result[0]);
        assertEquals(ECU_INFO2[1], result[1]);
        assertEquals(ECU_INFO2[2], result[2]);
    }

    /**
     * testGetControllerInfo3.
     * 
     * @throws ControllerCommunicationException
     *             if some communication error occurs
     * @throws ControllerWrongResponseException
     *             if wrong response from controller occurs
     * @throws ControllerNotFoundException
     */
    @Test
    public final void testGetControllerInfo3() throws ControllerCommunicationException, ControllerWrongResponseException,
            ControllerNotFoundException {
        String[] result = { "", "", "" };
        for (String message : BUFFER_STRING_ARRAY3) {
            result = bufferService.getControllerInfo(hexStringToByteArray(message), result);
        }
        assertEquals(ECU_INFO3[0], result[0]);
        assertEquals(ECU_INFO3[1], result[1]);
        assertEquals(ECU_INFO3[2], result[2]);
    }

    /**
     * testGetControllerInfoNegative.
     * 
     * @throws ControllerCommunicationException
     *             if some communication error occurs
     * @throws ControllerWrongResponseException
     *             if wrong response from controller occurs
     * @throws ControllerNotFoundException
     */
    @Test(expected = ControllerCommunicationException.class)
    public final void testControllerCommunicationException() throws ControllerCommunicationException, ControllerWrongResponseException,
            ControllerNotFoundException {
        bufferService.checkAdapterErrors(hexStringToByteArray(BUFFER_STRING_ARRAY_NEGATIVE[2]));
    }

    /**
     * testControllerNotFoundException.
     * @throws ControllerCommunicationException
     * @throws ControllerWrongResponseException
     * @throws ControllerNotFoundException
     */
    @Test(expected = ControllerNotFoundException.class)
    public final void testControllerNotFoundException() throws ControllerCommunicationException, ControllerWrongResponseException,
            ControllerNotFoundException {
        bufferService.checkAdapterErrors(hexStringToByteArray(BUFFER_STRING_ARRAY_NEGATIVE[1]));
    }

    /**
     * testGetControllerInfoNegativ1e.
     * 
     * @throws ControllerCommunicationException
     *             if some communication error occurs
     * @throws ControllerWrongResponseException
     *             if wrong response from controller occurs
     * @throws ControllerNotFoundException
     */
    @Test(expected = ControllerWrongResponseException.class)
    public final void testGetControllerInfoNegative1() throws ControllerCommunicationException, ControllerWrongResponseException,
            ControllerNotFoundException {
        String[] result = { "", "", "" };
        bufferService.getControllerInfo(hexStringToByteArray(BUFFER_MEAS_BLOCKS_4GROUPS), result);
    }

    /**
     * testGetMeasBlocksInfo.
     * 
     * @throws ControllerCommunicationException
     *             if some communication error occurs
     * @throws ControllerWrongResponseException
     *             if wrong response from controller occurs
     * @throws ControllerNotFoundException
     */
    @Test
    public final void testGetMeasBlocksInfo() throws ControllerCommunicationException, ControllerWrongResponseException,
            ControllerNotFoundException {
        DataStreamDTO[] result = bufferService.getMeasBlocksInfo(hexStringToByteArray(BUFFER_MEAS_BLOCKS_1GROUPS));
        assertNotEquals(DataStreamDTO.getDefault(context), result[0]);
        for (int i = 1; i < 4; i++) {
            assertEquals(DataStreamDTO.getDefault(context), result[i]);
        }

        result = bufferService.getMeasBlocksInfo(hexStringToByteArray(BUFFER_MEAS_BLOCKS_2GROUPS));
        for (int i = 0; i < 2; i++) {
            assertNotEquals(DataStreamDTO.getDefault(context), result[i]);
        }
        for (int i = 2; i < 4; i++) {
            assertEquals(DataStreamDTO.getDefault(context), result[i]);
        }

        result = bufferService.getMeasBlocksInfo(hexStringToByteArray(BUFFER_MEAS_BLOCKS_3GROUPS));
        for (int i = 0; i < 3; i++) {
            assertNotEquals(DataStreamDTO.getDefault(context), result[i]);
        }
        for (int i = 3; i < 4; i++) {
            assertEquals(DataStreamDTO.getDefault(context), result[i]);
        }

        result = bufferService.getMeasBlocksInfo(hexStringToByteArray(BUFFER_MEAS_BLOCKS_4GROUPS));
        for (int i = 0; i < 4; i++) {
            assertNotEquals(DataStreamDTO.getDefault(context), result[i]);
        }
    }

    /**
     * testGetFaultCodesInfo.
     * 
     * @throws ControllerCommunicationException
     *             if some communication error occurs
     * @throws ControllerWrongResponseException
     *             if wrong response from controller occurs
     * @throws ControllerNotFoundException
     */
    @Test
    public void testGetFaultCodesInfo() throws ControllerCommunicationException, ControllerWrongResponseException,
            ControllerNotFoundException {
        String result = bufferService.getFaultCodesInfo(hexStringToByteArray(BUFFER_FAULT_CODES1));
        assertEquals(FAULT_CODES_STRING1, result);

        result = bufferService.getFaultCodesInfo(hexStringToByteArray(BUFFER_FAULT_CODES2));
        assertEquals(FAULT_CODES_STRING2, result);

        result = bufferService.getFaultCodesInfo(hexStringToByteArray(BUFFER_FAULT_CODES_NO_ERRORS));
        assertEquals(context.getString(R.string.no_errors), result);
    }

    /**
     * testGetOutputTestsInfo.
     * 
     * @throws ControllerCommunicationException
     *             if some communication error occurs
     * @throws ControllerWrongResponseException
     *             if wrong response from controller occurs
     * @throws ControllerNotFoundException
     */
    @Test
    public void testGetOutputTestsInfo() throws ControllerCommunicationException, ControllerWrongResponseException,
            ControllerNotFoundException {
        String result = bufferService.getOutputTestsInfo(hexStringToByteArray(BUFFER_OUTPUTTESTS_CODES1));
        assertEquals(OUTPUTTESTS_STRING1, result);

        result = bufferService.getOutputTestsInfo(hexStringToByteArray(BUFFER_OUTPUTTESTS_CODES2));
        assertEquals(OUTPUTTESTS_STRING2, result);

        result = bufferService.getOutputTestsInfo(hexStringToByteArray(BUFFER_OUTPUTTESTS_END));
        assertEquals(context.getString(R.string.test_ended), result);
    }
    
    @Test
    public void testShouldEncodeAdapterLogCorrectly() {
        String result = bufferService.encodeAdapterLog(hexStringToByteArray(ADAPTER_LOG));
        assertEquals(ADAPTER_LOG_DECODED, result);
    }

}