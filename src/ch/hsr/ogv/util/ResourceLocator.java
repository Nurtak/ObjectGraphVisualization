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

		ICON_PNG("images/application_icon.gif"),

		ROOTLAYOUT_FXML("templates/RootLayout.fxml"),
		PANEPRESET_FXML("templates/PanePreset.fxml"),
		TOPTEXTFIELD_FXML("templates/TopTextField.fxml"),
		CENTERLABEL_FXML("templates/CenterLabel.fxml"),
		CENTERTEXTFIELD_FXML("templates/CenterTextField.fxml"),
		CLASSCONTEXTMENU_FXML("templates/ClassContextMenu.fxml"),

		LUCIDASANS_TTF("fonts/LUCIDASANSREGULAR.TTF"),
		SEGOEUI_TTF("fonts/SEGOEUI.TTF"),

		SCENE_CSS("css/Scene.css"),
		TSPLITMENUBUTTON_CSS("css/TSplitMenuButton.css"),
		LIGHTHEME_CSS("css/LightTheme.css"),
		DARKTHEME_CSS("css/DarkTheme.css"),
		TEXTFIELD_CSS("css/TextField.css"),

		OPEN_ARROW_OBJ("models/open_arrow.obj"),
		FILLED_ARROW_OBJ("models/filled_arrow.obj"),
		EMPTY_ARROW_OBJ("models/empty_arrow.obj"),
		FILLED_DIAMOND_OBJ("models/filled_diamond.obj"),
		EMPTY_DIAMOND_OBJ("models/empty_diamond.obj"),

		UNDIRECTED_ASSOCIATION_GIF("images/menu/assoc.gif"),
		DIRECTED_ASSOCIATION_GIF("images/menu/assoWithNav.gif"),
		BIDIRECTED_ASSOCIATION_GIF("images/menu/assoNavToNav.gif"),
		UNDIRECTED_AGGREGATION_GIF("images/menu/aggregation.gif"),
		DIRECTED_AGGREGATION_GIF("images/menu/aggregationToNavi.gif"),
		UNDIRECTED_COMPOSITION_GIF("images/menu/composite.gif"),
		DIRECTED_COMPOSITION_GIF("images/menu/compositeToNavi.gif"),
		GENERALIZATION_GIF("images/menu/general.gif"),
		DEPENDENCY_GIF("images/menu/depend.gif"),

		CLASS_GIF("images/menu/structureclass.gif"),
		OBJECT_GIF("images/menu/instance.gif"),
		RELATION_GIF("images/menu/relation.gif"),
		RENAME_GIF("images/menu/text_box2.gif"),
		RENAME_ATTRIBUTES_GIF("images/menu/testcase.gif"),
		CHANGE_DIRECTION_GIF("images/menu/synchronize_dgm_tab.gif"),
		DELETE_PNG("images/menu/trash.png");


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
