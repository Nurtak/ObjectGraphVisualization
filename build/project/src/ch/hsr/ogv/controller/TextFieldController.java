package ch.hsr.ogv.controller;

import java.util.ArrayList;
import java.util.regex.Pattern;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
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
import ch.hsr.ogv.util.MultiplicityParser;
import ch.hsr.ogv.view.Arrow;
import ch.hsr.ogv.view.ArrowLabel;
import ch.hsr.ogv.view.MessageBar;
import ch.hsr.ogv.view.MessageBar.MessageLevel;
import ch.hsr.ogv.view.PaneBox;

/**
 * 
 * @author Simon Gwerder
 *
 */
public class TextFieldController {

	private final static Logger logger = LoggerFactory.getLogger(TextFieldController.class);

	private final Pattern SPECIAL_REGEX_CHARS = Pattern.compile("[{}()\\[\\].+*?^$\\\\|]");
	
	String escape(String str) {
	    return SPECIAL_REGEX_CHARS.matcher(str).replaceAll("\\\\$0");
	}
	
	public void enableTopTextInput(ModelBox modelBox, PaneBox paneBox, ModelViewConnector mvConnector) {
		TextField topTextField = paneBox.getTopTextField();
		
		topTextField.addEventFilter(KeyEvent.KEY_TYPED, nonAlphaUnderscoreFilter());

		topTextField.focusedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> focusProperty, Boolean oldHasFocus, Boolean newHasFocus) {
				if (!newHasFocus) {
					paneBox.allowTopTextInput(false);
					if(modelBox instanceof ModelClass) {
						if(topTextField.getText() != null && !topTextField.getText().isEmpty() && !topTextField.getText().toLowerCase().equals(modelBox.getName().toLowerCase())) {
							String firstLetter = topTextField.getText().substring(0, 1);
							String classNameToCompare = topTextField.getText().replaceFirst(escape(firstLetter), firstLetter.toUpperCase());
							if(mvConnector.getModelManager().isClassNameTaken(classNameToCompare)) {
								MessageBar.setText("Could not rename class \"" + modelBox.getName() + "\", a class \"" + classNameToCompare + "\" already exists.", MessageLevel.ALERT);
								modelBox.setName(modelBox.getName());
							}
							else {
								modelBox.setName(checkClassName(modelBox.getName(), topTextField.getText()));
							}
						}
						else {
							modelBox.setName(checkClassName(modelBox.getName(), topTextField.getText()));
						}
					}
					else if(modelBox instanceof ModelObject) {
						ModelObject modelObject = (ModelObject) modelBox;
						if(topTextField.getText() != null && !topTextField.getText().isEmpty() && !topTextField.getText().equals(modelBox.getName())
								&& mvConnector.getModelManager().isObjectNameTaken(modelObject.getModelClass(), topTextField.getText())) {
							MessageBar.setText("Could not rename object \"" + modelBox.getName() + "\", an object \"" + topTextField.getText() + "\" already exists for this class.", MessageLevel.ALERT);
							modelBox.setName(modelBox.getName());
						}
						else {
							modelBox.setName(checkObjectName(modelBox.getName(), topTextField.getText()));
						}
					}
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
					
					for(ModelObject modelObject : modelClass.getModelObjects()) {
						modelObject.setName(modelObject.getName()); // this will trigger reset!
						PaneBox paneBoxObject = mvConnector.getPaneBox(modelObject);
						if(paneBoxObject != null) {
							paneBoxObject.setTopText(modelObject.getName() + " : " + newValue);
						}
					}
					
					for(ModelObject inheritingObject : modelClass.getInheritingObjects()) {
						inheritingObject.setName(inheritingObject.getName()); // this will trigger reset!
						PaneBox paneBoxObject = mvConnector.getPaneBox(inheritingObject);
						if(paneBoxObject != null) {
							paneBoxObject.setTopText(inheritingObject.getName() + " : " + newValue);
						}
					}
				}
			}
		});

		topTextField.addEventHandler(KeyEvent.KEY_PRESSED, (KeyEvent ke) -> {
			if (ke.getCode() == KeyCode.ENTER) { // apply
				paneBox.get().requestFocus();
			} else if (ke.getCode() == KeyCode.ESCAPE) { // abort
				paneBox.getTopTextField().setText(paneBox.getTopLabel().getText());
				paneBox.get().requestFocus();
			}
		});

	}
	
	public void enableCenterTextInput(ModelBox modelBox, PaneBox paneBox, ModelViewConnector mvConnector) {
		for (TextField centerTextField : paneBox.getCenterTextFields()) {
			
			if(modelBox instanceof ModelClass) {
				centerTextField.addEventFilter(KeyEvent.KEY_TYPED, nonAlphaUnderscoreFilter());
			}
			
			centerTextField.focusedProperty().addListener(new ChangeListener<Boolean>() {
				@Override
				public void changed(ObservableValue<? extends Boolean> focusProperty, Boolean oldHasFocus, Boolean newHasFocus) {
					if (!newHasFocus) { // loosing focus
						int rowIndex = paneBox.getCenterTextFields().indexOf(centerTextField);
						if (rowIndex >= 0) {
							Label centerLabel = paneBox.getCenterLabels().get(rowIndex);
							paneBox.allowCenterFieldTextInput(centerLabel, false);
							
							try {
								if (modelBox instanceof ModelClass) {
									ModelClass modelClass = (ModelClass) modelBox;
									if(centerTextField.getText() != null && !centerTextField.getText().equals(centerLabel.getText())
											&& mvConnector.getModelManager().isAttributeNameTaken(modelClass, centerTextField.getText())) {
										MessageBar.setText("Could not rename attribute \"" + centerLabel.getText() + "\", an attribute \"" + centerTextField.getText() + "\" already exists for this class.", MessageLevel.ALERT);
										modelClass.changeAttributeName(rowIndex, centerLabel.getText());
									}
									else if(centerTextField.getText() != null && !centerTextField.getText().equals(centerLabel.getText())
											&& mvConnector.getModelManager().isRoleNameTaken(modelClass, centerTextField.getText())) {
										MessageBar.setText("Could not rename attribute \"" + centerLabel.getText() + "\", there's a role \"" + centerTextField.getText() + "\" in relation with this class.", MessageLevel.ALERT);
										modelClass.changeAttributeName(rowIndex, centerLabel.getText());
									}
									else {
										modelClass.changeAttributeName(rowIndex, checkAttributeRoleName(centerLabel.getText(), centerTextField.getText()));
									}
								} else if (modelBox instanceof ModelObject) {
									ModelObject modelObject = (ModelObject) modelBox;
									Attribute attribute = modelObject.getModelClass().getAttributes().get(rowIndex);
									modelObject.changeAttributeValue(attribute, centerTextField.getText()); // attribute values can be anything, so no additional check needed
								}
							} catch (IndexOutOfBoundsException ioobe) {
								logger.debug("Changing attribute failed. IndexOutOfBoundsException: " + ioobe.getMessage());
							}
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
							
							for(ModelObject modelObject : modelClass.getModelObjects()) {
								PaneBox paneBoxObject = mvConnector.getPaneBox(modelObject);
								if(paneBoxObject != null) {
									paneBoxObject.setCenterText(rowIndex, newValue, centerTextField.getText());
								}
							}
							
							for(ModelObject inheritingObject : modelClass.getInheritingObjects()) {
								PaneBox paneBoxObject = mvConnector.getPaneBox(inheritingObject);
								if(paneBoxObject != null) {
									paneBoxObject.setCenterText(rowIndex, newValue, centerTextField.getText());
								}
							}
						}
					} catch (IndexOutOfBoundsException ioobe) {
						logger.debug("Changing attribute value failed. IndexOutOfBoundsException: " + ioobe.getMessage());
					}
				}
			});

			centerTextField.addEventHandler(KeyEvent.KEY_PRESSED, (KeyEvent ke) -> {
				if (ke.getCode() == KeyCode.ENTER) {
					paneBox.get().requestFocus();
				} else if (ke.getCode() == KeyCode.ESCAPE) {
					int rowIndex = paneBox.getCenterTextFields().indexOf(centerTextField);
					if (rowIndex >= 0) {
						Label centerLabel = paneBox.getCenterLabels().get(rowIndex);
						if (modelBox instanceof ModelClass) {
							ModelClass modelClass = (ModelClass) modelBox;
							modelClass.changeAttributeName(rowIndex, centerLabel.getText());
						} else if (modelBox instanceof ModelObject) {
							ModelObject modelObject = (ModelObject) modelBox;
							Attribute attribute = modelObject.getModelClass().getAttributes().get(rowIndex);
							modelObject.changeAttributeValue(attribute, modelObject.getAttributeValues().get(attribute));
						}
					}
					paneBox.get().requestFocus();
				}
			});
		}
	}
	
	public void enableArrowLabelTextInput(Arrow arrow, Relation relation, ModelViewConnector mvConnector) {
		
		// start role
		arrow.getLabelStartLeft().addEventFilter(KeyEvent.KEY_TYPED, nonAlphaUnderscoreFilter());
		arrow.getLabelStartLeft().getArrowTextField().focusedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> focusProperty, Boolean oldHasFocus, Boolean newHasFocus) {
				if (!newHasFocus) {
					ModelBox otherEndBox = relation.getEnd().getAppendant();
					if(otherEndBox instanceof ModelClass && mvConnector.getModelManager().isAttributeNameTaken((ModelClass) otherEndBox, arrow.getLabelStartLeft().getTextFieldText())) {
						MessageBar.setText("Could not set role \"" + arrow.getLabelStartLeft().getLabelText() + "\", there's an attribute in class \"" + otherEndBox.getName() + "\" with this name.", MessageLevel.ALERT);
						relation.setStartRoleName(arrow.getLabelStartLeft().getLabelText());
					}
					else {
						String role = checkRoleName(arrow.getLabelStartLeft().getLabelText(), arrow.getLabelStartLeft().getTextFieldText());
						relation.setStartRoleName(role);
					}
					arrow.drawArrow();
					arrow.getLabelStartLeft().allowTextInput(false);
				}
			}
		});
		
		// start multiplicity
		arrow.getLabelStartRight().addEventFilter(KeyEvent.KEY_TYPED, nonMultiplicityFilter());
		arrow.getLabelStartRight().getArrowTextField().focusedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> focusProperty, Boolean oldHasFocus, Boolean newHasFocus) {
				if (!newHasFocus) {
					String multiplicity = checkMultiplicity(arrow.getLabelStartRight().getLabelText(), arrow.getLabelStartRight().getTextFieldText());
					relation.setStartMultiplicity(multiplicity);
					arrow.drawArrow();
					arrow.getLabelStartRight().allowTextInput(false);
				}
			}
		});

		// end role
		arrow.getLabelEndLeft().addEventFilter(KeyEvent.KEY_TYPED, nonAlphaUnderscoreFilter());
		arrow.getLabelEndLeft().getArrowTextField().focusedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> focusProperty, Boolean oldHasFocus, Boolean newHasFocus) {
				if (!newHasFocus) {
					ModelBox otherEndBox = relation.getStart().getAppendant();
					if(otherEndBox instanceof ModelClass && mvConnector.getModelManager().isAttributeNameTaken((ModelClass) otherEndBox, arrow.getLabelEndLeft().getTextFieldText())) {
						MessageBar.setText("Could not set role \"" + arrow.getLabelEndLeft().getLabelText() + "\", there's an attribute in class \"" + otherEndBox.getName() + "\" with this name.", MessageLevel.ALERT);
						relation.setEndRoleName(arrow.getLabelEndLeft().getLabelText());
					}
					else {
						String role = checkRoleName(arrow.getLabelEndLeft().getLabelText(), arrow.getLabelEndLeft().getTextFieldText());
						relation.setEndRoleName(role);
					}
					arrow.drawArrow();
					arrow.getLabelEndLeft().allowTextInput(false);
				}
			}
		});

		// end multiplicity
		arrow.getLabelEndRight().addEventFilter(KeyEvent.KEY_TYPED, nonMultiplicityFilter());
		arrow.getLabelEndRight().getArrowTextField().focusedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> focusProperty, Boolean oldHasFocus, Boolean newHasFocus) {
				if (!newHasFocus) {
					String multiplicity = checkMultiplicity(arrow.getLabelEndRight().getLabelText(), arrow.getLabelEndRight().getTextFieldText());
					relation.setEndMultiplicity(multiplicity);
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
					arrow.requestFocus();
				} else if (ke.getCode() == KeyCode.ESCAPE) {
					arrowLabel.setText(arrowLabel.getLabelText());
					arrow.requestFocus();
				}
			});
		}
	}
	
	public static EventHandler<KeyEvent> nonAlphaUnderscoreFilter() {
		return new EventHandler<KeyEvent>() {
			public void handle(KeyEvent ke) {
				if(ke.getCharacter().matches("[\\p{Cntrl}]")) { // control characters are ok
					return;
				}
				if(ke.getCharacter().equals("_")) {
					return;
				}
				if(ke.isShiftDown() && ke.getCharacter().matches("[A-Z_]")) {
					return;
				}
				if(ke.getCharacter().matches("[a-z0-9]")) {
					return;
				}
				MessageBar.setText("Using characters other than A-Z, a-z, 0-9 and underscore is not recommended.", MessageLevel.WARN);
				//ke.consume();
			}
		};
	}
	
	public static EventHandler<KeyEvent> nonMultiplicityFilter() {
		return new EventHandler<KeyEvent>() {
			public void handle(KeyEvent ke) {
				if(ke.getCharacter().equals("*") || ke.getCharacter().equals(".") || ke.getCharacter().equals(",")) {
					return;
				}
				if(ke.getCharacter().matches("[\\p{Cntrl}]")) { // control characters are ok
					return;
				}
				if(ke.getCharacter().matches("[0-9]")) {
					return;
				}
				MessageBar.setText("Characters other than 0-9, '*', ',' and '.' are not allowed.", MessageLevel.WARN);
				ke.consume();
			}
		};
	}
	
	private String checkClassName(String oldName, String newName) {
		if(newName == null || newName.isEmpty()) {
			MessageBar.setText("Could not rename class \"" + oldName + "\", classname can not be empty.", MessageLevel.ALERT);
			return oldName;
		}
		String firstLetter = newName.substring(0, 1);
		if(firstLetter.equals("_")) { // beginning with underscore
			MessageBar.setText("Beginning a classname with an underscore is not recommended.", MessageLevel.WARN);
		}
		if(firstLetter.matches("[0-9]")) { // beginning with digit
			MessageBar.setText("Beginning a classname with a digit is not recommended.", MessageLevel.WARN);
		}
		if(!firstLetter.toUpperCase().equals(firstLetter)) { // first letter uppercase
			MessageBar.setText("First letter of new class name \"" + newName + "\", was set to uppercase.", MessageLevel.WARN);
			newName = newName.replaceFirst(escape(firstLetter), firstLetter.toUpperCase());
		}
		return newName;
	}
	
	private String checkObjectName(String oldName, String newName) {
		if(newName == null || newName.isEmpty()) {
			MessageBar.setText("Could not rename object \"" + oldName + "\", objectname can not be empty.", MessageLevel.ALERT);
			return oldName;
		}
		return newName;
	}
	
	private String checkAttributeRoleName(String oldName, String newName) {
		if(newName == null || newName.isEmpty()) {
			MessageBar.setText("Could not rename attribute \"" + oldName + "\", attribute name can not be empty.", MessageLevel.ALERT);
			return oldName;
		}
		String firstLetter = newName.substring(0, 1);
		if(firstLetter.matches("[0-9]")) { // beginning with digit
			MessageBar.setText("Beginning an attribute with a digit is not recommended.", MessageLevel.WARN);
		}
		return newName;
	}
	
	private String checkRoleName(String oldName, String newName) {
		if((oldName != null && !oldName.isEmpty()) && (newName == null || newName.isEmpty())) {
			MessageBar.setText("Could not set role \"" + oldName + "\", role can not be empty.", MessageLevel.ALERT);
			return oldName;
		}
		if(newName == null || newName.isEmpty()) {
			return oldName;
		}
		String firstLetter = newName.substring(0, 1);
		if(firstLetter.matches("[0-9]")) { // beginning with digit
			MessageBar.setText("Beginning a role with a digit is not recommended.", MessageLevel.WARN);
		}
		return newName;
	}
	
	private String checkMultiplicity(String oldMultiplicity, String newMultiplicity) {
		if((oldMultiplicity != null && !oldMultiplicity.isEmpty()) && (newMultiplicity == null || newMultiplicity.isEmpty())) {
			MessageBar.setText("Could not set multiplicity \"" + oldMultiplicity + "\", multiplicity can not be empty.", MessageLevel.ALERT);
			return oldMultiplicity;
		}
		if(newMultiplicity == null || newMultiplicity.isEmpty()) {
			return oldMultiplicity;
		}
		newMultiplicity = MultiplicityParser.getParsedMultiplicity(newMultiplicity);
		if(newMultiplicity == null) {
			MessageBar.setText("Could not set multiplicity replacing \"" + oldMultiplicity + "\", multiplicity must be of in the N-Form, where N is a digit > 0 or '*' or in the N..M-Form, where N is a digit >= 0, M is a digit >= 1 or '*' and M > N. (Comma separation possible)", MessageLevel.ALERT);
			return oldMultiplicity;
		}
		return newMultiplicity;
	}

}
