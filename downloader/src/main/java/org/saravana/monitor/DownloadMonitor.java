package org.saravana.monitor;

import java.util.ArrayList;
import java.util.List;

import org.saravana.domain.Download;
import org.springframework.stereotype.Component;

@Component
public class DownloadMonitor {

	private List<Download> status = new ArrayList<>();

	public void add(Download dstatus) {
		status.add(dstatus);
	}

	public List<Download> getAll() {
		return this.status;
	}

}
