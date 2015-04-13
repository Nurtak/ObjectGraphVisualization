package ch.hsr.ogv.controller;

import java.util.Observable;
import java.util.Observer;

import javafx.event.ActionEvent;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import ch.hsr.ogv.model.ModelBox;
import ch.hsr.ogv.model.ModelClass;
import ch.hsr.ogv.model.ModelObject;
import ch.hsr.ogv.model.Relation;
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
	private MenuItem instantiateObject;
	private MenuItem deleteClass;

	private ContextMenu objectCM;
	private MenuItem deleteObject;

	private ContextMenu relationCM;
	private MenuItem changeDirection;
	private MenuItem deleteRelation;

	private Menu newRelationM;
	private ToggleGroup newRelationTG;

	public ContextMenuController() {
		// Class
		classCM = new ContextMenu();
		instantiateObject = new MenuItem("Instantiate object");
		deleteClass = new MenuItem("Delete class");
		classCM.getItems().add(instantiateObject);
		classCM.getItems().add(deleteClass);

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

		newRelationM = new Menu("Picture Effect");
		newRelationTG = new ToggleGroup();
		// new
		// RadioMenuItem itemEffect = new RadioMenuItem(effect.getKey());
		// itemEffect.setUserData(effect.getValue());
		// itemEffect.setToggleGroup(groupEffect);
		// menuEffect.getItems().add(itemEffect);
		// }

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
		instantiateObject.setOnAction((ActionEvent e) -> {
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
