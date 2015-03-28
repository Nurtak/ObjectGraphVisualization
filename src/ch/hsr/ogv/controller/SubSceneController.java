package ch.hsr.ogv.controller;

import java.util.Observable;

import javafx.event.EventHandler;
import javafx.geometry.Point3D;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import ch.hsr.ogv.view.SubSceneAdapter;

public class SubSceneController extends Observable {
	
	public void handleSubSceneMouse(SubSceneAdapter subSceneAdapter) {
		subSceneAdapter.getSubScene().setOnMouseReleased(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent me) {
				// only request focus when left mouse button was clicked on the subscene
				if (MouseButton.PRIMARY.equals(me.getButton()) && me.isDragDetect()) {
					subSceneAdapter.getSubScene().requestFocus();
				}
			}
		});
		
		subSceneAdapter.getFloor().setOnMouseReleased(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent me) {
				if (MouseButton.PRIMARY.equals(me.getButton()) && me.isDragDetect()) {
					Point3D mouseCoords = new Point3D(me.getX(), me.getY(), me.getZ());
					setChanged();
					notifyObservers(mouseCoords);
					subSceneAdapter.getFloor().requestFocus();
				}
			}
		});
	}

}
