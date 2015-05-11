package ch.hsr.ogv.controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import ch.hsr.ogv.model.Attribute;
import ch.hsr.ogv.model.ModelBox;
import ch.hsr.ogv.model.ModelClass;
import ch.hsr.ogv.model.ModelObject;
import ch.hsr.ogv.view.PaneBox;

/**
 * 
 * @author Simon Gwerder
 *
 */
public class SpeedCreationController {

	private ModelViewConnector mvConnector;
	private TextField textField;
	private PaneBox paneBox;

	private EventHandler<KeyEvent> classKeyEvent;
	private ChangeListener<Boolean> classFocusChange;
	private EventHandler<KeyEvent> objectKeyEvent;
	private ChangeListener<Boolean> objectFocusChange;

	private volatile boolean disableSpeedCreation = false;

	public SpeedCreationController(PaneBox paneBox, ModelViewConnector mvConnector) {
		this(paneBox.getTopTextField(), paneBox, mvConnector); // starting with topTextField
	}

	private SpeedCreationController(TextField textField, PaneBox paneBox, ModelViewConnector mvConnector) {
		this.textField = textField;
		this.paneBox = paneBox;
		this.mvConnector = mvConnector;
		ModelBox modelBox = this.mvConnector.getModelBox(this.paneBox);
		if (modelBox instanceof ModelClass) {
			enableClassSpeedCreation();
		}
		else if (modelBox instanceof ModelObject) {
			enableObjectSpeedCreation();
		}
	}

	private void enableClassSpeedCreation() {
		this.classKeyEvent = (KeyEvent ke) -> {
			if (ke.getCode() == KeyCode.ENTER) { // continue
				this.disableSpeedCreation = true;
				disableClassSpeedCreation();
				Attribute newAttribute = this.mvConnector.handleCreateNewAttribute(this.paneBox);
				for (TextField centerTextField : this.paneBox.getCenterTextFields()) {
					if (centerTextField.getText().equals(newAttribute.getName())) {
						new SpeedCreationController(centerTextField, this.paneBox, this.mvConnector);
					}
				}
			}
			else if (ke.getCode() == KeyCode.ESCAPE) { // finish
				this.disableSpeedCreation = true;
				disableClassSpeedCreation();
			}
		};

		this.classFocusChange = new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> focusProperty, Boolean oldHasFocus, Boolean newHasFocus) {
				if (!newHasFocus && disableSpeedCreation) {
					disableClassSpeedCreation();
				}
			}
		};
		this.textField.addEventHandler(KeyEvent.KEY_PRESSED, this.classKeyEvent);
		this.textField.focusedProperty().addListener(this.classFocusChange);
	}

	private void disableClassSpeedCreation() {
		this.textField.removeEventHandler(KeyEvent.KEY_PRESSED, this.classKeyEvent);
		this.textField.focusedProperty().removeListener(this.classFocusChange);
	}

	private void enableObjectSpeedCreation() {
		this.objectKeyEvent = (KeyEvent ke) -> {
			if (ke.getCode() == KeyCode.ENTER) { // continue
				this.disableSpeedCreation = true;
				disableObjectSpeedCreation();
				Label selectedLabel = this.paneBox.getSelectedLabel();
				if (selectedLabel != null && selectedLabel.equals(this.paneBox.getTopLabel()) && !this.paneBox.getCenterLabels().isEmpty()) {
					Label firstCenterLabel = this.paneBox.getCenterLabels().get(0);
					this.paneBox.allowCenterFieldTextInput(firstCenterLabel, true);
					new SpeedCreationController(this.paneBox.getCenterTextFields().get(0), paneBox, mvConnector);
				}
				else if (selectedLabel != null && this.paneBox.getCenterLabels().contains(selectedLabel)) {
					int rowIndex = this.paneBox.getCenterLabels().indexOf(selectedLabel);
					if (rowIndex + 1 > 0 && rowIndex + 1 < this.paneBox.getCenterLabels().size()) {
						Label nextCenterLabel = this.paneBox.getCenterLabels().get(rowIndex + 1);
						this.paneBox.allowCenterFieldTextInput(nextCenterLabel, true);
						new SpeedCreationController(this.paneBox.getCenterTextFields().get(rowIndex + 1), paneBox, mvConnector);
					}
				}
			}
			else if (ke.getCode() == KeyCode.ESCAPE) { // finish
				this.disableSpeedCreation = true;
				disableObjectSpeedCreation();
			}
		};

		this.objectFocusChange = new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> focusProperty, Boolean oldHasFocus, Boolean newHasFocus) {
				if (!newHasFocus && disableSpeedCreation) {
					disableObjectSpeedCreation();
				}
			}
		};
		this.textField.addEventHandler(KeyEvent.KEY_PRESSED, this.objectKeyEvent);
		this.textField.focusedProperty().addListener(this.objectFocusChange);
	}

	private void disableObjectSpeedCreation() {
		this.textField.removeEventHandler(KeyEvent.KEY_PRESSED, this.objectKeyEvent);
		this.textField.focusedProperty().removeListener(this.objectFocusChange);
	}

}
