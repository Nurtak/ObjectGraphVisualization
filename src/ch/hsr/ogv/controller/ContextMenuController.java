package ch.hsr.ogv.controller;

import java.util.Observable;
import java.util.Observer;

import javafx.event.ActionEvent;
import javafx.geometry.Point3D;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.PickResult;
import ch.hsr.ogv.model.Attribute;
import ch.hsr.ogv.model.ModelBox;
import ch.hsr.ogv.model.ModelClass;
import ch.hsr.ogv.model.ModelObject;
import ch.hsr.ogv.model.Relation;
import ch.hsr.ogv.model.RelationType;
import ch.hsr.ogv.util.ResourceLocator;
import ch.hsr.ogv.util.ResourceLocator.Resource;
import ch.hsr.ogv.view.Arrow;
import ch.hsr.ogv.view.PaneBox;
import ch.hsr.ogv.view.Selectable;
import ch.hsr.ogv.view.SubSceneAdapter;

/**
 *
 * @author Adrian Rieser
 *
 */
public class ContextMenuController extends Observable implements Observer {

	private ModelViewConnector mvConnector;
	private RelationCreationProcess relationCreationProcess = new RelationCreationProcess();
	
	private volatile Selectable selected;
	private volatile Point3D position;

	// Subscene
	private ContextMenu subSceneCM;
	private MenuItem createClass;

	// Class
	private ContextMenu classCM;
	private MenuItem createObject;
	private MenuItem renameClass;
	private MenuItem addAttribute;
	private MenuItem deleteClass;

	// Object
	private ContextMenu objectCM;
	private MenuItem renameObject;
	private MenuItem deleteObject;

	// Relation
	private ContextMenu relationCM;
	private MenuItem changeDirection;
	private MenuItem setMultiplicity;
	private MenuItem setRoleName;
	private MenuItem deleteMultiplicity;
	private MenuItem deleteRoleName;
	private MenuItem deleteRelation;
	private MenuItem createUndirectedAssociation;
	private MenuItem createDirectedAssociation;
	private MenuItem createBidirectedAssociation;
	private MenuItem createUndirectedAggregation;
	private MenuItem createDirectedAggregation;
	private MenuItem createUndirectedComposition;
	private MenuItem createDirectedComposition;
	private MenuItem createGeneralization;
	private MenuItem createDependency;
	
	private volatile boolean atLineSelectionHelper = false;
	private volatile boolean atStartSelectionHelper = false;
	private volatile boolean atEndSelectionHelper = false;

	// Attribute
	private MenuItem renameAttribute;
	private MenuItem moveAttributeUp;
	private MenuItem moveAttributeDown;
	private MenuItem deleteAttribute;

	// Value (Attribute)
	private MenuItem setValue;
	private MenuItem deleteValue;
	
	private void atLineSelectionHelper() {
		this.atLineSelectionHelper = true;
		this.atStartSelectionHelper = false;
		this.atEndSelectionHelper = false;
	}

	private void atStartSelectionHelper() {
		this.atStartSelectionHelper = true;
		this.atLineSelectionHelper = false;
		this.atEndSelectionHelper = false;
	}

	private void atEndSelectionHelper() {
		this.atEndSelectionHelper = true;
		this.atLineSelectionHelper = false;
		this.atStartSelectionHelper = false;
	}
	
	public void setMVConnector(ModelViewConnector mvConnector) {
		this.mvConnector = mvConnector;
	}

