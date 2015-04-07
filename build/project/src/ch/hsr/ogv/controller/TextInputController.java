package ch.hsr.ogv.controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import ch.hsr.ogv.model.ModelBox;
import ch.hsr.ogv.view.PaneBox;

/**
 * 
 * @author Simon Gwerder
 *
 */
public class TextInputController {
	
	//private final int MAX_CHAR_COUNT = 32;

	public void enableTextInput(ModelBox modelBox, PaneBox paneBox) {
		TextField topTextField = paneBox.getTopTextField();
		
		topTextField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String oldValue, String newValue) {
            	//TODO validate input
//            	if(newValue.length() >= MAX_CHAR_COUNT) {
//            		int overflow = newValue.length() - MAX_CHAR_COUNT;
//            		newValue = newValue.substring(0, newValue.length() - overflow);
//            		newValue = newValue + "...";
//            	}
            	modelBox.setName(newValue);
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
