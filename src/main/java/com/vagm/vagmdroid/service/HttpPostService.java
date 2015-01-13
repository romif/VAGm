package com.vagm.vagmdroid.service;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Singleton;

/**
 * The Class HttpPostService.
 * 
 * @author roman_konovalov
 */
@Singleton
public class HttpPostService {

    /**
     * The Class MediaType.
     */
    public static class MediaType {

        /**
         * "multipart/form-data".
         */
        public static final String MULTIPART_FORM_DATA = "multipart/form-data";
    }
    
    private static final int MAX_BUFFER_SIZE = 1 * 1024 * 1024;
    
    private static final String LINE_END = "\r\n";
    
    private static final String TWO_HYPHENS = "--";

    /**
     * LOG.
     */
    private static final Logger LOG = LoggerFactory.getLogger(HttpPostService.class);

    /**
     * FORM_PARAM.
     */
    private static final String FORM_PARAM = "log";

    /**
     * Default constructor.
     */
    public HttpPostService() {
    }

    /**
     * doMultipartRequest.
     * 
     * @param urlTo
     *            urlTo
     * @param parmas
     *            parmas
     * @param file
     *            file
     * @param fileMimeType
     *            fileMimeType
     * @return response
     */
    public String doMultipartRequest(final String urlTo, final Map<String, String> parmas, final File file, final String fileMimeType) {
        
        DataOutputStream outputStream = null;
        InputStream inputStream = null;
        FileInputStream fileInputStream = null;

        final String boundary = "*****" + Long.toString(System.currentTimeMillis()) + "*****";
        
        String result = "";
        
        try {
            final URL url = new URL(urlTo);
            final HttpURLConnection connection = getConnection(boundary, url);

            outputStream = new DataOutputStream(connection.getOutputStream());
            outputStream.writeBytes(TWO_HYPHENS + boundary + LINE_END);
            outputStream.writeBytes("Content-Disposition: form-data; name=\"" + FORM_PARAM + "\"" + LINE_END);
            outputStream.writeBytes("Content-Type: " + fileMimeType + LINE_END);
            outputStream.writeBytes("Content-Transfer-Encoding: binary" + LINE_END);

            outputStream.writeBytes(LINE_END);
            
            fileInputStream = new FileInputStream(file);

            writeOutputStream(fileInputStream, outputStream);

            outputStream.writeBytes(LINE_END);

            uploadPOSTData(parmas, outputStream, boundary);

            outputStream.writeBytes(TWO_HYPHENS + boundary + TWO_HYPHENS + LINE_END);

            if (200 != connection.getResponseCode()) {
                LOG.error("Failed to upload code:" + connection.getResponseCode() + " " + connection.getResponseMessage());
                return null;
            }

            inputStream = connection.getInputStream();

            result = IOUtils.toString(inputStream, Charset.defaultCharset());
            outputStream.flush();

            return result;
        } catch (final IOException e) {
            LOG.error(e.getMessage());
            return null;
        } finally {
            IOUtils.closeQuietly(outputStream);
            IOUtils.closeQuietly(fileInputStream);
            IOUtils.closeQuietly(inputStream);
            
        }

    }

    private void uploadPOSTData(final Map<String, String> parmas, DataOutputStream outputStream, final String boundary) throws IOException {
        for (final Entry<String, String> entry : parmas.entrySet()) {
            final String key = entry.getKey();
            final String value = entry.getValue();

            outputStream.writeBytes(TWO_HYPHENS + boundary + LINE_END);
            outputStream.writeBytes("Content-Disposition: form-data; name=\"" + key + "\"" + LINE_END);
            outputStream.writeBytes("Content-Type: text/plain" + LINE_END);
            outputStream.writeBytes(LINE_END);
            outputStream.writeBytes(value);
            outputStream.writeBytes(LINE_END);
        }
    }

    private FileInputStream writeOutputStream(final FileInputStream fileInputStream, DataOutputStream outputStream) throws IOException {
        int bytesRead;
        int bytesAvailable;
        int bufferSize;
        byte[] buffer;
        
        bytesAvailable = fileInputStream.available();
        bufferSize = Math.min(bytesAvailable, MAX_BUFFER_SIZE);
        buffer = new byte[bufferSize];

        bytesRead = fileInputStream.read(buffer, 0, bufferSize);
        while (bytesRead > 0) {
            outputStream.write(buffer, 0, bufferSize);
            bytesAvailable = fileInputStream.available();
            bufferSize = Math.min(bytesAvailable, MAX_BUFFER_SIZE);
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);
        }
        return fileInputStream;
    }

    private HttpURLConnection getConnection(final String boundary, final URL url) throws IOException {
        HttpURLConnection connection;
        connection = (HttpURLConnection) url.openConnection();

        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setUseCaches(false);

        connection.setRequestMethod("POST");
        connection.setRequestProperty("Connection", "Keep-Alive");
        connection.setRequestProperty("User-Agent", "Android Multipart HTTP Client 1.0");
        connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
        return connection;
    }

}
