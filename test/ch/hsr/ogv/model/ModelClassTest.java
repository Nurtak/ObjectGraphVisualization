package ch.hsr.ogv.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import javafx.geometry.Point3D;
import javafx.scene.paint.Color;

import org.junit.Before;
import org.junit.Test;

public class ModelClassTest {

	private ModelManager mm;

	@Before
	public void setUp() throws Exception {
		mm = new ModelManager();
	}

	@Test
	public void testGetXYZ() {
		ModelClass modelClassA = mm.createClass(new Point3D(100, 200, 300), 100.0, 100.0, Color.BEIGE);
		assertEquals(100, modelClassA.getX(), 0.0);
		assertEquals(200, modelClassA.getY(), 0.0);
		assertEquals(300, modelClassA.getZ(), 0.0);
	}

	@Test
	public void testGetFriends() {
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

	@Test
	public void testCreateModelObject() {
		ModelClass modelClassA = mm.createClass(new Point3D(0, 0, 0), 100.0, 100.0, Color.BEIGE);
		List<Attribute> attrs = new ArrayList<Attribute>();
		Attribute attr = new Attribute("bla");
		attrs.add(attr);
		modelClassA.setAttributes(attrs);
		String objName = "a1";
		ModelObject modelObjectA1 = modelClassA.createModelObject(objName);
		assertEquals(objName, modelObjectA1.getName());
		assertTrue(modelObjectA1.getAttributeValues().keySet().contains(modelClassA.getAttributes().get(0)));
	}

	@Test
	public void testGetSuperClasses() {
		ModelClass modelClassA = mm.createClass(new Point3D(0, 0, 0), 100.0, 100.0, Color.BEIGE);
		ModelClass modelClassB = mm.createClass(new Point3D(-200, 0, 0), 100.0, 100.0, Color.BEIGE);
		mm.createRelation(modelClassA, modelClassB, RelationType.GENERALIZATION, Color.BLACK);
		assertEquals(modelClassB, modelClassA.getSuperClasses().get(0));
		assertEquals(1, modelClassA.getSuperClasses().size());
	}

	@Test
	public void testGetSubClasses() {
		ModelClass modelClassA = mm.createClass(new Point3D(0, 0, 0), 100.0, 100.0, Color.BEIGE);
		ModelClass modelClassB = mm.createClass(new Point3D(-200, 0, 0), 100.0, 100.0, Color.BEIGE);
		mm.createRelation(modelClassA, modelClassB, RelationType.GENERALIZATION, Color.BLACK);
		assertEquals(modelClassA, modelClassB.getSubClasses().get(0));
		assertEquals(1, modelClassB.getSubClasses().size());
	}

	@Test
	public void testGetSuperObjects() {
		ModelClass modelClassA = mm.createClass(new Point3D(0, 0, 0), 100.0, 100.0, Color.BEIGE);
		ModelClass modelClassB = mm.createClass(new Point3D(-200, 0, 0), 100.0, 100.0, Color.BEIGE);
		mm.createRelation(modelClassA, modelClassB, RelationType.GENERALIZATION, Color.BLACK);
		mm.createObject(modelClassA);
		mm.createObject(modelClassA);
		assertEquals(modelClassB, modelClassA.getSuperObjects().get(0).getModelClass());
		assertEquals(modelClassB, modelClassA.getSuperObjects().get(1).getModelClass());
		assertEquals(2, modelClassA.getSuperObjects().size());
		assertEquals(0, modelClassB.getSuperClasses().size());
	}

	@Test
	public void testGetSubModelObjectByModelObject() {
		ModelClass modelClassA = mm.createClass(new Point3D(0, 0, 0), 100.0, 100.0, Color.BEIGE);
		ModelClass modelClassB = mm.createClass(new Point3D(-200, 0, 0), 100.0, 100.0, Color.BEIGE);
		mm.createRelation(modelClassA, modelClassB, RelationType.GENERALIZATION, Color.BLACK);
		ModelObject modelObjectA1 = mm.createObject(modelClassA);
		assertEquals(modelObjectA1, modelClassA.getSubModelObject(modelClassA.getSuperObjects().get(0)));
	}

	@Test
	public void testGetInheritingObjects() {
		ModelClass modelClassA = mm.createClass(new Point3D(0, 0, 0), 100.0, 100.0, Color.BEIGE);
		ModelClass modelClassB = mm.createClass(new Point3D(-200, 0, 0), 100.0, 100.0, Color.BEIGE);
		mm.createRelation(modelClassA, modelClassB, RelationType.GENERALIZATION, Color.BLACK);
		mm.createObject(modelClassA);
		assertEquals(modelClassB, modelClassB.getInheritingObjects().get(0).getModelClass());
	}

	@Test
	public void testCreateAttribute() {
		ModelClass modelClassA = mm.createClass(new Point3D(0, 0, 0), 100.0, 100.0, Color.BEIGE);
		String attrName = "bla";
		Attribute attr = modelClassA.createAttribute(attrName);
		assertEquals(attrName, attr.getName());
	}

	@Test
	public void testCreateAttributeWithoutName() {
		ModelClass modelClassA = mm.createClass(new Point3D(0, 0, 0), 100.0, 100.0, Color.BEIGE);
		Attribute attr = modelClassA.createAttribute();
		assertEquals("attr1", attr.getName());
	}

	@Test
	public void testDeleteAttribute() {
		ModelClass modelClassA = mm.createClass(new Point3D(0, 0, 0), 100.0, 100.0, Color.BEIGE);
		Attribute attr1 = modelClassA.createAttribute("bla1");
		Attribute attr2 = modelClassA.createAttribute("bla2");
		assertEquals(attr1, modelClassA.getAttributes().get(0));
		assertEquals(attr2, modelClassA.getAttributes().get(1));
		assertEquals(2, modelClassA.getAttributes().size());
		modelClassA.deleteAttribute(0);
		assertEquals(attr2, modelClassA.getAttributes().get(0));
		assertEquals(1, modelClassA.getAttributes().size());
	}

	@Test
	public void testMoveAttributeUp() {
		ModelClass modelClassA = mm.createClass(new Point3D(0, 0, 0), 100.0, 100.0, Color.BEIGE);
		Attribute attr1 = modelClassA.createAttribute("bla1");
		Attribute attr2 = modelClassA.createAttribute("bla2");
		Attribute attr3 = modelClassA.createAttribute("bla3");
		assertEquals(attr1, modelClassA.getAttributes().get(0));
		assertEquals(attr2, modelClassA.getAttributes().get(1));
		assertEquals(attr3, modelClassA.getAttributes().get(2));
		assertEquals(3, modelClassA.getAttributes().size());
		modelClassA.moveAttributeUp(1);
		assertEquals(attr2, modelClassA.getAttributes().get(0));
		assertEquals(attr1, modelClassA.getAttributes().get(1));
		assertEquals(attr3, modelClassA.getAttributes().get(2));
		assertEquals(3, modelClassA.getAttributes().size());
	}

	@Test
	public void testMoveAttributeDown() {
		ModelClass modelClassA = mm.createClass(new Point3D(0, 0, 0), 100.0, 100.0, Color.BEIGE);
		Attribute attr1 = modelClassA.createAttribute("bla1");
		Attribute attr2 = modelClassA.createAttribute("bla2");
		Attribute attr3 = modelClassA.createAttribute("bla3");
		assertEquals(attr1, modelClassA.getAttributes().get(0));
		assertEquals(attr2, modelClassA.getAttributes().get(1));
		assertEquals(attr3, modelClassA.getAttributes().get(2));
		assertEquals(3, modelClassA.getAttributes().size());
		modelClassA.moveAttributeDown(1);
		assertEquals(attr1, modelClassA.getAttributes().get(0));
		assertEquals(attr3, modelClassA.getAttributes().get(1));
		assertEquals(attr2, modelClassA.getAttributes().get(2));
		assertEquals(3, modelClassA.getAttributes().size());
	}

	@Test
	public void testChangeAttributeName() {
		ModelClass modelClassA = mm.createClass(new Point3D(0, 0, 0), 100.0, 100.0, Color.BEIGE);
		Attribute attr = modelClassA.createAttribute("bla");
		String attrNameNew = "new";
		modelClassA.changeAttributeName(0, attrNameNew);
		assertEquals(attrNameNew, attr.getName());
	}

	@Test
	public void testDeleteSuperObject() {
		ModelClass modelClassA = mm.createClass(new Point3D(0, 0, 0), 100.0, 100.0, Color.BEIGE);
		ModelClass modelClassB = mm.createClass(new Point3D(-200, 0, 0), 100.0, 100.0, Color.BEIGE);
		mm.createRelation(modelClassA, modelClassB, RelationType.GENERALIZATION, Color.BLACK);
		mm.createObject(modelClassA);
		assertEquals(1, modelClassA.getSuperObjects().size());
		modelClassA.deleteSuperObject(modelClassA.getSuperObjects().get(0));
		assertTrue(modelClassA.getSuperObjects().isEmpty());
	}

}
