package ch.hsr.ogv.controller;

import java.util.Observable;

import javafx.geometry.Point3D;
import ch.hsr.ogv.view.Arrow;
import ch.hsr.ogv.view.PaneBox;
import ch.hsr.ogv.view.Selectable;
import ch.hsr.ogv.view.SubSceneAdapter;
import javafx.scene.SubScene;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

/**
 * 
 * @author Simon Gwerder
 *
 */
public class SelectionController extends Observable {
	
	private volatile Selectable selected = null;
	
	private Point3D selectionCoordinates;
	
	public Point3D getSelectionCoordinates() {
		return selectionCoordinates;
	}
	
	public boolean hasSelection() {
		return this.selected != null;
	}

	public Selectable getSelected() {
		return this.selected;
	}
	
	public boolean isSelected(Selectable selectable) {
		return this.selected != null && this.selected.equals(selectable);
	}
	
	public void enableSelection(Selectable selectable, SubSceneAdapter subSceneAdapter) {
		if(selectable instanceof PaneBox) {
			PaneBox paneBox = (PaneBox) selectable;
			setOnMouseOnClicked(paneBox, subSceneAdapter);
			setOnDragDetected(paneBox, subSceneAdapter);
		}
		else if (selectable instanceof Arrow) {
			Arrow arrow = (Arrow) selectable;
			setOnMouseClicked(arrow, subSceneAdapter);
		}
		else if (selectable instanceof SubSceneAdapter) {
			setOnMouseClicked(subSceneAdapter);
		}
	}
	
	private void setOnMouseClicked(SubSceneAdapter subSceneAdapter) {
		subSceneAdapter.getSubScene().setOnMouseClicked((MouseEvent me) -> {
			if (MouseButton.PRIMARY.equals(me.getButton()) && me.isDragDetect() && me.getPickResult().getIntersectedNode() instanceof SubScene) {
				setSelected(me, subSceneAdapter, true, subSceneAdapter);
			}
		});
		
		subSceneAdapter.getFloor().setOnMouseReleased((MouseEvent me) -> {
			if (MouseButton.PRIMARY.equals(me.getButton()) && me.isDragDetect()) {
				setSelected(me, subSceneAdapter.getFloor(), true, subSceneAdapter);
			}
		});
	}
	
	private void setOnMouseOnClicked(PaneBox paneBox, SubSceneAdapter subSceneAdapter) {
		paneBox.getTopLabel().setOnMouseClicked((MouseEvent me) -> {
			if(MouseButton.PRIMARY.equals(me.getButton()) && !paneBox.isSelected()) {
				setSelected(me, paneBox, true, subSceneAdapter);
			}
			if(MouseButton.PRIMARY.equals(me.getButton()) && paneBox.isSelected() && me.getClickCount() >= 2) {
				paneBox.allowTopTextInput(true);
			}
		});
		
		paneBox.getCenter().setOnMouseClicked((MouseEvent me) -> {
			if(MouseButton.PRIMARY.equals(me.getButton()) && !paneBox.isSelected()) {
				setSelected(me, paneBox, true, subSceneAdapter);
			}
        });
		
		paneBox.getBox().setOnMouseClicked((MouseEvent me) -> {
			if(MouseButton.PRIMARY.equals(me.getButton()) && !paneBox.isSelected()) {
				setSelected(me, paneBox, true, subSceneAdapter);
			}
        });
	}
	
	private void setOnMouseClicked(Arrow arrow, SubSceneAdapter subSceneAdapter) {
		arrow.setOnMouseClicked((MouseEvent me) -> {
			if(MouseButton.PRIMARY.equals(me.getButton()) && !arrow.isSelected()) {
				setSelected(me, arrow, true, subSceneAdapter);
			}
		});
	}
	
	private void setOnDragDetected(PaneBox paneBox, SubSceneAdapter subSceneAdapter) {
		paneBox.getTopLabel().setOnDragDetected((MouseEvent me) -> {
			if(MouseButton.PRIMARY.equals(me.getButton()) && me.isDragDetect() && !paneBox.isSelected()) {
				setSelected(me, paneBox, true, subSceneAdapter);
			}
		});
		
		paneBox.getCenter().setOnDragDetected((MouseEvent me) -> {
			if(MouseButton.PRIMARY.equals(me.getButton()) && me.isDragDetect() && !paneBox.isSelected()) {
				setSelected(me, paneBox, true, subSceneAdapter);
			}
        });
		
		paneBox.getBox().setOnDragDetected((MouseEvent me) -> {
			if(MouseButton.PRIMARY.equals(me.getButton()) && me.isDragDetect() && !paneBox.isSelected()) {
				setSelected(me, paneBox, true, subSceneAdapter);
			}
        });
	}
	
	private void setSelected(MouseEvent me, Selectable selectable, boolean selected, SubSceneAdapter subSceneAdapter) {
		this.selectionCoordinates = new Point3D(me.getX(), me.getY(), me.getZ());
		setSelected(selectable, selected, subSceneAdapter);
	}
	
	private void setSelected(Selectable selectable, boolean selected, SubSceneAdapter subSceneAdapter) {
		selectable.setSelected(selected);

		if(selected) {
			if(this.selected != null) {
				this.selected.setSelected(false); // deselect the old selected object
			}
			
			this.selected = selectable;
			
			selectable.requestFocus();
			
			if(selectable instanceof PaneBox) {
				PaneBox paneBox = (PaneBox) selectable;
				paneBox.get().toFront();
				subSceneAdapter.getFloor().toFront();
			}
			setChanged();
			notifyObservers(selectable);
		}
		else {
			this.selected = null;
			setChanged();
			notifyObservers(selectable);
		}
	}

}
