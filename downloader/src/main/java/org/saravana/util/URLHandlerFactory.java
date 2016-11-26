package org.saravana.util;

import java.net.URLStreamHandler;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class URLHandlerFactory {

	@Value("#{'${url.stream.handlers}'.split(',')}")
	private Set<String> urlHandlers;

	private Map<String, String> handlerMap;

	@PostConstruct
	public void init() {
		handlerMap = new HashMap<>();
		for (String urlHandler : urlHandlers) {
			String[] arr = urlHandler.split(":");
			handlerMap.put(arr[0], arr[1]);
		}
	}

	public URLStreamHandler getHandler(String scheme) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		if ("http".equals(scheme) || "https".equals(scheme) || "ftp".equals(scheme) || "file".equals(scheme))
			return null;
		String claz = handlerMap.get(scheme);
		if (claz == null) {
			throw new IllegalArgumentException("handler for scheme " + scheme + " not found");
		}
		return (URLStreamHandler) Class.forName(claz).newInstance();

	}

	@PreDestroy
	public void destroy() {
		handlerMap.clear();
	}
}
