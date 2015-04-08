package ch.hsr.ogv.controller;

import java.util.Observable;

import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.hsr.ogv.model.ModelBox;
import ch.hsr.ogv.util.ModelUtil;
import ch.hsr.ogv.view.PaneBox;

/**
 *
 * @author Adrian Rieser
 *
 */
public class ContextMenuController extends Observable {

	private final static Logger logger = LoggerFactory.getLogger(ContextMenuController.class);

	private ContextMenu classCM;
	private MenuItem cInstantiateObject;
	private MenuItem cRename;

	private ContextMenu objectCM;
	private MenuItem oRename;

	public ContextMenuController() {
		classCM = new ContextMenu();
		cInstantiateObject = new MenuItem("Instantiate object");
		cRename = new MenuItem("Rename Class");
		classCM.getItems().add(cInstantiateObject);
		classCM.getItems().add(cRename);

		objectCM = new ContextMenu();
		oRename = new MenuItem("Rename Object");
		objectCM.getItems().add(cRename);
	}

	public void enableContextMenu(ModelBox modelBox, PaneBox paneBox) {
		if (ModelUtil.isClass(modelBox)) {
			paneBox.getBox().addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent e) {
					if (paneBox.isSelected() && e.getButton() == MouseButton.SECONDARY) {
						classCM.show(paneBox.get(), e.getScreenX(), e.getScreenY());
					}
				}
			});
		} else if (ModelUtil.isObject(modelBox)) {
			paneBox.getBox().addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent e) {
					if (paneBox.isSelected() && e.getButton() == MouseButton.SECONDARY) {
						objectCM.show(paneBox.get(), e.getScreenX(), e.getScreenY());
					}
				}
			});
		}
	}

}
