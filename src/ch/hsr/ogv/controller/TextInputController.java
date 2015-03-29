package ch.hsr.ogv.controller;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import ch.hsr.ogv.model.ModelClass;
import ch.hsr.ogv.view.PaneBox;

/**
 * 
 * @author Simon Gwerder
 *
 */
public class TextInputController {
	
	//private final int MAX_CHAR_COUNT = 32;

	public void enableTextInput(ModelClass theClass, PaneBox paneBox) {
		Label topLabel = paneBox.getTopLabel();
		TextField topTextField = paneBox.getTopTextField();
		
		topLabel.setOnMouseClicked((MouseEvent me) -> {
			if(MouseButton.PRIMARY.equals(me.getButton()) && me.getClickCount() >= 1) {
				paneBox.allowTopTextInput(true);
				Platform.runLater(() -> {
					paneBox.getTopTextField().requestFocus();
					paneBox.getTopTextField().selectAll();
					paneBox.getTopTextField().applyCss();
				});
	        }
		});
		
		topTextField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String oldValue, String newValue) {
            	//TODO validate input
//            	if(newValue.length() >= MAX_CHAR_COUNT) {
//            		int overflow = newValue.length() - MAX_CHAR_COUNT;
//            		newValue = newValue.substring(0, newValue.length() - overflow);
//            		newValue = newValue + "...";
//            	}
            	theClass.setName(newValue);
            }
        });
		
		topTextField.setOnKeyReleased((KeyEvent ke) -> {
			if(ke.getCode() == KeyCode.ENTER) {
				//TODO validate input
				paneBox.get().requestFocus();
			}
		});
	}
	
}
