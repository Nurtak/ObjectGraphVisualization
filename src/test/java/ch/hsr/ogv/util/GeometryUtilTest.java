package ch.hsr.ogv.util;

import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GeometryUtilTest {

    @Test
    public void testDivideLineFractionHalf() {
        Point3D startPoint = new Point3D(0, 0, 0);
        Point3D endPoint = new Point3D(300, 300, 0);
        Point3D result = GeometryUtil.divideLineFraction(startPoint, endPoint, 1.0 / 2.0);

        Point3D middlePoint = new Point3D(150, 150, 0);
        assertEquals(middlePoint, result);
    }

    @Test
    public void testDivideLineFractionHalf2() {
        Point3D startPoint = new Point3D(0, 0, 0);
        Point3D endPoint = new Point3D(300, 600, 900);
        Point3D result = GeometryUtil.divideLineFraction(startPoint, endPoint, 1.0 / 2.0);

        Point3D middlePoint = new Point3D(150, 300, 450);
        assertEquals(middlePoint, result);
    }

    @Test
    public void testDivideLineFractionHalf3() {
        Point3D startPoint = new Point3D(100, 1000, 2000);
        Point3D endPoint = new Point3D(300, 1600, 2900);
        Point3D result = GeometryUtil.divideLineFraction(startPoint, endPoint, 1.0 / 2.0);

        Point3D middlePoint = new Point3D(200, 1300, 2450);
        assertEquals(middlePoint, result);
    }

    @Test
    public void testDivideLineFractionTrippleFirst() {
        Point3D startPoint = new Point3D(0, 0, 0);
        Point3D endPoint = new Point3D(300, 300, 0);
        Point3D result = GeometryUtil.divideLineFraction(startPoint, endPoint, 1.0 / 3.0);

        Point3D middlePoint = new Point3D(100, 100, 0);
        assertEquals(middlePoint, result);
    }

    @Test
    public void testDivideLineFractionTrippleSecond() {
        Point3D startPoint = new Point3D(0, 0, 0);
        Point3D endPoint = new Point3D(300, 300, 0);
        Point3D result = GeometryUtil.divideLineFraction(startPoint, endPoint, 2.0 / 3.0);

        Point3D middlePoint = new Point3D(200, 200, 0);
        assertEquals(middlePoint, result);
    }

    @Test
    public void testdivideLineFraction2D() {
        Point2D startPoint = new Point2D(0, 0);
        Point2D endPoint = new Point2D(300, 300);
        Point2D result = GeometryUtil.divideLineFraction(startPoint, endPoint, 2.0 / 3.0);

        Point2D middlePoint = new Point2D(200, 200);
        assertEquals(middlePoint, result);
    }

    @Test
    public void testRotateXAngle45() {
        Point3D startPoint = new Point3D(0, 0, 0);
        Point3D endPoint = new Point3D(300, 300, 0);
        double result = GeometryUtil.rotateXAngle(startPoint, endPoint);
        assertEquals(45.0, result, 0.5);
    }

    @Test
    public void testRotateXAngle0() {
        Point3D startPoint = new Point3D(0, 0, 0);
        Point3D endPoint = new Point3D(300, 0, 0);
        double result = GeometryUtil.rotateXAngle(startPoint, endPoint);
        assertEquals(0.0, result, 0.5);
    }

    @Test
    public void testRotateXAngle90() {
        Point3D startPoint = new Point3D(0, 0, 0);
        Point3D endPoint = new Point3D(0, 300, 0);
        double result = GeometryUtil.rotateXAngle(startPoint, endPoint);
        assertEquals(90.0, result, 0.5);
    }

    @Test
    public void testRotateYAngle90() {
        Point3D startPoint = new Point3D(0, 0, 0);
        Point3D endPoint = new Point3D(300, 0, 0);
        double result = GeometryUtil.rotateYAngle(startPoint, endPoint);
        assertEquals(90.0, result, 0.5);
    }

    @Test
    public void testRotateYAngle0() {
        Point3D startPoint = new Point3D(0, 0, 0);
        Point3D endPoint = new Point3D(0, 300, 0);
        double result = GeometryUtil.rotateYAngle(startPoint, endPoint);
        assertEquals(0.0, result, 0.5);
    }

    @Test
    public void testLineIntersect() {
        Point2D firstLineStart = new Point2D(0, 0);
        Point2D firstLineEnd = new Point2D(0, 200);
        Point2D secondLineStart = new Point2D(-100, 100);
        Point2D secondLineEnd = new Point2D(100, 100);
        Point2D intersection = new Point2D(0, 100);
        Point2D result = GeometryUtil.lineIntersect(firstLineStart, firstLineEnd, secondLineStart, secondLineEnd);
        assertEquals(intersection, result);
    }

}
