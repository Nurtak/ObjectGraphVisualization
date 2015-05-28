package ch.hsr.ogv.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import javafx.geometry.Point3D;
import javafx.scene.paint.Color;

import org.junit.Before;
import org.junit.Test;

public class ModelObjectTest {

	private ModelClass modelClassA;
	private ModelObject modelObjectA1;

	@Before
	public void setUp() throws Exception
	{
		ModelManager mm = new ModelManager();
		modelClassA = mm.createClass(new Point3D(-200, 200, 0), 100, 100, Color.BEIGE);
		modelObjectA1 = mm.createObject(modelClassA);
	}

	@Test
	public void testAttributeValue() {
		String attrName1 = "attr1";
		Attribute attr1 = new Attribute(attrName1);
		String attrValue1 = "bla1";
		modelObjectA1.addAttributeValue(attr1, attrValue1);
		assertEquals(attrValue1, modelObjectA1.getAttributeValue(attrName1));
		assertEquals(attrValue1, modelObjectA1.getAttributeValues().get(attr1));
	}

	@Test
	public void testIsSuperObject() {
		assertFalse(modelObjectA1.isSuperObject());
	}

	@Test
	public void testChangeAttributeValue() {
		String attrName1 = "attr1";
		Attribute attr1 = modelClassA.createAttribute(attrName1);
		assertEquals("", modelObjectA1.getAttributeValue(attrName1));
		String newAttrValue = "bli";
		modelObjectA1.changeAttributeValue(attr1, newAttrValue);
		assertEquals(newAttrValue, modelObjectA1.getAttributeValue(attrName1));
	}

	@Test
	public void testChangeAttributeValueWithName() {
		String attrName1 = "attr1";
		Attribute attr1 = modelClassA.createAttribute(attrName1);
		assertEquals("", modelObjectA1.getAttributeValue(attrName1));
		String newAttrValue = "bli";
		modelObjectA1.changeAttributeValue(attr1.getName(), newAttrValue);
		assertEquals(newAttrValue, modelObjectA1.getAttributeValue(attrName1));
	}

	@Test
	public void testChangeAttributeName() {
		String attrName1 = "attr1";
		Attribute attr1 = modelClassA.createAttribute(attrName1);
		String newAttrName1 = "newAttrName1";
		modelObjectA1.changeAttributeName(attr1, newAttrName1);
		assertEquals(modelObjectA1.getAttributeValues().get(attr1), modelObjectA1.getAttributeValue(newAttrName1));
	}

}
