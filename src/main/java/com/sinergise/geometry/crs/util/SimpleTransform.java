package com.sinergise.geometry.crs.util;

import org.locationtech.jts.geom.Coordinate;

public interface SimpleTransform {
	Coordinate transformPoint(Coordinate input);
}