package ch.hsr.ogv.controller;

import java.util.Observable;

import ch.hsr.ogv.view.Cuboid3D;
import ch.hsr.ogv.view.PaneBox3D;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Group;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

public class PaneBox3DController extends Observable {
	
	private volatile boolean isSelected = false;
	private volatile boolean editInProgress = false;
	
	private final static double EDIT_DRAG = 0.5;
	
	
	public boolean isBoxSelected() {
		return isSelected;
	}
	
	public boolean editInProgress() {
		return this.editInProgress;
	}

	public void addAllControls(PaneBox3D paneBox3D) {
		addTopTextListener(paneBox3D);
		addMouseListener(paneBox3D);
		addFocusListener(paneBox3D);
	}
	
	private void setSelected(PaneBox3D paneBox3D, boolean value) {
		this.isSelected = value;
		paneBox3D.setSelected(value);
	}
	
	private void setEditInProgress(PaneBox3D paneBox3D, boolean value) {
		this.editInProgress = value;
		setChanged();
		notifyObservers(paneBox3D);
	}
	
	private void addTopTextListener(PaneBox3D paneBox3D) {
		
		paneBox3D.getTop().focusedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean oldValue, Boolean newValue) {
            	if(paneBox3D.getTop().focusedProperty().get()) {
            		setSelected(paneBox3D, true);
				}
				else {
					//TODO validate input
					setSelected(paneBox3D, false);
            	}
            }
        });
		
		paneBox3D.getTop().textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String oldValue, String newValue) {
            	//TODO validate input
            	paneBox3D.adaptWidthByText(paneBox3D.getTop().getFont(), newValue);
            }
        });
		
		paneBox3D.getTop().setOnKeyReleased((KeyEvent ke) -> {
			if(ke.getCode() == KeyCode.ENTER) {
				//TODO validate input
				paneBox3D.getNode().requestFocus();
			}
		});
	}
	
	private void addMouseListener(PaneBox3D paneBox3D) {
		Group paneBox3DGroup = paneBox3D.getNode();
		
		paneBox3DGroup.setOnMouseDragged((MouseEvent me) -> {
			if(paneBox3DGroup.focusedProperty().get() && MouseButton.PRIMARY.equals(me.getButton())) {
				setEditInProgress(paneBox3D, true);
				//TODO improve movement => fix y jump of mouse at fast movements
				//double yPlaneAlpha = Math.abs(me.getY() - paneBox3DGroup.getTranslateY());
				//System.out.println(yPlaneAlpha);
				paneBox3DGroup.setTranslateX(paneBox3DGroup.getTranslateX() + me.getX() * EDIT_DRAG);
				paneBox3DGroup.setTranslateZ(paneBox3DGroup.getTranslateZ() + me.getZ() * EDIT_DRAG);
			}
		});
		
		paneBox3DGroup.setOnMouseReleased((MouseEvent me) -> {
			if(paneBox3DGroup.focusedProperty().get() && MouseButton.PRIMARY.equals(me.getButton())) {
				setEditInProgress(paneBox3D, false);
			}
		});
        
        paneBox3DGroup.setOnMouseClicked((MouseEvent me) -> {
			paneBox3DGroup.requestFocus();
        });
	}
	
	private void addFocusListener(PaneBox3D paneBox3D) {
		Group paneBox3DGroup = paneBox3D.getNode();
		Cuboid3D box = paneBox3D.getBox();
		Group selection = paneBox3D.getSelection();
       
		box.focusedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				if(!selection.isVisible()) {
					setSelected(paneBox3D, true);
				}
				else {
					setSelected(paneBox3D, false);
				}
			}
        });
        
        paneBox3DGroup.focusedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				if(paneBox3DGroup.focusedProperty().get()) {
					setSelected(paneBox3D, true);
				}
				else {
					setSelected(paneBox3D, false);
				}
			}
        });
	}
	
}
