package org.saravana;

import static org.junit.Assert.fail;
import static org.mockito.AdditionalMatchers.or;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.saravana.domain.Download;
import org.saravana.monitor.DownloadMonitor;
import org.saravana.service.DownloadService;

public class DownloaderAppTest {

	@Mock
	private DownloadService mockDownloadService;
	@Mock
	private DownloadMonitor mockMonitor;
	@Spy
	private Downloader spyDownloader = new Downloader();

	private static final String REF_URL_1 = "http://example.com/somefile.txt";
	private static final String REF_URL_2 = "http://example.com/somefile2.txt";

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		spyDownloader.setDownloadService(mockDownloadService);
		spyDownloader.setMonitor(mockMonitor);
	}

	@Test
	public void run_Test() {
		try {
			spyDownloader.run(REF_URL_1, REF_URL_2);
		} catch (Exception e) {
			fail("Unexpected Exception thrown" + e.getMessage());
		}

		verify(mockDownloadService, times(3)).submitDownload(any(String.class));
		verify(mockDownloadService, times(3)).submitDownload(any(String.class));
		verify(mockDownloadService, times(2)).submitDownload(or(eq(REF_URL_1), eq(REF_URL_2)));
		verify(mockDownloadService, times(1)).submitDownload(eq("End"));
	}

	@Test
	public void monitor_Test() {
		List<Download> downloads = new ArrayList<>();
		downloads.add(new Download(REF_URL_1));
		downloads.add(new Download(REF_URL_2));
		when(mockMonitor.getAll()).thenReturn(downloads);

		spyDownloader.monitor();
		verify(mockMonitor).getAll();
	}

	@Test
	public void stop_Test() {
		List<Download> downloads = new ArrayList<>();
		downloads.add(new Download(REF_URL_1));
		downloads.add(new Download(REF_URL_2));
		when(mockMonitor.getAll()).thenReturn(downloads);

		spyDownloader.stop();
		verify(mockMonitor, times(2)).getAll();
	}

}
