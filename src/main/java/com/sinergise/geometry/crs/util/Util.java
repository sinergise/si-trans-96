package com.sinergise.geometry.crs.util;

import java.io.Closeable;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Util {
	private static Logger logger = Logger.getLogger("com.sinergise.geometry.crs.util.Util");
	
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
				logger.log(Level.WARNING, "Error while closing resource "+r, t);
			}
		}
	}
}
