package ch.hsr.ogv.controller;

import javafx.geometry.Point3D;
import javafx.scene.Cursor;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.PickResult;
import ch.hsr.ogv.model.ModelBox;
import ch.hsr.ogv.view.Floor;
import ch.hsr.ogv.view.PaneBox;
import ch.hsr.ogv.view.SubSceneAdapter;

/**
 *
 * @author Simon Gwerder
 *
 */
public class DragMoveController extends DragController {

	protected volatile double origRelMouseX;
	protected volatile double origRelMouseY;
	protected volatile double origRelMouseZ;

	public void enableDragMove(ModelBox modelBox, PaneBox paneBox, SubSceneAdapter subSceneAdapter) {
		startOnMouseMoved(modelBox, paneBox, subSceneAdapter);
		moveOnMouseDragged(modelBox, paneBox, subSceneAdapter);
		endOnMouseReleased(paneBox.get(), subSceneAdapter);
	}

	private void startOnMouseMoved(ModelBox modelBox, PaneBox paneBox, SubSceneAdapter subSceneAdapter) {
		paneBox.get().addEventHandler(MouseEvent.MOUSE_MOVED, (MouseEvent me) -> {
			origRelMouseX = me.getX();
			origRelMouseY = me.getY();
			origRelMouseZ = me.getZ();
		});
	}

	private void moveOnMouseDragged(ModelBox modelBox, PaneBox paneBox, SubSceneAdapter subSceneAdapter) {
		paneBox.get().addEventHandler(MouseEvent.MOUSE_DRAGGED, (MouseEvent me) -> {
			setDragInProgress(subSceneAdapter, true);
			if (MouseButton.PRIMARY.equals(me.getButton()) && paneBox.isSelected()) {
				Floor floor = subSceneAdapter.getFloor();
				subSceneAdapter.getSubScene().setCursor(Cursor.MOVE);
				PickResult pick = me.getPickResult();
				if (pick != null && pick.getIntersectedNode() != null && floor.hasTile(pick.getIntersectedNode())) {
					Point3D coords = pick.getIntersectedNode().localToParent(pick.getIntersectedPoint());
					Point3D classCoordinates = new Point3D(coords.getX() - origRelMouseX, modelBox.getY(), coords.getZ() - origRelMouseZ);
					modelBox.setCoordinates(classCoordinates);
				}
			}
		});
	}
}
