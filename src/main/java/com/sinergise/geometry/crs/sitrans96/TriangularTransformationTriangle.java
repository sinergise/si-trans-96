package com.sinergise.geometry.crs.sitrans96;

import org.locationtech.jts.geom.Coordinate;

import com.sinergise.geometry.crs.util.AffineTransform2D;
import com.sinergise.geometry.crs.util.GeomUtil;
import com.sinergise.geometry.crs.util.SimpleTransform;

public class TriangularTransformationTriangle implements SimpleTransform {
	final TriangularTransformationPoint a;
	final TriangularTransformationPoint b;
	final TriangularTransformationPoint c;
	
	final AffineTransform2D affine;
	
	public TriangularTransformationTriangle(TriangularTransformationPoint a, TriangularTransformationPoint b, TriangularTransformationPoint c) {
		this(a, b, c, GeomUtil.triangleAffine(//
				a.src(), a.tgt(), //
				b.src(), b.tgt(), //
				c.src(), c.tgt())
			);
		
	}
	TriangularTransformationTriangle(TriangularTransformationPoint a, TriangularTransformationPoint b, TriangularTransformationPoint c, AffineTransform2D affine) {
		this.a = a;
		this.b = b;
		this.c = c;
		this.affine = affine;
	}

	public boolean contains(final Coordinate pos) {
		return GeomUtil.triangleContains(a.src(), b.src(), c.src(), pos);
	}
	
	public Coordinate transformPoint(Coordinate input) {
		return affine.transformPoint(input);
	}
}