package com.sinergise.geometry.crs.sitrans96;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Assert;
import org.junit.Test;
import org.locationtech.jts.algorithm.RobustDeterminant;
import org.locationtech.jts.geom.Coordinate;

import com.sinergise.geometry.crs.util.AffineTransform2D;

public class TestTriangularTransformer {
	/** point is CCW from p1-p2 */
	public static final int EXT_PTLINE_T_LEFT = 1;
	/** point is CW from p1-p2 */
	public static final int EXT_PTLINE_T_RIGHT = 2;
	
	public static final boolean contains(final TriangularTransformationTriangle tr, final Coordinate pos) {
		return triangleContains(tr.a.src(), tr.b.src(), tr.c.src(), pos);
	}

	public static final boolean triangleContains(Coordinate a, Coordinate b, Coordinate c, Coordinate pos) {
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

	/**
	 * @param dx  x2 - x1
	 * @param dy  y2 - y1
	 * @param dx2 x - x2
	 * @param dy2 y - y2
	 * 
	 * @return bit mask of EXT_PTLINE_T_LEFT and EXT_PTLINE_T_RIGHT
	 */
	public static final int pointLinePositionT(final double dx, final double dy, final double dx2, final double dy2)
	{
		final int leftT = RobustDeterminant.signOfDet2x2(dx, dy, dx2, dy2);
		return (leftT <= 0 ? EXT_PTLINE_T_LEFT : 0) | (leftT >= 0 ? EXT_PTLINE_T_RIGHT : 0);
	}

	
	@Test
	public void testGetTriangle() {
		TriangleProvider tt = SiTrans96.TIN_D48GK;
		for (int i = 0; i < 1000; i++) {
			Coordinate pos = randomCoord();
			TriangularTransformationTriangle tr = tt.getTriangleAt(pos);
			assertTrue("Point " + pos + " should be inside "+tr, contains(tr,pos));
		}
	}

	private Coordinate randomCoord() {
		return new Coordinate(300e3 + 200e3 * Math.random(), 200e3 * Math.random());
	}

	
	@Test
	public void testOnMeasuredPoints() {
		double[][] gkArr = {{529115.88, 93981.53}, {409017.67, 64039.28}, {400000, 150000}, {383743.089, 129366.187}};
		double[][] d96Arr = {{528745.469, 94466.309}, {408645.261, 64525.973},{399628.860, 150488.592}, {383371.444, 129854.667}};
		
		for (int i = 0; i < gkArr.length; i++) {
			doTestKnownPoint(gkArr[i][0], gkArr[i][1], d96Arr[i][0], d96Arr[i][1]);
		}
	}

	private void doTestKnownPoint(double x0, double y0, double x1, double y1) {
		Coordinate src = new Coordinate(x0, y0);
		Coordinate expRet = new Coordinate(x1, y1);
		assertTransform(src, expRet, 0.001);
	}

	private void assertTransform(Coordinate gk, Coordinate d96expected, double deltaDist) {
		Coordinate d96actual = SiTrans96.d48gk_to_d96tm(gk.x, gk.y);
		assertEquals(d96expected, d96actual, deltaDist);
		
		Coordinate d48trans = SiTrans96.d96tm_to_d48gk(d96actual.x, d96actual.y);
		assertEquals(gk, roundCoord(d48trans, deltaDist), deltaDist);
	
		Coordinate d48trans2 = SiTrans96.d96tm_to_d48gk(d96expected.x, d96expected.y);
		assertEquals(gk, roundCoord(d48trans2, deltaDist), deltaDist);
	}

	private Coordinate roundCoord(Coordinate d48trans, double deltaDist) {
		long rndX = Math.round(d48trans.x / deltaDist);
		long rndY = Math.round(d48trans.y / deltaDist);
		return new Coordinate(rndX * deltaDist, rndY * deltaDist);
	}

	private void assertEquals(Coordinate expected, Coordinate actual, double deltaDist) {
		Assert.assertEquals("Expected " + expected + ", but was " + actual, expected.x, actual.x, deltaDist);
		Assert.assertEquals("Expected " + expected + ", but was " + actual, expected.y, actual.y, deltaDist);
	}

	
	
	@Test
	public void testTriangleTransform() {
		TriangleProvider tt = SiTrans96.TIN_D48GK;
		for (int i = 0; i < 1000; i++) {
			Coordinate pos = randomCoord();
			TriangularTransformationTriangle tr = tt.getTriangleAt(pos);
			AffineTransform2D at = tr.affine;
			doTestAffine(at, tr.a.src(), tr.a.tgt(), 1e-6);
			doTestAffine(at, tr.b.src(), tr.b.tgt(), 1e-6);
			doTestAffine(at, tr.c.src(), tr.c.tgt(), 1e-6);
			assertTrue("Point " + pos + " should be inside "+tr, contains(tr,pos));
		}
	}

	
	private void doTestAffine(AffineTransform2D at, Coordinate src, Coordinate tgt, double deltaDist) {
		assertEquals(tgt, at.transformPoint(src), deltaDist);
	}

	
	
	@Test
	public void testTriangleVertices() {
		TriangleProvider tt = SiTrans96.TIN_D48GK;
		for (int i = 0; i < 1000; i++) {
			doTestVertices(tt.getTriangleAt(randomCoord()));
		}
	}


	private void doTestVertices(TriangularTransformationTriangle tr) {
		assertTransform(tr.a.src(), tr.a.tgt(), 1e-7);
		assertTransform(tr.b.src(), tr.b.tgt(), 1e-7);
		assertTransform(tr.c.src(), tr.c.tgt(), 1e-7);
		
		assertTrue(contains(tr,tr.a.src()));
		assertTrue(contains(tr,tr.b.src()));
		assertTrue(contains(tr,tr.c.src()));

		// Contains center
		double x = (tr.a.getX() + tr.b.getX() + tr.c.getX())/3;
		double y = (tr.a.getY() + tr.b.getY() + tr.c.getY())/3;
		assertTrue(contains(tr,new Coordinate(x, y)));

		// Contains midPoints
		x = (tr.a.getX() + tr.b.getX() + tr.c.getX()*0.01)/2.01;
		y = (tr.a.getY() + tr.b.getY() + tr.c.getY()*0.01)/2.01;
		assertTrue(tr +" "+x+","+y, contains(tr,new Coordinate(x, y)));

		// Contains midPoints
		x = (tr.c.getX() + tr.b.getX()+ tr.a.getX()*0.01)/2.01;
		y = (tr.c.getY() + tr.b.getY()+ tr.a.getY()*0.01)/2.01;
		assertTrue(contains(tr,new Coordinate(x, y)));

		// Contains midPoints
		x = (tr.a.getX() + tr.c.getX()+ tr.b.getX()*0.01)/2.01;
		y = (tr.a.getY() + tr.c.getY()+ tr.b.getY()*0.01)/2.01;
		assertTrue(contains(tr,new Coordinate(x, y)));

		
		//Doesn't contain 
		x = (tr.a.getX() + tr.b.getX() - tr.c.getX())/3;
		y = (tr.a.getY() + tr.b.getY() - tr.c.getY())/3;
		assertFalse(contains(tr,new Coordinate(x, y)));

		//Doesn't contain 
		x = (tr.a.getX() - tr.b.getX() + tr.c.getX())/3;
		y = (tr.a.getY() - tr.b.getY() + tr.c.getY())/3;
		assertFalse(contains(tr,new Coordinate(x, y)));

		//Doesn't contain 
		x = (- tr.a.getX() + tr.b.getX() + tr.c.getX())/3;
		y = (- tr.a.getY() + tr.b.getY() + tr.c.getY())/3;
		assertFalse(contains(tr,new Coordinate(x, y)));
	}
	
	@Test
	public void testOutOfBounds() {
		try {
			SiTrans96.TIN_D48GK.getTriangleAt(new Coordinate(1200000, 0));
			Assert.fail("Should throw exception on error");
		} catch (RuntimeException e) {
			// PASS
		}
		
		try {
			SiTrans96.TIN_D48GK.getTriangleAt(new Coordinate(12000000, -1200000));
			Assert.fail("Should throw exception on error");
		} catch (RuntimeException e) {
			// PASS
		}
	}
}
