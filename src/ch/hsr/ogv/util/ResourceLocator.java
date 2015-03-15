package ch.hsr.ogv.util;

import java.net.URL;

/**
 * 
 * @author Simon Gwerder
 *
 */
public class ResourceLocator {
	
	public static URL getResourcePath(Resource res) {
		return ResourceLocator.class.getClassLoader().getResource(res.getRelativePath());
	}
	
	public enum Resource {

		ICON_PNG("images/dummy_icon.png"),
		PANEPRESET_FXML("templates/PanePreset.fxml"),
		ROOTLAYOUT_FXML("templates/RootLayout.fxml"),
		LIGHTHEME_CSS("css/LightTheme.css"),
		DARKTHEME_CSS("css/DarkTheme.css"),
		TEXTFIELD_CSS("css/TextField.css"),
		OPEN_OBJ("models/arrow_open.obj"),
		FILLED_OBJ("models/arrow_filled.obj"),
		BORDER_OBJ("models/arrow_border.obj");
		
		private String relativePath = "./";
		
		private Resource(String relativePath) {
			this.setRelativePath(relativePath);
		}
		
		public String getRelativePath() {
			return relativePath;
		}
		
		public void setRelativePath(String relativePath) {
			this.relativePath = relativePath;
		}
		
	}

}
