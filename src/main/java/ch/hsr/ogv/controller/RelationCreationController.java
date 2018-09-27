package ch.hsr.ogv.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javafx.geometry.Point3D;
import javafx.scene.Node;
import ch.hsr.ogv.model.ModelBox;
import ch.hsr.ogv.model.ModelClass;
import ch.hsr.ogv.model.ModelManager;
import ch.hsr.ogv.model.ModelObject;
import ch.hsr.ogv.model.Relation;
import ch.hsr.ogv.model.RelationType;
import ch.hsr.ogv.view.Arrow;
import ch.hsr.ogv.view.ReflexiveArrow;
import ch.hsr.ogv.view.DashedArrow;
import ch.hsr.ogv.view.PaneBox;
import ch.hsr.ogv.view.SubSceneAdapter;

/**
 *
 * @author Adrian Rieser, Simon Gwerder
 * @version OGV 3.1, May 2015
 *
 */
public class RelationCreationController extends Observable implements Observer {

	private SelectionController selectionController;
	private SubSceneAdapter subSceneAdapter;
	private ModelViewConnector mvConnector;

	private volatile boolean isChoosingStartBox = false;
	private volatile boolean creationInProcess = false;
	private volatile boolean leftStartBox = false;

	private PaneBox startBox;
	private PaneBox endBox;
	private Arrow viewArrow;
	private ReflexiveArrow reflexiveViewArrow;
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
		if (relationType == null) {
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
		this.isChoosingStartBox = false;
		this.creationInProcess = true;
		this.startBox = startBox;
		setRelationType(relationType);
		initViewArrow(startBox, relationType);
		listenToSelections();
		selectiveMouseEvents();
		paneBoxMovedOver(startBox);
		setChanged();
		notifyObservers(this.viewArrow);
	}

	private void initViewArrow(PaneBox startBox, RelationType relationType) {
		if(!RelationType.DEPENDENCY.equals(relationType)) {
			this.viewArrow = new Arrow(startBox, startBox.getCenterPoint(), relationType);
			initReflexiveViewArrow();
		}
		else {
			this.viewArrow = new DashedArrow(startBox, startBox.getCenterPoint(), relationType);
		}
		this.subSceneAdapter.add(this.viewArrow);
		this.subSceneAdapter.add(this.viewArrow.getSelection());
		this.subSceneAdapter.worldReceiveMouseEvents();
		this.subSceneAdapter.restrictMouseEvents(this.subSceneAdapter.getVerticalHelper());
		this.subSceneAdapter.restrictMouseEvents(this.viewArrow);
	}
	
	private void initReflexiveViewArrow() {
		this.reflexiveViewArrow = new ReflexiveArrow(startBox, startBox, relationType);
		this.reflexiveViewArrow.setSelected(true); // only visible selection
		this.reflexiveViewArrow.setArrowVisible(false);
		this.subSceneAdapter.add(this.reflexiveViewArrow);
		this.subSceneAdapter.add(this.reflexiveViewArrow.getSelection());
	}
	
	private void viewArrowVisiblity(boolean isReflexive) {
		if(isReflexive && this.viewArrow != null && this.reflexiveViewArrow != null) {
			this.viewArrow.setArrowVisible(false);
			this.reflexiveViewArrow.setArrowVisible(true);
		}
		else if (!isReflexive && this.viewArrow != null && this.reflexiveViewArrow != null) {
			this.viewArrow.setArrowVisible(true);
			this.reflexiveViewArrow.setArrowVisible(false);
		}
		else if(this.viewArrow != null && this.reflexiveViewArrow == null) {
			this.viewArrow.setArrowVisible(true);
		}
	}
	
	public void endProcess(PaneBox selectedPaneBox) {
		this.endBox = selectedPaneBox;

		if (this.viewArrow != null && this.startBox != null && this.endBox != null) {
			Relation relation = this.mvConnector.handleCreateRelation(this.startBox, this.endBox, this.viewArrow.getRelationType());
			Arrow newArrow = this.mvConnector.getArrow(relation);
			if (newArrow != null) {
				this.selectionController.setSelected(newArrow, true, this.subSceneAdapter);
			}
		}

		abortProcess();
	}

