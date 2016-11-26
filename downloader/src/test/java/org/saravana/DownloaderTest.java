package org.saravana;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.saravana.domain.Download.Status;
import org.saravana.monitor.DownloadMonitor;
import org.saravana.service.DownloadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class DownloaderTest {

	@Autowired
	DownloadService service;

	@Autowired
	DownloadMonitor monitor;

	@Test
	public void testInvalidScheme() {
		List<String> schemes = Arrays.asList("abc://some");
		for (String scheme : schemes) {
			service.submitDownload(scheme);
		}
		Assert.assertEquals(Status.REJECTED, monitor.getAll().get(0).getStatus());
	}

}