	public ContextMenuController() {

		// Subscene
		subSceneCM = new ContextMenu();
		createClass = getMenuItem("Create Class", Resource.CLASS_GIF, subSceneCM);

		// Class
		classCM = new ContextMenu();
		renameClass = getMenuItem("Rename Class", Resource.RENAME_GIF, classCM);
		createObject = getMenuItem("Create Object", Resource.OBJECT_GIF, classCM);
		getClassRelationMenu("Create Relation", Resource.RELATION_GIF, classCM);
		addAttribute = getMenuItem("Add Attribute", Resource.ADD_ATTR_GIF, classCM);
		classCM.getItems().add(new SeparatorMenuItem());
		renameAttribute = getMenuItem("Rename Attribute", Resource.RENAME_ATTR_GIF, classCM);
		moveAttributeUp = getMenuItem("Move Up", Resource.MOVE_UP_PNG, classCM);
		moveAttributeDown = getMenuItem("Move Down", Resource.MOVE_DOWN_PNG, classCM);
		deleteAttribute = getMenuItem("Delete Attribute", Resource.DELETE_PNG, classCM);
		classCM.getItems().add(new SeparatorMenuItem());
		deleteClass = getMenuItem("Delete Class", Resource.DELETE_PNG, classCM);

		// Object
		objectCM = new ContextMenu();
		renameObject = getMenuItem("Rename Object", Resource.RENAME_GIF, objectCM);
		objectCM.getItems().add(new SeparatorMenuItem());
		setValue = getMenuItem("Set Value", Resource.RENAME_ATTR_GIF, objectCM);
		deleteValue = getMenuItem("Delete Value", Resource.DELETE_PNG, objectCM);
		objectCM.getItems().add(new SeparatorMenuItem());
		deleteObject = getMenuItem("Delete Object", Resource.DELETE_PNG, objectCM);

		// Relation
		relationCM = new ContextMenu();
		changeDirection = getMenuItem("Change Direction", Resource.CHANGE_DIRECTION_GIF, relationCM);
		relationCM.getItems().add(new SeparatorMenuItem());
		setMultiplicity = getMenuItem("Set Multiplicity", Resource.ADD_MULTIPLICITY_GIF, relationCM);
		deleteMultiplicity = getMenuItem("Delete Multiplicity", Resource.DELETE_PNG, relationCM);
		relationCM.getItems().add(new SeparatorMenuItem());
		setRoleName = getMenuItem("Set Role", Resource.ADD_ROLE_GIF, relationCM);
		deleteRoleName = getMenuItem("Delete Role", Resource.DELETE_PNG, relationCM);
		relationCM.getItems().add(new SeparatorMenuItem());
		deleteRelation = getMenuItem("Delete Relation", Resource.DELETE_PNG, relationCM);
	}

	private Menu getClassRelationMenu(String title, Resource image, ContextMenu parent) {
		Menu relationMenu = new Menu(title);
		relationMenu.setGraphic(getImageView(image));

		createUndirectedAssociation = getMenuItem("Association", Resource.UNDIRECTED_ASSOCIATION_GIF, relationMenu);
		createDirectedAssociation = getMenuItem("Directed Association", Resource.DIRECTED_ASSOCIATION_GIF, relationMenu);
		createBidirectedAssociation = getMenuItem("Bidirected Association", Resource.BIDIRECTED_ASSOCIATION_GIF, relationMenu);
		createUndirectedAggregation = getMenuItem("Aggregation", Resource.UNDIRECTED_AGGREGATION_GIF, relationMenu);
		createDirectedAggregation = getMenuItem("Directed Aggregation", Resource.DIRECTED_AGGREGATION_GIF, relationMenu);
		createUndirectedComposition = getMenuItem("Composition", Resource.UNDIRECTED_COMPOSITION_GIF, relationMenu);
		createDirectedComposition = getMenuItem("Directed Composition", Resource.DIRECTED_COMPOSITION_GIF, relationMenu);
		relationMenu.getItems().add(new SeparatorMenuItem());
		createGeneralization = getMenuItem("Generalization", Resource.GENERALIZATION_GIF, relationMenu);
		relationMenu.getItems().add(new SeparatorMenuItem());
		createDependency = getMenuItem("Dependency", Resource.DEPENDENCY_GIF, relationMenu);
		parent.getItems().add(relationMenu);
		return relationMenu;
	}

