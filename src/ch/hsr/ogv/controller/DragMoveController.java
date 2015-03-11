package ch.hsr.ogv.controller;

import java.util.Observable;
import java.util.Observer;

import javafx.scene.Group;
import ch.hsr.ogv.view.PaneBox3D;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.Cursor;

/**
 * 
 * @author Simon Gwerder
 *
 */
public class DragMoveController extends Observable implements Observer {
	
	private volatile PaneBox3D selected = null;
	private volatile boolean dragInProgress = false;
	
	private volatile double relMousePosX;
	private volatile double relMousePosZ;
	
	protected void setDragInProgress(PaneBox3D paneBox3D, boolean value) {
		this.dragInProgress = value;
		setChanged();
		notifyObservers(paneBox3D);
	}
	
	public boolean isDragInProgress() {
		return this.dragInProgress;
	}
	
	public void enableDragMove(PaneBox3D paneBox3D) {
		Group paneBox3DGroup = paneBox3D.getPaneBox();
		
		paneBox3DGroup.setOnMousePressed((MouseEvent me) -> {
			if(isSelected(paneBox3D) && MouseButton.PRIMARY.equals(me.getButton())) {
	            relMousePosX = me.getX();
	            relMousePosZ = me.getZ();
			}
        });
		
		paneBox3DGroup.setOnMouseDragged((MouseEvent me) -> {
			if(paneBox3DGroup.focusedProperty().get() && MouseButton.PRIMARY.equals(me.getButton())) {
				setDragInProgress(paneBox3D, true);
				//TODO improve movement => fix y jump of mouse at fast movements
				//double yPlaneAlpha = Math.abs(me.getY() - paneBox3DGroup.getTranslateY());
				//System.out.println(yPlaneAlpha);
				paneBox3DGroup.setCursor(Cursor.MOVE);
				paneBox3D.setTranslateX(paneBox3D.getTranslateX() + (me.getX() - relMousePosX));
				paneBox3D.setTranslateZ(paneBox3D.getTranslateZ() + (me.getZ() - relMousePosZ));
			}
		});
		
		paneBox3DGroup.setOnMouseReleased((MouseEvent me) -> {
			if(paneBox3DGroup.focusedProperty().get() && MouseButton.PRIMARY.equals(me.getButton())) {
				setDragInProgress(paneBox3D, false);
				paneBox3DGroup.setCursor(Cursor.DEFAULT);
			}
		});
	}
	
	private boolean isSelected(PaneBox3D paneBox3D) {
		return this.selected != null && this.selected.equals(paneBox3D);
	}

	@Override
	public void update(Observable o, Object arg) {
		if(o instanceof SelectionController) {
			SelectionController selectionController = (SelectionController) o;
			this.selected = selectionController.getSelected();
		}
	}

}
