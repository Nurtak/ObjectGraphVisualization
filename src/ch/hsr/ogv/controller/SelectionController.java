package ch.hsr.ogv.controller;

import java.util.Observable;

import javafx.geometry.Point3D;
import javafx.scene.SubScene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import ch.hsr.ogv.view.Arrow;
import ch.hsr.ogv.view.PaneBox;
import ch.hsr.ogv.view.Selectable;
import ch.hsr.ogv.view.SubSceneAdapter;

/**
 *
 * @author Simon Gwerder
 *
 */
public class SelectionController extends Observable {

	private volatile Selectable previousSelected = null;
	private volatile Selectable currentSelected = null;

	private Point3D previousSelectionCoord;
	private Point3D currentSelectionCoord;

	public Point3D getPreviousSelectionCoord() {
		return previousSelectionCoord;
	}

	public boolean hasPreviousSelection() {
		return this.previousSelected != null;
	}

	public Selectable getPreviousSelected() {
		return this.currentSelected;
	}

	public boolean isPreviousSelected(Selectable selectable) {
		return this.previousSelected != null && this.previousSelected.equals(selectable);
	}

	public Point3D getCurrentSelectionCoord() {
		return currentSelectionCoord;
	}

	public boolean hasCurrentSelection() {
		return this.currentSelected != null;
	}

	public Selectable getCurrentSelected() {
		return this.currentSelected;
	}

	public boolean isCurrentSelected(Selectable selectable) {
		return this.currentSelected != null && this.currentSelected.equals(selectable);
	}

	public void enableSubSceneSelection(SubSceneAdapter subSceneAdapter) {
		selectOnMouseClicked(subSceneAdapter);
	}

	public void enablePaneBoxSelection(PaneBox paneBox, SubSceneAdapter subSceneAdapter) {
		selectOnMouseClicked(paneBox, subSceneAdapter);
		selectOnDragDetected(paneBox, subSceneAdapter);
	}

	public void enableArrowSelection(Arrow arrow, SubSceneAdapter subSceneAdapter) {
		selectOnMouseClicked(arrow, subSceneAdapter);
	}

	private void selectOnMouseClicked(SubSceneAdapter subSceneAdapter) {
		subSceneAdapter.getSubScene().addEventHandler(MouseEvent.MOUSE_RELEASED, (MouseEvent me) -> {
			if ((MouseButton.PRIMARY.equals(me.getButton()) || MouseButton.SECONDARY.equals(me.getButton())) && me.isDragDetect() && me.getPickResult().getIntersectedNode() instanceof SubScene) {
				setSelected(me, subSceneAdapter, true, subSceneAdapter);
			}
		});

		subSceneAdapter.getFloor().addEventHandler(MouseEvent.MOUSE_RELEASED, (MouseEvent me) -> {
			if (MouseButton.PRIMARY.equals(me.getButton()) && me.isDragDetect()) {
				setSelected(me, subSceneAdapter.getFloor(), true, subSceneAdapter);
			}
		});
	}

