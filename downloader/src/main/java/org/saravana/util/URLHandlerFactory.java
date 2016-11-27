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
	private Map<String, Object> cache;

	@PostConstruct
	public void init() {
		cache = new HashMap<>();
		handlerMap = new HashMap<>();
		for (String urlHandler : urlHandlers) {
			String[] arr = urlHandler.split(":");
			handlerMap.put(arr[0], arr[1]);
		}
	}

	public URLStreamHandler getHandler(String scheme)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		if ("http".equals(scheme) || "https".equals(scheme) || "ftp".equals(scheme) || "file".equals(scheme))
			return null;
		String claz = handlerMap.get(scheme);
		if (claz == null) {
			throw new IllegalArgumentException("handler for scheme " + scheme + " not found");
		}
		return (URLStreamHandler) getHandlerCache(claz);
	}

	private URLStreamHandler getHandlerCache(String claz)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		Object handler = null;
		if (null == (handler = cache.get(claz))) {
			handler = Class.forName(claz).newInstance();
			cache.put(claz, handler);
		}
		return (URLStreamHandler) handler;
	}

	@PreDestroy
	public void destroy() {
		handlerMap.clear();
	}

	/**
	 * Method added for unit-testing
	 * 
	 * @param urlHandlers
	 */
	public void setUrlHandlers(Set<String> urlHandlers) {
		this.urlHandlers = urlHandlers;
		init();
	}
}
