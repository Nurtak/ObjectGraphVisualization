package ch.hsr.ogv.controller;

import java.util.Observable;
import java.util.Observer;

import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
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
	protected volatile double origDepth;
	
	protected boolean isSelected(PaneBox paneBox) {
		return this.selected != null && this.selected.equals(paneBox);
	}
	
	protected void setOnMousePressed(Group g, PaneBox paneBox, SubSceneAdapter subSceneAdapter) {
		g.setOnMousePressed((MouseEvent me) -> {
			if(isSelected(paneBox) && MouseButton.PRIMARY.equals(me.getButton())) {
				setDragInProgress(subSceneAdapter, true);
				origRelMouseX = me.getX();
				origRelMouseY = me.getY();
				origRelMouseZ = me.getZ();
				origTranslateX = paneBox.getTranslateX();
				origTranslateY = paneBox.getTranslateY();
				origTranslateZ = paneBox.getTranslateZ();
				origWidth = paneBox.getWidth();
				origHeight = paneBox.getHeight();
				origDepth = paneBox.getDepth();
			}
		});
 	}
	
	protected void setOnMouseReleased(Group g, PaneBox paneBox, SubSceneAdapter subSceneAdapter) {
		g.setOnMouseReleased((MouseEvent me) -> {
			if(MouseButton.PRIMARY.equals(me.getButton())) {
				setDragInProgress(subSceneAdapter, false);
				subSceneAdapter.getSubScene().setCursor(Cursor.DEFAULT);
			}
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
		}
	}
	
}
