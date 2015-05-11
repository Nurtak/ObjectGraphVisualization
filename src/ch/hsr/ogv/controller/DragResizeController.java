package ch.hsr.ogv.controller;

import javafx.geometry.Point3D;
import javafx.scene.Group;
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
public class DragResizeController extends DragController {
	
	protected volatile double origTranslateX;
	protected volatile double origTranslateY;
	protected volatile double origTranslateZ;

	protected volatile double origWidth;
	protected volatile double origHeight;
	
	public void enableDragResize(ModelBox modelBox, PaneBox paneBox, SubSceneAdapter subSceneAdapter) {
		enableDirection(paneBox.getSelection().getLineN(),   Cursor.N_RESIZE, modelBox, paneBox, subSceneAdapter);
		enableDirection(paneBox.getSelection().getPointNE(), Cursor.NE_RESIZE, modelBox, paneBox, subSceneAdapter);
		enableDirection(paneBox.getSelection().getLineE(),   Cursor.E_RESIZE, modelBox, paneBox, subSceneAdapter);
		enableDirection(paneBox.getSelection().getPointSE(), Cursor.SE_RESIZE, modelBox, paneBox, subSceneAdapter);
		enableDirection(paneBox.getSelection().getLineS(),   Cursor.S_RESIZE, modelBox, paneBox, subSceneAdapter);
		enableDirection(paneBox.getSelection().getPointSW(), Cursor.SW_RESIZE, modelBox, paneBox, subSceneAdapter);
		enableDirection(paneBox.getSelection().getLineW(),   Cursor.W_RESIZE, modelBox, paneBox, subSceneAdapter);
		enableDirection(paneBox.getSelection().getPointNW(), Cursor.NW_RESIZE, modelBox, paneBox, subSceneAdapter);
	}
	
	private void enableDirection(Group g, Cursor direction, ModelBox modelBox, PaneBox paneBox, SubSceneAdapter subSceneAdapter) {
		g.addEventHandler(MouseEvent.MOUSE_ENTERED, (MouseEvent me) -> {
			subSceneAdapter.getSubScene().setCursor(direction);
	    });
		
		g.addEventHandler(MouseEvent.MOUSE_EXITED, (MouseEvent me) -> {
			subSceneAdapter.getSubScene().setCursor(Cursor.DEFAULT);
	    });
		
		resizeOnMouseDragged(g, modelBox, paneBox, subSceneAdapter, direction);
		endOnMouseReleased(g, paneBox, subSceneAdapter);
	}
	
	private void setOriginals(ModelBox modelBox) {
		Point3D origCoords = modelBox.getCoordinates();
		origTranslateX = origCoords.getX();
		origTranslateY = origCoords.getY();
		origTranslateZ = origCoords.getZ();
		origWidth = modelBox.getWidth();
		origHeight = modelBox.getHeight();
	}
		
	private void resizeOnMouseDragged(Group g, ModelBox modelBox, PaneBox paneBox, SubSceneAdapter subSceneAdapter, Cursor direction) {
		Floor floor = subSceneAdapter.getFloor();
		g.addEventHandler(MouseEvent.MOUSE_DRAGGED, (MouseEvent me) -> {
			setOriginals(modelBox);
			setDragInProgress(subSceneAdapter, true);
			if(MouseButton.PRIMARY.equals(me.getButton())) {
				subSceneAdapter.getSubScene().setCursor(direction);
				PickResult pick = me.getPickResult();
				if(pick != null && pick.getIntersectedNode() != null && floor.hasTile(pick.getIntersectedNode())) {
					Point3D coords = pick.getIntersectedNode().localToParent(pick.getIntersectedPoint());
					if(Cursor.N_RESIZE.equals(direction)) {
						northResize(modelBox, paneBox, coords);
					}
					else if(Cursor.NE_RESIZE.equals(direction)) {
						northResize(modelBox, paneBox, coords);
						eastResize(modelBox, paneBox, coords);
					}
					else if(Cursor.E_RESIZE.equals(direction)) {
						eastResize(modelBox, paneBox, coords);
					}
					else if(Cursor.SE_RESIZE.equals(direction)) {
						southResize(modelBox, paneBox, coords);
						eastResize(modelBox, paneBox, coords);
					}
					else if(Cursor.S_RESIZE.equals(direction)) {
						southResize(modelBox, paneBox, coords);
					}
					else if(Cursor.SW_RESIZE.equals(direction)) {
						southResize(modelBox, paneBox, coords);
						westResize(modelBox, paneBox, coords);
					}
					else if(Cursor.W_RESIZE.equals(direction)) {
						westResize(modelBox, paneBox, coords);
					}
					else if(Cursor.NW_RESIZE.equals(direction)) {
						northResize(modelBox, paneBox, coords);
						westResize(modelBox, paneBox, coords);
					}
				}
			}
		});
	}
	
	protected void northResize(ModelBox modelBox, PaneBox paneBox, Point3D coords) {
		double newHeight = modelBox.getHeight() / 2 - modelBox.getZ() + coords.getZ();
		newHeight = restrictedHeight(paneBox, newHeight);
		modelBox.setHeight(newHeight);
		modelBox.setZ(origTranslateZ - origHeight / 2 + newHeight / 2);
	}
	
	protected void eastResize(ModelBox modelBox, PaneBox paneBox, Point3D coords) {
		double newWidth = modelBox.getWidth() / 2 + modelBox.getX() - coords.getX();
		newWidth = restrictedWidth(paneBox, newWidth);
		modelBox.setWidth(newWidth);
		modelBox.setX(origTranslateX + origWidth / 2 - newWidth / 2);
	}
	
	protected void southResize(ModelBox modelBox, PaneBox paneBox, Point3D coords) {
		double newHeight = modelBox.getHeight() / 2 + modelBox.getZ() - coords.getZ();
		newHeight = restrictedHeight(paneBox, newHeight);
		modelBox.setHeight(newHeight);
		modelBox.setZ(origTranslateZ + origHeight / 2 - newHeight / 2);
	}
	
	protected void westResize(ModelBox modelBox, PaneBox paneBox, Point3D coords) {
		double newWidth = modelBox.getWidth() / 2 - modelBox.getX() + coords.getX();
		newWidth = restrictedWidth(paneBox, newWidth);
		modelBox.setWidth(newWidth);
		modelBox.setX(origTranslateX - origWidth / 2 + newWidth / 2);
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
