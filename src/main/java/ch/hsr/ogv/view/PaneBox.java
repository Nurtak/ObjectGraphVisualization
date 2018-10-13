package ch.hsr.ogv.view;

import ch.hsr.ogv.util.ColorUtil;
import ch.hsr.ogv.util.FXMLResourceUtil;
import ch.hsr.ogv.util.ResourceLocator.Resource;
import ch.hsr.ogv.util.TextUtil;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Point3D;
import javafx.scene.CacheHint;
import javafx.scene.DepthTest;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.transform.Rotate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class PaneBox implements Selectable {

    private final static Logger logger = LoggerFactory.getLogger(PaneBox.class);

    public final static int CLASSBOX_DEPTH = 10;
    public final static int OBJECTBOX_DEPTH = 20;
    public final static int INIT_DEPTH = 10;

    public final static Color DEFAULT_COLOR = Color.CORNSILK;

    public final static double MIN_WIDTH = 100.0;
    public final static double MIN_HEIGHT = 100.0;

    public final static double MAX_WIDTH = Double.MAX_VALUE; // was at 464 before
    public final static double MAX_HEIGHT = Double.MAX_VALUE;

    public final static double BASE_HEIGHT = 72.0; // required min height with one centerlabel (experience value)
    public final static double TOP_LABEL_HEIGHT = 32.0;
    public final static double CENTER_LABEL_HEIGHT = 28.0;
    public final static double HORIZONTAL_BORDER_GAP = 7.0;
    public final static double VERTICAL_BORDER_GAP = 5.0;
    public final static int MAX_CENTER_LABELS = Integer.MAX_VALUE; // was at 15 before

    private Group paneBox = new Group();
    private BoxSelection selection = null;
    private BorderPane borderPane = null;

    private Label topLabel = null;
    private TextField topTextField = null;

    private ArrayList<Label> centerLabels = new ArrayList<Label>();
    private ArrayList<TextField> centerTextFields = new ArrayList<TextField>();

    private volatile Label selectedLabel = null;

    private volatile int indexCenterGrid = -1; // row index of centerlabels, where grid should begin, -1: no center grid

    private Color color;
    private Cuboid box;

    public Group get() {
        return this.paneBox;
    }

    public Cuboid getBox() {
        return this.box;
    }

    public Color getColor() {
        return this.color;
    }

    public void setColor(Color color) {
        this.color = color;
        this.borderPane.setStyle(getPaneStyle());
        this.box.setColor(color);
    }

    public PaneBox() {
        this(DEFAULT_COLOR);
    }

    public PaneBox(Color color) {
        initLayout();
        this.borderPane.setCache(true);
        this.borderPane.setCacheHint(CacheHint.SCALE_AND_ROTATE);
        this.borderPane.setDepthTest(DepthTest.ENABLE);

        // rotate the pane correctly
        this.borderPane.getTransforms().add(new Rotate(180, Rotate.Y_AXIS));
        this.borderPane.getTransforms().add(new Rotate(90, Rotate.X_AXIS));

        // position the pane so, that the center is at the scene's origin (0, 0, 0)
        this.borderPane.translateXProperty().bind(this.borderPane.widthProperty().divide(2));
        this.borderPane.translateZProperty().bind(this.borderPane.heightProperty().divide(2));

        // build the box that stays beneath the borderPane
        buildBox();

        // create the selection objects that stays with this box
        this.selection = new BoxSelection(getBox());
        this.selection.setVisible(false);

        // this.paneBoxSelection.getChildren().addAll(this.borderPane, this.box.getNode(), this.selection.getNode());
        this.paneBox.getChildren().addAll(this.borderPane, this.box);

        // position the whole group so, that the center is at scene's origin (0, 0, 0)
        setTranslateY(INIT_DEPTH / 2);
        setColor(color);
    }

    private String getPaneStyle() {
        return "-fx-background-color: " + ColorUtil.colorToCssColor(getColor()) + ";\n" + "-fx-border-color: black;\n" + "-fx-border-width: 2;";
    }

    private void initLayout() {
        this.borderPane = loadBorderPane();
        this.topTextField = loadTopTextField();
        // this.topTextField.setContextMenu(new ContextMenu()); // overrides the bugged default contextmenu
        Node topNode = this.borderPane.getTop();
        if ((topNode instanceof HBox)) {
            HBox topHBox = (HBox) topNode;
            if (!topHBox.getChildren().isEmpty() && topHBox.getChildren().get(0) instanceof Label) {
                this.topLabel = (Label) topHBox.getChildren().get(0);
            }
            HBox.setMargin(this.topTextField, new Insets(-1, -1, 0, -1));
            HBox.setHgrow(this.topTextField, Priority.ALWAYS);
            HBox.setMargin(this.topLabel, new Insets(-1, -1, 0, -1));
            HBox.setHgrow(this.topLabel, Priority.ALWAYS);
        }

    }

    private BorderPane loadBorderPane() {
        Object loadedPreset = FXMLResourceUtil.loadPreset(Resource.PANEPRESET_FXML); // load pane preset from fxml file
        if (loadedPreset != null && loadedPreset instanceof BorderPane) {
            return (BorderPane) loadedPreset;
        }
        return new BorderPane();
    }

    // TODO Move the loaders to code, to improve performance!
    private TextField loadTopTextField() {
        Object loadedPreset = FXMLResourceUtil.loadPreset(Resource.TOPTEXTFIELD_FXML); // load top textfield preset from fxml file
        if (loadedPreset != null && loadedPreset instanceof TextField) {
            return (TextField) loadedPreset;
        }
        return new TextField();
    }

    public void createCenterField() {
        Label centerLabel = new Label();
        centerLabel.setCacheHint(CacheHint.SCALE_AND_ROTATE);
        centerLabel.setDepthTest(DepthTest.ENABLE);
        centerLabel.setText("attr" + getCenter().getChildren().size() + 1);
        HBox.setHgrow(centerLabel, Priority.ALWAYS);
        centerLabel.setMaxWidth(Double.MAX_VALUE);
        centerLabel.setPrefWidth(Double.MAX_VALUE);
        centerLabel.setWrapText(false);
        centerLabel.setPadding(new Insets(0, 5, 0, 5));
        centerLabel.setMinHeight(28);
        centerLabel.setStyle("-fx-font-size: 16;");

        TextField centerTextField = loadCenterTextField();
        centerTextField.setText(centerLabel.getText());
        // centerTextField.setContextMenu(new ContextMenu()); // overrides the bugged default contextmenu
        this.centerLabels.add(centerLabel);
        this.centerTextFields.add(centerTextField);
        GridPane centerGridPane = getCenter();
        if (centerGridPane != null) {
            centerGridPane.add(centerLabel, 0, getCenter().getChildren().size());
        }
    }

    private TextField loadCenterTextField() {
        Object loadedPreset = FXMLResourceUtil.loadPreset(Resource.CENTERTEXTFIELD_FXML); // load center textfield preset from fxml file
        if (loadedPreset != null && loadedPreset instanceof TextField) {
            return (TextField) loadedPreset;
        }
        return new TextField();
    }

    private void buildBox() {
        this.box = new Cuboid(INIT_DEPTH);
        this.box.setDrawTopFace(false);
        this.box.widthProperty().bind(this.borderPane.widthProperty());
        this.box.heightProperty().bind(this.borderPane.heightProperty());
        this.box.getTransforms().add(new Rotate(90, Rotate.X_AXIS));
        this.box.translateXProperty().bind(this.borderPane.translateXProperty().subtract(this.borderPane.widthProperty().divide(2)));
        this.box.translateZProperty().bind(this.borderPane.translateZProperty().subtract(this.borderPane.heightProperty().divide(2)));
        this.box.translateYProperty().bind(this.borderPane.translateYProperty().subtract(INIT_DEPTH / 2));
    }

    public TextField getTopTextField() {
        return this.topTextField;
    }

    public Label getTopLabel() {
        return this.topLabel;
    }

    public ArrayList<Label> getCenterLabels() {
        return this.centerLabels;
    }

    public ArrayList<TextField> getCenterTextFields() {
        return this.centerTextFields;
    }

    private void swapTopField(Node labelOrTextField) {
        Node topNode = this.borderPane.getTop();
        if (topNode instanceof HBox) {
            HBox topHBox = (HBox) topNode;
            topHBox.getChildren().clear();
            topHBox.getChildren().add(labelOrTextField);
        }
    }

    private void swapCenterField(Node labelOrField, int rowIndex) {
        GridPane centerGridPane = getCenter();
        if (centerGridPane != null) {
            try {
                centerGridPane.getChildren().set(rowIndex, labelOrField);
                GridPane.setRowIndex(labelOrField, rowIndex);
            }
            catch (IndexOutOfBoundsException ioobe) {
                logger.debug("Swapping center field failed. IndexOutOfBoundsException: " + ioobe.getMessage());
            }
        }
    }

    public void setTopText(String text) {
        if (this.topTextField == null || this.topLabel == null)
            return;
        this.topTextField.setText(text);
        this.topLabel.setText(text);
    }

    public void setTopFont(Font font) {
        if (this.topTextField == null || this.topLabel == null)
            return;
        this.topTextField.setFont(font);
        this.topLabel.setFont(font);
    }

    public Font getTopFont() {
        return this.topTextField.getFont(); // doesnt matter if Font is taken from topLabel or topTextField
    }

    public void setTopUnderline(boolean underline) {
        this.topLabel.setUnderline(underline);
    }

    public void allowTopTextInput(boolean value) {
        if (value) {
            swapTopField(this.topTextField);
            Platform.runLater(() -> {
                this.topTextField.requestFocus();
                this.topTextField.selectAll();
                this.topTextField.applyCss();
            });
        }
        else {
            swapTopField(this.topLabel);
            if (isSelected()) {
                setLabelSelected(this.topLabel, true);
            }
        }
        this.topTextField.setEditable(value);
        this.topTextField.setDisable(!value);
    }

    public void allowCenterFieldTextInput(Label centerLabel, boolean value) {
        int rowIndex = this.centerLabels.indexOf(centerLabel);
        if (rowIndex < 0) {
            return;
        }

        try {
            TextField centerTextField = this.centerTextFields.get(rowIndex);
            centerTextField.setDisable(!value);
            centerTextField.setEditable(value);
            centerTextField.setVisible(value);
            if (value) {
                swapCenterField(centerTextField, rowIndex);
                Platform.runLater(() -> {
                    centerTextField.requestFocus();
                    centerTextField.selectAll();
                    centerTextField.applyCss();
                });
            }
            else {
                swapCenterField(centerLabel, rowIndex);
                if (isSelected()) {
                    setLabelSelected(centerLabel, true);
                }
            }
        }
        catch (IndexOutOfBoundsException ioobe) {
            logger.debug("Allowing textinput failed for center field. IndexOutOfBoundsException: " + ioobe.getMessage());
        }
    }

    public double calcMinWidth() {
        // + 70px / 40px for some additional space to compensate padding, insets, borders etc.
        double retWidth = TextUtil.computeTextWidth(getTopFont(), getTopTextField().getText(), 0.0D) + 70;
        for (TextField centerTextField : getCenterTextFields()) {
            double newWidth = TextUtil.computeTextWidth(centerTextField.getFont(), centerTextField.getText(), 0.0D) + 40;
            if (newWidth > retWidth) {
                retWidth = newWidth;
            }
        }
        return restrictedWidth(retWidth);
    }

    public double calcMinHeight() {
        double newMinHeight = minHeightPer(getCenterLabels().size());
        return restrictedHeight(newMinHeight);
    }

    private double minHeightPer(int countCenterLabels) {
        if (this.centerLabels.isEmpty()) {
            return 0.0;
        }
        return BASE_HEIGHT + (countCenterLabels - 1) * CENTER_LABEL_HEIGHT;
    }

    public Point3D getCenterLabelEndPos(int centerLabelIndex) {
        if (centerLabelIndex < 0 || centerLabelIndex >= this.centerLabels.size()) {
            return new Point3D(getTranslateX(), getTranslateY(), getTranslateZ());
        }
        double z = getTranslateZ() + (getHeight() / 2) - TOP_LABEL_HEIGHT - VERTICAL_BORDER_GAP
                   - (centerLabelIndex * CENTER_LABEL_HEIGHT) - (CENTER_LABEL_HEIGHT / 2);
        return new Point3D(getTranslateX() - (getWidth() / 2), getTranslateY(), z);
    }

    public GridPane getCenter() {
        Node centerNode = this.borderPane.getCenter();
        if (centerNode instanceof GridPane) {
            return (GridPane) centerNode;
        }
        return null;
    }

    public boolean isAllCenterGrid() {
        return this.indexCenterGrid == 0;
    }

    public boolean isNoCenterGrid() {
        return this.indexCenterGrid == -1;
    }

    public void setAllCenterGrid(boolean all) {
        if (all) {
            this.indexCenterGrid = 0;
        }
        else {
            this.indexCenterGrid = -1;
        }
    }

    public boolean hasCenterGrid(int index) {
        return this.indexCenterGrid != -1 && index <= this.indexCenterGrid && index < this.centerLabels.size();
    }

    public void setIndexCenterGrid(int index) {
        this.indexCenterGrid = index;
        recalcHasCenterGrid();
    }

    public void recalcHasCenterGrid() {
        for (int i = 0; i < this.centerLabels.size(); i++) {
            Label centerLabel = this.centerLabels.get(i);
            centerLabel.setBorder(null);
            boolean isLast = true;
            if (i < this.centerLabels.size() - 1) {
                Label nextCenterLabel = this.centerLabels.get(i + 1);
                isLast = !nextCenterLabel.isVisible();
            }
            if (!(this.indexCenterGrid <= -1) && i >= this.indexCenterGrid) {
                showCenterGrid(centerLabel, isLast);
            }
        }
    }

    private void showCenterGrid(Label centerLabel, boolean isLast) {
        Border border = null;
        BorderStroke bsMiddle = new BorderStroke(Color.BLACK, Color.BLACK, Color.TRANSPARENT, Color.BLACK, BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID,
                                                 BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1), null);
        BorderStroke bsLast = new BorderStroke(Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK, BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID,
                                               BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1), null);
        if (!isLast) {
            border = new Border(bsMiddle);
        }
        else {
            border = new Border(bsLast);
        }
        centerLabel.setBorder(border);
    }

    public void clearCenterFields() {
        this.centerLabels.clear();
        this.centerTextFields.clear();
        getCenter().getChildren().clear();
    }

    private void fillCreateCenterLabels(int upToRowIndex) {
        boolean hasIndex = upToRowIndex >= 0 && upToRowIndex < this.centerLabels.size();
        while (!hasIndex) {
            createCenterField();
            hasIndex = upToRowIndex >= 0 && upToRowIndex < this.centerLabels.size();
        }
        if (getSelectedLabel() != null && this.centerLabels.contains(getSelectedLabel())) {
            setLabelSelected(getSelectedLabel(), true);
        }
    }

    public void setCenterText(int rowIndex, String labelText, String textFieldText) {
        fillCreateCenterLabels(rowIndex);
        Label centerLabel = this.centerLabels.get(rowIndex);
        TextField centerTextField = this.centerTextFields.get(rowIndex);
        centerLabel.setText(labelText);
        centerTextField.setText(textFieldText);
    }

    @Override
    public void setSelected(boolean selected) {
        this.selection.setVisible(selected);
        if (selected) {
            get().toFront();
        }
        else {
            setAllLabelSelected(false);
        }
    }

    @Override
    public boolean isSelected() {
        return this.selection.isVisible();
    }

    @Override
    public BoxSelection getSelection() {
        return this.selection;
    }

    @Override
    public void requestFocus() {
        get().requestFocus();
    }

    public void setAllLabelSelected(boolean selected) {
        setLabelSelected(this.topLabel, selected);
        for (Label centerLabel : this.centerLabels) {
            setLabelSelected(centerLabel, selected);
        }
    }

    public void setLabelSelected(int rowIndex, boolean selected) {
        if (rowIndex >= 0 && rowIndex < this.centerLabels.size()) {
            setLabelSelected(this.centerLabels.get(rowIndex), selected);
        }
    }

    public void setLabelSelected(Label label, boolean selected) {
        if (selected) {
            if (this.selectedLabel != null && this.centerLabels.contains(this.selectedLabel)) {
                setLabelSelected(this.selectedLabel, false);
            }
            this.selectedLabel = label;
            BorderStroke bs = new BorderStroke(Color.DODGERBLUE, Color.DODGERBLUE, Color.DODGERBLUE, Color.DODGERBLUE, BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID,
                                               BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1), null);
            label.setBorder(new Border(bs));
        }
        else {
            this.selectedLabel = null;
            label.setBorder(null);
            recalcHasCenterGrid();
        }
    }

    public boolean isLabelSelected(Label label) {
        if (this.selectedLabel != null && this.centerLabels.contains(label)) {
            return this.selectedLabel.equals(label);
        }
        return false;
    }

    public Label getSelectedLabel() {
        return this.selectedLabel;
    }

    private double restrictedWidth(double width) {
        double retWidth = width;
        if (width < MIN_WIDTH) {
            retWidth = MIN_WIDTH;
        }
        else if (width > MAX_WIDTH) {
            retWidth = MAX_WIDTH;
        }
        return retWidth;
    }

    private double restrictedHeight(double height) {
        double retHeight = height;
        if (height < MIN_HEIGHT) {
            retHeight = MIN_HEIGHT;
        }
        else if (height > MAX_HEIGHT) {
            retHeight = MAX_HEIGHT;
        }
        return retHeight;
    }

    public void setWidth(double witdh) {
        this.borderPane.setPrefWidth(witdh);
    }

    public double getWidth() {
        return this.borderPane.getPrefWidth();
    }

    /**
     * Sets the minimum width of this box. Note that it can not be set blow {@link PaneBox#MIN_WIDTH}.
     *
     * @param width
     */
    public void setMinWidth(double width) {
        this.borderPane.setMinWidth(restrictedWidth(width));
    }

    public double getMinWidth() {
        return this.borderPane.getMinWidth();
    }

    public void setHeight(double height) {
        this.borderPane.setPrefHeight(height);
    }

    public double getHeight() {
        return this.borderPane.getPrefHeight();
    }

    /**
     * Sets the maximum width of this box. Note that it can not be set blow {@link PaneBox#MAX_WIDTH}.
     *
     * @param width
     */
    public void setMaxWidth(double width) {
        this.borderPane.setMaxWidth(restrictedWidth(width));
    }

    public double getMaxWidth() {
        return this.borderPane.getMaxWidth();
    }

    /**
     * Sets the minimum height of this box. Note that it can not be set above {@link PaneBox#MIN_HEIGHT}.
     *
     * @param height
     */
    public void setMinHeight(double height) {
        this.borderPane.setMinHeight(restrictedHeight(height));
    }

    public double getMinHeight() {
        return this.borderPane.getMinHeight();
    }

    /**
     * Sets the maximum height of this box. Note that it can not be set above {@link PaneBox#MAX_HEIGHT}.
     *
     * @param width
     */
    public void setMaxHeight(double height) {
        this.borderPane.setMaxHeight(restrictedHeight(height));
    }

    public double getMaxHeight() {
        return this.borderPane.getMaxHeight();
    }

    public void setDepth(double depth) {
        this.box.setDepth(depth);
        this.box.translateYProperty().bind(this.borderPane.translateYProperty().subtract(depth / 2));
        setTranslateY((depth / 2) - (getTranslateY() / 2));
    }

    public double getDepth() {
        return this.box.getDepth();
    }

    public void setTranslateY(double y) {
        this.paneBox.setTranslateY(y);
        this.selection.setTranslateY(y);
    }

    public void setTranslateXYZ(Point3D point) {
        setTranslateXYZ(point.getX(), point.getY(), point.getZ());
    }

    public void setTranslateXYZ(double x, double y, double z) {
        setTranslateX(x);
        setTranslateY(y);
        setTranslateZ(z);
    }

    public void setTranslateX(double x) {
        this.paneBox.setTranslateX(x);
        this.selection.setTranslateX(x);
    }

    public void setTranslateZ(double z) {
        this.paneBox.setTranslateZ(z);
        this.selection.setTranslateZ(z);
    }

    public double getTranslateY() {
        return this.paneBox.getTranslateY();
    }

    public double getTranslateX() {
        return this.paneBox.getTranslateX();
    }

    public double getTranslateZ() {
        return this.paneBox.getTranslateZ();
    }

    public void setVisible(boolean visible) {
        this.paneBox.setVisible(visible);
    }

    public void setPaneVisible(boolean visible) {
        this.borderPane.setVisible(visible);
    }

    public void setBoxVisible(boolean visible) {
        this.box.setVisible(visible);
    }

    public Point3D getCenterPoint() {
        double y = this.paneBox.getTranslateY() - (this.box.getDepth() / 2);
        return new Point3D(this.paneBox.getTranslateX(), y, this.paneBox.getTranslateZ());
    }

}
