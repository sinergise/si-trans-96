package com.sinergise.geometry.crs.util;

import java.io.Closeable;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Util {
	private static final Logger logger = LoggerFactory.getLogger(Util.class); 
	
	public static final void close(Closeable resource) throws IOException {
		if (resource != null) {
			resource.close();
		}
	}
	
	public static final void closeSilent(Closeable... resources) {
		for (Closeable r : resources) {
			try {
				close(r);
			} catch (Throwable t) {
				// be quiet
				logger.warn("Error while closing resource {}", r, t);
			}
		}
	}
}
