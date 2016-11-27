package org.saravana.consumer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.saravana.domain.Download;
import org.saravana.domain.Download.Status;
import org.saravana.monitor.DownloadMonitor;
import org.saravana.queue.DownloadQueue;

public class DownloadConsumerTest {

	@Spy
	private DownloadConsumerImpl spyDownloadConsumer = new DownloadConsumerImpl();
	@Spy
	private DownloadQueue mockQueue = new DownloadQueue();
	@Mock
	private DownloadMonitor mockDownloadMonitor;
	@Captor
	private ArgumentCaptor<Download> captorDownload;

	private static final String REF_URL = "http://example.com/somefile.txt";

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		spyDownloadConsumer.init();
		spyDownloadConsumer.setQueue(mockQueue);
		spyDownloadConsumer.setDownloadMonitor(mockDownloadMonitor);
	}

	@Test
	public void terminated_download_Test() {
		final Download download = new Download(REF_URL);
		download.setStatus(Status.TERMINATE);
		// add task to the blocking queue after 1 sec
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					Thread.sleep(5000);
					mockQueue.add(download);
				} catch (InterruptedException e) {
					fail("Interrupted");
				}

			}
		}).start();

		spyDownloadConsumer.getTask(false);

		verify(mockDownloadMonitor, times(0)).add(any(Download.class));
		verify(spyDownloadConsumer, times(1)).destroy();
		assertEquals(Status.TERMINATE, download.getStatus());
	}

	@Test
	public void running_download_Test() {
		final Download download = new Download(REF_URL);
		download.setStatus(Status.UNKNOWN);
		final Download download2 = new Download(REF_URL);
		download2.setStatus(Status.TERMINATE);
		// add task to the blocking queue after 1 sec
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					Thread.sleep(1000);
					mockQueue.add(download);
					Thread.sleep(1000);
					mockQueue.add(download2);
				} catch (InterruptedException e) {
					fail("Interrupted");
				}

			}
		}).start();
		// mock status from Status.UNKNOWN to Status.DOWNLOADED
		doAnswer(new Answer<Void>() {

			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				captorDownload.getValue().setStatus(Status.COMPLETED);
				return null;
			}

		}).when(spyDownloadConsumer).submitTask(captorDownload.capture());

		spyDownloadConsumer.getTask(false);

		verify(mockDownloadMonitor, times(1)).add(any(Download.class));
		verify(spyDownloadConsumer, times(1)).destroy();
		verify(spyDownloadConsumer, times(1)).submitTask(any(Download.class));
		// UNKNOWN -> COMPLETED
		assertEquals(Status.COMPLETED, download.getStatus());
		assertEquals(Status.TERMINATE, download2.getStatus());
	}

	@Test
	public void rejected_download_Test() {
		final Download download = new Download(REF_URL);
		download.setStatus(Status.REJECTED);
		final Download download2 = new Download(REF_URL);
		download2.setStatus(Status.TERMINATE);
		// add task to the blocking queue after 1 sec
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					Thread.sleep(1000);
					mockQueue.add(download);
					Thread.sleep(1000);
					mockQueue.add(download2);
				} catch (InterruptedException e) {
					fail("Interrupted");
				}

			}
		}).start();

		spyDownloadConsumer.getTask(false);

		verify(mockDownloadMonitor, times(1)).add(any(Download.class));
		verify(spyDownloadConsumer, times(1)).destroy();
		verify(spyDownloadConsumer, never()).submitTask(any(Download.class));
		assertEquals(Status.REJECTED, download.getStatus());
		assertEquals(Status.TERMINATE, download2.getStatus());
	}

}
