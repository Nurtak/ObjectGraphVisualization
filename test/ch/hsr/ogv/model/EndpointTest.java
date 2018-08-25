package ch.hsr.ogv.model;

import javafx.geometry.Point3D;
import javafx.scene.paint.Color;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EndpointTest {

	private ModelClass modelClassA;
	private ModelClass modelClassB;
	private Relation relationAB;

	@BeforeEach
	public void setUp() throws Exception {
		modelClassA = new ModelClass("A", new Point3D(0, 0, 0), 100.0, 100.0, Color.BEIGE);
		modelClassB = new ModelClass("B", new Point3D(-200, 0, 0), 100.0, 100.0, Color.BEIGE);
		relationAB = new Relation(modelClassA, modelClassB, RelationType.DIRECTED_ASSOCIATION);
	}

	@Test
	public void testGetFriend() {
		Endpoint endpointA = relationAB.getStart();
		Endpoint endpointB = relationAB.getEnd();
		assertEquals(endpointB, endpointA.getFriend());
		assertEquals(endpointA, endpointB.getFriend());
	}

	@Test
	public void testIsStart() {
		Endpoint endpointA = relationAB.getStart();
		assertTrue(endpointA.isStart());
	}

	@Test
	public void testIsEnd() {
		Endpoint endpointB = relationAB.getEnd();
		assertTrue(endpointB.isEnd());
	}

	@Test
	public void testGetAppendant() {
		Endpoint endpointA = relationAB.getStart();
		Endpoint endpointB = relationAB.getEnd();
		assertEquals(modelClassA, endpointA.getAppendant());
		assertEquals(modelClassB, endpointB.getAppendant());
	}

	@Test
	public void testUniqueID() {
		Endpoint endpointA = relationAB.getStart();
		Endpoint endpointB = relationAB.getEnd();
		assertNotEquals(endpointB.getUniqueID(), endpointA.getUniqueID());
	}

}
