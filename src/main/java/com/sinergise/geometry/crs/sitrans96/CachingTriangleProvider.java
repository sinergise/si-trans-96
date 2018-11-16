package com.sinergise.geometry.crs.sitrans96;

import org.locationtech.jts.geom.Coordinate;

public final class CachingTriangleProvider implements TriangleProvider {
	private final TriangleProvider delegate;
	private final ThreadLocal<TriangularTransformationTriangle> cached = new ThreadLocal<TriangularTransformationTriangle>();

	public CachingTriangleProvider(TriangleProvider delegate) {
		this.delegate = delegate;
	}

	public final TriangularTransformationTriangle getTriangleAt(Coordinate pos) {
		TriangularTransformationTriangle cacheTr = cached.get();
		if (cacheTr != null && cacheTr.contains(pos)) {
			return cacheTr;
		}
		TriangularTransformationTriangle ret = delegate.getTriangleAt(pos);
		cached.set(ret);
		return ret;
	}
	
}
