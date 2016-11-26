package org.saravana.service;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLStreamHandler;

import org.saravana.domain.Download;
import org.saravana.domain.Download.Status;
import org.saravana.producer.DownloadProducer;
import org.saravana.util.URLHandlerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DownloadServiceImpl implements DownloadService {

	private static final Logger LOG = LoggerFactory.getLogger(DownloadServiceImpl.class);

	@Autowired
	private DownloadProducer producer;

	@Autowired
	private URLHandlerFactory handlerFactory;

	@Override
	public boolean submitDownload(String spec) {
		Download download = new Download(spec);
		if ("End".equals(spec)) {
			download.setStatus(Status.TERMINATE);
		} else {
			URI uri = null;
			try {
				uri = new URI(spec);
			} catch (URISyntaxException e) {
				download.setMessage(e.getMessage());
				download.setStatus(Status.REJECTED);
				LOG.error("Exception while getting parsing " + e);
			}
			String scheme = uri.getScheme();
			try {
				URLStreamHandler handler = handlerFactory.getHandler(scheme);
				URL url = new URL(null, spec, handler);
				download.setUrl(url);
			} catch (MalformedURLException | InstantiationException | IllegalAccessException | ClassNotFoundException | IllegalArgumentException e) {
				download.setMessage(e.getMessage());
				download.setStatus(Status.REJECTED);
				LOG.error("Exception while getting handler " + e);
			}
		}
		return producer.submit(download);
	}

}
