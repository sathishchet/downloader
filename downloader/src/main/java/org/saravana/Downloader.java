package org.saravana;

import java.util.Arrays;
import java.util.List;

import org.saravana.domain.Download;
import org.saravana.domain.Download.Status;
import org.saravana.monitor.DownloadMonitor;
import org.saravana.service.DownloadService;
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
		final String format = "%-3s %-25s %-10s %-55s %-100s\n";
		System.out.printf(format, "NO", "LOCATION", "STATUS", "URL", "MESSAGE");
		System.out.println(
				"------------------------------------------------------------------------------------------------------------------------------------------------------");
		for (Download ds : monitor.getAll()) {
			System.out.printf(format, ++in + "", ds.getLocation(), ds.getStatus(), ds.getSpec(), ds.getMessage());
		}
		System.out.println(
				"------------------------------------------------------------------------------------------------------------------------------------------------------");
	}

	@Scheduled(initialDelay = 15000, fixedDelay = 10000)
	public void stop() {
		List<Status> notDone = Arrays.asList(Status.STARTED, Status.DOWNLOADING, Status.UNKNOWN);
		long unProc = monitor.getAll().stream().map(Download::getStatus).filter(notDone::contains).count();
		if (monitor.getAll().size() == total && unProc == 0) {
			SpringApplication.exit(context);
		}
	}
}
