package ch.hsr.ogv.controller;

import javafx.geometry.Point3D;
import javafx.scene.Group;
import ch.hsr.ogv.model.ModelClass;
import ch.hsr.ogv.view.Floor;
import ch.hsr.ogv.view.PaneBox;
import ch.hsr.ogv.view.SubSceneAdapter;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.PickResult;
import javafx.scene.Cursor;

/**
 * 
 * @author Simon Gwerder
 *
 */
public class DragMoveController extends DragController {
	
	public void enableDragMove(ModelClass theClass, PaneBox paneBox, SubSceneAdapter subSceneAdapter) {
		Group paneBoxGroup = paneBox.get();
		setOnMousePressed(paneBoxGroup, theClass, paneBox, subSceneAdapter);
		setOnMouseDragged(paneBoxGroup, theClass, paneBox, subSceneAdapter);
		setOnMouseReleased(paneBoxGroup, subSceneAdapter);
	}

	protected void setOnMouseDragged(Group g, ModelClass theClass, PaneBox paneBox, SubSceneAdapter subSceneAdapter) {
		Floor floor = subSceneAdapter.getFloor();
		g.setOnMouseDragged((MouseEvent me) -> {
			setDragInProgress(subSceneAdapter, true);
			if(g.focusedProperty().get() && MouseButton.PRIMARY.equals(me.getButton())) {
				subSceneAdapter.getSubScene().setCursor(Cursor.MOVE);
				PickResult pick = me.getPickResult();
				if(pick != null && pick.getIntersectedNode() != null && floor.hasTile(pick.getIntersectedNode())) {
					Point3D coords = pick.getIntersectedNode().localToParent(pick.getIntersectedPoint());
					Point3D classCoordinates = new Point3D(coords.getX() - origRelMouseX, theClass.getY(), coords.getZ() - origRelMouseZ); // only x and z is changeable
					theClass.setCoordinates(classCoordinates);
				}
			}
		});
	}
	
}
