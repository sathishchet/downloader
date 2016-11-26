package org.saravana.producer;

import org.saravana.domain.Download;
import org.saravana.queue.DownloadQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DownloadProducerImpl implements DownloadProducer {

	@Autowired
	private DownloadQueue queue;

	@Override
	public boolean submit(Download download) {
		return queue.put(download);
	}

}
