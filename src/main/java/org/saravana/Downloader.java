package org.saravana;

import java.util.Arrays;
import java.util.List;

import org.saravana.domain.Download;
import org.saravana.domain.Download.Status;
import org.saravana.monitor.DownloadMonitor;
import org.saravana.service.DownloadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@SpringBootApplication
@EnableScheduling
public class Downloader implements CommandLineRunner {

	private static final Logger LOG = LoggerFactory.getLogger(Downloader.class);
	@Autowired
	private DownloadService downloadService;

	@Autowired
	private DownloadMonitor monitor;

	@Autowired
	private ApplicationContext context;

	public static void main(String[] args) {
		SpringApplication.run(Downloader.class, args);
	}

	private int total;

	@Override
	public void run(String... args) throws Exception {
		this.total = args.length;
		for (String url : args) {
			downloadService.submitDownload(url);
		}
		downloadService.submitDownload("End");
	}

	@Scheduled(fixedDelay = 10000)
	public void monitor() {
		Integer in = 0;
		final String format = "%-3s %-25s %-10s %-55s %-100s";
		LOG.info(String.format(format, "NO", "LOCATION", "STATUS", "URL", "MESSAGE"));
		LOG.info(String.format(
				"------------------------------------------------------------------------------------------------------------------------------------------------------"));
		for (Download ds : monitor.getAll()) {
			LOG.info(String.format(format, ++in + "", ds.getLocation(), ds.getStatus(), ds.getSpec(), ds.getMessage()));
		}
		LOG.info(String.format(
				"------------------------------------------------------------------------------------------------------------------------------------------------------"));
	}

	@Scheduled(initialDelay = 15000, fixedDelay = 10000)
	public void stop() {
		List<Status> notDone = Arrays.asList(Status.DOWNLOADING, Status.UNKNOWN);
		long unProc = monitor.getAll().stream().map(Download::getStatus).filter(notDone::contains).count();
		if (monitor.getAll().size() == total && unProc == 0) {
			SpringApplication.exit(context);
		}
	}

	/**
	 * unit-testing
	 * 
	 * @param downloadService
	 */
	protected void setDownloadService(DownloadService downloadService) {
		this.downloadService = downloadService;
	}

	/**
	 * unit-testing
	 * 
	 * @param monitor
	 */
	protected void setMonitor(DownloadMonitor monitor) {
		this.monitor = monitor;
	}

}
