package ch.hsr.ogv.controller;

import java.util.HashMap;
import java.util.HashSet;
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
import ch.hsr.ogv.view.Arrow;
import ch.hsr.ogv.view.Floor;
import ch.hsr.ogv.view.PaneBox;
import ch.hsr.ogv.view.Selectable;
import ch.hsr.ogv.view.SubSceneAdapter;

/**
 *
 * @author Adrian Rieser
 *
 */
public class ModelViewConnector {

	private ModelManager modelManager = new ModelManager();

	private Map<ModelBox, PaneBox> boxes = new HashMap<ModelBox, PaneBox>();
	private Map<Relation, Arrow> arrows = new HashMap<Relation, Arrow>();

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
		Set<PaneBox> classPaneBoxes = new HashSet<PaneBox>();
		for (ModelBox modelBox : getBoxes().keySet()) {
			if (modelBox instanceof ModelClass) {
				classPaneBoxes.add(getBoxes().get(modelBox));
			}
		}
		return classPaneBoxes;
	}

	public Set<PaneBox> getObjectPaneBoxes() {
		Set<PaneBox> objectPaneBoxes = new HashSet<PaneBox>();
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

	public boolean containsBoxes(ModelBox modelBox) {
		return this.boxes.containsKey(modelBox);
	}

	public boolean containsArrows(Relation relation) {
		return this.arrows.containsKey(relation);
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
		ModelClass mcA = this.modelManager.createClass(new Point3D(-300, PaneBox.INIT_DEPTH / 2, 200), PaneBox.MIN_WIDTH, PaneBox.MIN_HEIGHT, PaneBox.DEFAULT_COLOR);
		mcA.setName("A");
		ModelClass mcB = this.modelManager.createClass(new Point3D(300, PaneBox.INIT_DEPTH / 2, 300), PaneBox.MIN_WIDTH, PaneBox.MIN_HEIGHT, PaneBox.DEFAULT_COLOR);
		mcB.setName("B");
//		ModelClass mcC = this.modelManager.createClass(new Point3D(300, PaneBox.INIT_DEPTH / 2, -300), PaneBox.MIN_WIDTH, PaneBox.MIN_HEIGHT, PaneBox.DEFAULT_COLOR);
//		mcC.setName("C");

		ModelObject moA1 = this.modelManager.createObject(mcA);
		ModelObject moB1 = this.modelManager.createObject(mcB);
//		ModelObject moB2 = this.modelManager.createObject(mcB);
//		ModelObject moB3 = this.modelManager.createObject(mcB);

		this.modelManager.createRelation(mcA, mcB, RelationType.UNDIRECTED_ASSOCIATION, Arrow.DEFAULT_COLOR);
//		this.modelManager.createRelation(mcC, mcB, RelationType.DIRECTED_AGGREGATION, Arrow.DEFAULT_COLOR);
//		this.modelManager.createRelation(mcC, mcA, RelationType.DEPENDENCY, Arrow.DEFAULT_COLOR);
		this.modelManager.createRelation(moA1, moB1, RelationType.OBJDIAGRAM, Arrow.DEFAULT_COLOR);
//		this.modelManager.createRelation(moA1, moB2, RelationType.OBJDIAGRAM, Arrow.DEFAULT_COLOR);
//		this.modelManager.createRelation(moA1, moB3, RelationType.OBJDIAGRAM, Arrow.DEFAULT_COLOR);

		mcA.createAttribute();
		mcA.createAttribute();
		mcA.createAttribute();
		mcA.createAttribute();
		mcA.createAttribute();

		mcB.createAttribute();
	}

	public PaneBox handleCreateNewClass(Point3D mouseCoords) {
		Point3D boxPosition = new Point3D(mouseCoords.getX(), PaneBox.INIT_DEPTH / 2, mouseCoords.getZ());
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
			ModelBox selectedModelBox = this.getModelBox(selectedPaneBox);
			if (selectedModelBox != null && selectedModelBox instanceof ModelClass) {
				ModelClass selectedModelClass = (ModelClass) selectedModelBox;
				Attribute newAttribute = selectedModelClass.createAttribute();
				int lastVisibleIndex = selectedPaneBox.numberCenterLabelShowing() - 1;
				Label lastVisibleLabel = selectedPaneBox.getCenterLabels().get(lastVisibleIndex);
				selectedPaneBox.allowCenterFieldTextInput(lastVisibleLabel, true);
				return newAttribute;
			}
		}
		return null;
	}

	public void handleDelete(Selectable selected) {
		if (selected instanceof PaneBox) {
			ModelBox modelToDelete = getModelBox((PaneBox) selected);
			if (modelToDelete instanceof ModelClass) {
				ModelClass classToDelete = (ModelClass) modelToDelete;
				this.modelManager.deleteClass(classToDelete);
			} else if (modelToDelete instanceof ModelObject) {
				ModelObject objectToDelete = (ModelObject) modelToDelete;
				this.modelManager.deleteObject(objectToDelete);
			}
		} else if (selected instanceof Arrow) {
			Relation relationToDelete = getRelation((Arrow) selected);
			this.modelManager.deleteRelation(relationToDelete);
		}
	}

	public void handleMoveAttributeUp(Selectable selected) {
		if (selected instanceof PaneBox) {
			PaneBox paneBox = (PaneBox) selected;
			Label selectedLabel = paneBox.getSelectedLabel();
			if (selectedLabel != null && paneBox.getCenterLabels().indexOf(selectedLabel) >= 0) {
				int rowIndex = paneBox.getCenterLabels().indexOf(selectedLabel);
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
			}
		}
	}

	public void handleColorPick(Selectable selected, Color pickedColor) {
		if (selected instanceof PaneBox) {
			ModelBox modelBox = this.getModelBox((PaneBox) selected);
			modelBox.setColor(pickedColor);
		} else if (selected instanceof Arrow) {
			Relation relation = this.getRelation((Arrow) selected);
			relation.setColor(pickedColor);
		} else if (selected instanceof SubSceneAdapter) {
			SubSceneAdapter subSceneAdapter = (SubSceneAdapter) selected;
			subSceneAdapter.getFloor().setColor(pickedColor);
		} else if (selected instanceof Floor) {
			Floor floor = (Floor) selected;
			floor.setColor(pickedColor);
		}
	}

	public void handleRename(Selectable selected) {
		if (selected instanceof PaneBox) {
			PaneBox selectedBox = (PaneBox) selected;
			Label selectedLabel = selectedBox.getSelectedLabel();
			if (selectedLabel == null) {
				selectedBox.allowTopTextInput(true);
			} else {
				selectedBox.allowCenterFieldTextInput(selectedLabel, true);
			}
		}
	}

	public void handleChangeDirection(Selectable selected){
		if (selected instanceof Arrow) {
			getRelation((Arrow) selected).changeDirection();
		}
	}
}
