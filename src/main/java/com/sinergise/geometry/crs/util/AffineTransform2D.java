package com.sinergise.geometry.crs.util;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.math.Matrix;

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

	public static AffineTransform2D triangleAffine(Coordinate src1, Coordinate tgt1, Coordinate src2, Coordinate tgt2, Coordinate src3, Coordinate tgt3) {
		double sx1 = src1.x;
		double sy1 = src1.y;
		double sx2 = src2.x;
		double sy2 = src2.y;
		double sx3 = src3.x;
		double sy3 = src3.y;
	
		double tx1 = tgt1.x;
		double ty1 = tgt1.y;
		double tx2 = tgt2.x;
		double ty2 = tgt2.y;
		double tx3 = tgt3.x;
		double ty3 = tgt3.y;
	
		
		double sumSx2 = sx1 * sx1 + sx2 * sx2 + sx3 * sx3;
		double sumSy2 = sy1 * sy1 + sy2 * sy2 + sy3 * sy3;
		double sumSxy = sx1 * sy1 + sx2 * sy2 + sx3 * sy3;
		double sumSx = sx1 + sx2 + sx3;
		double sumSy = sy1 + sy2 + sy3;
		double[][] Ax = new double[][] {
			{sumSx2, sumSxy, sumSx},
			{sumSxy, sumSy2, sumSy},
			{sumSx, sumSy, 3}
		};
		double[][] Ay = new double[3][3];
		for (int i = 0; i < 3; i++) {
			System.arraycopy(Ax[i], 0, Ay[i], 0, 3);
		}
		double[] Bxx = new double[] {
			tx1 * sx1 + tx2 * sx2 + tx3 * sx3,
			tx1 * sy1 + tx2 * sy2 + tx3 * sy3,
			tx1 + tx2 + tx3
		};
		double[] Bxy = new double[] {
			ty1 * sx1 + ty2 * sx2 + ty3 * sx3,
			ty1 * sy1 + ty2 * sy2 + ty3 * sy3,
			ty1 + ty2 + ty3
		};
		double[] mx = Matrix.solve(Ax, Bxx);
		double[] my = Matrix.solve(Ay, Bxy);
		return new AffineTransform2D(mx[0], my[0], mx[1], my[1], mx[2], my[2]);
	}
}
