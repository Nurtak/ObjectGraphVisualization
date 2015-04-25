package ch.hsr.ogv.view;

import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

public class MessageBar {
	
	private static int CLEAR_TIME_MILLIS = 5000;
	
	private TextField messageBar = new TextField();
	
	public TextField get() {
		return messageBar;
	}

	public MessageBar() {
		this.messageBar.setEditable(false);
		this.messageBar.setFocusTraversable(false);
		this.messageBar.setDisable(true);
		this.messageBar.setMinHeight(28);
		HBox.setHgrow(this.messageBar, Priority.ALWAYS);
		HBox.setMargin(this.messageBar, new Insets(5, 5, 5, 5));
	}
	
	private Task<Void> getClearTask() {
		Task<Void> clearTextFieldTask = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				Thread.sleep(CLEAR_TIME_MILLIS);
				messageBar.clear();
				return null;
			}
		};
		return clearTextFieldTask;
	}
	
	public void setText(String text, MessageLevel level) {
		this.messageBar.setText(text);
		switch(level) {
		case INFO:
			this.messageBar.setStyle("-fx-text-inner-color: #000000;");
			break;
		case WARN:
			this.messageBar.setStyle("-fx-text-inner-color: #8F551D;");
			break;
		case ERROR:
			this.messageBar.setStyle("-fx-text-inner-color: #CC3300;");
			break;
		default:
			this.messageBar.setStyle("-fx-text-inner-color: #000000;");
			break;
		
		}
		new Thread(getClearTask()).start();
	}
	
	public  enum MessageLevel {
		INFO, WARN, ERROR;
	}

}
