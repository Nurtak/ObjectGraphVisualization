package ch.hsr.ogv.controller;

import java.util.Observable;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import ch.hsr.ogv.view.PaneBox;
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
	
	public void enableSelection(PaneBox paneBox) {
		focusOnClick(paneBox);
		selectOnFocus(paneBox);
	}
	
	private void focusOnClick(PaneBox paneBox) {
		paneBox.get().setOnMouseClicked((MouseEvent me) -> {
			paneBox.get().requestFocus();
		});
		
		paneBox.getSelection().getLineN().setOnMouseClicked((MouseEvent me) -> {
			setSelected(paneBox, true);
			paneBox.getSelection().getLineN().requestFocus();
        });
	}
	
	private void setSelected(PaneBox paneBox, boolean selected) {
		if(selected) {
			this.selectedBox = paneBox;
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
	
	private void selectOnFocus(PaneBox paneBox) {
		paneBox.get().focusedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean oldValue, Boolean newValue) {
            	if(paneBox.get().focusedProperty().get()) {
            		setSelected(paneBox, true);
				}
				else {
					setSelected(paneBox, false);
            	}
            }
        });
		
		paneBox.getTop().focusedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean oldValue, Boolean newValue) {
            	if(paneBox.getTop().focusedProperty().get()) {
            		setSelected(paneBox, true);
				}
				else {
					setSelected(paneBox, false);
            	}
            }
        });
		
		paneBox.getBox().focusedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean oldValue, Boolean newValue) {
            	if(paneBox.getBox().focusedProperty().get()) {
            		setSelected(paneBox, true);
				}
				else {
					setSelected(paneBox, false);
            	}
            }
        });
		
		paneBox.getSelection().getLineN().focusedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean oldValue, Boolean newValue) {
            	if(paneBox.getSelection().getLineN().focusedProperty().get()) {
            		setSelected(paneBox, true);
				}
				else {
					setSelected(paneBox, false);
            	}
            }
        });
		
	}

}
