package ch.hsr.ogv.controller;

import java.util.Observable;



import ch.hsr.ogv.view.Cuboid3D;
import ch.hsr.ogv.view.PaneBox3D;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.SubScene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

public class PaneBox3DController extends Observable {
	
	private volatile boolean isSelected = false;
	private volatile boolean editInProgress = false;
	
	private final static double EDIT_DRAG = 1;
	
	public boolean isBoxSelected() {
		return isSelected;
	}
	
	public boolean editInProgress() {
		return this.editInProgress;
	}

	public void addAllControls(SubScene subScene, PaneBox3D paneBox3D) {
		addTopTextListener(paneBox3D);
		addGroupMouseListener(subScene, paneBox3D);
		addGroupFocusListener(paneBox3D);
		addSelectionListener(subScene, paneBox3D);
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
	
	private double relMousePosX;
	private double relMousePosZ;
	
	private void addGroupMouseListener(SubScene subScene, PaneBox3D paneBox3D) {
		Group paneBox3DGroup = paneBox3D.getNode();

		paneBox3DGroup.setOnMousePressed((MouseEvent me) -> {
			if(isSelected) {
				subScene.setCursor(Cursor.MOVE);
	            relMousePosX = me.getX();
	            relMousePosZ = me.getZ();
			}
        });
		
		paneBox3DGroup.setOnMouseDragged((MouseEvent me) -> {
			if(paneBox3DGroup.focusedProperty().get() && MouseButton.PRIMARY.equals(me.getButton())) {
				setEditInProgress(paneBox3D, true);
				//TODO improve movement => fix y jump of mouse at fast movements
				//double yPlaneAlpha = Math.abs(me.getY() - paneBox3DGroup.getTranslateY());
				//System.out.println(yPlaneAlpha);
				
				paneBox3DGroup.setTranslateX(paneBox3DGroup.getTranslateX() + (me.getX() - relMousePosX) * EDIT_DRAG);
				paneBox3DGroup.setTranslateZ(paneBox3DGroup.getTranslateZ() + (me.getZ() - relMousePosZ) * EDIT_DRAG);
			}
		});
		
		paneBox3DGroup.setOnMouseReleased((MouseEvent me) -> {
			if(paneBox3DGroup.focusedProperty().get() && MouseButton.PRIMARY.equals(me.getButton())) {
				setEditInProgress(paneBox3D, false);
				subScene.setCursor(Cursor.DEFAULT);
			}
		});
        
        paneBox3DGroup.setOnMouseClicked((MouseEvent me) -> {
			paneBox3DGroup.requestFocus();
        });
	}
	
	private void addGroupFocusListener(PaneBox3D paneBox3D) {
		Group paneBox3DGroup = paneBox3D.getNode();
		Cuboid3D box = paneBox3D.getBox();
		Group selection = paneBox3D.getSelection3D().getNode();
       
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
	
	private void addSelectionListener(SubScene subScene, PaneBox3D paneBox3D) {
		Group lineN = paneBox3D.getSelection3D().getLineN();
		lineN.setOnMouseEntered((MouseEvent me) -> {
			subScene.setCursor(Cursor.N_RESIZE);
        });
		
		lineN.setOnMouseExited((MouseEvent me) -> {
			subScene.setCursor(Cursor.DEFAULT);
        });
		
		Group lineE = paneBox3D.getSelection3D().getLineE();
		lineE.setOnMouseEntered((MouseEvent me) -> {
			subScene.setCursor(Cursor.E_RESIZE);
        });
		
		lineE.setOnMouseExited((MouseEvent me) -> {
			subScene.setCursor(Cursor.DEFAULT);
        });
		
		Group lineS = paneBox3D.getSelection3D().getLineS();
		lineS.setOnMouseEntered((MouseEvent me) -> {
			subScene.setCursor(Cursor.S_RESIZE);
        });
		
		lineS.setOnMouseExited((MouseEvent me) -> {
			subScene.setCursor(Cursor.DEFAULT);
        });
		
		Group lineW = paneBox3D.getSelection3D().getLineW();
		lineW.setOnMouseEntered((MouseEvent me) -> {
			subScene.setCursor(Cursor.W_RESIZE);
        });
		
		lineW.setOnMouseExited((MouseEvent me) -> {
			subScene.setCursor(Cursor.DEFAULT);
        });
		
		Group pointNE = paneBox3D.getSelection3D().getPointNE();
		pointNE.setOnMouseEntered((MouseEvent me) -> {
			subScene.setCursor(Cursor.NE_RESIZE);
        });
		pointNE.setOnMouseExited((MouseEvent me) -> {
			subScene.setCursor(Cursor.DEFAULT);
        });
		
		Group pointSE = paneBox3D.getSelection3D().getPointSE();
		pointSE.setOnMouseEntered((MouseEvent me) -> {
			subScene.setCursor(Cursor.SE_RESIZE);
        });
		pointSE.setOnMouseExited((MouseEvent me) -> {
			subScene.setCursor(Cursor.DEFAULT);
        });
		
		Group pointSW = paneBox3D.getSelection3D().getPointSW();
		pointSW.setOnMouseEntered((MouseEvent me) -> {
			subScene.setCursor(Cursor.SW_RESIZE);
        });
		pointSW.setOnMouseExited((MouseEvent me) -> {
			subScene.setCursor(Cursor.DEFAULT);
        });
		
		Group pointNW = paneBox3D.getSelection3D().getPointNW();
		pointNW.setOnMouseEntered((MouseEvent me) -> {
			subScene.setCursor(Cursor.NW_RESIZE);
        });
		pointNW.setOnMouseExited((MouseEvent me) -> {
			subScene.setCursor(Cursor.DEFAULT);
        });
	}
	
}
