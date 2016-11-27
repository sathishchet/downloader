package org.saravana.producer;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.saravana.domain.Download;
import org.saravana.queue.DownloadQueue;

public class DownloadProducerTest {
	private DownloadProducer downloadProducer;
	@Mock
	private DownloadQueue downloadQueue;
	@Mock
	private Download download;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		downloadProducer = new DownloadProducerImpl();
		downloadProducer.setQueue(downloadQueue);
	}

	@Test
	public void producer_submit_test() {
		downloadProducer.submit(download);
		verify(downloadQueue, times(1)).put(any(Download.class));
	}
}
