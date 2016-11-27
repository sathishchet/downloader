package org.saravana.consumer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.saravana.domain.Download;
import org.saravana.domain.Download.Status;
import org.saravana.domain.DownloadTask;
import org.saravana.monitor.DownloadMonitor;
import org.saravana.queue.DownloadQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class DownloadConsumerImpl implements DownloadConsumer {

	@Value("${max.exec.threads}")
	private int nThreads;

	@Value("${download.location}")
	private String path;

	@Autowired
	private DownloadQueue queue;

	@Autowired
	private DownloadMonitor downloadMonitor;

	private ExecutorService execService;

	@Override
	@PostConstruct
	public void init() {
		execService = Executors
				.newFixedThreadPool(nThreads <= 0 ? Runtime.getRuntime().availableProcessors() : nThreads);
	}

	private volatile boolean isRunning;

	@Override
	@PostConstruct
	public void download() {
		new Thread(() -> getTask(isRunning)).start();
	}

	protected void getTask(boolean isRunning) {
		while (!isRunning) {
			Download download = queue.take();
			if (download.getStatus() == Status.TERMINATE) {
				isRunning = !isRunning;
				destroy();
				return;
			}
			download.setPath(path);
			this.downloadMonitor.add(download);
			if (download.getStatus() != Status.REJECTED)
				submitTask(download);
		}
	}

	protected void submitTask(Download download) {
		this.execService.submit(new DownloadTask(download));
	}

	@Override
	@PreDestroy
	public void destroy() {
		execService.shutdown();
	}

	public DownloadQueue getQueue() {
		return queue;
	}

	public void setQueue(DownloadQueue queue) {
		this.queue = queue;
	}

	public DownloadMonitor getDownloadMonitor() {
		return downloadMonitor;
	}

	public void setDownloadMonitor(DownloadMonitor downloadMonitor) {
		this.downloadMonitor = downloadMonitor;
	}

}
