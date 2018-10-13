package ch.hsr.ogv.view;

import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.paint.Color;

public class ArrowSelection extends Group {

    private final static int INIT_SELECT_SIZE = 4;
    private final static Color SELECTION_COLOR = Color.DODGERBLUE;

    private SphereAdapter sphereStart;
    private SphereAdapter sphereEnd;

    public ArrowSelection() {
        this.sphereStart = new SphereAdapter(SELECTION_COLOR, INIT_SELECT_SIZE);
        this.sphereEnd = new SphereAdapter(SELECTION_COLOR, INIT_SELECT_SIZE);
        getChildren().addAll(this.sphereStart, this.sphereEnd);
    }

    private void setStartXYZ(Point3D point) {
        this.sphereStart.setTranslateX(point.getX());
        this.sphereStart.setTranslateY(point.getY());
        this.sphereStart.setTranslateZ(point.getZ());
    }

    private void setEndXYZ(Point3D point) {
        this.sphereEnd.setTranslateX(point.getX());
        this.sphereEnd.setTranslateY(point.getY());
        this.sphereEnd.setTranslateZ(point.getZ());
    }

    public void setStartEndXYZ(Point3D start, Point3D end) {
        setStartXYZ(start);
        setEndXYZ(end);
    }

}
