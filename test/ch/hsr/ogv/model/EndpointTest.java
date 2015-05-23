package ch.hsr.ogv.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import javafx.geometry.Point3D;
import javafx.scene.paint.Color;

import org.junit.Test;

public class EndpointTest {

	@Test
	public void testGetFriend() {
		ModelClass modelClassA = new ModelClass("A", new Point3D(0, 0, 0), 100.0, 100.0, Color.BEIGE);
		ModelClass modelClassB = new ModelClass("B", new Point3D(-200, 0, 0), 100.0, 100.0, Color.BEIGE);
		Relation relationAB = new Relation(modelClassA, modelClassB, RelationType.DIRECTED_ASSOCIATION);

		Endpoint endpointA = relationAB.getStart();
		Endpoint endpointB = relationAB.getEnd();

		assertEquals(endpointB, endpointA.getFriend());
		assertEquals(endpointA, endpointB.getFriend());
	}

	@Test
	public void testIsStart() {
		ModelClass modelClassA = new ModelClass("A", new Point3D(0, 0, 0), 100.0, 100.0, Color.BEIGE);
		ModelClass modelClassB = new ModelClass("B", new Point3D(-200, 0, 0), 100.0, 100.0, Color.BEIGE);
		Relation relationAB = new Relation(modelClassA, modelClassB, RelationType.DIRECTED_ASSOCIATION);

		Endpoint endpointA = relationAB.getStart();

		assertTrue(endpointA.isStart());
	}

	@Test
	public void testIsEnd() {
		ModelClass modelClassA = new ModelClass("A", new Point3D(0, 0, 0), 100.0, 100.0, Color.BEIGE);
		ModelClass modelClassB = new ModelClass("B", new Point3D(-200, 0, 0), 100.0, 100.0, Color.BEIGE);
		Relation relationAB = new Relation(modelClassA, modelClassB, RelationType.DIRECTED_ASSOCIATION);

		Endpoint endpointB = relationAB.getEnd();

		assertTrue(endpointB.isEnd());
	}

	@Test
	public void testGetAppendant() {
		ModelClass modelClassA = new ModelClass("A", new Point3D(0, 0, 0), 100.0, 100.0, Color.BEIGE);
		ModelClass modelClassB = new ModelClass("B", new Point3D(-200, 0, 0), 100.0, 100.0, Color.BEIGE);
		Relation relationAB = new Relation(modelClassA, modelClassB, RelationType.DIRECTED_ASSOCIATION);

		Endpoint endpointA = relationAB.getStart();
		Endpoint endpointB = relationAB.getEnd();

		assertEquals(modelClassA, endpointA.getAppendant());
		assertEquals(modelClassB, endpointB.getAppendant());
	}
}