	private void selectOnMouseClicked(PaneBox paneBox, SubSceneAdapter subSceneAdapter) {

		paneBox.getBox().addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent me) -> {
			if (MouseButton.PRIMARY.equals(me.getButton()) || MouseButton.SECONDARY.equals(me.getButton())) {
				paneBox.setAllLabelSelected(false);
				setSelected(me, paneBox, true, subSceneAdapter);
			}
		});

		paneBox.getCenter().addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent me) -> {
			if (MouseButton.PRIMARY.equals(me.getButton()) || MouseButton.SECONDARY.equals(me.getButton())) {
				paneBox.setAllLabelSelected(false);
				setSelected(me, paneBox, true, subSceneAdapter);
			}
		});

		paneBox.getSelection().addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent me) -> {
			if (MouseButton.PRIMARY.equals(me.getButton()) || MouseButton.SECONDARY.equals(me.getButton())) {
				paneBox.setAllLabelSelected(false);
			}
		});

		paneBox.getTopLabel().addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent me) -> {
			if (MouseButton.PRIMARY.equals(me.getButton()) || MouseButton.SECONDARY.equals(me.getButton())) {
				paneBox.setLabelSelected(paneBox.getTopLabel(), true);
				setSelected(me, paneBox, true, subSceneAdapter);
			}
			if (MouseButton.PRIMARY.equals(me.getButton()) && paneBox.isSelected() && me.getClickCount() >= 2) {
				paneBox.allowTopTextInput(true);
			}
		});

		for (Label centerLabel : paneBox.getCenterLabels()) {
			centerLabel.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent me) -> {
				if (MouseButton.PRIMARY.equals(me.getButton()) || MouseButton.SECONDARY.equals(me.getButton())) {
					paneBox.setLabelSelected(centerLabel, true);
					setSelected(me, paneBox, true, subSceneAdapter);
					me.consume(); // otherwise this centerLabel's parent = getCenter() will be called
				}
				if (MouseButton.PRIMARY.equals(me.getButton()) && paneBox.isSelected() && me.getClickCount() >= 2) {
					paneBox.allowCenterFieldTextInput(centerLabel, true);
				}
			});
		}

	}

	private void selectOnMouseClicked(Arrow arrow, SubSceneAdapter subSceneAdapter) {
		arrow.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent me) -> {
			if ((MouseButton.PRIMARY.equals(me.getButton()) || MouseButton.SECONDARY.equals(me.getButton())) && !arrow.isSelected()) {
				setSelected(me, arrow, true, subSceneAdapter);
			}
		});
	}

	private void selectOnDragDetected(PaneBox paneBox, SubSceneAdapter subSceneAdapter) {
		paneBox.getTopLabel().addEventHandler(MouseEvent.DRAG_DETECTED, (MouseEvent me) -> {
			if (MouseButton.PRIMARY.equals(me.getButton()) && me.isDragDetect() && !paneBox.isSelected()) {
				paneBox.setAllLabelSelected(false);
				setSelected(me, paneBox, true, subSceneAdapter);
			}
		});

		paneBox.getCenter().addEventHandler(MouseEvent.DRAG_DETECTED, (MouseEvent me) -> {
			if (MouseButton.PRIMARY.equals(me.getButton()) && me.isDragDetect() && !paneBox.isSelected()) {
				paneBox.setAllLabelSelected(false);
				setSelected(me, paneBox, true, subSceneAdapter);
			}
		});

		paneBox.getBox().addEventHandler(MouseEvent.DRAG_DETECTED, (MouseEvent me) -> {
			if (MouseButton.PRIMARY.equals(me.getButton()) && me.isDragDetect() && !paneBox.isSelected()) {
				paneBox.setAllLabelSelected(false);
				setSelected(me, paneBox, true, subSceneAdapter);
			}
		});
	}

	private void setSelected(MouseEvent me, Selectable selectable, boolean selected, SubSceneAdapter subSceneAdapter) {
		this.currentSelectionCoord = new Point3D(me.getX(), me.getY(), me.getZ());
		setSelected(selectable, selected, subSceneAdapter);
	}

	public void setSelected(Selectable selectable, boolean selected, SubSceneAdapter subSceneAdapter) {
		selectable.setSelected(selected);

		if (selected) {
			if (this.currentSelected != null && selectable != this.currentSelected) {
				this.previousSelected = this.currentSelected; // current selection becomes previous selected object
				this.previousSelectionCoord = this.currentSelectionCoord;
				setSelected(this.currentSelected, false, subSceneAdapter); // deselect the old selected object
			}
			this.currentSelected = selectable;
			selectable.requestFocus();
			subSceneAdapter.getFloor().toFront();

			setChanged();
			notifyObservers(selectable);
		} else {
			this.currentSelected = null;

			setChanged();
			notifyObservers(selectable);
		}
	}

}
