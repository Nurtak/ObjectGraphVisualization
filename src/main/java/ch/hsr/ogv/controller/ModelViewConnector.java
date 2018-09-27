package ch.hsr.ogv.controller;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javafx.geometry.Point3D;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import ch.hsr.ogv.model.Attribute;
import ch.hsr.ogv.model.ModelBox;
import ch.hsr.ogv.model.ModelClass;
import ch.hsr.ogv.model.ModelManager;
import ch.hsr.ogv.model.ModelObject;
import ch.hsr.ogv.model.Relation;
import ch.hsr.ogv.model.RelationType;
import ch.hsr.ogv.util.TextUtil;
import ch.hsr.ogv.view.Arrow;
import ch.hsr.ogv.view.ArrowLabel;
import ch.hsr.ogv.view.BoxSelection;
import ch.hsr.ogv.view.Floor;
import ch.hsr.ogv.view.PaneBox;
import ch.hsr.ogv.view.Selectable;
import ch.hsr.ogv.view.SubSceneAdapter;

/**
 *
 * @author Adrian Rieser
 * @version OGV 3.1, May 2015
 *
 */
public class ModelViewConnector {

	public static final double BASE_BOX_DEPTH = PaneBox.INIT_DEPTH + BoxSelection.INIT_SELECT_SIZE / 2;

	private ModelManager modelManager = new ModelManager();

	private Map<ModelBox, PaneBox> boxes = new LinkedHashMap<ModelBox, PaneBox>();
	private Map<Relation, Arrow> arrows = new LinkedHashMap<Relation, Arrow>();

	public ModelManager getModelManager() {
		return this.modelManager;
	}

	public Map<ModelBox, PaneBox> getBoxes() {
		return boxes;
	}

	public Map<Relation, Arrow> getArrows() {
		return arrows;
	}

	public PaneBox getPaneBox(ModelBox key) {
		return this.boxes.get(key);
	}

	public ModelBox getModelBox(PaneBox value) {
		for (Entry<ModelBox, PaneBox> entry : boxes.entrySet()) {
			if (entry.getValue().equals(value)) {
				return entry.getKey();
			}
		}
		return null;
	}

	public Set<PaneBox> getClassPaneBoxes() {
		Set<PaneBox> classPaneBoxes = new LinkedHashSet<PaneBox>();
		for (ModelBox modelBox : getBoxes().keySet()) {
			if (modelBox instanceof ModelClass) {
				classPaneBoxes.add(getBoxes().get(modelBox));
			}
		}
		return classPaneBoxes;
	}

	public Set<PaneBox> getObjectPaneBoxes() {
		Set<PaneBox> objectPaneBoxes = new LinkedHashSet<PaneBox>();
		for (ModelBox modelBox : getBoxes().keySet()) {
			if (modelBox instanceof ModelObject) {
				objectPaneBoxes.add(getBoxes().get(modelBox));
			}
		}
		return objectPaneBoxes;
	}

	public Arrow getArrow(Relation key) {
		return this.arrows.get(key);
	}

	public Relation getRelation(Arrow value) {
		for (Entry<Relation, Arrow> entry : arrows.entrySet()) {
			if (entry.getValue().equals(value)) {
				return entry.getKey();
			}
		}
		return null;
	}

	public boolean containsModelBox(ModelBox modelBox) {
		return this.boxes.containsKey(modelBox);
	}

	public boolean containsRelation(Relation relation) {
		return this.arrows.containsKey(relation);
	}

	public boolean containsPaneBox(PaneBox paneBox) {
		return this.boxes.values().contains(paneBox);
	}

	public boolean containsArrow(Arrow arrow) {
		return this.arrows.values().contains(arrow);
	}

	public boolean containsSelectable(Selectable selectable) {
		if (selectable instanceof PaneBox) {
			return containsPaneBox((PaneBox) selectable);
		}
		else if (selectable instanceof Arrow) {
			return containsArrow((Arrow) selectable);
		}
		return false;
	}

	public PaneBox putBoxes(ModelBox key, PaneBox value) {
		return this.boxes.put(key, value);
	}

	public Arrow putArrows(Relation key, Arrow value) {
		return this.arrows.put(key, value);
	}

	public PaneBox removeBoxes(ModelBox modelBox) {
		return this.boxes.remove(modelBox);
	}

	public Arrow removeArrows(Relation relation) {
		return this.arrows.remove(relation);
	}

