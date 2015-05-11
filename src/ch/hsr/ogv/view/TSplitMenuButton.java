package ch.hsr.ogv.view;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableMap;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;
import ch.hsr.ogv.util.ResourceLocator;
import ch.hsr.ogv.util.ResourceLocator.Resource;

/**
 * 
 * @author Adrian Rieser
 *
 */
public class TSplitMenuButton implements Toggle {

	private SplitMenuButton splitMenuButton;
	private MenuItem selectedChoice;
	private ToggleButton toggleButton = new ToggleButton();

	public TSplitMenuButton(SplitMenuButton splitMenuButton, MenuItem startChoice, ToggleGroup tg) {
		this.splitMenuButton = splitMenuButton;
		this.splitMenuButton.getStylesheets().add(ResourceLocator.getResourcePath(Resource.TSPLITMENUBUTTON_CSS).toExternalForm());
		this.selectedChoice = startChoice;
		toggleButton.setToggleGroup(tg);
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

	public final void setSelected(boolean value) {
		toggleButton.selectedProperty().set(value);
		if (toggleButton.selectedProperty().get()) {
			splitMenuButton.getStyleClass().add("tsplit-menu-button");
		}
		else {
			splitMenuButton.getStyleClass().remove("tsplit-menu-button");
		}
	}

	public final boolean isSelected() {
		return toggleButton.selectedProperty() == null ? false : toggleButton.selectedProperty().get();
	}

	@Override
	public ObservableMap<Object, Object> getProperties() {
		return toggleButton.getProperties();
	}

	@Override
	public ToggleGroup getToggleGroup() {
		return toggleButton.getToggleGroup();
	}

	@Override
	public Object getUserData() {
		return toggleButton.getUserData();
	}

	@Override
	public BooleanProperty selectedProperty() {
		return toggleButton.selectedProperty();
	}

	@Override
	public void setToggleGroup(ToggleGroup toggleGroup) {
		toggleButton.setToggleGroup(toggleGroup);
	}

	@Override
	public void setUserData(Object value) {
		toggleButton.setUserData(value);
	}

	@Override
	public ObjectProperty<ToggleGroup> toggleGroupProperty() {
		return toggleButton.toggleGroupProperty();
	}

}
