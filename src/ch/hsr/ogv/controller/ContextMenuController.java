package ch.hsr.ogv.controller;

import java.util.Observable;
import java.util.Observer;

import javafx.event.ActionEvent;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import ch.hsr.ogv.model.ModelBox;
import ch.hsr.ogv.model.ModelClass;
import ch.hsr.ogv.model.ModelObject;
import ch.hsr.ogv.model.Relation;
import ch.hsr.ogv.util.ResourceLocator;
import ch.hsr.ogv.util.ResourceLocator.Resource;
import ch.hsr.ogv.view.Arrow;
import ch.hsr.ogv.view.PaneBox;
import ch.hsr.ogv.view.Selectable;

/**
 *
 * @author Adrian Rieser
 *
 */
public class ContextMenuController extends Observable implements Observer {

	private ModelViewConnector mvConnector;
	private Selectable selected;

	private ContextMenu classCM;
	private MenuItem createObject;
	private MenuItem deleteClass;

	private ContextMenu objectCM;
	private MenuItem deleteObject;

	private ContextMenu relationCM;
	private MenuItem changeDirection;
	private MenuItem deleteRelation;

	private Menu createRelationM;
	private ToggleGroup createRelationTG;
	private MenuItem createUndirectedAssociation;
	private MenuItem createDirectedAssociation;
	private MenuItem createBidirectedAssociation;
	private MenuItem createUndirectedAggregation;
	private MenuItem createDirectedAggregation;
	private MenuItem createUndirectedComposition;
	private MenuItem createDirectedComposition;
	private MenuItem createGeneralization;
	private MenuItem createDependency;

	public ContextMenuController() {
		// Class
		classCM = new ContextMenu();
		createObject = new MenuItem("Create object");
		deleteClass = new MenuItem("Delete class");
		classCM.getItems().add(createObject);
		classCM.getItems().add(deleteClass);

		createRelationM = new Menu("Create Relation");
		createRelationTG = new ToggleGroup();

		createUndirectedAssociation = new MenuItem("Association");
		ImageView undirectedAssociationIV = new ImageView(ResourceLocator.getResourcePath(Resource.UNDIRECTED_ASSOCIATION_GIF).toExternalForm());
		createUndirectedAssociation.setGraphic(undirectedAssociationIV);

		createDirectedAssociation = new MenuItem("Directed Association");
		ImageView directedAssociationIV = new ImageView(ResourceLocator.getResourcePath(Resource.DIRECTED_ASSOCIATION_GIF).toExternalForm());
		createDirectedAssociation.setGraphic(directedAssociationIV);

		createBidirectedAssociation = new MenuItem("Bidirected Association");
		ImageView bidirectedAssociationIV = new ImageView(ResourceLocator.getResourcePath(Resource.BIDIRECTED_ASSOCIATION_GIF).toExternalForm());
		createBidirectedAssociation.setGraphic(bidirectedAssociationIV);

		createUndirectedAggregation = new MenuItem("Aggregation");
		ImageView undirectedAggregationIV = new ImageView(ResourceLocator.getResourcePath(Resource.UNDIRECTED_AGGREGATION_GIF).toExternalForm());
		createUndirectedAggregation.setGraphic(undirectedAggregationIV);

		createDirectedAggregation = new MenuItem("Directed Aggregation");
		ImageView directedAggregationIV = new ImageView(ResourceLocator.getResourcePath(Resource.DIRECTED_AGGREGATION_GIF).toExternalForm());
		createDirectedAggregation.setGraphic(directedAggregationIV);

		createUndirectedComposition = new MenuItem("Composition");
		ImageView undirectedCompositionnIV = new ImageView(ResourceLocator.getResourcePath(Resource.UNDIRECTED_COMPOSITION_GIF).toExternalForm());
		createUndirectedComposition.setGraphic(undirectedCompositionnIV);

		createDirectedComposition = new MenuItem("Directed Composition");
		ImageView directedCompositionIV = new ImageView(ResourceLocator.getResourcePath(Resource.DIRECTED_COMPOSITION_GIF).toExternalForm());
		createDirectedComposition.setGraphic(directedCompositionIV);

		createGeneralization = new MenuItem("Generalization");
		ImageView generalizationIV = new ImageView(ResourceLocator.getResourcePath(Resource.GENERALIZATION_GIF).toExternalForm());
		createGeneralization.setGraphic(generalizationIV);

		createDependency = new MenuItem("Dependency");
		ImageView dependencyIV = new ImageView(ResourceLocator.getResourcePath(Resource.DEPENDENCY_GIF).toExternalForm());
		createDependency.setGraphic(dependencyIV);

		createRelationM.getItems().add(createUndirectedAssociation);
		createRelationM.getItems().add(createDirectedAssociation);
		createRelationM.getItems().add(createBidirectedAssociation);
		createRelationM.getItems().add(createUndirectedAggregation);
		createRelationM.getItems().add(createDirectedAggregation);
		createRelationM.getItems().add(createUndirectedComposition);
		createRelationM.getItems().add(createDirectedComposition);
		// createRelationM.getItems().add(new Separator().);
		createRelationM.getItems().add(createGeneralization);
		createRelationM.getItems().add(createDependency);
		classCM.getItems().add(createRelationM);

		// Object
		objectCM = new ContextMenu();
		deleteObject = new MenuItem("Delete object");
		objectCM.getItems().add(deleteObject);

		// Relation
		relationCM = new ContextMenu();
		changeDirection = new MenuItem("Change direction");
		deleteRelation = new MenuItem("Delete relation");
		relationCM.getItems().add(changeDirection);
		relationCM.getItems().add(deleteRelation);

	}

	public void enableContextMenu(ModelBox modelBox, PaneBox paneBox) {
		if (modelBox instanceof ModelClass) {
			paneBox.get().addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent me) -> {
				if (paneBox.isSelected() && me.getButton() == MouseButton.SECONDARY) {
					classCM.show(paneBox.get(), me.getScreenX(), me.getScreenY());
				}
			});
		} else if ((modelBox instanceof ModelObject)) {
			paneBox.get().addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent me) -> {
				if (paneBox.isSelected() && me.getButton() == MouseButton.SECONDARY) {
					objectCM.show(paneBox.get(), me.getScreenX(), me.getScreenY());
				}
			});
		}
	}

	public void enableContextMenu(Relation relation, Arrow arrow) {
		arrow.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent me) -> {
			if (arrow.isSelected() && me.getButton() == MouseButton.SECONDARY) {
				relationCM.show(arrow, me.getScreenX(), me.getScreenY());
			}
		});
	}

	public void setMVConnector(ModelViewConnector mvConnector) {
		this.mvConnector = mvConnector;
		fillContextMenu();
	}

	public void fillContextMenu() {
		// Class
		createObject.setOnAction((ActionEvent e) -> {
			mvConnector.handleCreateNewObject(selected);
		});
		deleteClass.setOnAction((ActionEvent e) -> {
			mvConnector.handleDelete(selected);
		});

		// Object
		deleteObject.setOnAction((ActionEvent e) -> {
			mvConnector.handleDelete(selected);
		});

		// Relation
		deleteRelation.setOnAction((ActionEvent e) -> {
			mvConnector.handleDelete(selected);
		});
	}

	@Override
	public void update(Observable o, Object arg) {
		if (o instanceof SelectionController && (arg instanceof PaneBox || arg instanceof Arrow)) {
			SelectionController selectionController = (SelectionController) o;
			if (selectionController.hasSelection()) {
				this.selected = (Selectable) arg;
			}
		}
	}
}
