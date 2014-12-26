package com.vagm.vagmdroid.service;

import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.vagm.vagmdroid.constants.VAGmConstans;
import com.vagm.vagmdroid.util.NumberUtil;

@Singleton
public class BackgroundService {

    private static final Logger LOG = LoggerFactory.getLogger(BackgroundService.class);

    @Inject
    private NumberUtil numberUtil;

    @Inject
    private BufferService bufferService;

    public boolean doTask(final byte[] buffer) {
        int responseCode = numberUtil.byteToInt(buffer[0]);

        switch (responseCode) {
            case VAGmConstans.ADAPTER_LOG_RES:
                appendLog(buffer);
                return true;
    
            default:
                return false;
        }

    }

    private void appendLog(byte[] buffer) {
        String adapterLog = bufferService.encodeAdapterLog(buffer);

        LOG.debug(adapterLog);
    }

}
