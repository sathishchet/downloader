package org.saravana.service;

import org.saravana.producer.DownloadProducer;
import org.saravana.util.URLHandlerFactory;

public interface DownloadService {

	boolean submitDownload(String url);

	public DownloadProducer getProducer();

	public void setProducer(DownloadProducer producer);

	public URLHandlerFactory getHandlerFactory();

	public void setHandlerFactory(URLHandlerFactory handlerFactory);
}
