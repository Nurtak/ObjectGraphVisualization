package ch.hsr.ogv.controller.contextmenu;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import ch.hsr.ogv.model.ModelClass;
import ch.hsr.ogv.view.PaneBox;
import ch.hsr.ogv.view.SubSceneAdapter;

public class ClassCMController {

	private ContextMenu cm;

	public ClassCMController() {
		cm = new ContextMenu();
		MenuItem cmItem1 = new MenuItem("Instantiate object from class");
		cmItem1.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				System.out.println("" + "");
			}
		});
		cm.getItems().add(cmItem1);
	}

	public void show(ModelClass modelClass, PaneBox paneBox, SubSceneAdapter subSceneAdapter) {
		paneBox.get().setOnMouseClicked((MouseEvent me) -> {
			if (MouseButton.SECONDARY.equals(me.getButton())) {
				cm.show(paneBox.get(), me.getScreenX(), me.getScreenY());
			}
		});
	}

}
