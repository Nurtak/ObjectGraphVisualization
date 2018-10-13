package ch.hsr.ogv.model;

import javafx.geometry.Point3D;
import javafx.scene.paint.Color;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
    public void testGetFriendMM() {
        ModelManager mm = new ModelManager();
        ModelClass modelClassA = mm.createClass(new Point3D(0, 0, 0), 100, 100, Color.BEIGE);
        ModelClass modelClassB = mm.createClass(new Point3D(-200, 0, 0), 100, 100, Color.BEIGE);
        Relation relationAB = mm.createRelation(modelClassA, modelClassB, RelationType.DIRECTED_ASSOCIATION, Color.BLACK);

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
    public void testIsStartMM() {
        ModelManager mm = new ModelManager();
        ModelClass modelClassA = mm.createClass(new Point3D(0, 0, 0), 100, 100, Color.BEIGE);
        ModelClass modelClassB = mm.createClass(new Point3D(-200, 0, 0), 100, 100, Color.BEIGE);
        Relation relationAB = mm.createRelation(modelClassA, modelClassB, RelationType.DIRECTED_ASSOCIATION, Color.BLACK);

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
    public void testIsEndMM() {
        ModelManager mm = new ModelManager();
        ModelClass modelClassA = mm.createClass(new Point3D(0, 0, 0), 100, 100, Color.BEIGE);
        ModelClass modelClassB = mm.createClass(new Point3D(-200, 0, 0), 100, 100, Color.BEIGE);
        Relation relationAB = mm.createRelation(modelClassA, modelClassB, RelationType.DIRECTED_ASSOCIATION, Color.BLACK);

        Endpoint endpointB = relationAB.getEnd();

        assertTrue(relationAB.isEnd(endpointB));
    }

    @Test
    public void testChangeDirection() {
        ModelManager mm = new ModelManager();
        ModelClass modelClassA = mm.createClass(new Point3D(0, 0, 0), 100, 100, Color.BEIGE);
        ModelClass modelClassB = mm.createClass(new Point3D(-200, 0, 0), 100, 100, Color.BEIGE);
        Relation relationAB = mm.createRelation(modelClassA, modelClassB, RelationType.DIRECTED_ASSOCIATION, Color.BLACK);

        Endpoint endpointStart = relationAB.getStart();
        Endpoint endpointEnd = relationAB.getEnd();

        assertEquals(endpointStart, modelClassA.getEndpoints().get(0));
        assertEquals(endpointEnd, modelClassB.getEndpoints().get(0));

        relationAB.changeDirection();

        assertEquals(endpointStart, relationAB.getStart());
        assertEquals(endpointEnd, relationAB.getEnd());

        assertEquals(endpointEnd, modelClassA.getEndpoints().get(0));
        assertEquals(endpointStart, modelClassB.getEndpoints().get(0));

        assertEquals(endpointEnd, relationAB.getFriend(endpointStart));
        assertEquals(endpointStart, relationAB.getFriend(endpointEnd));
    }

}
