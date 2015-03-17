package ch.hsr.ogv.controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import ch.hsr.ogv.view.PaneBox;

/**
 * 
 * @author Simon Gwerder
 *
 */
public class TextInputController {
	
	private final int MAX_CHAR_COUNT = 50;

	public void enableTextInput(PaneBox paneBox) {
		TextField topTextField = paneBox.getTop();
		
		topTextField.setOnMouseClicked((MouseEvent me) -> {
			if(MouseButton.PRIMARY.equals(me.getButton()) && me.getClickCount() >= 2) {
	        }
		});
		
		topTextField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String oldValue, String newValue) {
            	//TODO validate input
            	if(newValue.length() >= MAX_CHAR_COUNT) {
            		int overflow = newValue.length() - MAX_CHAR_COUNT;
            		newValue = newValue.substring(0, newValue.length() - overflow);
            		newValue = newValue + "...";
            	}
           		paneBox.adaptWidthByText(topTextField.getFont(), newValue);

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
