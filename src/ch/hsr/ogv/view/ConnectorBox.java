package ch.hsr.ogv.view;

import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;

public class ConnectorBox extends Group {
	
	public ConnectorBox(PaneBox paneBox, Point3D centerLabelPos, Arrow arrow) {
		Box conectorBox = new Box(arrow.getWidth(), paneBox.getDepth(), arrow.getWidth());
		setTranslateX(centerLabelPos.getX() + PaneBox.HORIZONTAL_BORDER_GAP);
		setTranslateY(centerLabelPos.getY() + (paneBox.getDepth() / 2));
		setTranslateZ(centerLabelPos.getZ());
		PhongMaterial material = new PhongMaterial();
		material.setDiffuseColor(arrow.getColor());
		material.setSpecularColor(arrow.getColor().brighter());
		conectorBox.setMaterial(material);
		getChildren().add(conectorBox);
	}
	
}
