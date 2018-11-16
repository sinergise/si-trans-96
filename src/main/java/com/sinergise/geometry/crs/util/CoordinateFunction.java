package com.sinergise.geometry.crs.util;

public interface CoordinateFunction<R> {
	R apply(double x, double y);
}