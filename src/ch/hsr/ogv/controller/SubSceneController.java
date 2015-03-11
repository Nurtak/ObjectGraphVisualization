package ch.hsr.ogv.controller;

import javafx.event.EventHandler;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import ch.hsr.ogv.view.SubSceneAdapter;

public class SubSceneController {
	
	public void handleMouse(SubSceneAdapter subSceneAdapter) {
		subSceneAdapter.getSubScene().setOnMouseReleased(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent t) {
				// only request focus when left mouse button was clicked on the subscene
				if(MouseButton.PRIMARY.equals(t.getButton()) && t.isDragDetect()) {
					subSceneAdapter.getSubScene().requestFocus();
				}
			}
        });
	}

}
