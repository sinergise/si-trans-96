package com.sinergise.geometry.crs.sitrans96;

import java.util.Arrays;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.triangulate.IncrementalDelaunayTriangulator;
import org.locationtech.jts.triangulate.quadedge.QuadEdge;
import org.locationtech.jts.triangulate.quadedge.QuadEdgeSubdivision;
import org.locationtech.jts.triangulate.quadedge.TriangleVisitor;

public class TriangleProvider {
	QuadEdgeSubdivision delaunay;
	TriangularTransformationPoint[] points;

	public TriangleProvider(TriangularTransformationPoint[] pointData) {
		this.points = pointData;
		this.delaunay = buildTriangles(pointData);
	}

	public TriangleProvider inverse() {
		TriangularTransformationPoint[] invPoints = new TriangularTransformationPoint[points.length];
		for (int i = 0; i < invPoints.length; i++) {
			invPoints[i] = points[i].reverse();
		}
		return new TriangleProvider(invPoints);
	}

	public TriangularTransformationTriangle getTriangleAt(Coordinate pos) {
		QuadEdge e = delaunay.locate(pos);
		if (e != null) {
			TriangularTransformationTriangle data = (TriangularTransformationTriangle) e.getData();
			if (data != null) {
				return data;
			}
		}
		throw new IllegalArgumentException("Coordinate out of bounds " + pos + " ENV: " + delaunay.getEnvelope());
	}

	private static final QuadEdgeSubdivision buildTriangles(TriangularTransformationPoint[] points) {
		QuadEdgeSubdivision subdiv = new QuadEdgeSubdivision(aggregateEnvelope(points), 0);

		IncrementalDelaunayTriangulator triangulator = new IncrementalDelaunayTriangulator(subdiv);
		triangulator.insertSites(Arrays.asList(points));

		buildTriangleObjects(subdiv);

		return subdiv;
	}

	private static Envelope aggregateEnvelope(TriangularTransformationPoint[] points) {
		Envelope e = new Envelope();
		for (TriangularTransformationPoint tp : points) {
			e.expandToInclude(tp.src());
		}
		return e;
	}

	private static void buildTriangleObjects(QuadEdgeSubdivision subdiv) {
		TriangleVisitor triVisitor = new TriangleVisitor() {
			public void visit(QuadEdge[] triEdges) {
				TriangularTransformationTriangle tri = new TriangularTransformationTriangle(//
						(TriangularTransformationPoint) triEdges[0].orig(),
						(TriangularTransformationPoint) triEdges[1].orig(),
						(TriangularTransformationPoint) triEdges[2].orig());
				triEdges[0].setData(tri);
				triEdges[1].setData(tri);
				triEdges[2].setData(tri);
			}
		};
		subdiv.visitTriangles(triVisitor, false);
	}
}
