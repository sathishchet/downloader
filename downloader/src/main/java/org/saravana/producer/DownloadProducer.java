package org.saravana.producer;

import org.saravana.domain.Download;

public interface DownloadProducer {

	boolean submit(Download url);

}
