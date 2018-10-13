package ch.hsr.ogv.util;

import java.net.URL;

public class ResourceLocator {

    public static URL getResourcePath(Resource res) {
        return ResourceLocator.class.getClassLoader().getResource(res.getRelativePath());
    }

    public enum Resource {

        ICON_GIF("images/OGV.gif"),

        ROOTLAYOUT_FXML("templates/RootLayout.fxml"),
        PANEPRESET_FXML("templates/PanePreset.fxml"),
        TOPTEXTFIELD_FXML("templates/TopTextField.fxml"),
        CENTERLABEL_FXML("templates/CenterLabel.fxml"),
        CENTERTEXTFIELD_FXML("templates/CenterTextField.fxml"),
        ARROWTEXTFIELD_FXML("templates/ArrowTextField.fxml"),

        LUCIDASANS_TTF("fonts/LUCIDASANSREGULAR.TTF"),
        SEGOEUI_TTF("fonts/SEGOEUI.TTF"),

        SCENE_CSS("css/Scene.css"),
        TSPLITMENUBUTTON_CSS("css/TSplitMenuButton.css"),
        TEXTFIELD_CSS("css/TextField.css"),

        OPEN_ARROW_OBJ("models/open_arrow.obj"),
        FILLED_ARROW_OBJ("models/filled_arrow.obj"),
        EMPTY_ARROW_OBJ("models/empty_arrow.obj"),
        FILLED_DIAMOND_OBJ("models/filled_diamond.obj"),
        EMPTY_DIAMOND_OBJ("models/empty_diamond.obj"),
        ARC_OBJ("models/arc.obj"),

        UNDIRECTED_ASSOCIATION_GIF("images/menu/assoc.gif"),
        DIRECTED_ASSOCIATION_GIF("images/menu/assoWithNav.gif"),
        BIDIRECTED_ASSOCIATION_GIF("images/menu/assoNavToNav.gif"),
        UNDIRECTED_AGGREGATION_GIF("images/menu/aggregation.gif"),
        DIRECTED_AGGREGATION_GIF("images/menu/aggregationToNavi.gif"),
        UNDIRECTED_COMPOSITION_GIF("images/menu/composite.gif"),
        DIRECTED_COMPOSITION_GIF("images/menu/compositeToNavi.gif"),
        GENERALIZATION_GIF("images/menu/general.gif"),
        DEPENDENCY_GIF("images/menu/depend.gif"),
        OBJRELATION_GIF("images/menu/rightAngleOff.gif"),

        IMPORT_PNG("images/menu/application-import.png"),
        ADD_ATTR_GIF("images/menu/artifactPrj.gif"),
        CLASS_GIF("images/menu/structureclass.gif"),
        OBJECT_GIF("images/menu/instance.gif"),
        RELATION_GIF("images/menu/relation.gif"),
        RENAME_GIF("images/menu/text_box2.gif"),
        RENAME_ATTR_GIF("images/menu/testcase.gif"),
        MOVE_UP_PNG("images/menu/bullet_arrow_up.png"),
        MOVE_DOWN_PNG("images/menu/bullet_arrow_down.png"),
        CHANGE_DIRECTION_GIF("images/menu/synchronize_dgm_tab.gif"),
        ASSOCIATION_CLASS_GIF("images/menu/associationClass.gif"),
        ADD_MULTIPLICITY_GIF("images/menu/multiplicity.gif"),
        ADD_ROLE_GIF("images/menu/ColRole.gif"),
        DELETE_PNG("images/menu/trash.png"),

        MESSAGE_DEFAULT_PNG("images/menu/message_default.png"),
        MESSAGE_INFO_PNG("images/menu/message_info.png"),
        MESSAGE_WARN_PNG("images/menu/message_warn.png"),
        MESSAGE_ERROR_PNG("images/menu/message_error.png");

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
