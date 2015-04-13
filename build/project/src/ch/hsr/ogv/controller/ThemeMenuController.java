package ch.hsr.ogv.controller;

import com.aquafx_project.AquaFx;

import ch.hsr.ogv.util.ResourceLocator;
import ch.hsr.ogv.util.ResourceLocator.Resource;
import javafx.application.Application;
import javafx.scene.layout.BorderPane;

/**
 * 
 * @author Simon Gwerder
 *
 */
public class ThemeMenuController {

	public void handleSetTheme(BorderPane rootLayout, Style style) {
		setStyle(rootLayout, style);
	}

	private void setStyle(BorderPane rootLayout, Style style) {
		String lightTheme = ResourceLocator.getResourcePath(Resource.LIGHTHEME_CSS).toExternalForm();
		String darkTheme  = ResourceLocator.getResourcePath(Resource.DARKTHEME_CSS).toExternalForm();
		switch(style) {
		case CASPIANDARK:
			Application.setUserAgentStylesheet(Application.STYLESHEET_CASPIAN);
			setTheme(rootLayout, darkTheme);
			break;
		case MODENA:
			Application.setUserAgentStylesheet(Application.STYLESHEET_MODENA);
			setTheme(rootLayout, lightTheme);
			break;
		case AQUA:
			AquaFx.style();
			setTheme(rootLayout, lightTheme);
			break;
		default:
			Application.setUserAgentStylesheet(Application.STYLESHEET_MODENA);
			setTheme(rootLayout, lightTheme);
			break;
		}
	}
	
	private void setTheme(BorderPane rootLayout, String theme) {
		rootLayout.getStylesheets().clear();
		rootLayout.getStylesheets().add(theme);
		rootLayout.applyCss();
	}
	
	public enum Style {
		MODENA, CASPIANDARK, AQUA, LIGHTJMETRO
	}
	
}