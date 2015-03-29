package ch.hsr.ogv.controller;

import java.util.Observable;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import ch.hsr.ogv.view.PaneBox;
import ch.hsr.ogv.view.SubSceneAdapter;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

/**
 * 
 * @author Simon Gwerder
 *
 */
public class SelectionController extends Observable {
	
	private volatile PaneBox selectedBox = null;
	
	public boolean hasSelection() {
		return this.selectedBox != null;
	}

	public PaneBox getSelected() {
		return this.selectedBox;
	}
	
	public void enableSelection(PaneBox paneBox, SubSceneAdapter subSceneAdapter) {
		focusOnClick(paneBox, subSceneAdapter);
		focusOnDragDetected(paneBox, subSceneAdapter);
		selectOnFocus(paneBox, subSceneAdapter);
	}
	
	private void focusOnClick(PaneBox paneBox, SubSceneAdapter subSceneAdapter) {
		paneBox.get().setOnMouseClicked((MouseEvent me) -> {
			if(MouseButton.PRIMARY.equals(me.getButton()) && me.isDragDetect() && !paneBox.get().focusedProperty().get()) { // if not already focused
				paneBox.get().requestFocus();
			}
		});
		
//		paneBox.getSelection().setOnMouseClicked((MouseEvent me) -> {
//			if(MouseButton.PRIMARY.equals(me.getButton()) && me.isDragDetect()) {
//				setSelected(paneBox, true, subSceneAdapter);
//				paneBox.get().requestFocus();
//			}
//        });

		
	}
	
	private void focusOnDragDetected(PaneBox paneBox, SubSceneAdapter subSceneAdapter) {
		paneBox.get().setOnDragDetected((MouseEvent me) -> {
			if(MouseButton.PRIMARY.equals(me.getButton()) && me.isDragDetect() && !paneBox.get().focusedProperty().get()) { // if not already focused
				paneBox.get().requestFocus();
			}
		});
		
//		paneBox.getSelection().setOnDragDetected((MouseEvent me) -> {
//			if(MouseButton.PRIMARY.equals(me.getButton()) && me.isDragDetect()) {
//				setSelected(paneBox, true, subSceneAdapter);
//				paneBox.get().requestFocus();
//			}
//        });
	}
	
	private void setSelected(PaneBox paneBox, boolean selected, SubSceneAdapter subSceneAdapter) {
		if(selected) {
			this.selectedBox = paneBox;
			paneBox.get().toFront();
			subSceneAdapter.getFloor().toFront();
			setChanged();
			notifyObservers(paneBox);
		}
		else {
			this.selectedBox = null;
			setChanged();
			notifyObservers(paneBox);
		}
		paneBox.setSelected(selected);
	}
	
	private void selectOnFocus(PaneBox paneBox, SubSceneAdapter subSceneAdapter) {
		paneBox.get().focusedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean oldValue, Boolean newValue) {
            	if(paneBox.get().focusedProperty().get()) {
            		setSelected(paneBox, true, subSceneAdapter);
				}
				else {
					setSelected(paneBox, false, subSceneAdapter);
            	}
            }
        });
		
		paneBox.getTopLabel().focusedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean oldValue, Boolean newValue) {
            	if(paneBox.getTopLabel().focusedProperty().get()) {
            		setSelected(paneBox, true, subSceneAdapter);
				}
				else {
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
