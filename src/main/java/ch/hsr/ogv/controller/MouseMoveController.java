package ch.hsr.ogv.controller;

import ch.hsr.ogv.view.Floor;
import ch.hsr.ogv.view.PaneBox;
import ch.hsr.ogv.view.VerticalHelper;
import javafx.geometry.Point3D;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.PickResult;

import java.util.Observable;

public class MouseMoveController extends Observable {

    public void enableMouseMove(Floor floor) {

        floor.addEventHandler(MouseEvent.MOUSE_MOVED, (MouseEvent me) -> {
            PickResult pick = me.getPickResult();
            Point3D movePoint = pick.getIntersectedNode().localToParent(pick.getIntersectedPoint());
            setChanged();
            notifyObservers(movePoint);
        });

    }

    public void enableMouseMove(VerticalHelper verticalHelper) {
        verticalHelper.addEventHandler(MouseEvent.MOUSE_MOVED, (MouseEvent me) -> {
            PickResult pick = me.getPickResult();
            Point3D movePoint = pick.getIntersectedNode().localToParent(pick.getIntersectedPoint());
            setChanged();
            notifyObservers(movePoint);
        });
    }

    public void enableMouseMove(PaneBox paneBox) {
        paneBox.get().addEventHandler(MouseEvent.MOUSE_MOVED, (MouseEvent me) -> {
            setChanged();
            notifyObservers(paneBox);
        });
    }

}
