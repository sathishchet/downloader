package org.saravana.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.saravana.domain.Download;
import org.saravana.producer.DownloadProducer;
import org.saravana.util.URLHandlerFactory;

public class DownloadServiceTest {
	@Mock
	private DownloadProducer mockProducer;
	@Mock
	private URLHandlerFactory mockHandlerFactory;

	private DownloadService downloadService;
	@Captor
	private ArgumentCaptor<Download> captorDownload;

	private static final String REF_URL = "http://example.com/somefile.txt";

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		downloadService = new DownloadServiceImpl();
		downloadService.setProducer(mockProducer);
		downloadService.setHandlerFactory(mockHandlerFactory);
		when(mockHandlerFactory.getHandler(any(String.class))).thenReturn(null);
	}

	@Test
	public void download_end_Test() throws Exception {
		when(mockProducer.submit(captorDownload.capture())).thenReturn(any(Boolean.class));

		downloadService.submitDownload("End");

		verify(mockHandlerFactory, times(0)).getHandler(any(String.class));
		verify(mockProducer).submit(any(Download.class));
		assertEquals("End", captorDownload.getValue().getSpec());
		assertEquals(Download.Status.TERMINATE, captorDownload.getValue().getStatus());

	}

	@Test(expected = NullPointerException.class)
	public void download_null_spec_Test() throws Exception {
		downloadService.submitDownload(null);

		verify(mockHandlerFactory, never()).getHandler(any(String.class));
		verify(mockProducer, never()).submit(any(Download.class));
	}

	@Test
	public void download_error_spec_Test() throws Exception {
		when(mockProducer.submit(captorDownload.capture())).thenReturn(any(Boolean.class));

		downloadService.submitDownload("abchs!@#%%^^&&*(");

		verify(mockHandlerFactory, never()).getHandler(any(String.class));
		verify(mockProducer, times(1)).submit(any(Download.class));
		assertEquals(Download.Status.REJECTED, captorDownload.getValue().getStatus());
	}

	@Test
	public void download_success_spec_Test() throws Exception {
		when(mockProducer.submit(captorDownload.capture())).thenReturn(any(Boolean.class));

		downloadService.submitDownload(REF_URL);

		verify(mockHandlerFactory, times(1)).getHandler(any(String.class));
		verify(mockProducer, times(1)).submit(any(Download.class));
		assertEquals(Download.Status.UNKNOWN, captorDownload.getValue().getStatus());
		assertEquals(REF_URL, captorDownload.getValue().getUrl().toString());
	}

}
