package com.vagm.vagmdroid.service;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Singleton;

/**
 * The Class LogService.
 * 
 * @author roman_konovalov
 */
@Singleton
public class FileService {

    /**
     * VAGMDROIDLOG_FILE_NAME.
     */
    private static final String VAGMDROIDLOG_FILE_NAME = "vagmdroidlog.log";

    /**
     * LOG.
     */
    private static final Logger LOG = LoggerFactory.getLogger(FileService.class);

    /**
     * Default constructor.
     */
    public FileService() {
    }

    /**
     * zip file.
     * 
     * @param file
     *            file
     * @return zipped file
     * @throws IOException
     *             if exception occurs
     */
    public File zip(File file) throws IOException {
        File zipFile = File.createTempFile("ZipFileTest", ".zip");

        final int maxBufferSize = 1 * 1024 * 1024;
        BufferedInputStream origin = null;
        ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(zipFile)));
        try {
            byte[] data = new byte[maxBufferSize];

            FileInputStream fi = new FileInputStream(file);
            origin = new BufferedInputStream(fi, maxBufferSize);
            try {
                ZipEntry entry = new ZipEntry(VAGMDROIDLOG_FILE_NAME);
                out.putNextEntry(entry);
                int count;
                while ((count = origin.read(data, 0, maxBufferSize)) != -1) {
                    out.write(data, 0, count);
                }
            } finally {
                origin.close();
            }
        } finally {
            out.close();
        }
        return zipFile;
    }

    /**
     * convertStreamToString.
     * 
     * @param file
     * @return string
     */
    public String convertStreamToString(final File file) {

        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
            return convertStreamToString(inputStream);
        } catch (final IOException e) {
            LOG.error(e.getMessage());
        } finally {
            try {
                inputStream.close();
            } catch (final IOException e) {
                LOG.error(e.getMessage());
            }
        }
        return "";
    }

    public String convertStreamToString(InputStream inputStream) {
        BufferedReader reader = null;
        final StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            reader = new BufferedReader(new InputStreamReader(inputStream));
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (final IOException e) {
            LOG.error(e.getMessage());
        } finally {
            try {
                reader.close();
            } catch (final IOException e) {
                LOG.error(e.getMessage());
            }
        }
        return sb.toString();
    }

}
