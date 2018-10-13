package ch.hsr.ogv.model;

import javafx.geometry.Point3D;
import javafx.scene.paint.Color;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ModelManagerTest {

    private ModelManager mm;

    @BeforeEach
    public void setUp() throws Exception {
        mm = new ModelManager();
    }

    @Test
    public void testCreateClass() {
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
        mm.createClass(new Point3D(0, 0, 0), 100, 100, Color.BEIGE);
        mm.createClass(new Point3D(-200, 0, 0), 100, 100, Color.BEIGE);
        mm.clearClasses();
        assertTrue(mm.getClasses().isEmpty());
        mm.createClass(new Point3D(-400, 0, 0), 100, 100, Color.BEIGE);
        assertFalse(mm.getClasses().isEmpty());
        mm.clearClasses();
        assertTrue(mm.getClasses().isEmpty());
    }


    @Test
    public void testDeleteClass() {
        ModelClass modelClassA = mm.createClass(new Point3D(0, 0, 0), 100, 100, Color.BEIGE);
        mm.deleteClass(modelClassA);
        assertFalse(mm.getClasses().contains(modelClassA));
        ModelClass modelClassB = mm.createClass(new Point3D(-200, 0, 0), 100, 100, Color.BEIGE);
        ModelClass modelClassC = mm.createClass(new Point3D(-400, 0, 0), 100, 100, Color.BEIGE);
        assertTrue(mm.getClasses().contains(modelClassB));
        assertTrue(mm.getClasses().contains(modelClassC));
        mm.deleteClass(modelClassB);
        assertFalse(mm.getClasses().contains(modelClassB));
        assertTrue(mm.getClasses().contains(modelClassC));
    }

    @Test
    public void testDeleteClassWithRelation() {
        ModelClass modelClassA = mm.createClass(new Point3D(0, 0, 0), 100, 100, Color.BEIGE);
        ModelClass modelClassB = mm.createClass(new Point3D(-200, 0, 0), 100, 100, Color.BEIGE);
        Relation relationAB1 = mm.createRelation(modelClassA, modelClassB, RelationType.DIRECTED_ASSOCIATION, Color.BLACK);
        mm.deleteClass(modelClassB);
        assertFalse(mm.getRelations().contains(relationAB1));
    }

    @Test
    public void testDeleteObject() {
        ModelClass modelClassA = mm.createClass(new Point3D(0, 0, 0), 100, 100, Color.BEIGE);
        modelClassA.setName("A");
        ModelObject modelObjectA1 = mm.createObject(modelClassA);
        ModelObject modelObjectA2 = mm.createObject(modelClassA);
        assertTrue(mm.getModelClass("A").getModelObjects().contains(modelObjectA1));
        assertTrue(mm.getModelClass("A").getModelObjects().contains(modelObjectA2));
        mm.deleteObject(modelObjectA1);
        assertFalse(mm.getModelClass("A").getModelObjects().contains(modelObjectA1));
        assertTrue(mm.getModelClass("A").getModelObjects().contains(modelObjectA2));
    }

    @Test
    public void testDeleteRelation() {
        ModelClass modelClassA = mm.createClass(new Point3D(0, 0, 0), 100, 100, Color.BEIGE);
        ModelClass modelClassB = mm.createClass(new Point3D(-200, 0, 0), 100, 100, Color.BEIGE);
        Relation relationAB1 = mm.createRelation(modelClassA, modelClassB, RelationType.DIRECTED_ASSOCIATION, Color.BLACK);
        mm.deleteRelation(relationAB1);
        assertTrue(mm.getRelations().isEmpty());
        Relation relationAB2 = mm.createRelation(modelClassA, modelClassB, RelationType.DIRECTED_ASSOCIATION, Color.BLACK);
        Relation relationAB3 = mm.createRelation(modelClassA, modelClassB, RelationType.DIRECTED_ASSOCIATION, Color.BLACK);
        mm.deleteRelation(relationAB2);
        assertFalse(mm.getRelations().contains(relationAB1));
        assertFalse(mm.getRelations().contains(relationAB2));
        assertTrue(mm.getRelations().contains(relationAB3));
    }

    @Test
    public void testClearRelations() {
        ModelClass modelClassA = mm.createClass(new Point3D(0, 0, 0), 100, 100, Color.BEIGE);
        ModelClass modelClassB = mm.createClass(new Point3D(-200, 0, 0), 100, 100, Color.BEIGE);
        mm.createRelation(modelClassA, modelClassB, RelationType.DIRECTED_ASSOCIATION, Color.BLACK);
        mm.clearRelations();
        assertTrue(mm.getRelations().isEmpty());
        mm.createRelation(modelClassA, modelClassB, RelationType.DIRECTED_ASSOCIATION, Color.BLACK);
        mm.createRelation(modelClassA, modelClassB, RelationType.DIRECTED_ASSOCIATION, Color.BLACK);
        mm.clearRelations();
        assertTrue(mm.getRelations().isEmpty());
    }

    @Test
    public void testGetModelClass() {
        ModelClass modelClassA = mm.createClass(new Point3D(0, 0, 0), 100, 100, Color.BEIGE);
        modelClassA.setName("A");
        assertNotNull(mm.getModelClass("A"));
        mm.deleteClass(modelClassA);
        assertNull(mm.getModelClass("A"));
        ModelClass modelClassB = mm.createClass(new Point3D(-200, 0, 0), 100, 100, Color.BEIGE);
        assertNull(mm.getModelClass(""));
        modelClassB.setName("");
        assertNull(mm.getModelClass(""));
    }

    @Test
    public void testIsClassNameTaken() {
        assertFalse(mm.isClassNameTaken(""));
        assertFalse(mm.isClassNameTaken("A"));
        ModelClass modelClassA = mm.createClass(new Point3D(0, 0, 0), 100, 100, Color.BEIGE);
        modelClassA.setName("A");
        assertTrue(mm.isClassNameTaken("A"));
        modelClassA.setName("B");
        assertFalse(mm.isClassNameTaken("A"));
        assertTrue(mm.isClassNameTaken("B"));
    }

}
