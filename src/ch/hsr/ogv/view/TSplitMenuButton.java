package ch.hsr.ogv.view;

import ch.hsr.ogv.util.ResourceLocator;
import ch.hsr.ogv.util.ResourceLocator.Resource;
import javafx.scene.Node;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitMenuButton;

public class TSplitMenuButton {
	
	private SplitMenuButton splitMenuButton;
	private MenuItem selectedChoice;
	
	private boolean isSelected = false;
	
	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
		if(isSelected) {
			this.splitMenuButton.getStyleClass().add("tsplit-menu-button");
		}
		else {
			this.splitMenuButton.getStyleClass().remove("tsplit-menu-button");
		}
	}

	public TSplitMenuButton(SplitMenuButton splitMenuButton) {
		this.splitMenuButton = splitMenuButton;
		this.splitMenuButton.getStylesheets().add(ResourceLocator.getResourcePath(Resource.TSPLITMENUBUTTON_CSS).toExternalForm());
	}
	
	public MenuItem selectedChoice() {
		return this.selectedChoice;
	}
	
	public void setChoice(MenuItem menuItem) {
		this.selectedChoice = menuItem;
		Node graphic = menuItem.getGraphic();
		this.splitMenuButton.setGraphic(graphic);
		String title = menuItem.getText();
		this.splitMenuButton.setText(title);
	}

}
