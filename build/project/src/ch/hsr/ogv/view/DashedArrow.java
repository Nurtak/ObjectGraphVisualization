package ch.hsr.ogv.view;

import java.util.ArrayList;

import javafx.geometry.Point3D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Box;
import ch.hsr.ogv.model.RelationType;
import ch.hsr.ogv.util.GeometryUtil;

public class DashedArrow extends Arrow {
	
	private static final int DASHED_ELEMENT_COUNT = 20;

	private ArrayList<Box> dashedLines;
	
	public DashedArrow(PaneBox startBox, PaneBox endBox, RelationType type) {
		super(startBox, endBox, type);
	}
	
	public DashedArrow(PaneBox startBox, Point3D endPoint, RelationType type) {
		super(startBox, endPoint, type);
	}
	
	@Override
	protected void addElementsToGroup() {
		getChildren().clear();
		getChildren().addAll(this.dashedLines);
		getChildren().addAll(this.arrowStart, this.arrowEnd);
		getChildren().addAll(this.lineSelectionHelpers);
		getChildren().addAll(this.startSelectionHelper, this.endSelectionHelper);
		getChildren().addAll(this.labelStartRight, this.labelStartLeft, this.labelEndRight, this.labelEndLeft);
	}
	
	@Override
	protected void prepareLines() {
		super.prepareLines();
		this.dashedLines = new ArrayList<Box>();
		for (int i = 0; i < DASHED_ELEMENT_COUNT; i++) {
			Box dashedLine = new Box(this.width, this.width, this.startEndDistance);
			dashedLine.setVisible(false);
			this.dashedLines.add(dashedLine);
		}
	}
	
	@Override
	protected void setLineVisibility() {
		this.line.setVisible(false);
		for (Box dashedLine : this.dashedLines) {
			dashedLine.setVisible(true);
		}
	}
	
	@Override
	protected void setSingleElements() {
		double endGap = this.arrowEnd.getAdditionalGap();
		double startGap = this.arrowStart.getAdditionalGap();
		double gapDistance = this.startEndDistance - (endGap + startGap) / 2;

		this.line.setDepth(gapDistance);
		this.line.setTranslateZ((-endGap + startGap) / 4);

		ArrayList<Point3D> dashedLineCoords = divideLine(new Point3D(0, 0, -gapDistance / 2), new Point3D(0, 0, gapDistance / 2), this.dashedLines.size());

		for (int i = 0; i < this.dashedLines.size(); i++) {
			Box dashedLine = this.dashedLines.get(i);
			dashedLine.setDepth(gapDistance / (2 * DASHED_ELEMENT_COUNT));
			Point3D dashedLineCoord = dashedLineCoords.get(i);
			dashedLine.setTranslateZ(dashedLineCoord.getZ() - dashedLine.getDepth() + (-endGap + startGap) / 4);
		}
	}
	
	private ArrayList<Point3D> divideLine(Point3D start, Point3D end, int count) {
		ArrayList<Point3D> pointList = new ArrayList<Point3D>();
		for (int i = 1; i <= count; i++) {
			pointList.add(GeometryUtil.divideLineFraction(start, end, ((double) i) / ((double) count)));
		}
		return pointList;
	}
	
	@Override
	public void setColor(Color color) {
		super.setColor(color);
		for (Box dashedLine : this.dashedLines) {
			applyColor(dashedLine, this.color);
		}
	}
	
	@Override
	public void setSelected(boolean selected) {
		super.setSelected(selected);
		Color colorToApply = colorToApply(selected);
		for (Box dashedLine : this.dashedLines) {
			applyColor(dashedLine, colorToApply);
		}
	}

}
