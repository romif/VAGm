package com.vagm.vagmdroid.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.content.Context;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.vagm.vagmdroid.R;

/**
 * The Class FaultCodesServer.
 * 
 * @author roman_konovalov
 */
@Singleton
public class FaultCodesService {

    private static final String DTC_DESCRIPTION_FILE = "DTC_description";

    private static final String DTC_FOLDER = "DTC";

    private static final String ASSETS_SOURCE = "assets";

    private Map<Integer, String> dtcDescriptionMap = null;

    /**
     * LOG.
     */
    private static final Logger LOG = LoggerFactory.getLogger(FaultCodesService.class);

    /**
     * context.
     */
    @Inject
    private Context context;

    /**
     * getDTC.
     * 
     * @param errorCode
     *            errorCode
     * @return DTC
     */
    public String getDTC(final int errorCode) {
        InputStream inputStream = null;
        BufferedReader reader = null;

        try {
            if (errorCode < 1012) {
                inputStream = FaultCodesService.class.getClassLoader().getResourceAsStream(
                        ASSETS_SOURCE + File.separator + DTC_FOLDER + File.separator + "dtc.txt");
            } else {
                if (errorCode < 2012) {
                    inputStream = FaultCodesService.class.getClassLoader().getResourceAsStream(
                            ASSETS_SOURCE + File.separator + DTC_FOLDER + File.separator + "dtc1.txt");
                } else {
                    if (errorCode < 16361) {
                        inputStream = FaultCodesService.class.getClassLoader().getResourceAsStream(
                                ASSETS_SOURCE + File.separator + DTC_FOLDER + File.separator + "dtc2.txt");
                    } else {
                        if (errorCode < 17878) {
                            inputStream = FaultCodesService.class.getClassLoader().getResourceAsStream(
                                    ASSETS_SOURCE + File.separator + DTC_FOLDER + File.separator + "dtc3.txt");
                        } else {
                            inputStream = FaultCodesService.class.getClassLoader().getResourceAsStream(
                                    ASSETS_SOURCE + File.separator + DTC_FOLDER + File.separator + "dtc4.txt");
                        }
                    }
                }
            }
            reader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"));

            String st;
            while (((st = reader.readLine()) != null)) {
                if (Integer.parseInt(st.substring(0, 5)) == errorCode) {
                    return st.substring(8);
                }
            }

        } catch (final IOException ex) {
            LOG.error("Cannot read NA37.txt", ex);
        } finally {
            IOUtils.closeQuietly(reader);
        }

        return context.getString(R.string.unknown_error);
    }

    /**
     * getErrorType.
     * 
     * @param type
     *            type
     * @return ErrorType
     */
    public String getErrorType(final int type) {
        String description = getDtcDescriptionMap().get(type);
        return description != null ? description : "";
    }

    private Map<Integer, String> getDtcDescriptionMap() {
        if (dtcDescriptionMap != null) {
            return dtcDescriptionMap;
        }

        dtcDescriptionMap = new HashMap<>();
        InputStream inputStream = null;
        BufferedReader reader = null;

        try {
            inputStream = FaultCodesService.class.getClassLoader().getResourceAsStream(
                    ASSETS_SOURCE + File.separator + DTC_FOLDER + File.separator + DTC_DESCRIPTION_FILE);
            reader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"));

            String st;
            while (((st = reader.readLine()) != null)) {
                dtcDescriptionMap.put(Integer.parseInt(st.substring(0, 3)), st.substring(3));
            }

        } catch (NumberFormatException | IOException e) {
            LOG.error("Cannot read DTC_description", e);
        } finally {
            IOUtils.closeQuietly(reader);
        }

        return dtcDescriptionMap;
    }

}
