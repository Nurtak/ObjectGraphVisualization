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
public class DragMoveController extends DragController {
	
	public void enableDrag(PaneBox paneBox) {
		Group paneBoxGroup = paneBox.get();
		
		paneBoxGroup.setOnMousePressed((MouseEvent me) -> {
			if(isSelected(paneBox) && MouseButton.PRIMARY.equals(me.getButton())) {
	            relMousePosX = me.getX();
	            relMousePosZ = me.getZ();
			}
        });
		
		paneBoxGroup.setOnMouseDragged((MouseEvent me) -> {
			if(paneBoxGroup.focusedProperty().get() && MouseButton.PRIMARY.equals(me.getButton())) {
				setDragInProgress(paneBox, true);
				//TODO improve movement => fix y jump of mouse at fast movements
				//double yPlaneAlpha = Math.abs(me.getY() - paneBoxGroup.getTranslateY());
				//System.out.println(yPlaneAlpha);
				paneBoxGroup.setCursor(Cursor.MOVE);
				paneBox.setTranslateX(paneBox.getTranslateX() + (me.getX() - relMousePosX));
				paneBox.setTranslateZ(paneBox.getTranslateZ() + (me.getZ() - relMousePosZ));
			}
		});
		
		paneBoxGroup.setOnMouseReleased((MouseEvent me) -> {
			if(paneBoxGroup.focusedProperty().get() && MouseButton.PRIMARY.equals(me.getButton())) {
				setDragInProgress(paneBox, false);
				paneBoxGroup.setCursor(Cursor.DEFAULT);
			}
		});
	}
	
}
