package com.vagm.vagmdroid.service;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.IOUtils;

import com.google.inject.Singleton;

/**
 * The Class LogService.
 * 
 * @author roman_konovalov
 */
@Singleton
public class FileService {
    
    private static final String VAGMDROIDLOG_FILE_NAME = "vagm.log";

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
    public File zip(final File file) throws IOException {
        final File zipFile = File.createTempFile("vagmlog.zip", null);

        ZipOutputStream out = null;
        InputStream input = null;
        try {
            out = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(zipFile)));
            input = new BufferedInputStream(new FileInputStream(file));
            
            ZipEntry entry = new ZipEntry(VAGMDROIDLOG_FILE_NAME);
            out.putNextEntry(entry);
            IOUtils.copy(input, out);
        } finally {
            IOUtils.closeQuietly(out);
            IOUtils.closeQuietly(input);
        }
        return zipFile;
    }

}
