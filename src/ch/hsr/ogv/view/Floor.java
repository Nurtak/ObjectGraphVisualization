package ch.hsr.ogv.view;

import java.util.HashSet;

import javafx.scene.DepthTest;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;

public class Floor extends Group implements Selectable {

	private HashSet<Rectangle> tiles = new HashSet<Rectangle>();
	private final double TILE_SIZE = 1000;
	private final int TILE_DIMENSION = 10;
	private Color color = Color.WHITESMOKE;
	
	private volatile boolean selected = false;

	public Floor() {
		for (int x = 0; x < TILE_DIMENSION; x++) {
			for (int z = 0; z < TILE_DIMENSION; z++) {
				buildFloorTile(x, z);
			}
		}

		for (Rectangle tile : this.tiles) {
			getChildren().add(tile);
		}
		setMouseTransparent(true);
	}

	private void buildFloorTile(int x, int z) {
		Rectangle tile = new Rectangle(TILE_SIZE, TILE_SIZE, color);
		tile.setDepthTest(DepthTest.ENABLE);
		tile.getTransforms().add(new Rotate(90, Rotate.X_AXIS));
		tile.setTranslateX(-((TILE_DIMENSION * TILE_SIZE) / 2) + (x * TILE_SIZE));
		tile.setTranslateZ(-((TILE_DIMENSION * TILE_SIZE) / 2) + (z * TILE_SIZE));
		tile.setOpacity(0.6);
		this.tiles.add(tile);
	}

	public void setSeeable(boolean value) {
		for (Rectangle tile : this.tiles) {
			if (value) {
				tile.setFill(getColor());
			} else {
				tile.setFill(Color.TRANSPARENT);
			}
		}

	}

	public void setColor(Color color) {
		for (Rectangle tile : this.tiles) {
			tile.setFill(color);
		}
		this.color = color;
	}

	public Color getColor() {
		return this.color;
	}

	public boolean hasTile(Node node) {
		if (node == null || !(node instanceof Rectangle))
			return false;
		Rectangle rect = (Rectangle) node;
		return this.tiles.contains(rect);
	}

	@Override
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	@Override
	public boolean isSelected() {
		return this.selected;
	}

	@Override
	public Group getSelection() {
		return null; // floor has no real (visible) selection
	}

}
