package ch.hsr.ogv.controller;

import javafx.scene.Group;
import ch.hsr.ogv.view.PaneBox;
import ch.hsr.ogv.view.SubSceneAdapter;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.Cursor;

/**
 * 
 * @author Simon Gwerder
 *
 */
public class DragResizeController extends DragController {
	
	public void enableDragResize(PaneBox paneBox, SubSceneAdapter subSceneAdapter) {
		enableNLine(paneBox, subSceneAdapter);
	}
	
	private void enableNLine(PaneBox paneBox, SubSceneAdapter subSceneAdapter) {
		Group lineN = paneBox.getSelection().getLineN();
		
		lineN.setOnMouseEntered((MouseEvent me) -> {
			lineN.setCursor(Cursor.N_RESIZE);
	    });
		
		lineN.setOnMouseExited((MouseEvent me) -> {
			lineN.setCursor(Cursor.DEFAULT);
	    });
		
		setOnMousePressed(lineN, paneBox, subSceneAdapter);
		setOnMouseDragged(lineN, paneBox, subSceneAdapter);
		setOnMouseReleased(lineN, subSceneAdapter);
	}

	@Override
	protected void setOnMouseDragged(Group g, PaneBox paneBox, SubSceneAdapter subSceneAdapter) {
		g.setOnMouseDragged((MouseEvent me) -> {
			if(MouseButton.PRIMARY.equals(me.getButton())) {
				g.setCursor(Cursor.N_RESIZE);
				
				//TODO: needs lots of improvements
		        double newHeight = paneBox.getHeight() + (me.getZ() - relMousePosZ);
		        paneBox.setHeight(newHeight);
		        relMousePosZ = me.getZ();
			}
		});
	}
		
}
