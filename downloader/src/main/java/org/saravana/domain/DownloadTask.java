package org.saravana.domain;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;
import org.saravana.domain.Download.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DownloadTask implements Runnable {

	private static final Logger LOG = LoggerFactory.getLogger(DownloadTask.class);

	private Download download;

	public DownloadTask(Download download) {
		this.download = download;
	}

	@Override
	public void run() {
		Runnable task = () -> {
			getTask();
		};

		try {
			new Thread(task).start();
		} catch (Exception e) {
			download.setStatus(Status.FAILED);
			download.setMessage(e.getMessage());
			LOG.error("Exception while copying ", e);
		}

	}

	protected void getTask() {
		String fullPath = download.getUrl().getFile();
		File file = new File(download.getPath(), fullPath.substring(fullPath.lastIndexOf('/')));
		OutputStream os = null;
		try {
			if (file.exists())
				file.delete();
			download.setStatus(Status.DOWNLOADING);
			os = new FileOutputStream(file);
			IOUtils.copyLarge(download.getUrl().openStream(), os);
			IOUtils.closeQuietly(os);
			download.setLocation(file.getAbsolutePath());
			download.setStatus(Status.COMPLETED);
			download.setMessage("Done");
		} catch (IOException e) {
			if (file.exists())
				file.delete();
			download.setStatus(Status.FAILED);
			download.setMessage(e.getMessage());
			LOG.error("Exception while copying ", e);
		}
	}
}
