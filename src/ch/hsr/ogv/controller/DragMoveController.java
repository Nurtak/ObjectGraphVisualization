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
		Floor floor = subSceneAdapter.getFloor();
		
		paneBoxGroup.setOnMousePressed((MouseEvent me) -> {
			if(isSelected(paneBox) && MouseButton.PRIMARY.equals(me.getButton())) {
				relMousePosX = me.getX();
				relMousePosZ = me.getZ();
				subSceneAdapter.onlyFloorMouseEvent(true);
			}
        });
		
		paneBoxGroup.setOnMouseDragged((MouseEvent me) -> {
			if(paneBoxGroup.focusedProperty().get() && MouseButton.PRIMARY.equals(me.getButton())) {
				setDragInProgress(paneBox, true);
				paneBoxGroup.setCursor(Cursor.MOVE);
				PickResult pick = me.getPickResult();
				if(pick != null && pick.getIntersectedNode() != null && floor.equalsRectangle(pick.getIntersectedNode())) {
					Point3D coords = floor.localToParent(pick.getIntersectedPoint());
					paneBox.setTranslateX(coords.getX() - relMousePosX);
					paneBox.setTranslateZ(coords.getZ() - relMousePosZ);
					//paneBox.setTranslateY(coords.getY());
				}
				
			}
		});
		
		paneBoxGroup.setOnMouseReleased((MouseEvent me) -> {
			setDragInProgress(paneBox, false);
			paneBoxGroup.setCursor(Cursor.DEFAULT);
			subSceneAdapter.onlyFloorMouseEvent(false);
		});
	}
	
}
