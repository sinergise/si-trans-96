package com.sinergise.geometry.crs.util;

import org.locationtech.jts.geom.Coordinate;

public final class AffineTransform2D implements SimpleTransform {
	final double m0;
	final double m1;
	final double m2;
	final double m3;
	final double m4;
	final double m5;

	public AffineTransform2D(double m0, double m1, double m2, double m3, double m4, double m5) {
		this.m0 = m0;
		this.m1 = m1;
		this.m2 = m2;
		this.m3 = m3;
		this.m4 = m4;
		this.m5 = m5;
	}

	public Coordinate transformPoint(Coordinate input) {
		return new Coordinate(x(input.x, input.y), y(input.x, input.y));
	}

	public double x(final double x, final double y) {
		return m0 * x + m2 * y + m4;
	}

	public double y(final double x, final double y) {
		return m1 * x + m3 * y + m5;
	}
}
