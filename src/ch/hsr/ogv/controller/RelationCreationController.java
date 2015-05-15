package ch.hsr.ogv.controller;

import java.util.Collection;
import java.util.HashSet;
import java.util.Observable;
import java.util.Observer;

import javafx.geometry.Point3D;
import javafx.scene.Node;
import ch.hsr.ogv.model.ModelBox;
import ch.hsr.ogv.model.ModelClass;
import ch.hsr.ogv.model.ModelObject;
import ch.hsr.ogv.model.Relation;
import ch.hsr.ogv.model.RelationType;
import ch.hsr.ogv.view.Arrow;
import ch.hsr.ogv.view.PaneBox;
import ch.hsr.ogv.view.SubSceneAdapter;

/**
 *
 * @author Adrian Rieser, Simon Gwerder
 *
 */
public class RelationCreationController extends Observable implements Observer {

	private SelectionController selectionController;
	private SubSceneAdapter subSceneAdapter;
	private ModelViewConnector mvConnector;

	private volatile boolean isChoosingStartBox = false;
	private volatile boolean creationInProcess = false;

	private PaneBox startBox;
	private PaneBox endBox;
	private Arrow viewArrow;
	private RelationType relationType = RelationType.UNDIRECTED_ASSOCIATION;

	public void setSelectionController(SelectionController selectionController) {
		this.selectionController = selectionController;
	}

	public void setSubSceneAdapter(SubSceneAdapter subSceneAdapter) {
		this.subSceneAdapter = subSceneAdapter;
	}

	public void setMvConnector(ModelViewConnector mvConnector) {
		this.mvConnector = mvConnector;
	}

	public Arrow getViewArrow() {
		return viewArrow;
	}
	
	private void setRelationType(RelationType relationType) {
		this.relationType = relationType;
		if(relationType == null) {
			this.relationType = RelationType.UNDIRECTED_ASSOCIATION;
		}
	}

	public PaneBox getStartBox() {
		return startBox;
	}

	public PaneBox getEndBox() {
		return endBox;
	}

	public boolean isChoosingStartBox() {
		return isChoosingStartBox;
	}

	public boolean isInProcess() {
		return creationInProcess;
	}
	
	public void startChoosingStartBox(RelationType relationType) {
		this.isChoosingStartBox = true;
		this.creationInProcess = false;
		setRelationType(relationType);
		
		selectiveMouseEvents();
	}
	
	public void endChoosingStartBox() {
		this.isChoosingStartBox = false;
		this.creationInProcess = false;
		subSceneAdapter.worldReceiveMouseEvents();
		subSceneAdapter.restrictMouseEvents(this.subSceneAdapter.getVerticalHelper());
	}

	public void startProcess(PaneBox startBox) {
		startProcess(startBox, this.relationType);
	}
	
	public void startProcess(PaneBox startBox, RelationType relationType) {
		setRelationType(relationType);
		this.isChoosingStartBox = false;
		this.creationInProcess = true;
		this.startBox = startBox;
		this.viewArrow = new Arrow(startBox, startBox.getCenterPoint(), relationType);
		subSceneAdapter.add(this.viewArrow);
		subSceneAdapter.add(this.viewArrow.getSelection());
		subSceneAdapter.worldReceiveMouseEvents();
		subSceneAdapter.restrictMouseEvents(this.subSceneAdapter.getVerticalHelper());
		subSceneAdapter.restrictMouseEvents(this.viewArrow);

		listenToSelections();

		selectiveMouseEvents();
		setChanged();
		notifyObservers(this.viewArrow);
	}
	
