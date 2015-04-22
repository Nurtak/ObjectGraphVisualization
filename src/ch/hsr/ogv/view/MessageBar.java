package ch.hsr.ogv.view;

import javafx.scene.control.TextField;

public class MessageBar {
	
	private TextField messageBar = new TextField();
	
	public TextField getMessageBar() {
		return messageBar;
	}

	public MessageBar() {
		this.messageBar.setEditable(false);
		this.messageBar.setDisable(true);
	}

}
