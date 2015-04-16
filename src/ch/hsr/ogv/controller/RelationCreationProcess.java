package ch.hsr.ogv.controller;

import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import javafx.geometry.Point3D;
import javafx.scene.Node;
import ch.hsr.ogv.model.RelationType;
import ch.hsr.ogv.view.Arrow;
import ch.hsr.ogv.view.PaneBox;
import ch.hsr.ogv.view.SubSceneAdapter;

public class RelationCreationProcess implements Observer {

	private SelectionController selectionController;
	private SubSceneAdapter subSceneAdapter;
	private ModelViewConnector mvConnector;
	
	private volatile boolean creationInProcess = false;
	
	private Arrow viewArrow;
	private PaneBox startBox;
	private PaneBox endBox;
	
	public Arrow getViewArrow() {
		return viewArrow;
	}

	public PaneBox getStartBox() {
		return startBox;
	}

	public PaneBox getEndBox() {
		return endBox;
	}
	
	public boolean isInProcess() {
		return creationInProcess;
	}
	
	public void startProcess(ModelViewConnector mvConnector, SelectionController selectionController, SubSceneAdapter subSceneAdapter, PaneBox startBox, RelationType relationType) {
		this.selectionController = selectionController;
		this.subSceneAdapter = subSceneAdapter;
		this.mvConnector = mvConnector;
		this.creationInProcess = true;
		this.startBox = startBox;
		this.viewArrow = new Arrow(startBox, startBox.getCenterPoint(), relationType);
		subSceneAdapter.add(this.viewArrow);
		subSceneAdapter.add(this.viewArrow.getSelection());
		subSceneAdapter.receiveMouseEvents(false, this.viewArrow);
		restrictMouseEvents();
	}
	
	public void endProcess(SubSceneAdapter subSceneAdapter) {
		this.creationInProcess = false;
		
		if(this.viewArrow != null) {
			subSceneAdapter.remove(this.viewArrow.getSelection());
			subSceneAdapter.remove(this.viewArrow);
			this.viewArrow = null;
		}
		
		if(this.startBox != null) {
			this.startBox.setSelected(false);
			this.startBox = null;
		}
		
		if(this.endBox != null) {
			this.endBox.setSelected(false);
			this.endBox = null;
		}

		this.subSceneAdapter.receiveMouseEvents(false, this.subSceneAdapter.getFloor());
	}
	
	private void restrictMouseEvents() {
		if(mvConnector == null) return;
		Set<PaneBox> classPaneBoxes = mvConnector.getClassPaneBoxes();
		Node[] nodes = new Node[classPaneBoxes.size() + 1];
		int i = 0;
		for(PaneBox classPaneBox : classPaneBoxes) {
			nodes[i] = classPaneBox.get();
			i++;
		}
		nodes[classPaneBoxes.size()] = this.subSceneAdapter.getFloor();
		this.subSceneAdapter.receiveMouseEvents(true, nodes);
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
		if(o instanceof MouseMoveController && arg instanceof Point3D && this.creationInProcess) {
			if(this.endBox != null && !this.endBox.equals(this.startBox)) {
				this.endBox.setSelected(false);
				this.endBox = null;
			}
			Point3D movePoint = (Point3D) arg;
			this.viewArrow.setPoints(this.startBox, movePoint);
			this.viewArrow.drawArrow();
			
			if(!(this.selectionController.getCurrentSelected() instanceof Arrow) && !pointBoxXZ(this.startBox, movePoint)) {
				this.selectionController.setSelected(this.viewArrow, true, this.subSceneAdapter);
				this.startBox.setSelected(true); // only visually show selection
			}
		}
		else if(o instanceof MouseMoveController && arg instanceof PaneBox && this.creationInProcess) {
			PaneBox paneBoxMovedOver = (PaneBox) arg;
			this.endBox = paneBoxMovedOver;
			this.endBox.setSelected(true); // only visually show selection
			this.viewArrow.setPointsBasedOnBoxes(this.startBox, this.endBox);
			this.viewArrow.drawArrow();
		}
	}

}
