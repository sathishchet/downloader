package org.saravana.queue;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;

import javax.annotation.PreDestroy;

import org.saravana.domain.Download;
import org.springframework.stereotype.Service;

@Service
public class DownloadQueue {

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
			e.printStackTrace();
		}
		return status;
	}

	public Download take() {
		try {
			return queue.take();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return null;
	}

	@PreDestroy
	public void destroy() {
		queue.clear();
	}

}
