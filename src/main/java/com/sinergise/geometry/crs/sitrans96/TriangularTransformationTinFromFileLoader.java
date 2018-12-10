package com.sinergise.geometry.crs.sitrans96;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.channels.Channels;
import java.util.ArrayList;

import org.locationtech.jts.geom.Coordinate;

import com.sinergise.geometry.crs.util.Util;

public class TriangularTransformationTinFromFileLoader {
	private static final String FNAME_TRANS_POINTS_BIN = "/GK2TM_VVT4.bin";

	public static TriangleProvider loadTin() {
		return new TriangleProvider(loadPointDataFromFile());
	}

	private static TriangularTransformationPoint[] loadPointDataFromFile() {
		ArrayList<TriangularTransformationPoint> ret = new ArrayList<TriangularTransformationPoint>(1000);
		InputStream is = null;

		try {
			is = TriangularTransformationTinFromFileLoader.class.getResourceAsStream(FNAME_TRANS_POINTS_BIN);
			ByteBuffer buf = ByteBuffer.allocate(is.available());
			Channels.newChannel(is).read(buf);
			buf.rewind();
			DoubleBuffer dbuf = buf.asDoubleBuffer();
			while (dbuf.hasRemaining()) {
				ret.add(new TriangularTransformationPoint(new Coordinate(dbuf.get(), dbuf.get()),
						new Coordinate(dbuf.get(), dbuf.get())));
			}
			return ret.toArray(new TriangularTransformationPoint[ret.size()]);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			Util.closeSilent(is);
		}
	}
}
