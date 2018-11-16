package com.sinergise.geometry.crs.sitrans96;

import org.locationtech.jts.geom.Coordinate;

import com.sinergise.geometry.crs.util.SimpleTransform;

/**
 * S. Berk in Ž. Komadina (2010). Trikotniško zasnovana transformacija med
 * starim in novim državnim koordinatnim sistemom Slovenije. Geografski
 * informacijski sistemi v Sloveniji 2009–2010. GIS v Sloveniji, št. 10. Založba
 * ZRC, Ljubljana, str. 291–299.
 * <p>
 * The official triangular transform = affine transform withing a triangle:
 * <ul>
 * <li>Determine triangle</li>
 * <li>Determine affine transform using triangle vertices as ideally converted
 * points</li>
 * <li>Use the transform on the point</li>
 * </ul>
 * 
 */
public class SiTrans96 {

	public static class D48gkToD96tmTriangular implements SimpleTransform {
		public Coordinate transformPoint(Coordinate pos) {
			return TIN_D48GK.getTriangleAt(pos).transformPoint(pos);
		}
	}

	public static class D96tmToD48gkTriangular implements SimpleTransform {
		public Coordinate transformPoint(Coordinate pos) {
			return TIN_D96TM.getTriangleAt(pos).transformPoint(pos);
		}
	}

	static final TriangleProvider TIN_D48GK;
	static final TriangleProvider TIN_D96TM;
	static {
		TriangleFromTinProvider srcProvider = TriangularTransformationTinFromFileLoader.loadTin();
		TIN_D48GK = new CachingTriangleProvider(srcProvider);
		TIN_D96TM = new CachingTriangleProvider(srcProvider.inverse());
	}

	public static final SimpleTransform D48GK_TO_D96TM_TRIANGULAR = new D48gkToD96tmTriangular();
	public static final SimpleTransform D96TM_TO_D48GK_TRIANGULAR = new D96tmToD48gkTriangular();

	public static Coordinate d48gk_to_d96tm(double gkE, double gkN) {
		return d48gk_to_d96tm(new Coordinate(gkE, gkN));
	}

	public static Coordinate d96tm_to_d48gk(double d96X, double d96Y) {
		return d96tm_to_d48gk(new Coordinate(d96X, d96Y));
	}

	public static Coordinate d48gk_to_d96tm(Coordinate pos) {
		return D48GK_TO_D96TM_TRIANGULAR.transformPoint(pos);
	}

	public static Coordinate d96tm_to_d48gk(Coordinate pos) {
		return D96TM_TO_D48GK_TRIANGULAR.transformPoint(pos);
	}
	
	public static void main(String[] args) {
		System.out.println(d48gk_to_d96tm(350394, 142349));
	}
}
