package com.vagm.vagmdroid.dto;

import java.io.File;
import java.util.Map;

public class HttpPostDTO {

	final String urlTo;
	final Map<String, String> parmas;
	final File file;
	final String fileMimeType;

	public HttpPostDTO(String urlTo, Map<String, String> parmas, File file,
			String fileMimeType) {
		this.urlTo = urlTo;
		this.parmas = parmas;
		this.file = file;
		this.fileMimeType = fileMimeType;
	}

	public String getUrlTo() {
		return urlTo;
	}

	public Map<String, String> getParmas() {
		return parmas;
	}

	public File getFile() {
		return file;
	}

	public String getFileMimeType() {
		return fileMimeType;
	}

}
