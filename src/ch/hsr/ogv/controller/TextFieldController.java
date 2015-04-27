package ch.hsr.ogv.controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
public class TextFieldController {

	private final static Logger logger = LoggerFactory.getLogger(TextFieldController.class);

	public void enableTopTextInput(ModelBox modelBox, PaneBox paneBox) {
		TextField topTextField = paneBox.getTopTextField();

		// TODO NullPointerException at undo

		topTextField.focusedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> focusProperty, Boolean oldHasFocus, Boolean newHasFocus) {
				if (!newHasFocus) {
					paneBox.allowTopTextInput(false);
					modelBox.setName(topTextField.getText());
				}
			}
		});

		topTextField.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observableValue, String oldValue, String newValue) {
				
				if (modelBox instanceof ModelClass) {
					ModelClass modelClass = (ModelClass) modelBox;
					
					double newWidth = paneBox.calcMinWidth();
					paneBox.setMinWidth(newWidth);
					if (newWidth > paneBox.getWidth()) {
						modelClass.setWidth(paneBox.getMinWidth());
					}
					
					// TODO remove setting it here to model and adapt object name properly
					modelBox.setName(topTextField.getText());
					for(ModelObject modelObject : modelClass.getModelObjects()) {
						modelObject.setName(modelObject.getName());
					}
				}
			}
		});

		topTextField.addEventHandler(KeyEvent.KEY_PRESSED, (KeyEvent ke) -> {
			if (ke.getCode() == KeyCode.ENTER) {
				// TODO validate input
				paneBox.get().requestFocus();
			} else if (ke.getCode() == KeyCode.ESCAPE) {
				// TODO validate input, reset old name
				paneBox.get().requestFocus();
			}
		});

	}
	
	public void enableCenterTextInput(ModelBox modelBox, PaneBox paneBox) {
		for (TextField centerTextField : paneBox.getCenterTextFields()) {
			centerTextField.focusedProperty().addListener(new ChangeListener<Boolean>() {
				@Override
				public void changed(ObservableValue<? extends Boolean> focusProperty, Boolean oldHasFocus, Boolean newHasFocus) {
					if (!newHasFocus) { // loosing focus
						int rowIndex = paneBox.getCenterTextFields().indexOf(centerTextField);
						if (rowIndex >= 0) {
							Label centerLabel = paneBox.getCenterLabels().get(rowIndex);
							paneBox.allowCenterFieldTextInput(centerLabel, false);
						}
						
						try {
							if (modelBox instanceof ModelClass) {
								ModelClass modelClass = (ModelClass) modelBox;
								modelClass.changeAttributeName(rowIndex, centerTextField.getText());
							} else if (modelBox instanceof ModelObject) {
								ModelObject modelObject = (ModelObject) modelBox;
								Attribute attribute = modelObject.getModelClass().getAttributes().get(rowIndex);
								modelObject.changeAttributeValue(attribute, centerTextField.getText());
							}
						} catch (IndexOutOfBoundsException ioobe) {
							logger.debug("Changing attribute failed. IndexOutOfBoundsException: " + ioobe.getMessage());
						}
					}
				}
			});

			centerTextField.textProperty().addListener(new ChangeListener<String>() {
				@Override
				public void changed(ObservableValue<? extends String> observableValue, String oldValue, String newValue) {
					try {
						int rowIndex = paneBox.getCenterTextFields().indexOf(centerTextField);
						if (modelBox instanceof ModelClass) {
							ModelClass modelClass = (ModelClass) modelBox;
							
							double newWidth = paneBox.calcMinWidth();
							paneBox.setMinWidth(newWidth);
							if (newWidth > paneBox.getWidth()) {
								modelClass.setWidth(paneBox.getMinWidth());
							}
							
							Attribute attribute = modelClass.getAttributes().get(rowIndex);
							for(ModelObject modelObject : modelClass.getModelObjects()) {
								modelObject.changeAttributeName(attribute, centerTextField.getText());
							}
						}
					} catch (IndexOutOfBoundsException ioobe) {
						logger.debug("Changing attribute value failed. IndexOutOfBoundsException: " + ioobe.getMessage());
					}
				}
			});

			centerTextField.addEventHandler(KeyEvent.KEY_PRESSED, (KeyEvent ke) -> {
				if (ke.getCode() == KeyCode.ENTER) {
					// TODO validate input
					paneBox.get().requestFocus();
				} else if (ke.getCode() == KeyCode.ESCAPE) {
					// TODO validate input, reset old name
					paneBox.get().requestFocus();
				}
			});
		}
	}

}
