/*
 *
 */
package com.sinergise.geometry.crs.util;

import org.locationtech.jts.algorithm.RobustDeterminant;
import org.locationtech.jts.geom.Coordinate;

public class GeomUtil {
	/** point is CCW from p1-p2 */
	public static final int EXT_PTLINE_T_LEFT = 1;
	/** point is CW from p1-p2 */
	public static final int EXT_PTLINE_T_RIGHT = 2;
	
    private GeomUtil() {}
    
	/**
	 * @param dx  x2 - x1
	 * @param dy  y2 - y1
	 * @param dx2 x - x2
	 * @param dy2 y - y2
	 * 
	 * @return bit mask of EXT_PTLINE_T_LEFT and EXT_PTLINE_T_RIGHT
	 */
	public final static int pointLinePositionT(final double dx, final double dy, final double dx2, final double dy2)
	{
		final int leftT = RobustDeterminant.signOfDet2x2(dx, dy, dx2, dy2);
		return (leftT <= 0 ? EXT_PTLINE_T_LEFT : 0) | (leftT >= 0 ? EXT_PTLINE_T_RIGHT : 0);
	}

	public static boolean triangleContains(Coordinate a, Coordinate b, Coordinate c, Coordinate pos) {
		final double xa = a.x;
		final double ya = a.y;
		final double xb = b.x;
		final double yb = b.y;
		final double xc = c.x;
		final double yc = c.y;
		final double x = pos.x;
		final double y = pos.y;
		
		final int posA = pointLinePositionT(xc - xb, yc - yb, x - xc, y - yc); // pos vs. B-C
		final int posB = pointLinePositionT(xa - xc, ya - yc, x - xa, y - ya); // pos vs. C-A
		final int posC = pointLinePositionT(xb - xa, yb - ya, x - xb, y - yb); // pos vs. A-B
		return (posB & posA) != 0 && (posC & posB) != 0;
	}

	public static AffineTransform2D triangleAffine(Coordinate src1, Coordinate tgt1, Coordinate src2, Coordinate tgt2, Coordinate src3, Coordinate tgt3) {
		final double x1 = src1.x;
		final double y1 = src1.y;
		final double x2 = src2.x;
		final double y2 = src2.y;
		final double x3 = src3.x;
		final double y3 = src3.y;
		
		final double u1 = tgt1.x;
		final double v1 = tgt1.y;
		final double u2 = tgt2.x;
		final double v2 = tgt2.y;
		final double u3 = tgt3.x;
		final double v3 = tgt3.y;
		
		
		final double dy3 = y2 - y1;
		final double dy2 = y1 - y3;
		final double dy1 = y3 - y2;
		final double dx3 = x2 - x1;
		final double dx2 = x1 - x3;
		final double dx1 = x3 - x2;
		final double xy3 = x2 * y1 - x1 * y2;
		final double xy2 = x1 * y3 - x3 * y1;
		final double xy1 = x3 * y2 - x2 * y3;

		final double acdfB = x3 * dy3 + x2 * dy2 + x1 * dy1;
		final double beB = y3 * dx3 + y2 * dx2 + y1 * dx1;

		final double aT = u3 * dy3 + u2 * dy2 + u1 * dy1;
		final double bT = u3 * dx3 + u2 * dx2 + u1 * dx1;
		final double cT = u3 * xy3 + u2 * xy2 + u1 * xy1;

		final double dT = v3 * dy3 + v2 * dy2 + v1 * dy1;
		final double eT = v3 * dx3 + v2 * dx2 + v1 * dx1;
		final double fT = v3 * xy3 + v2 * xy2 + v1 * xy1;
		
		// (m0, m2, m4) == (a, b, c)
		// (m1, m3, m5) == (d, e, f)
		return new AffineTransform2D(
			aT/acdfB, dT/acdfB, bT/beB, eT/beB, cT/acdfB, fT/acdfB	
		);
	}
}
 