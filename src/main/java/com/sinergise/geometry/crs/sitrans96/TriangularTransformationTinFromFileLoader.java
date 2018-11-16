package com.sinergise.geometry.crs.sitrans96;

import static java.lang.Double.parseDouble;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;

import org.locationtech.jts.geom.Coordinate;

import com.sinergise.geometry.crs.util.Util;

public class TriangularTransformationTinFromFileLoader {
	private static final String FNAME_TRANS_POINTS = "/GK2TM_VVT4.csv";

	public static TriangleFromTinProvider loadTin() {
		return new TriangleFromTinProvider(loadPointDataFromFile());
	}

	private static TriangularTransformationPoint[] loadPointDataFromFile() {
		ArrayList<TriangularTransformationPoint> ret = new ArrayList<TriangularTransformationPoint>(1000);

		LineNumberReader rdr = null;
		InputStream is = null;
		try {
			is = TriangularTransformationTinFromFileLoader.class.getResourceAsStream(FNAME_TRANS_POINTS);
			rdr = new LineNumberReader(new InputStreamReader(is));

			String curLine;
			while ((curLine = rdr.readLine()) != null) {
				ret.add(readPointFromLine(curLine));
			}

			return ret.toArray(new TriangularTransformationPoint[ret.size()]);
		} catch(IOException e) {
			throw new RuntimeException(e);
		} finally {
			Util.closeSilent(rdr, is);
		}
	}

	private static TriangularTransformationPoint readPointFromLine(String curLine) {
		String[] parts = curLine.trim().split("\\s+");

		double gkE = parseDouble(parts[3]);
		double gkN = parseDouble(parts[4]);
		double d96e = parseDouble(parts[1]);
		double d96n = parseDouble(parts[2]);
		return new TriangularTransformationPoint(new Coordinate(gkE, gkN), new Coordinate(d96e, d96n));
	}
}
