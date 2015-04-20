package ch.hsr.ogv.util;

import javafx.geometry.Point2D;
import javafx.geometry.Point3D;

public class GeometryUtil {

	public static Point2D lineIntersect(Point2D firstLineStart, Point2D firstLineEnd, Point2D secondLineStart, Point2D secondLineEnd) {
		double x1 = firstLineStart.getX(), y1 = firstLineStart.getY(), x2 = firstLineEnd.getX(), y2 = firstLineEnd.getY(), x3 = secondLineStart.getX(), y3 = secondLineStart.getY(), x4 = secondLineEnd
				.getX(), y4 = secondLineEnd.getY();
		return GeometryUtil.lineIntersect(x1, y1, x2, y2, x3, y3, x4, y4);
	}

	public static Point2D lineIntersect(double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4) {
		double denom = (y4 - y3) * (x2 - x1) - (x4 - x3) * (y2 - y1);
		if (denom == 0.0) { // Lines are parallel.
			return null;
		}
		double ua = ((x4 - x3) * (y1 - y3) - (y4 - y3) * (x1 - x3)) / denom;
		double ub = ((x2 - x1) * (y1 - y3) - (y2 - y1) * (x1 - x3)) / denom;
		if (ua >= 0.0 && ua <= 1.0 && ub >= 0.0 && ub <= 1.0) {
			// Get the intersection point.
			return new Point2D(x1 + ua * (x2 - x1), y1 + ua * (y2 - y1));
		}
		return null;
	}

	public static double rotateZAngle(Point3D p1, Point3D p2) {
		double xDiff = p2.getX() - p1.getX();
		double zDiff = p2.getZ() - p1.getZ();
		return Math.toDegrees(Math.atan2(xDiff, zDiff));
	}

	public static double rotateXAngle(Point3D p1, Point3D p2) {
		double height = p2.getY() - p1.getY();
		double hypothenuse = p1.distance(p2);
		if (hypothenuse == 0.0) {
			return 0.0;
		}
		return Math.toDegrees(Math.asin(height / hypothenuse));
	}

	public static Point2D divideLineFraction(Point2D start, Point2D end, double fraction) {
		double x = start.getX() + fraction * (end.getX() - start.getX());
		double y = start.getY() + fraction * (end.getY() - start.getY());
		return new Point2D(x, y);
	}

	public static Point3D divideLineFraction(Point3D start, Point3D end, double fraction) {
		double x = start.getX() + fraction * (end.getX() - start.getX());
		double y = start.getY() + fraction * (end.getY() - start.getY());
		double z = start.getZ() + fraction * (end.getZ() - start.getZ());
		return new Point3D(x, y, z);
	}

}
