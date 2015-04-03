package ch.hsr.ogv.controller;

import javafx.geometry.Point3D;
import ch.hsr.ogv.model.ModelBox;
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
	
	protected volatile double origRelMouseX;
	protected volatile double origRelMouseY;
	protected volatile double origRelMouseZ;
	
	public void enableDragMove(ModelBox modelBox, PaneBox paneBox, SubSceneAdapter subSceneAdapter) {
		setOnMouseMoved(modelBox, paneBox, subSceneAdapter);
		setOnMouseDragged(modelBox, paneBox, subSceneAdapter);
		setOnMouseReleased(paneBox.get(), subSceneAdapter);
	}
	
	private void setOnMouseMoved(ModelBox modelBox, PaneBox paneBox, SubSceneAdapter subSceneAdapter) {
		paneBox.get().setOnMouseMoved((MouseEvent me) -> {
			origRelMouseX = me.getX();
			origRelMouseY = me.getY();
			origRelMouseZ = me.getZ();
		});
	}

	private void dragProcess(MouseEvent me, ModelBox modelBox, SubSceneAdapter subSceneAdapter) {
		Floor floor = subSceneAdapter.getFloor();
		subSceneAdapter.getSubScene().setCursor(Cursor.MOVE);
		PickResult pick = me.getPickResult();
		if(pick != null && pick.getIntersectedNode() != null && floor.hasTile(pick.getIntersectedNode())) {
			Point3D coords = pick.getIntersectedNode().localToParent(pick.getIntersectedPoint());
			Point3D classCoordinates = new Point3D(coords.getX() - origRelMouseX, modelBox.getY(), coords.getZ() - origRelMouseZ); // only x and z is changeable
			modelBox.setCoordinates(classCoordinates);
		}
	}
	
	private void setOnMouseDragged(ModelBox modelBox, PaneBox paneBox, SubSceneAdapter subSceneAdapter) {
		paneBox.get().setOnMouseDragged((MouseEvent me) -> {
			setDragInProgress(subSceneAdapter, true);
			if(paneBox.isSelected() && MouseButton.PRIMARY.equals(me.getButton())) {
				dragProcess(me, modelBox, subSceneAdapter);
			}
		});
	}
	
}
