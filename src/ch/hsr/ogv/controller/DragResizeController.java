package ch.hsr.ogv.controller;

import javafx.scene.Group;
import ch.hsr.ogv.view.PaneBox;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.Cursor;

/**
 * 
 * @author Simon Gwerder
 *
 */
public class DragResizeController extends DragController {
	
	public void enableDrag(PaneBox paneBox) {
		enableNLine(paneBox);
	}
	
	private void enableNLine(PaneBox paneBox) {
		Group lineN = paneBox.getSelection().getLineN();
		
		lineN.setOnMouseEntered((MouseEvent me) -> {
			lineN.setCursor(Cursor.N_RESIZE);
	    });
		
		lineN.setOnMouseExited((MouseEvent me) -> {
			lineN.setCursor(Cursor.DEFAULT);
	    });
		
		lineN.setOnMousePressed((MouseEvent me) -> {
	        if(isSelected(paneBox) && MouseButton.PRIMARY.equals(me.getButton())) {
	        	relMousePosZ = me.getZ();
	        }
	    });
		
		lineN.setOnMouseDragged((MouseEvent me) -> {
			if(MouseButton.PRIMARY.equals(me.getButton())) {
				lineN.setCursor(Cursor.N_RESIZE);
				setDragInProgress(paneBox, true);
				//TODO: needs lots of improvements
		        double newHeight = paneBox.getHeight() + (me.getZ() - relMousePosZ);
		        paneBox.setHeight(newHeight);
		        relMousePosZ = me.getZ();
			}
		});
		
		lineN.setOnMouseReleased((MouseEvent me) -> {
			if(MouseButton.PRIMARY.equals(me.getButton())) {
				setDragInProgress(paneBox, false);
				lineN.setCursor(Cursor.DEFAULT);
			}
		});
	}
		
}