	public void createDummyContent() {
		ModelClass mcA = this.modelManager.createClass(new Point3D(300, BASE_BOX_DEPTH, 0), PaneBox.MIN_WIDTH, PaneBox.MIN_HEIGHT, PaneBox.DEFAULT_COLOR);
		mcA.setName("A");
		ModelClass mcB = this.modelManager.createClass(new Point3D(-300, BASE_BOX_DEPTH, 0), PaneBox.MIN_WIDTH, PaneBox.MIN_HEIGHT, PaneBox.DEFAULT_COLOR);
		mcB.setName("B");
		this.modelManager.createRelation(mcB, mcA, RelationType.UNDIRECTED_ASSOCIATION, Arrow.DEFAULT_COLOR);
	}

	public PaneBox handleCreateNewClass(Point3D mouseCoords) {
		Point3D boxPosition = new Point3D(mouseCoords.getX(), BASE_BOX_DEPTH, mouseCoords.getZ());
		ModelClass newClass = this.modelManager.createClass(boxPosition, PaneBox.MIN_WIDTH, PaneBox.MIN_HEIGHT, PaneBox.DEFAULT_COLOR);
		PaneBox newBox = getPaneBox(newClass);
		if (newBox != null) {
			newBox.allowTopTextInput(true);
		}
		return newBox;
	}

	public PaneBox handleCreateNewObject(Selectable selected) {
		if (selected instanceof PaneBox) {
			ModelBox selectedModelBox = this.getModelBox((PaneBox) selected);
			if (selectedModelBox != null && selectedModelBox instanceof ModelClass) {
				ModelClass selectedModelClass = (ModelClass) selectedModelBox;
				ModelObject newObject = this.modelManager.createObject(selectedModelClass);
				PaneBox newBox = this.getPaneBox(newObject);
				if (newBox != null) {
					newBox.allowTopTextInput(true);
					return newBox;
				}
			}
		}
		return null;
	}

	public Relation handleCreateRelation(PaneBox startBox, PaneBox endBox, RelationType relationType) {
		ModelBox modelBoxStart = this.getModelBox(startBox);
		ModelBox modelBoxEnd = this.getModelBox(endBox);
		return this.modelManager.createRelation(modelBoxStart, modelBoxEnd, relationType, Arrow.DEFAULT_COLOR);
	}

	public Attribute handleCreateNewAttribute(Selectable selected) {
		if (selected instanceof PaneBox) {
			PaneBox selectedPaneBox = (PaneBox) selected;
			selectedPaneBox.setAllLabelSelected(false);
			ModelBox selectedModelBox = this.getModelBox(selectedPaneBox);
			if (selectedModelBox != null && selectedModelBox instanceof ModelClass) {
				ModelClass selectedModelClass = (ModelClass) selectedModelBox;
				String newAttributeName = "attr" + (selectedModelClass.getAttributes().size() + 1);
				while (this.modelManager.isAttributeNameTaken(selectedModelClass, newAttributeName) || this.modelManager.isRoleNameTaken(selectedModelClass, newAttributeName)) {
					newAttributeName = TextUtil.countUpTrailing(newAttributeName, selectedModelClass.getAttributes().size());
				}
				Attribute newAttribute = selectedModelClass.createAttribute(newAttributeName);
				int lastCenterLabelIndex = selectedPaneBox.getCenterLabels().size() - 1;
				Label lastCenterLabel = selectedPaneBox.getCenterLabels().get(lastCenterLabelIndex);
				selectedPaneBox.allowCenterFieldTextInput(lastCenterLabel, true);
				return newAttribute;
			}
		}
		return null;
	}

	public void handleClearAll() {
		modelManager.clearRelations();
		modelManager.clearClasses();
	}

