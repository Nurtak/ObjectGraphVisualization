package ch.hsr.ogv.controller;

import java.util.Observable;

import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.input.MouseEvent;
import ch.hsr.ogv.view.PaneBox;
import ch.hsr.ogv.view.SubSceneAdapter;

/**
 * 
 * @author Simon Gwerder
 *
 */
public abstract class DragController extends Observable {

	private volatile PaneBox selected = null;
	private volatile boolean dragInProgress = false;
	
	protected void endOnMouseReleased(Group g, SubSceneAdapter subSceneAdapter) {
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

}
