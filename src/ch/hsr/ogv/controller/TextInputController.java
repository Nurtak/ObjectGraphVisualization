package ch.hsr.ogv.controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import ch.hsr.ogv.view.PaneBox3D;

/**
 * 
 * @author Simon Gwerder
 *
 */
public class TextInputController {

	public void enableTextInput(PaneBox3D paneBox3D) {
		TextField topTextField = paneBox3D.getTop();
		
		topTextField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String oldValue, String newValue) {
            	//TODO validate input
            	paneBox3D.adaptWidthByText(topTextField.getFont(), newValue);
            }
        });
		
		topTextField.setOnKeyReleased((KeyEvent ke) -> {
			if(ke.getCode() == KeyCode.ENTER) {
				//TODO validate input
				paneBox3D.getPaneBox().requestFocus();
			}
		});
	}
	
}
