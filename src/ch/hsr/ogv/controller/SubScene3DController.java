package ch.hsr.ogv.controller;

import javafx.event.EventHandler;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import ch.hsr.ogv.view.SubScene3D;

public class SubScene3DController {
	
	public void handleMouse(SubScene3D subScene3D) {
		subScene3D.getSubScene().setOnMouseReleased(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent t) {
				// only request focus when left mouse button was clicked on the subscene
				if(MouseButton.PRIMARY.equals(t.getButton()) && t.isDragDetect()) {
					subScene3D.getSubScene().requestFocus();
				}
			}
        });
	}

}
