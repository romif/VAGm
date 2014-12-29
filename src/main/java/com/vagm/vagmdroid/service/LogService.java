package com.vagm.vagmdroid.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.os.Environment;

import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * The Class LogService.
 * 
 * @author roman_konovalov
 */
@Singleton
public class LogService {

    /**
     * LOG.
     */
    private static final Logger LOG = LoggerFactory.getLogger(LogService.class);
    
    private final DateTimeFormatter formatter = DateTimeFormat.forPattern("dd MMM yyyy"); 
    
    private final static Pattern DATE_PATTERN = Pattern.compile("^\\d{2}\\s.{3}\\s\\d{4}.*");
    
    private static final String LINE_END = "\r\n";

    /**
     * propertyService.
     */
    @Inject
    private PropertyService propertyService;

    /**
     * Default constructor.
     */
    public LogService() {
    }

    /**
     * getLogFile.
     * 
     * @return LogFile
     */
    public File getLogFile() {

        final File file = new File(Environment.getExternalStorageDirectory() + File.separator + propertyService.getAppName() + File.separator
                + propertyService.getLogFileName());

        if (!file.exists()) {
            LOG.error("File not found:" + Environment.getExternalStorageDirectory() + File.separator + propertyService.getAppName()
                    + File.separator + propertyService.getLogFileName());
        }
        return file;
    }
    
    public String getTodayLog(final File file) {
        InputStream inputStream = null;
        
        final DateTime today = new DateTime().withTimeAtStartOfDay();
        boolean isLineFound = false;

        BufferedReader reader = null;
        final StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            inputStream = new FileInputStream(file);
            reader = new BufferedReader(new InputStreamReader(inputStream, Charset.defaultCharset()));
            while ((line = reader.readLine()) != null) {
                if (!isLineFound && DATE_PATTERN.matcher(line).matches() && formatter.parseDateTime(line.substring(0, 11)).isEqual(today)) {
                    isLineFound = true;
                }
                
                if (isLineFound) {
                    sb.append(line);   
                    sb.append(LINE_END);
                }
            }
        } catch (final IOException e) {
            LOG.error(e.getMessage(), e);
        } finally {
            IOUtils.closeQuietly(reader);
        }
        return sb.toString();
    }

}
