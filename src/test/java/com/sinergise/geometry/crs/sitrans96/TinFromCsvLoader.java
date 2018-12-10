package com.sinergise.geometry.crs.sitrans96;

import static java.lang.Double.parseDouble;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;

import org.locationtech.jts.geom.Coordinate;

import com.sinergise.geometry.crs.util.Util;

public class TinFromCsvLoader {
	private static final String FNAME_TRANS_POINTS = "GK2TM_VVT4.csv";
	
	public static void main(String[] args) throws IOException {
		TriangularTransformationPoint[] pts = loadPointDataFromCSVFile();
		storePointData(pts, new File(args[0]));
	}

	private static void storePointData(TriangularTransformationPoint[] pts, File file) throws IOException {
		FileOutputStream fos = new FileOutputStream(file);
		DataOutputStream dos = new DataOutputStream(fos);
		try {
			for (TriangularTransformationPoint p : pts) {
				Coordinate src = p.src();
				dos.writeDouble(src.x);
				dos.writeDouble(src.y);
				Coordinate tgt = p.tgt();
				dos.writeDouble(tgt.x);
				dos.writeDouble(tgt.y);
			}
		} finally {
			Util.closeSilent(dos, fos);
		}
	}

	private static TriangularTransformationPoint[] loadPointDataFromCSVFile() {
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
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			Util.closeSilent(rdr, is);
		}
	}

	private static TriangularTransformationPoint readPointFromLine(String curLine) throws IOException {
		String[] parts = curLine.trim().split("\\s+");
	
		double gkE = parseDouble(parts[3]);
		double gkN = parseDouble(parts[4]);
		double d96e = parseDouble(parts[1]);
		double d96n = parseDouble(parts[2]);
		return new TriangularTransformationPoint(new Coordinate(gkE, gkN), new Coordinate(d96e, d96n));
	}

}
