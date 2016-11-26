package org.saravana.domain;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;
import org.saravana.domain.Download.Status;

public class DownloadTask implements Runnable {

	private Download download;

	public DownloadTask(Download download) {
		this.download = download;
	}

	@Override
	public void run() {
		Runnable task = () -> {
			String fullPath = download.getUrl().getFile();
			File file = new File(download.getPath(), fullPath.substring(fullPath.lastIndexOf('/')));
			download.setLocation(file.getAbsolutePath());
			OutputStream os = null;
			try {
				if (file.exists())
					file.delete();
				download.setStatus(Status.DOWNLOADING);
				os = new FileOutputStream(file);
				IOUtils.copyLarge(download.getUrl().openStream(), os);
				IOUtils.closeQuietly(os);
				download.setStatus(Status.COMPLETED);
				download.setMessage("Done");
			} catch (IOException e) {
				if (file.exists())
					file.delete();
				download.setStatus(Status.FAILED);
				download.setMessage(e.getMessage());
			}
		};

		try {
			new Thread(task).start();
		} catch (Exception e) {
			download.setStatus(Status.FAILED);
			download.setMessage(e.getMessage());
		}

	}
}
