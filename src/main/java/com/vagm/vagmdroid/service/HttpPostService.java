package com.vagm.vagmdroid.service;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * The Class HttpPostService.
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

	/**
	 * LOG.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(HttpPostService.class);
	
	/**
	 * FORM_PARAM.
	 */
	private static final String FORM_PARAM = "log";

	/**
	 * fileService.
	 */
	@Inject
	private FileService fileService;

	/**
	 * Default constructor.
	 */
	public HttpPostService() {
	}

	/**
	 * doMultipartRequest.
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
		HttpURLConnection connection = null;
		DataOutputStream outputStream = null;
		InputStream inputStream = null;

		final String twoHyphens = "--";
		final String boundary = "*****" + Long.toString(System.currentTimeMillis()) + "*****";
		final String lineEnd = "\r\n";

		String result = "";

		int bytesRead, bytesAvailable, bufferSize;
		byte[] buffer;
		final int maxBufferSize = 1 * 1024 * 1024;

		try {
			final FileInputStream fileInputStream = new FileInputStream(file);

			final URL url = new URL(urlTo);
			connection = (HttpURLConnection) url.openConnection();

			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setUseCaches(false);

			connection.setRequestMethod("POST");
			connection.setRequestProperty("Connection", "Keep-Alive");
			connection.setRequestProperty("User-Agent", "Android Multipart HTTP Client 1.0");
			connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

			outputStream = new DataOutputStream(connection.getOutputStream());
			outputStream.writeBytes(twoHyphens + boundary + lineEnd);
			outputStream.writeBytes("Content-Disposition: form-data; name=\"" + FORM_PARAM + "\""
					+ lineEnd);
			outputStream.writeBytes("Content-Type: " + fileMimeType + lineEnd);
			outputStream.writeBytes("Content-Transfer-Encoding: binary" + lineEnd);

			outputStream.writeBytes(lineEnd);

			bytesAvailable = fileInputStream.available();
			bufferSize = Math.min(bytesAvailable, maxBufferSize);
			buffer = new byte[bufferSize];

			bytesRead = fileInputStream.read(buffer, 0, bufferSize);
			while (bytesRead > 0) {
				outputStream.write(buffer, 0, bufferSize);
				bytesAvailable = fileInputStream.available();
				bufferSize = Math.min(bytesAvailable, maxBufferSize);
				bytesRead = fileInputStream.read(buffer, 0, bufferSize);
			}

			outputStream.writeBytes(lineEnd);

			// Upload POST Data
			final Iterator<String> keys = parmas.keySet().iterator();
			while (keys.hasNext()) {
				final String key = keys.next();
				final String value = parmas.get(key);

				outputStream.writeBytes(twoHyphens + boundary + lineEnd);
				outputStream.writeBytes("Content-Disposition: form-data; name=\"" + key + "\"" + lineEnd);
				outputStream.writeBytes("Content-Type: text/plain" + lineEnd);
				outputStream.writeBytes(lineEnd);
				outputStream.writeBytes(value);
				outputStream.writeBytes(lineEnd);
			}

			outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

			if (200 != connection.getResponseCode()) {
				LOG.error("Failed to upload code:" + connection.getResponseCode() + " " + connection.getResponseMessage());
				fileInputStream.close();
				outputStream.close();
				return null;
			}

			inputStream = connection.getInputStream();

			result = fileService.convertStreamToString(inputStream);

			fileInputStream.close();
			inputStream.close();
			outputStream.flush();
			outputStream.close();

			return result;
		} catch (final Exception e) {
			LOG.error(e.getMessage());
			return null;
		}

	}

}
