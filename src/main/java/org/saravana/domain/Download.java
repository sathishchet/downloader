package org.saravana.domain;

import java.net.URL;

public class Download {

	private URL url;

	private String path;

	private String location = "-";

	private Status status = Status.UNKNOWN;

	private String spec;

	private String message = "";

	public Download(String spec) {
		this.spec = spec;
	}

	public Download(URL url, String path) {
		this.url = url;
		this.path = path;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getSpec() {
		return spec;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public void setSpec(String spec) {
		this.spec = spec;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public void setUrl(URL url) {
		this.url = url;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getLocation() {
		return location;
	}

	public String getPath() {
		return path;
	}

	public Status getStatus() {
		return status;
	}

	public URL getUrl() {
		return url;
	}

	public enum Status {
		UNKNOWN, DOWNLOADING, COMPLETED, FAILED, REJECTED, TERMINATE
	}

}
