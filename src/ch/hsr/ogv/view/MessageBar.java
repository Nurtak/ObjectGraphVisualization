package ch.hsr.ogv.view;

import java.util.concurrent.atomic.AtomicInteger;

import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

public class MessageBar {

	private static int CLEAR_TIME_MILLIS = 5000;
	private static TextField messageBar;
	
	private static AtomicInteger threadCount = new AtomicInteger(0);
	
	public static TextField getTextField() {
		synchronized(messageBar) {
			return messageBar;
		}
	}

	static {
		messageBar = new TextField();
		messageBar.setEditable(false);
		messageBar.setFocusTraversable(false);
		messageBar.setDisable(true);
		messageBar.setMinHeight(28);
		HBox.setHgrow(messageBar, Priority.ALWAYS);
		HBox.setMargin(messageBar, new Insets(5, 5, 5, 5));
	}
	
	private static Task<Void> getClearTask() {
		Task<Void> clearTextFieldTask = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				Thread.sleep(CLEAR_TIME_MILLIS);
				synchronized(messageBar) {
					messageBar.clear();
				}
				return null;
			}
		};
		return clearTextFieldTask;
	}
	
	public static void setText(String text, MessageLevel level) {
		synchronized(messageBar) {
			messageBar.setText(text);
			switch(level) {
			case INFO:
				messageBar.setStyle("-fx-font-weight: bold; -fx-text-inner-color: #000000;");
				break;
			case WARN:
				messageBar.setStyle("-fx-font-weight: bold; -fx-text-inner-color: #717100;");
				break;
			case ERROR:
				messageBar.setStyle("-fx-font-weight: bold; -fx-text-inner-color: #CC2900;");
				break;
			default:
				messageBar.setStyle("-fx-font-weight: bold; -fx-text-inner-color: #000000;");
				break;
			}
			if(threadCount.get() <= 0) {
				new Thread(getClearTask()).start();
			}
		}
	}
	
	public  enum MessageLevel {
		INFO, WARN, ERROR;
	}

}