	public void enableContextMenu(SubSceneAdapter subSceneAdapter) {
		subSceneAdapter.getFloor().addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent me) -> {
			if (subSceneAdapter.getFloor().isSelected() && me.getButton() == MouseButton.SECONDARY && me.isStillSincePress()) {
				PickResult pick = me.getPickResult();
				if (pick != null && pick.getIntersectedNode() != null && subSceneAdapter.getFloor().hasTile(pick.getIntersectedNode())) {
					this.position = pick.getIntersectedNode().localToParent(pick.getIntersectedPoint());
				}
				subSceneCM.hide();
				subSceneCM.show(subSceneAdapter.getFloor(), me.getScreenX(), me.getScreenY());
			} else if (subSceneCM.isShowing()) {
				subSceneCM.hide();
			}
		});
	}

	public void enablePaneBoxContextMenu(ModelBox modelBox, PaneBox paneBox, SubSceneAdapter subSceneAdapter) {
		paneBox.get().addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent me) -> {
			if (me.getButton() == MouseButton.SECONDARY && me.isStillSincePress()) {
				hideAllContextMenus();
				if (modelBox instanceof ModelClass) {
					// Class
					enableAttributeSelected(false);
					createObject.setDisable(((ModelClass) modelBox).getSuperClass() != null);
					addAttribute.setDisable(paneBox.getCenterLabels().size() >= PaneBox.MAX_CENTER_LABELS);
					classCM.show(paneBox.get(), me.getScreenX(), me.getScreenY());
				} else if ((modelBox instanceof ModelObject)) {
					// Object
					hideAllContextMenus();
					setValue.setDisable(true);
					deleteValue.setDisable(true);
					objectCM.show(paneBox.get(), me.getScreenX(), me.getScreenY());
				}
			}
			me.consume();
		});
		
		paneBox.getSelection().addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent me) -> {
			if (me.getButton() == MouseButton.SECONDARY && me.isStillSincePress()) {
				hideAllContextMenus();
				if (modelBox instanceof ModelClass) {
					// Class
					enableAttributeSelected(false);
					createObject.setDisable(((ModelClass) modelBox).getSuperClass() != null);
					addAttribute.setDisable(paneBox.getCenterLabels().size() >= PaneBox.MAX_CENTER_LABELS);
					classCM.show(paneBox.get(), me.getScreenX(), me.getScreenY());
				} else if ((modelBox instanceof ModelObject)) {
					// Object
					hideAllContextMenus();
					setValue.setDisable(true);
					deleteValue.setDisable(true);
					objectCM.show(paneBox.get(), me.getScreenX(), me.getScreenY());
				}
			}
			me.consume();
		});
	}
	
	public void enableCenterFieldContextMenu(ModelBox modelBox, PaneBox paneBox, SubSceneAdapter subSceneAdapter) {
		for (Label centerLabel : paneBox.getCenterLabels()) {
			enableContextMenu(centerLabel, modelBox, paneBox, subSceneAdapter);
		}
	}

	private void enableContextMenu(Label label, ModelBox modelBox, PaneBox paneBox, SubSceneAdapter subSceneAdapter) {
		label.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent me) -> {
			if (me.getButton() == MouseButton.SECONDARY && me.isStillSincePress()) {
				hideAllContextMenus();
				if (modelBox instanceof ModelClass) {
					// Label on Class
					createObject.setDisable(((ModelClass) modelBox).getSuperClass() != null);
					enableAttributeSelected(true);
					addAttribute.setDisable(paneBox.getCenterLabels().size() >= PaneBox.MAX_CENTER_LABELS);
					int rowIndex = paneBox.getCenterLabels().indexOf(paneBox.getSelectedLabel());
					moveAttributeUp.setDisable(rowIndex <= 0 || rowIndex > paneBox.getCenterLabels().size() - 1);
					moveAttributeDown.setDisable(rowIndex < 0 || rowIndex >= paneBox.getCenterLabels().size() - 1);
					classCM.show(paneBox.get(), me.getScreenX(), me.getScreenY());
				} else if (modelBox instanceof ModelObject) {
					// Label on Object
					setValue.setDisable(false);
					deleteValue.setDisable(false);
					objectCM.show(paneBox.get(), me.getScreenX(), me.getScreenY());
				}
				me.consume();
			}
		});

	}
	
	private void enableRelationContextMenu(Arrow arrow, MouseEvent me, boolean disableMultiRole, boolean disableDirection, boolean atStart) {
		hideAllContextMenus();
		changeDirection.setDisable(disableDirection);
		setMultiplicity.setDisable(disableMultiRole);
		setRoleName.setDisable(disableMultiRole);
		deleteMultiplicity.setDisable(!arrow.hasRightText(atStart) || disableMultiRole);
		deleteRoleName.setDisable(!arrow.hasLeftText(atStart) || disableMultiRole);
		relationCM.show(arrow, me.getScreenX(), me.getScreenY());
		me.consume();
	}
	
	private boolean roleAttributeConflict(Arrow arrow, Relation relation) {
		if(relation != null) {
			ModelBox startModelBox = relation.getStart().getAppendant();
			ModelBox endModelBox = relation.getEnd().getAppendant();
			if(startModelBox instanceof ModelClass && endModelBox instanceof ModelClass) {
				ModelClass startModelClass = (ModelClass) startModelBox;
				ModelClass endModelClass = (ModelClass) endModelBox;
				for(Attribute attribute : startModelClass.getAttributes()) {
					boolean conflictStart = arrow.getLabelStartLeft().getLabelText().equals(attribute.getName());
					if(conflictStart) return true;
				}
				for(Attribute attribute : endModelClass.getAttributes()) {
					boolean conflictEnd = arrow.getLabelEndLeft().getLabelText().equals(attribute.getName());
					if(conflictEnd) return true;
				}
			}
		}
		return false;
	}
	
	private boolean relationTypeConflict(Arrow arrow) {
		return arrow.getRelationType().equals(RelationType.GENERALIZATION)
		    || arrow.getRelationType().equals(RelationType.DEPENDENCY)
			|| arrow.getRelationType().equals(RelationType.ASSOZIATION_CLASS)
			|| arrow.getRelationType().equals(RelationType.OBJDIAGRAM)
			|| arrow.getRelationType().equals(RelationType.OBJGRAPH);
	}

	public void enableContextMenu(Arrow arrow, Relation relation) {
		
		arrow.getLineSelectionHelper().addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent me) -> {
			if (me.getButton() == MouseButton.SECONDARY && me.isStillSincePress()) {
				atLineSelectionHelper();
				boolean disableMRD = relationTypeConflict(arrow);
				boolean disableDirection = disableMRD || roleAttributeConflict(arrow, relation);
				enableRelationContextMenu(arrow, me, true, disableDirection, false);
			}
		});

		arrow.getStartSelectionHelper().addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent me) -> {
			if (me.getButton() == MouseButton.SECONDARY && me.isStillSincePress()) {
				atStartSelectionHelper();
				boolean disableMRD = relationTypeConflict(arrow);
				boolean disableDirection = disableMRD || roleAttributeConflict(arrow, relation);
				enableRelationContextMenu(arrow, me, disableMRD, disableDirection, true);
			}
		});

		arrow.getEndSelectionHelper().addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent me) -> {
			if (me.getButton() == MouseButton.SECONDARY && me.isStillSincePress()) {
				atEndSelectionHelper();
				boolean disableMRD = relationTypeConflict(arrow);
				boolean disableDirection = disableMRD || roleAttributeConflict(arrow, relation);
				enableRelationContextMenu(arrow, me, disableMRD, disableDirection, false);
			}
		});
		
		arrow.getLabelStartLeft().getArrowText().addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent me) -> {
			if (me.getButton() == MouseButton.SECONDARY && me.isStillSincePress()) {
				atStartSelectionHelper();
				boolean disableMRD = relationTypeConflict(arrow);
				boolean disableDirection = disableMRD || roleAttributeConflict(arrow, relation);
				enableRelationContextMenu(arrow, me, disableMRD, disableDirection, true);
			}
		});
		
		arrow.getLabelStartRight().getArrowText().addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent me) -> {
			if (me.getButton() == MouseButton.SECONDARY && me.isStillSincePress()) {
				atStartSelectionHelper();
				boolean disableMRD = relationTypeConflict(arrow);
				boolean disableDirection = disableMRD || roleAttributeConflict(arrow, relation);
				enableRelationContextMenu(arrow, me, disableMRD, disableDirection, true);
			}
		});
		
		arrow.getLabelEndLeft().getArrowText().addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent me) -> {
			if (me.getButton() == MouseButton.SECONDARY && me.isStillSincePress()) {
				atEndSelectionHelper();
				boolean disableMRD = relationTypeConflict(arrow);
				boolean disableDirection = disableMRD || roleAttributeConflict(arrow, relation);
				enableRelationContextMenu(arrow, me, disableMRD, disableDirection, false);
			}
		});
		
		arrow.getLabelEndRight().getArrowText().addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent me) -> {
			if (me.getButton() == MouseButton.SECONDARY && me.isStillSincePress()) {
				atEndSelectionHelper();
				boolean disableMRD = relationTypeConflict(arrow);
				boolean disableDirection = disableMRD || roleAttributeConflict(arrow, relation);
				enableRelationContextMenu(arrow, me, disableMRD, disableDirection, false);
			}
		});
	}

	private void hideAllContextMenus() {
		subSceneCM.hide();
		classCM.hide();
		objectCM.hide();
		relationCM.hide();
	}

	private void enableAttributeSelected(boolean isAttributeActive) {
		renameAttribute.setDisable(!isAttributeActive);
		moveAttributeUp.setDisable(!isAttributeActive);
		moveAttributeDown.setDisable(!isAttributeActive);
		deleteAttribute.setDisable(!isAttributeActive);
	}

	private void startRelationCreation(SelectionController selectionController, SubSceneAdapter subSceneAdapter,
									   PaneBox selectedPaneBox, RelationType relationType) {
		//TODO
		this.relationCreationProcess.startProcess(this.mvConnector, selectionController, subSceneAdapter, selectedPaneBox, relationType);
	}
	
	public void enableActionEvents(SelectionController selectionController, SubSceneAdapter subSceneAdapter) {

		// SubScene
		createClass.setOnAction((ActionEvent e) -> {
			PaneBox newPaneBox = this.mvConnector.handleCreateNewClass(position);
			selectionController.setSelected(newPaneBox, true, subSceneAdapter);
		});

		// Class
		createObject.setOnAction((ActionEvent e) -> {
			PaneBox newPaneBox = this.mvConnector.handleCreateNewObject(selected);
			selectionController.setSelected(newPaneBox, true, subSceneAdapter);
		});
		renameClass.setOnAction((ActionEvent e) -> {
			this.mvConnector.handleRenameClassOrObject(selected);
		});
		addAttribute.setOnAction((ActionEvent e) -> {
			this.mvConnector.handleCreateNewAttribute(selected);

		});
		deleteClass.setOnAction((ActionEvent e) -> {
			this.mvConnector.handleDelete(selected);
		});
		createUndirectedAssociation.setOnAction((ActionEvent e) -> {
			if(selected instanceof PaneBox) {
				PaneBox selectedPaneBox = (PaneBox) selected;
				startRelationCreation(selectionController, subSceneAdapter, selectedPaneBox, RelationType.UNDIRECTED_ASSOCIATION);
			}
		});
		createDirectedAssociation.setOnAction((ActionEvent e) -> {
			if(selected instanceof PaneBox) {
				PaneBox selectedPaneBox = (PaneBox) selected;
				startRelationCreation(selectionController, subSceneAdapter, selectedPaneBox, RelationType.DIRECTED_ASSOCIATION);
			}
		});
		createBidirectedAssociation.setOnAction((ActionEvent e) -> {
			if(selected instanceof PaneBox) {
				PaneBox selectedPaneBox = (PaneBox) selected;
				startRelationCreation(selectionController, subSceneAdapter, selectedPaneBox, RelationType.BIDIRECTED_ASSOCIATION);
			}
		});
		createUndirectedAggregation.setOnAction((ActionEvent e) -> {
			if(selected instanceof PaneBox) {
				PaneBox selectedPaneBox = (PaneBox) selected;
				startRelationCreation(selectionController, subSceneAdapter, selectedPaneBox, RelationType.UNDIRECTED_AGGREGATION);
			}
		});
		createDirectedAggregation.setOnAction((ActionEvent e) -> {
			if(selected instanceof PaneBox) {
				PaneBox selectedPaneBox = (PaneBox) selected;
				startRelationCreation(selectionController, subSceneAdapter, selectedPaneBox, RelationType.DIRECTED_AGGREGATION);
			}
		});
		createUndirectedComposition.setOnAction((ActionEvent e) -> {
			if(selected instanceof PaneBox) {
				PaneBox selectedPaneBox = (PaneBox) selected;
				startRelationCreation(selectionController, subSceneAdapter, selectedPaneBox, RelationType.UNDIRECTED_COMPOSITION);
			}
		});
		createDirectedComposition.setOnAction((ActionEvent e) -> {
			if(selected instanceof PaneBox) {
				PaneBox selectedPaneBox = (PaneBox) selected;
				startRelationCreation(selectionController, subSceneAdapter, selectedPaneBox, RelationType.DIRECTED_COMPOSITION);
			}
		});
		createGeneralization.setOnAction((ActionEvent e) -> {
			if(selected instanceof PaneBox) {
				PaneBox selectedPaneBox = (PaneBox) selected;
				startRelationCreation(selectionController, subSceneAdapter, selectedPaneBox, RelationType.GENERALIZATION);
			}
		});
		createDependency.setOnAction((ActionEvent e) -> {
			if(selected instanceof PaneBox) {
				PaneBox selectedPaneBox = (PaneBox) selected;
				startRelationCreation(selectionController, subSceneAdapter, selectedPaneBox, RelationType.DEPENDENCY);
			}
		});

		// Object
		renameObject.setOnAction((ActionEvent e) -> {
			mvConnector.handleRenameClassOrObject(selected);
		});
		deleteObject.setOnAction((ActionEvent e) -> {
			mvConnector.handleDelete(selected);
		});

		// Relation
		changeDirection.setOnAction((ActionEvent e) -> {
			mvConnector.handleChangeDirection(selected);
		});
		
		setMultiplicity.setOnAction((ActionEvent e) -> {
			if(atLineSelectionHelper) return;
			if(atStartSelectionHelper) {
				mvConnector.handleSetMultiplicity(selected, true);
			}
			else if(atEndSelectionHelper) {
				mvConnector.handleSetMultiplicity(selected, false);
			}
		});
		
		deleteMultiplicity.setOnAction((ActionEvent e) -> {
			if(atLineSelectionHelper) return;
			if(atStartSelectionHelper) {
				mvConnector.handleDeleteMultiplicty(selected, true);
			}
			else if(atEndSelectionHelper) {
				mvConnector.handleDeleteMultiplicty(selected, false);
			}
		});
		
		setRoleName.setOnAction((ActionEvent e) -> {
			if(atLineSelectionHelper) return;
			if(atStartSelectionHelper) {
				mvConnector.handleSetRoleName(selected, true);
			}
			else if(atEndSelectionHelper) {
				mvConnector.handleSetRoleName(selected, false);
			}
		});
		
		deleteRoleName.setOnAction((ActionEvent e) -> {
			if(atLineSelectionHelper) return;
			if(atStartSelectionHelper) {
				mvConnector.handleDeleteRoleName(selected, true);
			}
			else if(atEndSelectionHelper) {
				mvConnector.handleDeleteRoleName(selected, false);
			}

		});
		
		deleteRelation.setOnAction((ActionEvent e) -> {
			mvConnector.handleDelete(selected);
		});

		// Attribute
		renameAttribute.setOnAction((ActionEvent e) -> {
			mvConnector.handleRenameFieldOrValue(selected);
		});
		moveAttributeUp.setOnAction((ActionEvent e) -> {
			mvConnector.handleMoveAttributeUp(selected);
		});
		moveAttributeDown.setOnAction((ActionEvent e) -> {
			mvConnector.handleMoveAttributeDown(selected);
		});
		deleteAttribute.setOnAction((ActionEvent e) -> {
			mvConnector.handleDeleteAttribute(selected);
		});

		// Value (Attribute)
		setValue.setOnAction((ActionEvent e) -> {
			mvConnector.handleRenameFieldOrValue(selected);
		});
		
		deleteValue.setOnAction((ActionEvent e) -> {
			mvConnector.handleDeleteAttributeValue(selected);
		});

	}

	@Override
	public void update(Observable o, Object arg) {
		if (o instanceof SelectionController && (arg instanceof PaneBox || arg instanceof Arrow)) {
			SelectionController selectionController = (SelectionController) o;
			if (selectionController.hasCurrentSelection()) {
				this.selected = (Selectable) arg;
			}
		}
	}

	private ImageView getImageView(Resource image) {
		return new ImageView(ResourceLocator.getResourcePath(image).toExternalForm());
	}

	private MenuItem getMenuItem(String title, Resource image, ContextMenu parent) {
		MenuItem menuItem = new MenuItem(title);
		menuItem.setGraphic(getImageView(image));
		parent.getItems().add(menuItem);
		return menuItem;
	}

	private MenuItem getMenuItem(String title, Resource image, Menu parent) {
		MenuItem menuItem = new MenuItem(title);
		menuItem.setGraphic(getImageView(image));
		parent.getItems().add(menuItem);
		return menuItem;
	}
}
