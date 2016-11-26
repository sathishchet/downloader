package org.saravana.queue;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;

import javax.annotation.PreDestroy;

import org.saravana.domain.Download;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class DownloadQueue {

	private static final Logger LOG = LoggerFactory.getLogger(DownloadQueue.class);

	private BlockingQueue<Download> queue = new SynchronousQueue<Download>(true);

	public boolean add(Download download) {
		return queue.add(download);
	}

	public Download remove() {
		return queue.remove();
	}

	public boolean put(Download download) {
		boolean status = false;
		try {
			queue.put(download);
			status = true;
		} catch (InterruptedException e) {
			LOG.error("Exception while put in queue " + e);
		}
		return status;
	}

	public Download take() {
		try {
			return queue.take();
		} catch (InterruptedException e) {
			LOG.error("Exception while take from queue " + e);
		}
		return null;
	}

	@PreDestroy
	public void destroy() {
		queue.clear();
	}

}
