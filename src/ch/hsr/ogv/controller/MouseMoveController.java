package ch.hsr.ogv.controller;

import java.util.Observable;

import javafx.geometry.Point3D;
import javafx.scene.input.MouseEvent;
import ch.hsr.ogv.view.SubSceneAdapter;

public class MouseMoveController extends Observable {

	public void enableSubSceneMouseMove(SubSceneAdapter subSceneAdapter) {
		onMouseMove(subSceneAdapter);
	}
	
	private void onMouseMove(SubSceneAdapter subSceneAdapter) {
		subSceneAdapter.getSubScene().addEventHandler(MouseEvent.MOUSE_MOVED, (MouseEvent me) -> {
			setChanged();
			notifyObservers(new Point3D(me.getX(), me.getY(), me.getZ()));
		});
		
		subSceneAdapter.getFloor().addEventHandler(MouseEvent.MOUSE_MOVED, (MouseEvent me) -> {
			setChanged();
			notifyObservers(new Point3D(me.getX(), me.getY(), me.getZ()));
		});
	}

}
