package com.sinergise.geometry.crs.sitrans96;

import static java.lang.Math.random;
import static org.junit.Assert.assertTrue;

import java.text.DecimalFormat;

import org.junit.Test;
import org.locationtech.jts.geom.Coordinate;

public class TestTriangularTransformerSpeed {

	public static void main(String[] args) {
		new TestTriangularTransformerSpeed().testSpeedLocal();
	}

	@Test
	public void testSpeedLocal() {
		testSpeed(1, 500000000);
		testSpeed(100, 50000);
		testSpeed(1000, 50000);

		testSpeed(1, 500);
		testSpeed(2, 500);
		testSpeed(10, 500);
		testSpeed(50, 500);
		testSpeed(100, 500);
		testSpeed(200, 1000);
		testSpeed(500, 1000);
		testSpeed(1000, 2000);
		testSpeed(2000, 3000);
		testSpeed(5000, 5000);
		testSpeed(10000, 6000);
		testSpeed(20000, 7000);
		testSpeed(50000, 8000);
		testSpeed(100000, 9000);
		System.out.println("===============================");

		testSpeed(1, 500);
		testSpeed(2, 500);
		testSpeed(10, 500);
		testSpeed(50, 500);
		testSpeed(100, 500);
		testSpeed(200, 1000);
		testSpeed(500, 1000);
		testSpeed(1000, 2000);
		testSpeed(2000, 3000);
		testSpeed(5000, 5000);
		testSpeed(10000, 6000);
		testSpeed(20000, 7000);
		testSpeed(50000, 8000);
		testSpeed(100000, 9000);
	}

	private static void testSpeed(double ptDist, int expectedNanos) {
		final int cnt = Math.max(1000, 500000000/expectedNanos);
		final Coordinate[] dta = preparePoints(ptDist, cnt);
		double sum = 0; //need to do something with result or JIT might eliminate the computation

		final long t0 = System.nanoTime();
		for (int i = 0; i < cnt; i++) {
			sum += trans(dta[i]);
		}
		long dt = System.nanoTime() - t0;

		long nanosPerPoint = dt/cnt;
		assertTrue("Should complete under "+expectedNanos+". Was " + nanosPerPoint + "ns/pt", nanosPerPoint < expectedNanos);
		System.out.println("Point distance " + ptDist + " m :" + formatNanoTime(nanosPerPoint) + "/pt "+ (1000000000L/nanosPerPoint) + " pt/s  ["+sum+"]");
	}

	private static Coordinate[] preparePoints(double ptDist, final int cnt) {
		Coordinate[] dta = new Coordinate[cnt];
		double rndX = 500e3;
		double rndY = 150e3;
		for (int i = 0; i < cnt; i++) {
			rndX += ptDist * (2 * random() - 1);
			rndY += ptDist * (2 * random() - 1);
	
			if (rndX > 650e3) {
				rndX -= 150e3;
			} else if (rndX < 350e3) {
				rndX += 150e3;
			}
	
			if (rndY > 300e3) {
				rndY -= 150e3;
			} else if (rndY < 0) {
				rndY += 150e3;
			}
			dta[i] = new Coordinate(rndX, rndY);
		}
		return dta;
	}

	private static double trans(Coordinate pos) {
		Coordinate ret = SiTrans96.d48gk_to_d96tm(pos);
		return ret.x + ret.y; 
	}

	private static String formatNanoTime(long nanos) {
		double amount = nanos;
		String[] prefixes = {"n","µ","m",""};
		int idx = 0;
		while (amount > 1000 && idx < 3) {
			amount /= 1000.0;
			idx++; 
		}
		return DecimalFormat.getInstance().format(amount)+" "+prefixes[idx]+"s";
	}
}
