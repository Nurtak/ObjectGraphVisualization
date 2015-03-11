package ch.hsr.ogv.controller;

import java.util.Observable;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import ch.hsr.ogv.view.PaneBox3D;
import javafx.scene.input.MouseEvent;

/**
 * 
 * @author Simon Gwerder
 *
 */
public class SelectionController extends Observable {
	
	private volatile PaneBox3D selectedBox = null;
	
	public boolean hasSelection() {
		return this.selectedBox != null;
	}

	public PaneBox3D getSelected() {
		return this.selectedBox;
	}
	
	public void enableSelection(PaneBox3D paneBox3D) {
		focusOnClick(paneBox3D);
		selectOnFocus(paneBox3D);
	}
	
	private void focusOnClick(PaneBox3D paneBox3D) {
		paneBox3D.getPaneBox().setOnMouseClicked((MouseEvent me) -> {
			paneBox3D.getPaneBox().requestFocus();
		});
	}
	
	private void setSelected(PaneBox3D paneBox3D, boolean selected) {
		if(selected) {
			this.selectedBox = paneBox3D;
			setChanged();
			notifyObservers(paneBox3D);
		}
		else {
			this.selectedBox = null;
			setChanged();
			notifyObservers(paneBox3D);
		}
		paneBox3D.setSelected(selected);
	}
	
	private void selectOnFocus(PaneBox3D paneBox3D) {
		paneBox3D.getPaneBox().focusedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean oldValue, Boolean newValue) {
            	if(paneBox3D.getPaneBox().focusedProperty().get()) {
            		setSelected(paneBox3D, true);
				}
				else {
					setSelected(paneBox3D, false);
            	}
            }
        });
		
		paneBox3D.getTop().focusedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean oldValue, Boolean newValue) {
            	if(paneBox3D.getTop().focusedProperty().get()) {
            		setSelected(paneBox3D, true);
				}
				else {
					setSelected(paneBox3D, false);
            	}
            }
        });
		
		paneBox3D.getBox().focusedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean oldValue, Boolean newValue) {
            	if(paneBox3D.getBox().focusedProperty().get()) {
            		setSelected(paneBox3D, true);
				}
				else {
					setSelected(paneBox3D, false);
            	}
            }
        });
	}

}
