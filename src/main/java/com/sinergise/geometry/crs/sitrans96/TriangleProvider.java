package com.sinergise.geometry.crs.sitrans96;

import org.locationtech.jts.geom.Coordinate;

public interface TriangleProvider {
	TriangularTransformationTriangle getTriangleAt(Coordinate pos);
}
