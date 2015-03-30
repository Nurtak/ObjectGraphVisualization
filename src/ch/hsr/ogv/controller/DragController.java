package ch.hsr.ogv.controller;

import java.util.Observable;
import java.util.Observer;


import javafx.geometry.Point3D;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import ch.hsr.ogv.model.ModelClass;
import ch.hsr.ogv.view.PaneBox;
import ch.hsr.ogv.view.SubSceneAdapter;

/**
 * 
 * @author Simon Gwerder
 *
 */
public abstract class DragController extends Observable implements Observer {

	private volatile PaneBox selected = null;
	private volatile boolean dragInProgress = false;
	
	protected volatile double origRelMouseX;
	protected volatile double origRelMouseY;
	protected volatile double origRelMouseZ;
	
	protected volatile double origTranslateX;
	protected volatile double origTranslateY;
	protected volatile double origTranslateZ;
	
	protected volatile double origWidth;
	protected volatile double origHeight;
	//protected volatile double origDepth;
	
	protected boolean isSelected(PaneBox paneBox) {
		return this.selected != null && this.selected.equals(paneBox);
	}
	
	protected void setOnMousePressed(Group g, ModelClass theClass, PaneBox paneBox, SubSceneAdapter subSceneAdapter) {
		g.setOnMousePressed((MouseEvent me) -> {
			if(isSelected(paneBox) && MouseButton.PRIMARY.equals(me.getButton())) {
				setDragInProgress(subSceneAdapter, true);
				origRelMouseX = me.getX();
				origRelMouseY = me.getY();
				origRelMouseZ = me.getZ();
				Point3D origCoords = theClass.getCoordinates();
				origTranslateX = origCoords.getX();
				origTranslateY = origCoords.getY();
				origTranslateZ = origCoords.getZ();
				origWidth = theClass.getWidth();
				origHeight = theClass.getHeight();
				//origDepth = paneBox.getDepth();
			}
		});
 	}
	
	protected void setOnMouseReleased(Group g, SubSceneAdapter subSceneAdapter) {
		g.setOnMouseReleased((MouseEvent me) -> {
			setDragInProgress(subSceneAdapter, false);
			subSceneAdapter.getSubScene().setCursor(Cursor.DEFAULT);
		});
	}
	
	protected void setDragInProgress(SubSceneAdapter subSceneAdapter, boolean value) {
		this.dragInProgress = value;
		subSceneAdapter.onlyFloorMouseEvent(value);
		setChanged();
		notifyObservers(this.selected);
	}
	
	public boolean isDragInProgress() {
		return this.dragInProgress;
	}
	
	@Override
	public void update(Observable o, Object arg) {
		if(o instanceof SelectionController) {
			SelectionController selectionController = (SelectionController) o;
			this.selected = selectionController.getSelected();
			Point3D selectionCoodinates = selectionController.getSelectionCoordinates();
			if(selectionController.getSelectionCoordinates() != null) {
				origRelMouseX = selectionCoodinates.getX();
				origRelMouseY = selectionCoodinates.getY();
				origRelMouseZ = selectionCoodinates.getZ();
			}
		}
	}
	
}
