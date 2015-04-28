package ch.hsr.ogv.controller;

import java.util.ArrayList;

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
import ch.hsr.ogv.model.Relation;
import ch.hsr.ogv.view.Arrow;
import ch.hsr.ogv.view.ArrowLabel;
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
	
	public void enableArrowLabelTextInput(Arrow arrow, Relation relation) {
		
		arrow.getLabelStartLeft().getArrowTextField().focusedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> focusProperty, Boolean oldHasFocus, Boolean newHasFocus) {
				if (!newHasFocus) {
					relation.setStartRoleName(arrow.getLabelStartLeft().getText());
					arrow.drawArrow();
					arrow.getLabelStartLeft().allowTextInput(false);
				}
			}
		});
		
		arrow.getLabelStartRight().getArrowTextField().focusedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> focusProperty, Boolean oldHasFocus, Boolean newHasFocus) {
				if (!newHasFocus) {
					relation.setStartMultiplicity(arrow.getLabelStartRight().getText());
					arrow.drawArrow();
					arrow.getLabelStartRight().allowTextInput(false);
				}
			}
		});

		arrow.getLabelEndLeft().getArrowTextField().focusedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> focusProperty, Boolean oldHasFocus, Boolean newHasFocus) {
				if (!newHasFocus) {
					relation.setEndRoleName(arrow.getLabelEndLeft().getText());
					arrow.drawArrow();
					arrow.getLabelEndLeft().allowTextInput(false);
				}
			}
		});

		arrow.getLabelEndRight().getArrowTextField().focusedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> focusProperty, Boolean oldHasFocus, Boolean newHasFocus) {
				if (!newHasFocus) {
					relation.setEndMultiplicity(arrow.getLabelEndRight().getText());
					arrow.drawArrow();
					arrow.getLabelEndRight().allowTextInput(false);
				}
			}
		});


		ArrayList<ArrowLabel> tempArrowLabels = new ArrayList<ArrowLabel>();
		tempArrowLabels.add(arrow.getLabelStartLeft());
		tempArrowLabels.add(arrow.getLabelStartRight());
		tempArrowLabels.add(arrow.getLabelEndLeft());
		tempArrowLabels.add(arrow.getLabelEndRight());
		
		for(ArrowLabel arrowLabel : tempArrowLabels) {
			TextField arrowTextField = arrowLabel.getArrowTextField();
			arrowTextField.textProperty().addListener(new ChangeListener<String>() {
				@Override
				public void changed(ObservableValue<? extends String> observableValue, String oldValue, String newValue) {
					arrowLabel.setWidth(arrowLabel.calcMinWidth());
					arrow.drawArrow();
				}
			});

			arrowTextField.addEventHandler(KeyEvent.KEY_PRESSED, (KeyEvent ke) -> {
				if (ke.getCode() == KeyCode.ENTER) {
					// TODO validate input
					arrow.requestFocus();
				} else if (ke.getCode() == KeyCode.ESCAPE) {
					// TODO validate input, reset old name
					arrow.requestFocus();
				}
			});
		}
	}

}