	public void abortProcess() {
		this.isChoosingStartBox = false;
		this.creationInProcess = false;
		this.leftStartBox = false;
		unlistenToSelections();

		if (this.viewArrow != null) {
			subSceneAdapter.remove(this.viewArrow.getSelection());
			subSceneAdapter.remove(this.viewArrow);
			this.viewArrow = null;
		}
		if(this.reflexiveViewArrow != null) {
			this.subSceneAdapter.remove(this.reflexiveViewArrow);
			this.subSceneAdapter.remove(this.reflexiveViewArrow.getSelection());
			this.reflexiveViewArrow = null;
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

	private void listenToSelections() {
		selectionController.addObserver(this);
	}

	private void unlistenToSelections() {
		selectionController.deleteObserver(this);
	}

	private boolean isClassesRelation(RelationType relationType) {
		return relationType == RelationType.UNDIRECTED_ASSOCIATION || relationType == RelationType.DIRECTED_ASSOCIATION || relationType == RelationType.BIDIRECTED_ASSOCIATION
				|| relationType == RelationType.UNDIRECTED_AGGREGATION || relationType == RelationType.DIRECTED_AGGREGATION || relationType == RelationType.UNDIRECTED_COMPOSITION
				|| relationType == RelationType.DIRECTED_COMPOSITION || relationType == RelationType.GENERALIZATION || relationType == RelationType.DEPENDENCY
				|| relationType == RelationType.ASSOZIATION_CLASS;
	}

	private boolean isObjectsRelation(RelationType relationType) {
		return relationType == RelationType.OBJDIAGRAM || relationType == RelationType.OBJGRAPH;
	}

	private boolean checkRelation(ModelBox start, ModelBox end, RelationType relationType) {
		ModelManager modelManager = this.mvConnector.getModelManager();
		if (start == null || end == null || relationType == null) {
			return false;
		}
		else if (!start.getClass().equals(end.getClass())) { // are both either of type ModelObject or ModelClass
			return false;
		}
		else if (start instanceof ModelClass && (isObjectsRelation(relationType) || (relationType == RelationType.GENERALIZATION && !isCycleFree((ModelClass) start, (ModelClass) end)))) {
			return false;
		}
		else if (start instanceof ModelClass && end instanceof ModelClass) {
			ModelClass startClass = (ModelClass) start;
			ModelClass endClass = (ModelClass) end;
			List<Relation> baseRelations = modelManager.getRelationsBetween(startClass, endClass);
			for (Relation baseRelation : baseRelations) {
				boolean bothGeneralization = relationType == RelationType.GENERALIZATION && baseRelation.getRelationType() == RelationType.GENERALIZATION;
				boolean bothDependency = relationType == RelationType.DEPENDENCY && baseRelation.getRelationType() == RelationType.DEPENDENCY;
				if ((relationType == RelationType.DEPENDENCY && start.equals(end)) || bothGeneralization || (bothDependency && baseRelation.getStart().getAppendant().equals(startClass))) {
					return false;
				}
			}
		}
		if (start instanceof ModelObject && end instanceof ModelObject && isObjectsRelation(relationType)) {
			ModelObject startObject = (ModelObject) start;
			ModelObject endObject = (ModelObject) end;
			ModelClass startClass = startObject.getModelClass();

			if (startObject.isSuperObject() || endObject.isSuperObject()) {
				return false;
			}

			List<Relation> baseRelations = modelManager.getRelationsBetween(startClass, endObject.getModelClass());
			if (startClass != null && baseRelations.isEmpty()) { // underlying classes are not connected
				List<ModelClass> superClasses = endObject.getModelClass().getSuperClasses();
				if(superClasses.isEmpty()) {
					return false;
				}
				List<Relation> superBaseRelations = new ArrayList<Relation>();
				for(ModelClass superClass : superClasses) {
					superBaseRelations.addAll(modelManager.getRelationsBetween(startClass, superClass));
				}
				if(superBaseRelations.isEmpty()) {
					return false;
				}
				else if(superBaseRelations.size() == 1 && (superBaseRelations.get(0).getRelationType().equals(RelationType.GENERALIZATION) || superBaseRelations.get(0).getRelationType().equals(RelationType.DEPENDENCY))) {
					return false;
				}

			}
			else if (startClass != null && !baseRelations.isEmpty()) { // no object relation at Generalization / Dependency only
				if (baseRelations.size() == 1 && (baseRelations.get(0).getRelationType().equals(RelationType.GENERALIZATION) || baseRelations.get(0).getRelationType().equals(RelationType.DEPENDENCY))) {
					return false;
				}
			}
		}
		else if (start instanceof ModelObject && isClassesRelation(relationType)) {
			return false;
		}
		return true;
	}

	private boolean isCycleFree(ModelClass startClass, ModelClass endClass) {
		if(startClass.equals(endClass)) {
			return false;
		}
		for (ModelClass endSuperClass : endClass.getSuperClasses()) {
			if (endSuperClass.equals(startClass)) {
				return false;
			}
			return isCycleFree(startClass, endSuperClass);
		}
		return true;
	}

	private void selectiveMouseEvents() {
		Collection<PaneBox> boxes = new LinkedHashSet<PaneBox>();
		boolean isClassesRelation = isClassesRelation(this.relationType);
		boolean isObjectsRelation = isObjectsRelation(this.relationType);
		for (ModelBox modelBox : this.mvConnector.getBoxes().keySet()) {
			if (this.creationInProcess && !checkRelation(this.mvConnector.getModelBox(this.startBox), modelBox, this.relationType)) {
				continue;
			}
			PaneBox paneBox = this.mvConnector.getPaneBox(modelBox);
			if (isClassesRelation && modelBox instanceof ModelClass && paneBox != null) {
				boxes.add(paneBox);
			}
			else if (isObjectsRelation && modelBox instanceof ModelObject && paneBox != null) {
				ModelObject modelObject = (ModelObject) modelBox;
				if (!modelObject.isSuperObject()) {
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

	// update methods starting here
	
	private void resetStartBox() {
		if (this.startBox != null) {
			this.startBox.setSelected(false);
			this.startBox = null;
		}
	}
	
	private void setStartBox(PaneBox paneBoxMovedOver) {
		resetStartBox();
		
		this.startBox = paneBoxMovedOver;
		this.startBox.setSelected(true); // only visually show selection
	}
	
	private void floorMovedOver(Point3D movePoint) {
		if (this.endBox != null) {
			if (!this.endBox.equals(this.startBox)) {
				this.endBox.setSelected(false);
			}
			arrangedDrawArrows(false);
			this.endBox = null;
			viewArrowVisiblity(false);
			this.selectionController.setSelected(this.viewArrow, true, this.subSceneAdapter);
			if (!this.leftStartBox && !coordsInsideBox(movePoint, this.startBox)) {
				this.leftStartBox = true;
			}
		}
		this.viewArrow.setPoints(this.startBox, movePoint);
		this.viewArrow.drawArrow();
		this.startBox.setSelected(true); // only visually show selection
	}

	private void paneBoxMovedOver(PaneBox paneBoxMovedOver) {
		if (this.endBox != null && !this.endBox.equals(paneBoxMovedOver)) {
			if (!this.endBox.equals(this.startBox)) {
				this.endBox.setSelected(false);
			}
			arrangedDrawArrows(false);
			this.endBox = null;
			this.leftStartBox = true;
		}
		this.endBox = paneBoxMovedOver;
		if (this.leftStartBox) { // this.endBox.equals(this.startBox) && 
			viewArrowVisiblity(this.endBox.equals(this.startBox));
			arrangedDrawArrows(true);
		}
		this.selectionController.setSelected(this.viewArrow, true, this.subSceneAdapter);
		this.endBox.setSelected(true); // only visually show selection
	}
	
	private boolean coordsInsideBox(Point3D coords, PaneBox paneBox) {
		if (coords.getX() > paneBox.getTranslateX() + (paneBox.getWidth() / 2) || coords.getX() < paneBox.getTranslateX() - (paneBox.getWidth() / 2)) {
			return false;
		}
		if (coords.getY() > paneBox.getTranslateY() + (paneBox.getDepth() / 2) || coords.getY() < paneBox.getTranslateY() - (paneBox.getDepth() / 2)) {
			return false;
		}
		if (coords.getZ() > paneBox.getTranslateZ() + (paneBox.getHeight() / 2) || coords.getZ() < paneBox.getTranslateZ() - (paneBox.getHeight() / 2)) {
			return false;
		}
		return true;
	}

	private void arrangedDrawArrows(boolean addViewArrow) {
		ModelBox startModelBox = this.mvConnector.getModelBox(this.startBox);
		ModelBox endModelBox = this.mvConnector.getModelBox(this.endBox);
		if (addViewArrow) {
			int relationsCount = this.mvConnector.getModelManager().getRelationsBetween(startModelBox, endModelBox).size();
			this.mvConnector.arrangeArrowNumbers(startModelBox, endModelBox, 1);
			this.viewArrow.arrangeEndpoints(this.startBox, this.endBox, relationsCount + 1, relationsCount + 1);
			if(this.reflexiveViewArrow != null) {
				this.reflexiveViewArrow.arrangeEndpoints(this.startBox, this.endBox, relationsCount + 1, relationsCount + 1);
			}
		}
		else {
			this.mvConnector.arrangeArrowNumbers(startModelBox, endModelBox, 0);
			this.viewArrow.arrangeEndpoints(this.startBox, this.endBox, 1, 1);
			if(this.reflexiveViewArrow != null) {
				this.reflexiveViewArrow.arrangeEndpoints(this.startBox, this.endBox, 1, 1);
			}
		}
	}

	@Override
	public void update(Observable o, Object arg) {
		if (o instanceof MouseMoveController && arg instanceof Point3D && this.isChoosingStartBox) { // choosing start box, mouseover non-box
			resetStartBox();
		}
		else if (o instanceof MouseMoveController && arg instanceof PaneBox && this.isChoosingStartBox) { // choosing start box, mouseover box
			PaneBox paneBoxMovedOver = (PaneBox) arg;
			setStartBox(paneBoxMovedOver);
		}
		else if (o instanceof MouseMoveController && arg instanceof Point3D && this.creationInProcess) {
			Point3D movePoint = (Point3D) arg;
			floorMovedOver(movePoint);
		}
		else if (o instanceof MouseMoveController && arg instanceof PaneBox && this.creationInProcess) { // creation in process
			PaneBox paneBoxMovedOver = (PaneBox) arg;
			paneBoxMovedOver(paneBoxMovedOver);
		}
	}
	
}