	public void handleDelete(Selectable selected) {
		if (selected instanceof PaneBox) {
			PaneBox paneBox = (PaneBox) selected;
			ModelBox modelToDelete = getModelBox(paneBox);
			boolean centerlabelSelected = paneBox.getSelectedLabel() != null && paneBox.getCenterLabels().indexOf(paneBox.getSelectedLabel()) >= 0;
			if (modelToDelete instanceof ModelClass) {
				if (!centerlabelSelected) { // delete the whole class
					ModelClass classToDelete = (ModelClass) modelToDelete;
					this.modelManager.deleteClass(classToDelete);
				}
				else { // delete selected attribute
					handleDeleteAttribute(selected);
				}

			}
			else if (modelToDelete instanceof ModelObject) {
				ModelObject objectToDelete = (ModelObject) modelToDelete;
				if (objectToDelete.isSuperObject() && !centerlabelSelected) {
					return; // do nothing
				}
				else if (!centerlabelSelected) { // delete the whole object
					this.modelManager.deleteObject(objectToDelete);
				}
				else { // clear attribute value
					handleDeleteAttributeValue(selected);
				}
			}
		}
		else if (selected instanceof Arrow) {
			Arrow arrow = (Arrow) selected;
			Relation relationToDelete = getRelation(arrow);
			boolean arrowLabelSelected = arrow.getSelectedLabel() != null;
			if (!arrowLabelSelected) {
				this.modelManager.deleteRelation(relationToDelete);
			}
			else {
				handleDeleteMultiplicityRole(selected);
			}
		}
	}

	public void handleMoveAttributeUp(Selectable selected) {
		if (selected instanceof PaneBox) {
			PaneBox paneBox = (PaneBox) selected;
			Label selectedLabel = paneBox.getSelectedLabel();
			if (selectedLabel != null && paneBox.getCenterLabels().indexOf(selectedLabel) >= 0) {
				int rowIndex = paneBox.getCenterLabels().indexOf(selectedLabel);
				paneBox.setLabelSelected(rowIndex - 1, true);
				ModelBox modelBox = getModelBox(paneBox);
				if (modelBox instanceof ModelClass) {
					ModelClass modelClass = (ModelClass) modelBox;
					modelClass.moveAttributeUp(rowIndex);
				}
			}
		}
	}

	public void handleMoveAttributeDown(Selectable selected) {
		if (selected instanceof PaneBox) {
			PaneBox paneBox = (PaneBox) selected;
			Label selectedLabel = paneBox.getSelectedLabel();
			if (selectedLabel != null && paneBox.getCenterLabels().indexOf(selectedLabel) >= 0) {
				int rowIndex = paneBox.getCenterLabels().indexOf(selectedLabel);
				paneBox.setLabelSelected(rowIndex + 1, true);
				ModelBox modelBox = getModelBox(paneBox);
				if (modelBox instanceof ModelClass) {
					ModelClass modelClass = (ModelClass) modelBox;
					modelClass.moveAttributeDown(rowIndex);
				}
			}
		}
	}

	public void handleDeleteAttribute(Selectable selected) {
		if (selected instanceof PaneBox) {
			PaneBox paneBox = (PaneBox) selected;
			Label selectedLabel = paneBox.getSelectedLabel();
			if (selectedLabel != null && paneBox.getCenterLabels().indexOf(selectedLabel) >= 0) {
				int rowIndex = paneBox.getCenterLabels().indexOf(selectedLabel);
				ModelBox modelBox = getModelBox(paneBox);
				if (modelBox instanceof ModelClass) {
					ModelClass modelClass = (ModelClass) modelBox;
					modelClass.deleteAttribute(rowIndex);
				}
				paneBox.setAllLabelSelected(false);
			}
		}
	}

	public void handleDeleteAttributeValue(Selectable selected) {
		if (selected instanceof PaneBox) {
			PaneBox paneBox = (PaneBox) selected;
			Label selectedLabel = paneBox.getSelectedLabel();
			if (selectedLabel != null && paneBox.getCenterLabels().indexOf(selectedLabel) >= 0) {
				int rowIndex = paneBox.getCenterLabels().indexOf(selectedLabel);
				ModelBox modelBox = getModelBox(paneBox);
				if (modelBox instanceof ModelObject) {
					ModelObject modelObject = (ModelObject) modelBox;
					Attribute attribute = modelObject.getModelClass().getAttributes().get(rowIndex);
					modelObject.changeAttributeValue(attribute, "");
				}
			}
		}
	}

	public void handleColorPick(Selectable selected, Color pickedColor) {
		if (selected instanceof PaneBox) {
			ModelBox modelBox = this.getModelBox((PaneBox) selected);
			modelBox.setColor(pickedColor);
		}
		else if (selected instanceof Arrow) {
			Relation relation = this.getRelation((Arrow) selected);
			relation.setColor(pickedColor);
		}
		else if (selected instanceof SubSceneAdapter) {
			SubSceneAdapter subSceneAdapter = (SubSceneAdapter) selected;
			subSceneAdapter.getFloor().setColor(pickedColor);
		}
		else if (selected instanceof Floor) {
			Floor floor = (Floor) selected;
			floor.setColor(pickedColor);
		}
	}

