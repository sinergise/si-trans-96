package com.sinergise.geometry.crs.sitrans96;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import org.locationtech.jts.geom.Coordinate;

import com.sinergise.geometry.crs.util.Util;

public class TriangularTransformationTinFromFileLoader {
	private static final String FNAME_TRANS_POINTS_BIN = "/GK2TM_VVT4.bin";
	private static final int NUM_POINTS = 899;

	public static TriangleProvider loadTin() {
		return new TriangleProvider(loadPointDataFromFile());
	}

	private static TriangularTransformationPoint[] loadPointDataFromFile() {

		try {
			ByteBuffer buf = ByteBuffer.allocate(NUM_POINTS * 4 * 8);

			InputStream is = TriangularTransformationTinFromFileLoader.class.getResourceAsStream(FNAME_TRANS_POINTS_BIN);
			ReadableByteChannel rbc = Channels.newChannel(is);
			try {
				while (rbc.read(buf) > 0) {}
			} finally {
				Util.closeSilent(rbc);
			}
			buf.rewind();

			TriangularTransformationPoint[] ret = new TriangularTransformationPoint[buf.remaining()/4/8];
			DoubleBuffer dbuf = buf.asDoubleBuffer();
			int pos = 0;
			
			while (dbuf.hasRemaining()) {
				ret[pos++] = new TriangularTransformationPoint(//
						new Coordinate(dbuf.get(), dbuf.get()), //
						new Coordinate(dbuf.get(), dbuf.get()));
			}
			return ret;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
