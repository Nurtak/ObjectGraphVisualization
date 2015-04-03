package ch.hsr.ogv.controller;

import java.util.Observable;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Point3D;
import ch.hsr.ogv.view.PaneBox;
import ch.hsr.ogv.view.Selectable;
import ch.hsr.ogv.view.SubSceneAdapter;
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
	
	public boolean isSelected(Selectable selecable) {
		return this.selected != null && this.selected.equals(selecable);
	}
	
	public void enableSelection(PaneBox paneBox, SubSceneAdapter subSceneAdapter) {
		focusOnClick(paneBox, subSceneAdapter);
		focusOnDragDetected(paneBox, subSceneAdapter);
		selectOnFocus(paneBox, subSceneAdapter);
	}
	
	private void focusOnClick(PaneBox paneBox, SubSceneAdapter subSceneAdapter) {
		paneBox.getTopLabel().setOnMouseClicked((MouseEvent me) -> {
			if(MouseButton.PRIMARY.equals(me.getButton())) { // if not already focused
				this.selectionCoordinates = new Point3D(me.getX(), me.getY(), me.getZ());
				paneBox.getTopLabel().requestFocus();
			}
			if(MouseButton.PRIMARY.equals(me.getButton()) && paneBox.getTopLabel().focusedProperty().get() && me.getClickCount() >= 2) {
				paneBox.allowTopTextInput(true);
			}
		});
		
		paneBox.getCenter().setOnMouseClicked((MouseEvent me) -> {
			if(MouseButton.PRIMARY.equals(me.getButton())) {
				this.selectionCoordinates = new Point3D(me.getX(), me.getY(), me.getZ());
				paneBox.getCenter().requestFocus();
			}
        });
		
		paneBox.getBox().setOnMouseClicked((MouseEvent me) -> {
			if(MouseButton.PRIMARY.equals(me.getButton())) {
				this.selectionCoordinates = new Point3D(me.getX(), me.getY(), me.getZ());
				paneBox.getBox().requestFocus();
			}
        });
	}
	
	private void focusOnDragDetected(PaneBox paneBox, SubSceneAdapter subSceneAdapter) {
		paneBox.getTopLabel().setOnDragDetected((MouseEvent me) -> {
			if(MouseButton.PRIMARY.equals(me.getButton()) && me.isDragDetect() && !paneBox.getTopLabel().focusedProperty().get()) { // if not already focused
				this.selectionCoordinates = new Point3D(me.getX(), me.getY(), me.getZ());
				paneBox.getTopLabel().requestFocus();
			}
		});
		
		paneBox.getCenter().setOnDragDetected((MouseEvent me) -> {
			if(MouseButton.PRIMARY.equals(me.getButton()) && me.isDragDetect() && !paneBox.getCenter().focusedProperty().get()) { // if not already focused
				this.selectionCoordinates = new Point3D(me.getX(), me.getY(), me.getZ());
				paneBox.getCenter().requestFocus();
			}
        });
		
		paneBox.getBox().setOnDragDetected((MouseEvent me) -> {
			if(MouseButton.PRIMARY.equals(me.getButton()) && me.isDragDetect() && !paneBox.getBox().focusedProperty().get()) { // if not already focused
				this.selectionCoordinates = new Point3D(me.getX(), me.getY(), me.getZ());
				paneBox.getBox().requestFocus();
			}
        });
	}
	
	private void setSelected(PaneBox paneBox, boolean selected, SubSceneAdapter subSceneAdapter) {
		if(selected) {
			this.selected = paneBox;
			paneBox.get().toFront();
			subSceneAdapter.getFloor().toFront();
			setChanged();
			notifyObservers(paneBox);
		}
		else {
			this.selected = null;
			setChanged();
			notifyObservers(paneBox);
		}
		paneBox.setSelected(selected);
	}
	
	private void selectOnFocus(PaneBox paneBox, SubSceneAdapter subSceneAdapter) {
		paneBox.getTopLabel().focusedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean oldValue, Boolean newValue) {
            	if(paneBox.getTopLabel().focusedProperty().get()) {
            		setSelected(paneBox, true, subSceneAdapter);
				}
				else if(!paneBox.getTopTextField().focusedProperty().get()) {
					setSelected(paneBox, false, subSceneAdapter);
            	}
            }
        });
		
		paneBox.getTopTextField().focusedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean oldValue, Boolean newValue) {
            	if(paneBox.getTopTextField().focusedProperty().get()) {
            		setSelected(paneBox, true, subSceneAdapter);
				}
				else {
					setSelected(paneBox, false, subSceneAdapter);
            	}
            }
        });
		
		paneBox.getCenter().focusedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean oldValue, Boolean newValue) {
            	if(paneBox.getCenter().focusedProperty().get()) {
            		setSelected(paneBox, true, subSceneAdapter);
				}
				else {
					setSelected(paneBox, false, subSceneAdapter);
            	}
            }
        });
		
		paneBox.getBox().focusedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean oldValue, Boolean newValue) {
            	if(paneBox.getBox().focusedProperty().get()) {
            		setSelected(paneBox, true, subSceneAdapter);
				}
				else {
					setSelected(paneBox, false, subSceneAdapter);
            	}
            }
        });
	
		paneBox.getSelection().focusedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean oldValue, Boolean newValue) {
            	if(paneBox.getSelection().focusedProperty().get()) {
            		setSelected(paneBox, true, subSceneAdapter);
				}
				else {
					setSelected(paneBox, false, subSceneAdapter);
            	}
            }
        });
		
	}

}