	public void abortProcess() {
		this.isChoosingStartBox = false;
		this.creationInProcess = false;
		unlistenToSelections();
		
		if (this.viewArrow != null) {
			subSceneAdapter.remove(this.viewArrow.getSelection());
			subSceneAdapter.remove(this.viewArrow);
			this.viewArrow = null;
		}
		if (this.startBox != null) {
			this.startBox.setSelected(false);
			this.startBox = null;
		}
		if (this.endBox != null) {
			this.endBox.setSelected(false);
			this.endBox = null;
		}
		subSceneAdapter.worldReceiveMouseEvents();
		subSceneAdapter.restrictMouseEvents(this.subSceneAdapter.getVerticalHelper());

		setChanged();
		notifyObservers(this.viewArrow);
	}

	public void endProcess(PaneBox selectedPaneBox) {
		this.endBox = selectedPaneBox;

		if (viewArrow != null && startBox != null && endBox != null) {
			Relation relation = mvConnector.handleCreateRelation(startBox, endBox, viewArrow.getRelationType());
			Arrow newArrow = mvConnector.getArrow(relation);
			if (newArrow != null) {
				this.selectionController.setSelected(newArrow, true, this.subSceneAdapter);
			}
		}
		
		abortProcess();
	}


	private void listenToSelections() {
		selectionController.addObserver(this);
	}

	private void unlistenToSelections() {
		selectionController.deleteObserver(this);
	}
	
	private boolean isClassesRelation(RelationType relationType) {
		return relationType == RelationType.UNDIRECTED_ASSOCIATION
				|| relationType == RelationType.DIRECTED_ASSOCIATION
				|| relationType == RelationType.BIDIRECTED_ASSOCIATION
				|| relationType == RelationType.UNDIRECTED_AGGREGATION
				|| relationType == RelationType.DIRECTED_AGGREGATION
				|| relationType == RelationType.UNDIRECTED_COMPOSITION
				|| relationType == RelationType.DIRECTED_COMPOSITION 
			    || relationType == RelationType.GENERALIZATION 
			    || relationType == RelationType.DEPENDENCY
			    || relationType == RelationType.ASSOZIATION_CLASS;
	}
	
	private boolean isObjectsRelation(RelationType relationType) {
		return relationType == RelationType.OBJDIAGRAM || relationType == RelationType.OBJGRAPH;
	}
	
	private boolean checkRelation(ModelBox start, ModelBox end, RelationType relationType) {
		if (start == null || end == null || relationType == null) {
			return false;
		}
		else if(!start.getClass().equals(end.getClass())) { // are both either of type ModelObject or ModelClass
			return false;
		}
		else if (start.equals(end)) { // TODO: reflexive relation
			return false;
		}
		else if (start instanceof ModelClass && (isObjectsRelation(relationType) || (relationType == RelationType.GENERALIZATION && !isCycleFree((ModelClass) start, (ModelClass) end)))) {
			return false;
		}
		else if (start instanceof ModelObject && end instanceof ModelObject && isObjectsRelation(relationType)) {
			ModelObject startObject = (ModelObject) start;
			ModelObject endObject = (ModelObject) end;
			ModelClass startClass = startObject.getModelClass();
			if(startObject.isSuperObject() || endObject.isSuperObject()) {
				return false;
			}
			else if(startObject.getRelationWith(endObject) != null) { // they are connected already
				return false;
			}
			else if(startClass != null && startClass.getRelationWith(endObject.getModelClass()) == null) { // underlying classes are not connected
				return false;
			}
		}
		else if (start instanceof ModelObject && isClassesRelation(relationType)) {
			return false;
		}
		return true;
	}

	private boolean isCycleFree(ModelClass startClass, ModelClass endClass) {
		for (ModelClass endSuperClass : endClass.getSuperClasses()) {
			if (endSuperClass.equals(startClass)) {
				return false;
			}
			return isCycleFree(startClass, endSuperClass);
		}
		return true;
	}
	
