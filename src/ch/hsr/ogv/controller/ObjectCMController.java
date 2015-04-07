package ch.hsr.ogv.controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import ch.hsr.ogv.view.PaneBox;
import ch.hsr.ogv.view.SubSceneAdapter;

public class ObjectCMController {
	
	private ContextMenu cm;

	public ObjectCMController() {
		cm = new ContextMenu();
		MenuItem cmItem1 = new MenuItem("TODO: Object");
		cmItem1.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent e) {
				System.out.println("ITEM1!");
			}
		});
		cm.getItems().add(cmItem1);
	}

	public void show(PaneBox paneBox, SubSceneAdapter subSceneAdapter) {
		paneBox.get().setOnMouseClicked((MouseEvent me) -> {
			if (MouseButton.SECONDARY.equals(me.getButton())) {
				cm.show(subSceneAdapter.getSubScene(), me.getScreenX(), me.getScreenY());
			}
		});
	}
}
