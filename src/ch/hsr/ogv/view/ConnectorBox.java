package ch.hsr.ogv.view;

import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;

public class ConnectorBox extends Group {
	
	
	public ConnectorBox(PaneBox paneBox, int centerLabelIndex, Arrow arrow) {
		Box conectorBox = new Box(2 * PaneBox.BORDER_GAP, arrow.getWidth(), arrow.getWidth());
		Point3D centerLabelPos = paneBox.getCenterLabelEndPos(centerLabelIndex);
		setTranslateX(centerLabelPos.getX() + PaneBox.BORDER_GAP);
		setTranslateY(centerLabelPos.getY());
		setTranslateZ(centerLabelPos.getZ());
		PhongMaterial material = new PhongMaterial();
		material.setDiffuseColor(arrow.getColor());
		material.setSpecularColor(arrow.getColor().brighter());
		conectorBox.setMaterial(material);
		getChildren().add(conectorBox);
	}

}