	public void handleRenameClassOrObject(Selectable selected) {
		if (selected instanceof PaneBox) {
			PaneBox selectedBox = (PaneBox) selected;
			selectedBox.allowTopTextInput(true);
		}
	}

	public void handleRenameFieldOrValue(Selectable selected) {
		if (selected instanceof PaneBox) {
			PaneBox selectedBox = (PaneBox) selected;
			Label selectedLabel = selectedBox.getSelectedLabel();
			if (selectedLabel != null && !selectedLabel.equals(selectedBox.getTopLabel())) {
				selectedBox.allowCenterFieldTextInput(selectedLabel, true);
			}
		}
	}

	public void handleChangeDirection(Selectable selected) {
		if (selected instanceof Arrow) {
			getRelation((Arrow) selected).changeDirection();
		}
	}

	public void showLabel(ArrowLabel arrowLabel) {
		arrowLabel.showLabel(true);
		arrowLabel.setLabelSelected(true);
		arrowLabel.allowTextInput(true);
	}

	public void handleSetRoleName(Selectable selected, boolean atStart) {
		if (selected instanceof Arrow) {
			Arrow arrow = (Arrow) selected;
			if (atStart) {
				showLabel(arrow.getLabelStartLeft());
			}
			else {
				showLabel(arrow.getLabelEndLeft());
			}
		}
	}

	public void handleSetMultiplicity(Selectable selected, boolean atStart) {
		if (selected instanceof Arrow) {
			Arrow arrow = (Arrow) selected;
			if (atStart) {
				showLabel(arrow.getLabelStartRight());
			}
			else {
				showLabel(arrow.getLabelEndRight());
			}
		}
	}

	public void handleSetMultiplicityRoleName(Selectable selected) {
		if (selected instanceof Arrow) {
			Arrow arrow = (Arrow) selected;
			ArrowLabel selectedLabel = arrow.getSelectedLabel();
			if (selectedLabel != null) {
				showLabel(selectedLabel);
			}
		}
	}

	public void handleDeleteRoleName(Selectable selected, boolean atStart) {
		if (selected instanceof Arrow) {
			if (atStart) {
				getRelation((Arrow) selected).setStartRoleName("");
			}
			else {
				getRelation((Arrow) selected).setEndRoleName("");
			}
		}
	}

	public void handleDeleteMultiplicty(Selectable selected, boolean atStart) {
		if (selected instanceof Arrow) {
			if (atStart) {
				getRelation((Arrow) selected).setStartMultiplicity("");
			}
			else {
				getRelation((Arrow) selected).setEndMultiplicity("");
			}
		}
	}

	public void handleDeleteMultiplicityRole(Selectable selected) {
		if (selected instanceof Arrow) {
			Arrow arrow = (Arrow) selected;
			ArrowLabel selectedLabel = arrow.getSelectedLabel();
			if (arrow.isStart(selectedLabel)) {
				if (arrow.isLeft(selectedLabel)) {
					getRelation((Arrow) selected).setStartRoleName("");
				}
				else {
					getRelation((Arrow) selected).setStartMultiplicity("");
				}
			}
			else {
				if (arrow.isLeft(selectedLabel)) {
					getRelation((Arrow) selected).setEndRoleName("");
				}
				else {
					getRelation((Arrow) selected).setEndMultiplicity("");
				}
			}
		}
	}

	public void arrangeArrowNumbers(ModelBox startModelBox, ModelBox endModelBox, int plusNumber) {
		List<Relation> relationList = this.modelManager.getRelationsBetween(startModelBox, endModelBox);
		for (int i = 0; i < relationList.size(); i++) {
			Arrow arrow = getArrow(relationList.get(i));
			PaneBox startPaneBox = getPaneBox(relationList.get(i).getStart().getAppendant());
			PaneBox endPaneBox = getPaneBox(relationList.get(i).getEnd().getAppendant());
			if (arrow != null && startPaneBox != null && endPaneBox != null) {
				arrow.arrangeEndpoints(startPaneBox, endPaneBox, i + 1, relationList.size() + plusNumber);
			}
		}
	}

	public void arrangeArrowNumbers(ModelBox startModelBox, ModelBox endModelBox) {
		arrangeArrowNumbers(startModelBox, endModelBox, 0);
	}

}
