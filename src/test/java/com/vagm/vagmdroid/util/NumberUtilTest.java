package com.vagm.vagmdroid.util;

import static com.vagm.vagmdroid.service.TestConstatnts.ADAPTER_LOG;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class NumberUtilTest {

    @Test
    public void testShouldConvertCorectly() {
        assertEquals(NumberUtil.bytesToHex(NumberUtil.hexStringToByteArray(ADAPTER_LOG)), ADAPTER_LOG);
    }

}
