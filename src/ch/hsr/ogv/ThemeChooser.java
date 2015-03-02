package ch.hsr.ogv;

import com.aquafx_project.AquaFx;
import javafx.application.Application;

public class ThemeChooser {
	
	private StageManager stageManager;
	
	public ThemeChooser(StageManager stageManager) {
		this.stageManager = stageManager;
	}
	
	public void setStyle(Style style) {
		switch(style) {
		case CASPIANDARK:
			setCaspianDark();
			break;
		case MODENA:
			setModena();
			break;
		case AQUA:
			setAqua();
			break;
		default:
			setModena();
			break;
		}
	}
	
	private void setModena() {
		Application.setUserAgentStylesheet(Application.STYLESHEET_MODENA);
		this.stageManager.setLightTheme();
	}
	
	private void setCaspianDark() {
		Application.setUserAgentStylesheet(Application.STYLESHEET_CASPIAN);
		this.stageManager.setDarkTheme();
	}
	
	private void setAqua() {
		AquaFx.style();
		this.stageManager.setLightTheme();
	}
	
	public enum Style {
		MODENA, CASPIANDARK, AQUA, LIGHTJMETRO
	}

}


