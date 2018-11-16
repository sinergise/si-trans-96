package com.sinergise.geometry.crs.sitrans96;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.triangulate.quadedge.Vertex;

public final class TriangularTransformationPoint extends Vertex {
	private final Coordinate tgt;
	
	public TriangularTransformationPoint(Coordinate src, Coordinate tgt) {
		super(src);
		this.tgt = tgt;
	}
	
	public Coordinate tgt() {
		return tgt;
	}
	
	public Coordinate src() {
		return this.getCoordinate();
	}
	
	public TriangularTransformationPoint reverse() {
		return new TriangularTransformationPoint(tgt, src());
	}
}