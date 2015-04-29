package ch.hsr.ogv.view;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

public class MessageBar {

	private static int CLEAR_TIME_SECOND = 5;
	private static TextField messageBar;
	
	private static AtomicInteger countDown = new AtomicInteger(0);
	private static AtomicBoolean taskRunning = new AtomicBoolean(false);
	
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
			countDown.set(CLEAR_TIME_SECOND);
			if(!taskRunning.get()) {
				taskRunning.set(true);
				new Thread(new MessageTask()).start();
			}
		}
	}
	
	public static class MessageTask extends Task<Void> {
		
		@Override
		protected Void call() throws Exception {
			while(countDown.get() > 0) {
				Thread.sleep(1000);
				countDown.decrementAndGet();
			}
			messageBar.clear();
			taskRunning.set(false);
			return null;
		}
		
	}
	
	public  enum MessageLevel {
		INFO, WARN, ERROR;
	}

}
