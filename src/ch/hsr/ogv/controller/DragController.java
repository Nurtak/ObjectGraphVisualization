package ch.hsr.ogv.controller;

import java.util.Observable;
import java.util.Observer;

import ch.hsr.ogv.view.PaneBox;

public abstract class DragController extends Observable implements Observer {

	private volatile PaneBox selected = null;
	private volatile boolean dragInProgress = false;
	
	protected volatile double relMousePosX;
	protected volatile double relMousePosZ;
	
	protected boolean isSelected(PaneBox paneBox) {
		return this.selected != null && this.selected.equals(paneBox);
	}
	
	protected void setDragInProgress(PaneBox paneBox, boolean value) {
		this.dragInProgress = value;
		setChanged();
		notifyObservers(paneBox);
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
