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
public class DragResizeController extends DragController {
	
	public void enableDragResize(ModelClass modelClass, PaneBox paneBox, SubSceneAdapter subSceneAdapter) {
		enableDirection(paneBox.getSelection().getLineN(),   Cursor.N_RESIZE, modelClass, paneBox, subSceneAdapter);
		enableDirection(paneBox.getSelection().getPointNE(), Cursor.NE_RESIZE, modelClass, paneBox, subSceneAdapter);
		enableDirection(paneBox.getSelection().getLineE(),   Cursor.E_RESIZE, modelClass, paneBox, subSceneAdapter);
		enableDirection(paneBox.getSelection().getPointSE(), Cursor.SE_RESIZE, modelClass, paneBox, subSceneAdapter);
		enableDirection(paneBox.getSelection().getLineS(),   Cursor.S_RESIZE, modelClass, paneBox, subSceneAdapter);
		enableDirection(paneBox.getSelection().getPointSW(), Cursor.SW_RESIZE, modelClass, paneBox, subSceneAdapter);
		enableDirection(paneBox.getSelection().getLineW(),   Cursor.W_RESIZE, modelClass, paneBox, subSceneAdapter);
		enableDirection(paneBox.getSelection().getPointNW(), Cursor.NW_RESIZE, modelClass, paneBox, subSceneAdapter);
	}
	
	private void enableDirection(Group g, Cursor direction, ModelClass modelClass, PaneBox paneBox, SubSceneAdapter subSceneAdapter) {
		g.setOnMouseEntered((MouseEvent me) -> {
			subSceneAdapter.getSubScene().setCursor(direction);
	    });
		
		g.setOnMouseExited((MouseEvent me) -> {
			subSceneAdapter.getSubScene().setCursor(Cursor.DEFAULT);
	    });
		
		setOnMouseMoved(modelClass, paneBox, subSceneAdapter);
		setOnMouseDragged(g, modelClass, paneBox, subSceneAdapter, direction);
		setOnMouseReleased(g, subSceneAdapter);
	}
	
	protected void setOnMouseDragged(Group g, ModelClass modelClass, PaneBox paneBox, SubSceneAdapter subSceneAdapter, Cursor direction) {
		Floor floor = subSceneAdapter.getFloor();
		g.setOnMouseDragged((MouseEvent me) -> {
			setDragInProgress(subSceneAdapter, true);
			if(MouseButton.PRIMARY.equals(me.getButton())) {
				subSceneAdapter.getSubScene().setCursor(direction);
				PickResult pick = me.getPickResult();
				if(pick != null && pick.getIntersectedNode() != null && floor.hasTile(pick.getIntersectedNode())) {
					Point3D coords = pick.getIntersectedNode().localToParent(pick.getIntersectedPoint());
					if(Cursor.N_RESIZE.equals(direction)) {
						northResize(modelClass, paneBox, coords);
					}
					else if(Cursor.NE_RESIZE.equals(direction)) {
						northResize(modelClass, paneBox, coords);
						eastResize(modelClass, paneBox, coords);
					}
					else if(Cursor.E_RESIZE.equals(direction)) {
						eastResize(modelClass, paneBox, coords);
					}
					else if(Cursor.SE_RESIZE.equals(direction)) {
						southResize(modelClass, paneBox, coords);
						eastResize(modelClass, paneBox, coords);
					}
					else if(Cursor.S_RESIZE.equals(direction)) {
						southResize(modelClass, paneBox, coords);
					}
					else if(Cursor.SW_RESIZE.equals(direction)) {
						southResize(modelClass, paneBox, coords);
						westResize(modelClass, paneBox, coords);
					}
					else if(Cursor.W_RESIZE.equals(direction)) {
						westResize(modelClass, paneBox, coords);
					}
					else if(Cursor.NW_RESIZE.equals(direction)) {
						northResize(modelClass, paneBox, coords);
						westResize(modelClass, paneBox, coords);
					}
				}
			}
		});
	}
	
	protected void northResize(ModelClass modelClass, PaneBox paneBox, Point3D coords) {
		double newHeight = modelClass.getHeight() / 2 - modelClass.getZ() + coords.getZ();
		newHeight = restrictedHeight(paneBox, newHeight);
		modelClass.setHeight(newHeight);
		modelClass.setZ(origTranslateZ - origHeight / 2 + newHeight / 2);
	}
	
	protected void eastResize(ModelClass modelClass, PaneBox paneBox, Point3D coords) {
		double newWidth = modelClass.getWidth() / 2 + modelClass.getX() - coords.getX();
		newWidth = restrictedWidth(paneBox, newWidth);
		modelClass.setWidth(newWidth);
		modelClass.setX(origTranslateX + origWidth / 2 - newWidth / 2);
	}
	
	protected void southResize(ModelClass modelClass, PaneBox paneBox, Point3D coords) {
		double newHeight = modelClass.getHeight() / 2 + modelClass.getZ() - coords.getZ();
		newHeight = restrictedHeight(paneBox, newHeight);
		modelClass.setHeight(newHeight);
		modelClass.setZ(origTranslateZ + origHeight / 2 - newHeight / 2);
	}
	
	protected void westResize(ModelClass modelClass, PaneBox paneBox, Point3D coords) {
		double newWidth = modelClass.getWidth() / 2 - modelClass.getX() + coords.getX();
		newWidth = restrictedWidth(paneBox, newWidth);
		modelClass.setWidth(newWidth);
		modelClass.setX(origTranslateX - origWidth / 2 + newWidth / 2);
	}
	
	private double restrictedWidth(PaneBox paneBox, double newWidth) {
		double retWidth = newWidth;
		if(newWidth <= paneBox.getMinWidth()) {
			retWidth = paneBox.getMinWidth();
		}
		else if(newWidth >= paneBox.getMaxWidth()) {
			retWidth = paneBox.getMaxWidth();
		}
		return retWidth;
	}
	
	private double restrictedHeight(PaneBox paneBox, double newHeight) {
		double retHeight = newHeight;
		if(newHeight <= paneBox.getMinHeight()) {
			retHeight = paneBox.getMinHeight();
		}
		else if(newHeight >= paneBox.getMaxHeight()) {
			retHeight = paneBox.getMaxHeight();
		}
		return retHeight;
	}
		
}
