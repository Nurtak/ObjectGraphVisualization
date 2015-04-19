package ch.hsr.ogv.controller;

import java.util.Observable;

import javafx.geometry.Point3D;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.PickResult;
import ch.hsr.ogv.view.Floor;
import ch.hsr.ogv.view.PaneBox;
import ch.hsr.ogv.view.VerticalHelper;

public class MouseMoveController extends Observable {

	public void enableMouseMove(Floor floor) {

		floor.addEventHandler(MouseEvent.MOUSE_MOVED, (MouseEvent me) -> {
			PickResult pick = me.getPickResult();
			//if (pick != null && pick.getIntersectedNode() != null && floor.hasTile(pick.getIntersectedNode())) {
				Point3D movePoint = pick.getIntersectedNode().localToParent(pick.getIntersectedPoint());
				setChanged();
				notifyObservers(movePoint);
			//}
		});
		
	}
	
	public void enableMouseMove(VerticalHelper verticalHelper) {
		verticalHelper.addEventHandler(MouseEvent.MOUSE_MOVED, (MouseEvent me) -> {
			PickResult pick = me.getPickResult();
			//if (pick != null && pick.getIntersectedNode() != null && floor.hasTile(pick.getIntersectedNode())) {
				Point3D movePoint = pick.getIntersectedNode().localToParent(pick.getIntersectedPoint());
				setChanged();
				notifyObservers(movePoint);
			//}
		});
	}
	
	public void enableMouseMove(PaneBox paneBox) {
		paneBox.get().addEventHandler(MouseEvent.MOUSE_MOVED, (MouseEvent me) -> {
			setChanged();
			notifyObservers(paneBox);
		});
	}
	
}
