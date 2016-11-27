package org.saravana.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.RETURNS_SMART_NULLS;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ DownloadTask.class, IOUtils.class })
public class DownloadTaskTest {

	private DownloadTask downloadTask;
	private Download download;

	private static final String REF_URL = "http://example.com/somefile.txt";
	private static final String LOCAL_PATH = "/tmp/";
	private URL mockURL;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		mockURL = new URL(REF_URL);
		download = new Download(REF_URL);
		download.setPath(LOCAL_PATH);

	}

	@Ignore
	public void process_download_success_Test() {
		Download spyDownload = spy(download);
		downloadTask = new DownloadTask(spyDownload);
		doReturn(mockURL).when(spyDownload).getUrl();
		PowerMockito.mockStatic(IOUtils.class);
		try {
			PowerMockito.when(IOUtils.copyLarge(any(InputStream.class), any(OutputStream.class)))
					.thenAnswer(RETURNS_SMART_NULLS);
		} catch (IOException e) {
			fail("IOUtils test error");
		}
		downloadTask.getTask();

		assertNotNull(spyDownload);
		assertEquals(Download.Status.COMPLETED, spyDownload.getStatus());
	}

	@Test
	public void process_download_fail_Test() {
		Download spyDownload = spy(download);
		downloadTask = new DownloadTask(spyDownload);
		doReturn(mockURL).when(spyDownload).getUrl();

		downloadTask.getTask();

		assertNotNull(spyDownload);
		assertEquals(Download.Status.FAILED, spyDownload.getStatus());
	}
}
