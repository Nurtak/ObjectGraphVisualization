package ch.hsr.ogv.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import javafx.geometry.Point3D;
import javafx.scene.paint.Color;

import org.junit.Test;

public class ModelManagerTest {

	@Test
	public void testCreateClass() {
		ModelManager mm = new ModelManager();
		assertTrue(mm.getClasses().isEmpty());
		ModelClass modelClassA = mm.createClass(new Point3D(0, 0, 0), 100, 100, Color.BEIGE);
		assertTrue(mm.getClasses().contains(modelClassA));
		assertEquals(1, mm.getClasses().size());
		ModelClass modelClassB = mm.createClass(new Point3D(-200, 0, 0), 100, 100, Color.BEIGE);
		assertTrue(mm.getClasses().contains(modelClassA));
		assertTrue(mm.getClasses().contains(modelClassB));
		assertEquals(2, mm.getClasses().size());
	}

	@Test
	public void testCreateObject() {
		ModelManager mm = new ModelManager();
		ModelClass modelClassA = mm.createClass(new Point3D(0, 0, 0), 100, 100, Color.BEIGE);
		assertTrue(modelClassA.getModelObjects().isEmpty());
		ModelObject modelObjectA1 = mm.createObject(modelClassA);
		assertEquals(modelObjectA1, modelClassA.getModelObjects().get(0));
		assertEquals(1, modelClassA.getModelObjects().size());
		ModelObject modelObjectA2 = mm.createObject(modelClassA);
		assertEquals(modelObjectA1, modelClassA.getModelObjects().get(0));
		assertEquals(modelObjectA2, modelClassA.getModelObjects().get(1));
		assertEquals(2, modelClassA.getModelObjects().size());
	}

	@Test
	public void testCreateRelation() {
		ModelManager mm = new ModelManager();
		assertTrue(mm.getRelations().isEmpty());
		ModelClass modelClassA = mm.createClass(new Point3D(0, 0, 0), 100, 100, Color.BEIGE);
		ModelClass modelClassB = mm.createClass(new Point3D(-200, 0, 0), 100, 100, Color.BEIGE);
		Relation relationAB1 = mm.createRelation(modelClassA, modelClassB, RelationType.DIRECTED_ASSOCIATION, Color.BLACK);
		assertTrue(mm.getRelations().contains(relationAB1));
		assertEquals(1, mm.getRelations().size());
		Relation relationAB2 = mm.createRelation(modelClassA, modelClassB, RelationType.DIRECTED_ASSOCIATION, Color.BLACK);
		assertTrue(mm.getRelations().contains(relationAB1));
		assertTrue(mm.getRelations().contains(relationAB2));
		assertEquals(2, mm.getRelations().size());
	}

	@Test
	public void testClearClasses() {
		ModelManager mm = new ModelManager();
		mm.createClass(new Point3D(0, 0, 0), 100, 100, Color.BEIGE);
		mm.createClass(new Point3D(-200, 0, 0), 100, 100, Color.BEIGE);
		mm.clearClasses();
		assertTrue(mm.getClasses().isEmpty());
		mm.createClass(new Point3D(-400, 0, 0), 100, 100, Color.BEIGE);
		assertTrue(!mm.getClasses().isEmpty());
		mm.clearClasses();
		assertTrue(mm.getClasses().isEmpty());
	}


	@Test
	public void testDeleteClass() {
		ModelManager mm = new ModelManager();
		ModelClass modelClassA = mm.createClass(new Point3D(0, 0, 0), 100, 100, Color.BEIGE);
		mm.deleteClass(modelClassA);
		assertTrue(!mm.getClasses().contains(modelClassA));
		ModelClass modelClassB = mm.createClass(new Point3D(-200, 0, 0), 100, 100, Color.BEIGE);
		ModelClass modelClassC = mm.createClass(new Point3D(-400, 0, 0), 100, 100, Color.BEIGE);
		assertTrue(mm.getClasses().contains(modelClassB));
		assertTrue(mm.getClasses().contains(modelClassC));
		mm.deleteClass(modelClassB);
		assertTrue(!mm.getClasses().contains(modelClassB));
		assertTrue(mm.getClasses().contains(modelClassC));
	}

	@Test
	public void testDeleteClassWithRelation() {
		ModelManager mm = new ModelManager();
		ModelClass modelClassA = mm.createClass(new Point3D(0, 0, 0), 100, 100, Color.BEIGE);
		ModelClass modelClassB = mm.createClass(new Point3D(-200, 0, 0), 100, 100, Color.BEIGE);
		Relation relationAB1 = mm.createRelation(modelClassA, modelClassB, RelationType.DIRECTED_ASSOCIATION, Color.BLACK);
		mm.deleteClass(modelClassB);
		assertTrue(!mm.getRelations().contains(relationAB1));
	}

	@Test
	public void testDeleteObject() {
		ModelManager mm = new ModelManager();
		ModelClass modelClassA = mm.createClass(new Point3D(0, 0, 0), 100, 100, Color.BEIGE);
		modelClassA.setName("A");
		ModelObject modelObjectA1 = mm.createObject(modelClassA);
		ModelObject modelObjectA2 = mm.createObject(modelClassA);
		assertTrue(mm.getModelClass("A").getModelObjects().contains(modelObjectA1));
		assertTrue(mm.getModelClass("A").getModelObjects().contains(modelObjectA2));
		mm.deleteObject(modelObjectA1);
		assertTrue(!mm.getModelClass("A").getModelObjects().contains(modelObjectA1));
		assertTrue(mm.getModelClass("A").getModelObjects().contains(modelObjectA2));
	}

}