	private void selectiveMouseEvents() {
		Collection<PaneBox> boxes = new HashSet<PaneBox>();
		boolean isClassesRelation = isClassesRelation(this.relationType);
		boolean isObjectsRelation = isObjectsRelation(this.relationType);
		for(ModelBox modelBox : this.mvConnector.getBoxes().keySet()) {
			if(this.creationInProcess && !checkRelation(this.mvConnector.getModelBox(this.startBox), modelBox, this.relationType)) {
				continue;
			}
			PaneBox paneBox = this.mvConnector.getPaneBox(modelBox);
			if (isClassesRelation && modelBox instanceof ModelClass && paneBox != null) {
				boxes.add(paneBox);
			}
			else if (isObjectsRelation && modelBox instanceof ModelObject && paneBox != null) {
				ModelObject modelObject = (ModelObject) modelBox;
				if(!modelObject.isSuperObject()) {
					boxes.add(paneBox);
				}
			}
		}
		Node[] nodes = new Node[boxes.size() + 1];
		int i = 0;
		for (PaneBox box : boxes) {
			nodes[i] = box.get();
			i++;
		}
		nodes[boxes.size()] = this.subSceneAdapter.getFloor();
		this.subSceneAdapter.worldRestrictMouseEvents();
		this.subSceneAdapter.receiveMouseEvents(nodes);
	}
	
	private boolean pointBoxXZ(PaneBox box, Point3D movePoint) {
		double leftBorder = this.startBox.getCenterPoint().getX() + this.startBox.getWidth() / 2;
		double rightBorder = this.startBox.getCenterPoint().getX() - this.startBox.getWidth() / 2;
		double topBorder = this.startBox.getCenterPoint().getZ() + this.startBox.getHeight() / 2;
		double bottomBorder = this.startBox.getCenterPoint().getZ() - this.startBox.getHeight() / 2;
		return movePoint.getX() < leftBorder && movePoint.getX() > rightBorder && movePoint.getZ() < topBorder && movePoint.getZ() > bottomBorder;
	}

	@Override
	public void update(Observable o, Object arg) {
		if (o instanceof MouseMoveController && arg instanceof Point3D && this.creationInProcess) {
			if (this.endBox != null && !this.endBox.equals(this.startBox)) {
				this.endBox.setSelected(false);
				this.endBox = null;
			}
			Point3D movePoint = (Point3D) arg;
			this.viewArrow.setPoints(this.startBox, movePoint);
			this.viewArrow.drawArrow();

			if (!(this.selectionController.getCurrentSelected() instanceof Arrow) && !pointBoxXZ(this.startBox, movePoint)) {
				this.selectionController.setSelected(this.viewArrow, true, this.subSceneAdapter);
				this.startBox.setSelected(true); // only visually show selection
			}
		}
		else if (o instanceof MouseMoveController && arg instanceof Point3D && this.isChoosingStartBox) { // choosing start box, mouseover non-box
			if(this.startBox != null) {
				this.startBox.setSelected(false);
				this.startBox = null;
			}
		}
		else if (o instanceof MouseMoveController && arg instanceof PaneBox && this.isChoosingStartBox) { // choosing start box, mouseover box
			if(this.startBox != null) {
				this.startBox.setSelected(false);
				this.startBox = null;
			}
			PaneBox paneBoxMovedOver = (PaneBox) arg;
			this.startBox = paneBoxMovedOver;
			this.startBox.setSelected(true); // only visually show selection
		}
		else if (o instanceof MouseMoveController && arg instanceof PaneBox && this.creationInProcess) { // creation in process
			PaneBox paneBoxMovedOver = (PaneBox) arg;
			if (this.endBox != null && !this.endBox.equals(this.startBox) && !this.endBox.equals(paneBoxMovedOver)) {
				this.endBox.setSelected(false);
				this.endBox = null;
			}
			this.endBox = paneBoxMovedOver;
			this.endBox.setSelected(true); // only visually show selection
			this.viewArrow.setPointsBasedOnBoxes(this.startBox, this.endBox);
			this.viewArrow.drawArrow();
		}
	}

}