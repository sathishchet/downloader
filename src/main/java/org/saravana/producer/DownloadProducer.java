package org.saravana.producer;

import org.saravana.domain.Download;
import org.saravana.queue.DownloadQueue;

public interface DownloadProducer {

	boolean submit(Download url);

	public DownloadQueue getQueue();

	public void setQueue(DownloadQueue queue);
}
