package org.saravana.consumer;

import org.saravana.monitor.DownloadMonitor;
import org.saravana.queue.DownloadQueue;

public interface DownloadConsumer {

	void destroy();

	void download();

	void init();

	public DownloadQueue getQueue();

	public void setQueue(DownloadQueue queue);

	public DownloadMonitor getDownloadMonitor();

	public void setDownloadMonitor(DownloadMonitor downloadMonitor);
}
