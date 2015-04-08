package ch.hsr.ogv.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
public class TextFieldController {
	
	private final static Logger logger = LoggerFactory.getLogger(TextFieldController.class);
	
	public void enableTextInput(ModelBox modelBox, PaneBox paneBox) {
		TextField topTextField = paneBox.getTopTextField();
		
		//TODO NullPointerException at undo
		
		topTextField.focusedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> focusProperty, Boolean oldHasFocus, Boolean newHasFocus) {
				if (!newHasFocus) {
					paneBox.allowTopTextInput(false);
				}
			}
		});
		
		topTextField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String oldValue, String newValue) {
            	modelBox.setName(newValue);
            }
        });
		
		topTextField.addEventHandler(KeyEvent.KEY_PRESSED, (KeyEvent ke) -> {
			if(ke.getCode() == KeyCode.ENTER) {
				//TODO validate input
				paneBox.get().requestFocus();
			}
			else if(ke.getCode() == KeyCode.ESCAPE) {
				//TODO validate input, reset old name
				paneBox.get().requestFocus();
			}
		});
		
		for(TextField centerTextField : paneBox.getCenterTextFields()) {
			centerTextField.focusedProperty().addListener(new ChangeListener<Boolean>() {
				@Override
				public void changed(ObservableValue<? extends Boolean> focusProperty, Boolean oldHasFocus, Boolean newHasFocus) {
					if (!newHasFocus) {
						int rowIndex = paneBox.getCenterTextFields().indexOf(centerTextField);
						if(rowIndex >= 0) {
							Label centerLabel = paneBox.getCenterLabels().get(rowIndex);
							paneBox.allowCenterFieldTextInput(centerLabel, false);
						}
					}
				}
			});
			
			centerTextField.textProperty().addListener(new ChangeListener<String>() {
	            @Override
	            public void changed(ObservableValue<? extends String> observableValue, String oldValue, String newValue) {
	            	int rowIndex = paneBox.getCenterTextFields().indexOf(centerTextField);
	            	try {
		            	if(modelBox instanceof ModelClass) {
		            		ModelClass modelClass = (ModelClass) modelBox;
		            		modelClass.changeAttributeName(rowIndex, newValue);
		            	}
		            	else if(modelBox instanceof ModelObject) {
		            		ModelObject modelObject = (ModelObject) modelBox;
		            		Attribute attribute = modelObject.getModelClass().getAttributes().get(rowIndex);
		            		modelObject.changeAttributeValue(attribute, newValue);
		            	}
	            	}
	        		catch(IndexOutOfBoundsException ioobe) {
	        			logger.debug("Changing attribute failed. IndexOutOfBoundsException: " + ioobe.getMessage());
	        		}
	            }
	        });
			
			centerTextField.addEventHandler(KeyEvent.KEY_PRESSED, (KeyEvent ke) -> {
				if(ke.getCode() == KeyCode.ENTER) {
					//TODO validate input
					paneBox.get().requestFocus();
				}
				else if(ke.getCode() == KeyCode.ESCAPE) {
					//TODO validate input, reset old name
					paneBox.get().requestFocus();
				}
			});
		}
		
	}
	
}
