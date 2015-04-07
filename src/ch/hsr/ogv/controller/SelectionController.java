package ch.hsr.ogv.controller;

import java.util.Observable;

import javafx.geometry.Point3D;
import ch.hsr.ogv.view.Arrow;
import ch.hsr.ogv.view.PaneBox;
import ch.hsr.ogv.view.Selectable;
import ch.hsr.ogv.view.SubSceneAdapter;
import javafx.scene.SubScene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

/**
 * 
 * @author Simon Gwerder
 *
 */
public class SelectionController extends Observable {

	private volatile Selectable selected = null;

	private Point3D selectionCoordinates;

	public Point3D getSelectionCoordinates() {
		return selectionCoordinates;
	}

	public boolean hasSelection() {
		return this.selected != null;
	}

	public Selectable getSelected() {
		return this.selected;
	}

	public boolean isSelected(Selectable selectable) {
		return this.selected != null && this.selected.equals(selectable);
	}

	public void enableSubSceneSelection(SubSceneAdapter subSceneAdapter) {
		selectOnMouseClicked(subSceneAdapter);
	}

	public void enablePaneBoxSelection(PaneBox paneBox, SubSceneAdapter subSceneAdapter) {
		selectOnClicked(paneBox, subSceneAdapter);
		selectOnDragDetected(paneBox, subSceneAdapter);
	}

	public void enableArrowSelection(Arrow arrow, SubSceneAdapter subSceneAdapter) {
		selectOnMouseClicked(arrow, subSceneAdapter);
	}

	private void selectOnMouseClicked(SubSceneAdapter subSceneAdapter) {
		subSceneAdapter.getSubScene().addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent me) -> {
			if (MouseButton.PRIMARY.equals(me.getButton()) && me.isDragDetect() && me.getPickResult().getIntersectedNode() instanceof SubScene) {
				setSelected(me, subSceneAdapter, true, subSceneAdapter);
			}
		});

		subSceneAdapter.getFloor().addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent me) -> {
			if (MouseButton.PRIMARY.equals(me.getButton()) && me.isDragDetect()) {
				setSelected(me, subSceneAdapter.getFloor(), true, subSceneAdapter);
			}
		});
	}

	private void selectOnClicked(PaneBox paneBox, SubSceneAdapter subSceneAdapter) {
		paneBox.getTopLabel().addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent me) -> {
			if (MouseButton.PRIMARY.equals(me.getButton()) && !paneBox.isSelected()) {
				setSelected(me, paneBox, true, subSceneAdapter);
			}
			if (MouseButton.PRIMARY.equals(me.getButton()) && paneBox.isSelected() && me.getClickCount() >= 2) {
				paneBox.allowTopTextInput(true);
			}
		});

		paneBox.getCenter().addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent me) -> {
			if (MouseButton.PRIMARY.equals(me.getButton())) {
				setSelected(me, paneBox, true, subSceneAdapter);
			}
		});
		
		for(Label centerLabel : paneBox.getCenterLabels()) {
			centerLabel.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent me) -> {
				if (MouseButton.PRIMARY.equals(me.getButton())) {
					setSelected(me, paneBox, true, subSceneAdapter);
					me.consume();
				}
				if (MouseButton.PRIMARY.equals(me.getButton()) && paneBox.isSelected() && me.getClickCount() >= 2) {
					paneBox.allowCenterFieldTextInput(centerLabel, true);
					me.consume();
				}
			});
		}

		paneBox.getBox().addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent me) -> {
			if (MouseButton.PRIMARY.equals(me.getButton())) {
				setSelected(me, paneBox, true, subSceneAdapter);
			}
		});
	}

	private void selectOnMouseClicked(Arrow arrow, SubSceneAdapter subSceneAdapter) {
		arrow.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent me) -> {
			if (MouseButton.PRIMARY.equals(me.getButton()) && !arrow.isSelected()) {
				setSelected(me, arrow, true, subSceneAdapter);
			}
		});
	}

	private void selectOnDragDetected(PaneBox paneBox, SubSceneAdapter subSceneAdapter) {
		paneBox.getTopLabel().addEventHandler(MouseEvent.DRAG_DETECTED, (MouseEvent me) -> {
			if (MouseButton.PRIMARY.equals(me.getButton()) && me.isDragDetect() && !paneBox.isSelected()) {
				setSelected(me, paneBox, true, subSceneAdapter);
			}
		});
		
		paneBox.getCenter().addEventHandler(MouseEvent.DRAG_DETECTED, (MouseEvent me) -> {
			if (MouseButton.PRIMARY.equals(me.getButton()) && me.isDragDetect() && !paneBox.isSelected()) {
				setSelected(me, paneBox, true, subSceneAdapter);
			}
		});

		paneBox.getBox().addEventHandler(MouseEvent.DRAG_DETECTED, (MouseEvent me) -> {
			if (MouseButton.PRIMARY.equals(me.getButton()) && me.isDragDetect() && !paneBox.isSelected()) {
				setSelected(me, paneBox, true, subSceneAdapter);
			}
		});
	}

	private void setSelected(MouseEvent me, Selectable selectable, boolean selected, SubSceneAdapter subSceneAdapter) {
		this.selectionCoordinates = new Point3D(me.getX(), me.getY(), me.getZ());
		setSelected(selectable, selected, subSceneAdapter);
	}

	private void setSelected(Selectable selectable, boolean selected, SubSceneAdapter subSceneAdapter) {
		selectable.setSelected(selected);

		if (selected) {
			if (this.selected != null && selectable != this.selected) {
				this.selected.setSelected(false); // deselect the old selected object
			}

			this.selected = selectable;

			selectable.requestFocus();

			if (selectable instanceof PaneBox) {
				PaneBox paneBox = (PaneBox) selectable;
				paneBox.get().toFront();
				subSceneAdapter.getFloor().toFront();
			}
			setChanged();
			notifyObservers(selectable);
		} else {
			this.selected = null;
			setChanged();
			notifyObservers(selectable);
		}
	}

}
