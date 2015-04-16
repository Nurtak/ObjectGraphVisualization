package ch.hsr.ogv.controller;

import java.util.Observable;
import java.util.Observer;

import javafx.geometry.Point3D;
import javafx.scene.input.PickResult;
import ch.hsr.ogv.model.RelationType;
import ch.hsr.ogv.view.Arrow;
import ch.hsr.ogv.view.PaneBox;
import ch.hsr.ogv.view.SubSceneAdapter;

public class RelationCreationProcess implements Observer {

	private SelectionController selectionController;
	private SubSceneAdapter subSceneAdapter;
	
	private volatile boolean creationInProgress = false;
	
	private Arrow viewArrow;
	private PaneBox startBox;
	
	public boolean isInProgress() {
		return creationInProgress;
	}
	
	public void createViewArrow(SelectionController selectionController, SubSceneAdapter subSceneAdapter, PaneBox startBox, RelationType relationType) {
		this.selectionController = selectionController;
		this.subSceneAdapter = subSceneAdapter;
		this.creationInProgress = true;
		this.startBox = startBox;
		this.viewArrow = new Arrow(startBox, startBox.getCenterPoint(), relationType);
		subSceneAdapter.add(this.viewArrow);
		subSceneAdapter.add(this.viewArrow.getSelection());
	}
	
	public void removeViewArrow(SubSceneAdapter subSceneAdapter) {
		this.creationInProgress = false;
		if(this.viewArrow != null) {
			subSceneAdapter.remove(this.viewArrow.getSelection());
			subSceneAdapter.remove(this.viewArrow);
		}
		this.viewArrow = null;
		this.startBox = null;
	}
	
	private boolean pointBoxXZ(PaneBox box, Point3D movePoint) {
		double leftBorder = this.startBox.getCenterPoint().getX() + this.startBox.getWidth() / 2;
		double rightBorder = this.startBox.getCenterPoint().getX() - this.startBox.getWidth() / 2;
		double topBorder = this.startBox.getCenterPoint().getZ() + this.startBox.getHeight() / 2;
		double bottomBorder = this.startBox.getCenterPoint().getZ() - this.startBox.getHeight() / 2;
		return movePoint.getX() < leftBorder && movePoint.getX() > rightBorder
				&& movePoint.getZ() < topBorder && movePoint.getZ() > bottomBorder;
	}
	
	@Override
	public void update(Observable o, Object arg) {
		if(this.selectionController == null || this.subSceneAdapter == null) return;
		if(o instanceof MouseMoveController && arg instanceof PickResult && this.creationInProgress) {
			PickResult pick = (PickResult) arg;
			Point3D movePoint = pick.getIntersectedNode().localToParent(pick.getIntersectedPoint());
			System.out.println("x: " + movePoint.getX() + ", y: " + movePoint.getY() + ", z: " + movePoint.getZ());
			this.viewArrow.setPoints(this.startBox, movePoint);
			this.viewArrow.drawArrow();
			
			if(!(this.selectionController.getCurrentSelected() instanceof Arrow) && !pointBoxXZ(this.startBox, movePoint)) {
				this.selectionController.setSelected(this.viewArrow, true, this.subSceneAdapter);
			}
		}
	}

}
