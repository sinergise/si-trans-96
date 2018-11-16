package com.sinergise.geometry.crs.sitrans96;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Assert;
import org.junit.Test;
import org.locationtech.jts.geom.Coordinate;

import com.sinergise.geometry.crs.util.AffineTransform2D;

public class TestTriangularTransformer {
	
	@Test
	public void testRefPoint() {
		Coordinate src = new Coordinate(400000, 150000);
		Coordinate expRet = new Coordinate(399628.860, 150488.592);
		assertTransform(src, expRet, 0.005);
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
			assertTrue("Point " + pos + " should be inside "+tr, tr.contains(pos));
		}
	}

	@Test
	public void testOnMeasuredPoints() {
		double[][] gkArr = {{529115.88, 93981.53}, {409017.67, 64039.28}};
		double[][] d96Arr = {{528745.447, 94466.311}, {408645.299, 64526.048}};
		
		for (int i = 0; i < gkArr.length; i++) {
			Coordinate gk = new Coordinate(gkArr[i][0], gkArr[i][1]);
			Coordinate d96expected = new Coordinate(d96Arr[i][0], d96Arr[i][1]);
			
			Coordinate d96actual = SiTrans96.d48gk_to_d96tm(gk.x, gk.y);
			assertEquals(d96expected, d96actual, 0.08);
		}
	}


	@Test
	public void testGetTriangle() {
		TriangleProvider tt = SiTrans96.TIN_D48GK;
		for (int i = 0; i < 1000; i++) {
			Coordinate pos = randomCoord();
			TriangularTransformationTriangle tr = tt.getTriangleAt(pos);
			assertTrue("Point " + pos + " should be inside "+tr, tr.contains(pos));
		}
	}


	@Test
	public void testTriangleVertices() {
		TriangleProvider tt = SiTrans96.TIN_D48GK;
		for (int i = 0; i < 1000; i++) {
			doTestVertices(tt.getTriangleAt(randomCoord()));
		}
	}


	private static void assertTransform(Coordinate gk, Coordinate d96expected, double deltaDist) {
		Coordinate d96actual = SiTrans96.d48gk_to_d96tm(gk.x, gk.y);
		assertEquals(d96expected, d96actual, deltaDist);
		
		Coordinate d48trans = SiTrans96.d96tm_to_d48gk(d96actual.x, d96actual.y);
		assertEquals(gk, d48trans, deltaDist);

		Coordinate d48trans2 = SiTrans96.d96tm_to_d48gk(d96expected.x, d96expected.y);
		assertEquals(gk, d48trans2, deltaDist);
	}

	
	private static void assertEquals(Coordinate expected, Coordinate actual, double deltaDist) {
		Assert.assertEquals("Expected " + expected + ", but was " + actual, expected.x, actual.x, deltaDist);
		Assert.assertEquals("Expected " + expected + ", but was " + actual, expected.y, actual.y, deltaDist);
	}
	
	private static void doTestAffine(AffineTransform2D at, Coordinate src, Coordinate tgt, double deltaDist) {
		assertEquals(tgt, at.transformPoint(src), deltaDist);
	}

	private void doTestVertices(TriangularTransformationTriangle tr) {
		assertTransform(tr.a.src(), tr.a.tgt(), 1e-6);
		assertTransform(tr.b.src(), tr.b.tgt(), 1e-6);
		assertTransform(tr.c.src(), tr.c.tgt(), 1e-6);
		
		assertTrue(tr.contains(tr.a.src()));
		assertTrue(tr.contains(tr.b.src()));
		assertTrue(tr.contains(tr.c.src()));

		// Contains center
		double x = (tr.a.getX() + tr.b.getX() + tr.c.getX())/3;
		double y = (tr.a.getY() + tr.b.getY() + tr.c.getY())/3;
		assertTrue(tr.contains(new Coordinate(x, y)));

		// Contains midPoints
		x = (tr.a.getX() + tr.b.getX() + tr.c.getX()*0.01)/2.01;
		y = (tr.a.getY() + tr.b.getY() + tr.c.getY()*0.01)/2.01;
		assertTrue(tr +" "+x+","+y, tr.contains(new Coordinate(x, y)));

		// Contains midPoints
		x = (tr.c.getX() + tr.b.getX()+ tr.a.getX()*0.01)/2.01;
		y = (tr.c.getY() + tr.b.getY()+ tr.a.getY()*0.01)/2.01;
		assertTrue(tr.contains(new Coordinate(x, y)));

		// Contains midPoints
		x = (tr.a.getX() + tr.c.getX()+ tr.b.getX()*0.01)/2.01;
		y = (tr.a.getY() + tr.c.getY()+ tr.b.getY()*0.01)/2.01;
		assertTrue(tr.contains(new Coordinate(x, y)));

		
		//Doesn't contain 
		x = (tr.a.getX() + tr.b.getX() - tr.c.getX())/3;
		y = (tr.a.getY() + tr.b.getY() - tr.c.getY())/3;
		assertFalse(tr.contains(new Coordinate(x, y)));

		//Doesn't contain 
		x = (tr.a.getX() - tr.b.getX() + tr.c.getX())/3;
		y = (tr.a.getY() - tr.b.getY() + tr.c.getY())/3;
		assertFalse(tr.contains(new Coordinate(x, y)));

		//Doesn't contain 
		x = (- tr.a.getX() + tr.b.getX() + tr.c.getX())/3;
		y = (- tr.a.getY() + tr.b.getY() + tr.c.getY())/3;
		assertFalse(tr.contains(new Coordinate(x, y)));
	}


	public static Coordinate randomCoord() {
		return new Coordinate(300e3 + 200e3 * Math.random(), 200e3 * Math.random());
	}
}
