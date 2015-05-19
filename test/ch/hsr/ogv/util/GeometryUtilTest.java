package ch.hsr.ogv.util;

import static org.junit.Assert.assertEquals;
import javafx.geometry.Point3D;

import org.junit.Test;

public class GeometryUtilTest {

	@Test
	public void testDivideLineFractionHalf() {
		Point3D startPoint = new Point3D(0, 0, 0);
		Point3D endPoint = new Point3D(300, 300, 0);
		Point3D result = GeometryUtil.divideLineFraction(startPoint, endPoint, 1.0/2.0);

		Point3D middlePoint = new Point3D(150, 150, 0);
		assertEquals(middlePoint, result);
	}

	@Test
	public void testDivideLineFractionHalf2() {
		Point3D startPoint = new Point3D(0, 0, 0);
		Point3D endPoint = new Point3D(300, 600, 900);
		Point3D result = GeometryUtil.divideLineFraction(startPoint, endPoint, 1.0/2.0);

		Point3D middlePoint = new Point3D(150, 300, 450);
		assertEquals(middlePoint, result);
	}

	@Test
	public void testDivideLineFractionHalf3() {
		Point3D startPoint = new Point3D(100, 1000, 2000);
		Point3D endPoint = new Point3D(300, 1600, 2900);
		Point3D result = GeometryUtil.divideLineFraction(startPoint, endPoint, 1.0/2.0);

		Point3D middlePoint = new Point3D(200, 1300, 2450);
		assertEquals(middlePoint, result);
	}

	@Test
	public void testDivideLineFractionTrippleFirst() {
		Point3D startPoint = new Point3D(0, 0, 0);
		Point3D endPoint = new Point3D(300, 300, 0);
		Point3D result = GeometryUtil.divideLineFraction(startPoint, endPoint, 1.0/3.0);

		Point3D middlePoint = new Point3D(100, 100, 0);
		assertEquals(middlePoint, result);
	}

	@Test
	public void testDivideLineFractionTrippleSecond() {
		Point3D startPoint = new Point3D(0, 0, 0);
		Point3D endPoint = new Point3D(300, 300, 0);
		Point3D result = GeometryUtil.divideLineFraction(startPoint, endPoint, 2.0/3.0);

		Point3D middlePoint = new Point3D(200, 200, 0);
		assertEquals(middlePoint, result);
	}



}
