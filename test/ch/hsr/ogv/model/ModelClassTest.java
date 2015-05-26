package ch.hsr.ogv.model;

import static org.junit.Assert.assertEquals;
import javafx.geometry.Point3D;
import javafx.scene.paint.Color;

import org.junit.Test;

public class ModelClassTest {

	@Test
	public void testGetXYZ() {
		ModelManager mm = new ModelManager();
		ModelClass modelClassA = mm.createClass(new Point3D(100, 200, 300), 100.0, 100.0, Color.BEIGE);
		assertEquals(100, modelClassA.getX(), 0.0);
		assertEquals(200, modelClassA.getY(), 0.0);
		assertEquals(300, modelClassA.getZ(), 0.0);
	}

	@Test
	public void testGetFriends() {
		ModelManager mm = new ModelManager();
		ModelClass modelClassA = mm.createClass(new Point3D(0, 0, 0), 100.0, 100.0, Color.BEIGE);
		ModelClass modelClassB = mm.createClass(new Point3D(-200, 0, 0), 100.0, 100.0, Color.BEIGE);
		ModelClass modelClassC = mm.createClass(new Point3D(-400, 0, 0), 100.0, 100.0, Color.BEIGE);
		Relation relationAB = mm.createRelation(modelClassA, modelClassB, RelationType.DIRECTED_ASSOCIATION, Color.BLACK);
		Relation relationAC = mm.createRelation(modelClassA, modelClassC, RelationType.DIRECTED_ASSOCIATION, Color.BLACK);

		assertEquals(2, modelClassA.getFriends().size());

		assertEquals(relationAB.getEnd(), modelClassA.getFriends().get(relationAB.getStart()));
		assertEquals(relationAC.getEnd(), modelClassA.getFriends().get(relationAC.getStart()));
	}

	@Test
	public void testGetModelObject() {
		ModelManager mm = new ModelManager();
		ModelClass modelClassA = mm.createClass(new Point3D(0, 0, 0), 100.0, 100.0, Color.BEIGE);
		ModelClass modelClassB = mm.createClass(new Point3D(-200, 0, 0), 100.0, 100.0, Color.BEIGE);
		ModelObject modelObjectA1 = mm.createObject(modelClassA);
		ModelObject modelObjectA2 = mm.createObject(modelClassA);
		ModelObject modelObjectB1 = mm.createObject(modelClassB);
		ModelObject modelObjectB2 = mm.createObject(modelClassB);
		assertEquals(modelObjectA1, modelClassA.getModelObject(modelObjectA1.getUniqueID()));
		assertEquals(modelObjectA2, modelClassA.getModelObject(modelObjectA2.getUniqueID()));
		assertEquals(modelObjectB1, modelClassB.getModelObject(modelObjectB1.getUniqueID()));
		assertEquals(modelObjectB2, modelClassB.getModelObject(modelObjectB2.getUniqueID()));
	}


}
