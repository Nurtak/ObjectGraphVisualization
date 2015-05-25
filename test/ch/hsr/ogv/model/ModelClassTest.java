package ch.hsr.ogv.model;

import static org.junit.Assert.*;
import javafx.geometry.Point3D;
import javafx.scene.paint.Color;

import org.junit.Test;

import ch.hsr.ogv.util.MultiplicityParser;

public class ModelClassTest {
	
	@Test
	public void testGetXYZ() {
		ModelClass modelClassA = new ModelClass("A", new Point3D(100, 200, 300), 100.0, 100.0, Color.BEIGE);
		assertEquals(100, modelClassA.getX(), 0.0);
		assertEquals(200, modelClassA.getY(), 0.0);
		assertEquals(300, modelClassA.getZ(), 0.0);
	}
	
	@Test
	public void testGetFriends() {
		ModelClass modelClassA = new ModelClass("A", new Point3D(0, 0, 0), 100.0, 100.0, Color.BEIGE);
		ModelClass modelClassB = new ModelClass("B", new Point3D(-200, 0, 0), 100.0, 100.0, Color.BEIGE);
		ModelClass modelClassC = new ModelClass("C", new Point3D(-400, 0, 0), 100.0, 100.0, Color.BEIGE);
		Relation relationAB = new Relation(modelClassA, modelClassB, RelationType.DIRECTED_ASSOCIATION);
		Relation relationAC = new Relation(modelClassA, modelClassC, RelationType.DIRECTED_ASSOCIATION);
		
		
		assertEquals(2, modelClassA.getFriends().size());
		assertEquals(200, modelClassA.getY(), 0.0);
		assertEquals(300, modelClassA.getZ(), 0.0);
	}
}
