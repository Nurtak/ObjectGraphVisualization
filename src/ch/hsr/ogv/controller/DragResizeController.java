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
public class DragResizeController extends DragController {
	
	public void enableDragResize(PaneBox paneBox, SubSceneAdapter subSceneAdapter) {
		enableDirection(paneBox.getSelection().getLineN(),   Cursor.N_RESIZE, paneBox, subSceneAdapter);
		enableDirection(paneBox.getSelection().getPointNE(), Cursor.NE_RESIZE, paneBox, subSceneAdapter);
		enableDirection(paneBox.getSelection().getLineE(),   Cursor.E_RESIZE, paneBox, subSceneAdapter);
		enableDirection(paneBox.getSelection().getPointSE(), Cursor.SE_RESIZE, paneBox, subSceneAdapter);
		enableDirection(paneBox.getSelection().getLineS(),   Cursor.S_RESIZE, paneBox, subSceneAdapter);
		enableDirection(paneBox.getSelection().getPointSW(), Cursor.SW_RESIZE, paneBox, subSceneAdapter);
		enableDirection(paneBox.getSelection().getLineW(),   Cursor.W_RESIZE, paneBox, subSceneAdapter);
		enableDirection(paneBox.getSelection().getPointNW(), Cursor.NW_RESIZE, paneBox, subSceneAdapter);
	}
	
	private void enableDirection(Group g, Cursor direction, PaneBox paneBox, SubSceneAdapter subSceneAdapter) {
		g.setOnMouseEntered((MouseEvent me) -> {
			subSceneAdapter.getSubScene().setCursor(direction);
	    });
		
		g.setOnMouseExited((MouseEvent me) -> {
			subSceneAdapter.getSubScene().setCursor(Cursor.DEFAULT);
	    });
		
		setOnMousePressed(g, paneBox, subSceneAdapter);
		setOnMouseDragged(g, paneBox, subSceneAdapter, direction);
		setOnMouseReleased(g, paneBox, subSceneAdapter);
	}
	
	protected void setOnMouseDragged(Group g, PaneBox paneBox, SubSceneAdapter subSceneAdapter, Cursor direction) {
		Floor floor = subSceneAdapter.getFloor();
		g.setOnMouseDragged((MouseEvent me) -> {
			setDragInProgress(subSceneAdapter, true);
			if(MouseButton.PRIMARY.equals(me.getButton())) {
				subSceneAdapter.getSubScene().setCursor(direction);
				PickResult pick = me.getPickResult();
				if(pick != null && pick.getIntersectedNode() != null && floor.hasTile(pick.getIntersectedNode())) {
					Point3D coords = pick.getIntersectedNode().localToParent(pick.getIntersectedPoint());
					if(Cursor.N_RESIZE.equals(direction)) {
						northResize(paneBox, coords);
					}
					else if(Cursor.NE_RESIZE.equals(direction)) {
						northResize(paneBox, coords);
						eastResize(paneBox, coords);
					}
					else if(Cursor.E_RESIZE.equals(direction)) {
						eastResize(paneBox, coords);
					}
					else if(Cursor.SE_RESIZE.equals(direction)) {
						southResize(paneBox, coords);
						eastResize(paneBox, coords);
					}
					else if(Cursor.S_RESIZE.equals(direction)) {
						southResize(paneBox, coords);
					}
					else if(Cursor.SW_RESIZE.equals(direction)) {
						southResize(paneBox, coords);
						westResize(paneBox, coords);
					}
					else if(Cursor.W_RESIZE.equals(direction)) {
						westResize(paneBox, coords);
					}
					else if(Cursor.NW_RESIZE.equals(direction)) {
						northResize(paneBox, coords);
						westResize(paneBox, coords);
					}
				}
			}
		});
	}
	
	protected void northResize(PaneBox paneBox, Point3D coords) {
		double newHeight = paneBox.getHeight() / 2 - paneBox.getTranslateZ() + coords.getZ();
		newHeight = restrictedHeight(paneBox, newHeight);
		paneBox.setHeight(newHeight);
		paneBox.setTranslateZ(origTranslateZ - origHeight / 2 + newHeight / 2);
	}
	
	protected void eastResize(PaneBox paneBox, Point3D coords) {
		double newWidth = paneBox.getWidth() / 2 + paneBox.getTranslateX() - coords.getX();
		newWidth = restrictedWidth(paneBox, newWidth);
		paneBox.setWidth(newWidth);
		paneBox.setTranslateX(origTranslateX + origWidth / 2 - newWidth / 2);
	}
	
	protected void southResize(PaneBox paneBox, Point3D coords) {
		double newHeight = paneBox.getHeight() / 2 + paneBox.getTranslateZ() - coords.getZ();
		newHeight = restrictedHeight(paneBox, newHeight);
		paneBox.setHeight(newHeight);
		paneBox.setTranslateZ(origTranslateZ + origHeight / 2 - newHeight / 2);
	}
	
	protected void westResize(PaneBox paneBox, Point3D coords) {
		double newWidth = paneBox.getWidth() / 2 - paneBox.getTranslateX() + coords.getX();
		newWidth = restrictedWidth(paneBox, newWidth);
		paneBox.setWidth(newWidth);
		paneBox.setTranslateX(origTranslateX - origWidth / 2 + newWidth / 2);
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
