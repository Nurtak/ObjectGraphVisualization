package ch.hsr.ogv.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import javafx.geometry.Point3D;
import javafx.scene.paint.Color;

import org.junit.Test;

public class RelationTest {

	@Test
	public void testGetFriend() {
		ModelClass modelClassA = new ModelClass("A", new Point3D(0, 0, 0), 100.0, 100.0, Color.BEIGE);
		ModelClass modelClassB = new ModelClass("B", new Point3D(-200, 0, 0), 100.0, 100.0, Color.BEIGE);
		Relation relationAB = new Relation(modelClassA, modelClassB, RelationType.DIRECTED_ASSOCIATION);

		Endpoint endpointA = relationAB.getStart();
		Endpoint endpointB = relationAB.getEnd();

		assertEquals(endpointB, relationAB.getFriend(endpointA));
		assertEquals(endpointA, relationAB.getFriend(endpointB));
	}

	@Test
	public void testIsStart() {
		ModelClass modelClassA = new ModelClass("A", new Point3D(0, 0, 0), 100.0, 100.0, Color.BEIGE);
		ModelClass modelClassB = new ModelClass("B", new Point3D(-200, 0, 0), 100.0, 100.0, Color.BEIGE);
		Relation relationAB = new Relation(modelClassA, modelClassB, RelationType.DIRECTED_ASSOCIATION);

		Endpoint endpointA = relationAB.getStart();

		assertTrue(relationAB.isStart(endpointA));
	}

	@Test
	public void testIsEnd() {
		ModelClass modelClassA = new ModelClass("A", new Point3D(0, 0, 0), 100.0, 100.0, Color.BEIGE);
		ModelClass modelClassB = new ModelClass("B", new Point3D(-200, 0, 0), 100.0, 100.0, Color.BEIGE);
		Relation relationAB = new Relation(modelClassA, modelClassB, RelationType.DIRECTED_ASSOCIATION);

		Endpoint endpointB = relationAB.getEnd();

		assertTrue(relationAB.isEnd(endpointB));
	}

	@Test
	public void testChangeDirection() {
		ModelClass modelClassA = new ModelClass("A", new Point3D(0, 0, 0), 100.0, 100.0, Color.BEIGE);
		ModelClass modelClassB = new ModelClass("B", new Point3D(-200, 0, 0), 100.0, 100.0, Color.BEIGE);
		Relation relationAB = new Relation(modelClassA, modelClassB, RelationType.DIRECTED_ASSOCIATION);

		Endpoint endpointA = relationAB.getStart();
		Endpoint endpointB = relationAB.getEnd();

		relationAB.changeDirection();

		assertEquals(endpointB, relationAB.getStart());
		assertEquals(endpointA, relationAB.getEnd());

		assertEquals(endpointB, modelClassA.getEndpoints().get(0));
		assertEquals(endpointA, modelClassB.getEndpoints().get(0));

		assertEquals(endpointB, relationAB.getFriend(endpointA));
		assertEquals(endpointA, relationAB.getFriend(endpointB));
	}

}
