package ch.hsr.ogv.controller;

import java.util.Observable;

import javafx.scene.input.MouseEvent;
import javafx.scene.input.PickResult;
import ch.hsr.ogv.view.SubSceneAdapter;

public class MouseMoveController extends Observable {

	public void enableSubSceneMouseMove(SubSceneAdapter subSceneAdapter) {
		onMouseMove(subSceneAdapter);
	}
	
	private void onMouseMove(SubSceneAdapter subSceneAdapter) {
//		subSceneAdapter.getSubScene().addEventHandler(MouseEvent.MOUSE_MOVED, (MouseEvent me) -> {
//			setChanged();
//			notifyObservers(new Point3D(me.getPickResult().getIntersectedPoint().getX(), me.getPickResult().getIntersectedPoint().getY(), me.getPickResult().getIntersectedPoint().getZ()));
//		});
		
		subSceneAdapter.getFloor().addEventHandler(MouseEvent.MOUSE_MOVED, (MouseEvent me) -> {
			//Floor floor = subSceneAdapter.getFloor();
			PickResult pick = me.getPickResult();
			if (pick != null && pick.getIntersectedNode() != null) { // && floor.hasTile(pick.getIntersectedNode())
				setChanged();
				notifyObservers(pick);
			}
		});
	}

}
