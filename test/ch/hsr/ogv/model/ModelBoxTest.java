package ch.hsr.ogv.model;

import static org.junit.Assert.assertEquals;
import javafx.geometry.Point3D;
import javafx.scene.paint.Color;

import org.junit.Before;
import org.junit.Test;

public class ModelBoxTest {

	private ModelBox mb;

	@Before
	public void setUp() throws Exception {
		mb = new ModelBox("", new Point3D(-200, 200, 0), 100, 100, Color.BEIGE);
	}

	@Test
	public void testSetCoordinates() {
		Point3D newPoint = new Point3D(123, 234, 345);
		mb.setCoordinates(newPoint);
		assertEquals(newPoint, mb.getCoordinates());
	}

	@Test
	public void testSetX() {
		double newX = 123;
		mb.setX(newX);
		assertEquals(new Point3D(123, 200, 0), mb.getCoordinates());
	}

	@Test
	public void testSetY() {
		double newY = 123;
		mb.setY(newY);
		assertEquals(new Point3D(-200, 123, 0), mb.getCoordinates());
	}

	@Test
	public void testSetZ() {
		double newZ = 123;
		mb.setZ(newZ);
		assertEquals(new Point3D(-200, 200, 123), mb.getCoordinates());
	}

	@Test
	public void testSetWidth() {
		double newWidth = 123;
		mb.setWidth(123);
		assertEquals(newWidth, mb.getWidth(), 0.0);
	}

	@Test
	public void testSetHeight() {
		double newHeight = 123;
		mb.setHeight(123);
		assertEquals(newHeight, mb.getHeight(), 0.0);
	}

	@Test
	public void testSetColor() {
		Color newColor = Color.BLUE;
		mb.setColor(newColor);
		assertEquals(newColor, mb.getColor());
	}
}
