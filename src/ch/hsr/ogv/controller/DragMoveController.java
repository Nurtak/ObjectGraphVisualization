package ch.hsr.ogv.controller;

import javafx.geometry.Point3D;
import javafx.scene.Group;
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
	
	public void enableDragMove(PaneBox paneBox, SubSceneAdapter subSceneAdapter) {
		Group paneBoxGroup = paneBox.get();
		setOnMousePressed(paneBoxGroup, paneBox, subSceneAdapter);
		setOnMouseDragged(paneBoxGroup, paneBox, subSceneAdapter);
		setOnMouseReleased(paneBoxGroup, paneBox, subSceneAdapter);
	}

	protected void setOnMouseDragged(Group g, PaneBox paneBox, SubSceneAdapter subSceneAdapter) {
		Floor floor = subSceneAdapter.getFloor();
		g.setOnMouseDragged((MouseEvent me) -> {
			setDragInProgress(subSceneAdapter, true);
			if(g.focusedProperty().get() && MouseButton.PRIMARY.equals(me.getButton())) {
				subSceneAdapter.getSubScene().setCursor(Cursor.MOVE);
				PickResult pick = me.getPickResult();
				if(pick != null && pick.getIntersectedNode() != null && floor.hasTile(pick.getIntersectedNode())) {
					Point3D coords = pick.getIntersectedNode().localToParent(pick.getIntersectedPoint());
					paneBox.setTranslateX(coords.getX() - origRelMouseX);
					paneBox.setTranslateZ(coords.getZ() - origRelMouseZ);
					//paneBox.setTranslateY(coords.getY());
				}
			}
		});
	}
	
}
