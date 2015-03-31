package ch.hsr.ogv.view;

import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.image.ImageView;
import ch.hsr.ogv.util.ResourceLocator;
import ch.hsr.ogv.util.ResourceLocator.Resource;

public class TSplitMenuButton {

	private SplitMenuButton splitMenuButton;
	private MenuItem selectedChoice;

	private boolean isSelected = false;

	public TSplitMenuButton(SplitMenuButton splitMenuButton, MenuItem startChoice) {
		this.splitMenuButton = splitMenuButton;
		this.splitMenuButton.getStylesheets().add(ResourceLocator.getResourcePath(Resource.TSPLITMENUBUTTON_CSS).toExternalForm());
		this.selectedChoice = startChoice;
	}

	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
		if (isSelected) {
			splitMenuButton.getStyleClass().add("tsplit-menu-button");
		} else {
			splitMenuButton.getStyleClass().remove("tsplit-menu-button");
		}
	}

	public MenuItem selectedChoice() {
		return selectedChoice;
	}

	public void setChoice(MenuItem newChoice) {
		selectedChoice = newChoice;
		ImageView graphic = (ImageView) newChoice.getGraphic();
		ImageView graphic2 = new ImageView(graphic.getImage());
		splitMenuButton.setGraphic(graphic2);
		String title = newChoice.getText();
		splitMenuButton.setText(title);
	}

}
