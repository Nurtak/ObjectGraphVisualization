package ch.hsr.ogv.controller;

import javafx.scene.SubScene;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import ch.hsr.ogv.view.SubSceneAdapter;
import ch.hsr.ogv.view.SubSceneCamera;
import ch.hsr.ogv.view.Xform;

/**
 * 
 * @author Simon Gwerder
 *
 */
public class RotationCamera extends CameraBase {

	@Override
	public void handleMouse(SubSceneAdapter subSceneAdapter) {
		SubScene subScene = subSceneAdapter.getSubScene();
		SubSceneCamera ssCamera = subSceneAdapter.getSubSceneCamera();

		subScene.setOnMousePressed((MouseEvent me) -> {
			if (!moveCamera) {
				return;
			}

			mousePosX = me.getSceneX();
			mousePosY = me.getSceneY();
			mouseOldX = me.getSceneX();
			mouseOldY = me.getSceneY();
		});

		subScene.setOnMouseDragged((MouseEvent me) -> {
			if (!moveCamera) {
				return;
			}
			mouseOldX = mousePosX;
			mouseOldY = mousePosY;
			mousePosX = me.getSceneX();
			mousePosY = me.getSceneY();
			mouseDeltaX = (mousePosX - mouseOldX);
			mouseDeltaY = (mousePosY - mouseOldY);
			Xform cameraXform = ssCamera.getCameraXform();
			Xform cameraXform2 = ssCamera.getCameraXform2();

			double modifier = MODIFIER;

			if (me.isControlDown()) {
				modifier = CONTROL_MULTIPLIER;
			}
			if (me.isShiftDown()) {
				modifier = SHIFT_MULTIPLIER;
			}
			if (me.isPrimaryButtonDown()) {
				cameraXform2.t.setX(cameraXform2.t.getX() + mouseDeltaX * MODIFIER_FACTOR * modifier * 3.0);
				cameraXform2.t.setY(cameraXform2.t.getY() + mouseDeltaY * MODIFIER_FACTOR * modifier * 3.0);
			} else if (me.isSecondaryButtonDown() && !lockedTopView) {
				cameraXform.ry.setAngle(cameraXform.ry.getAngle() - mouseDeltaX * MODIFIER_FACTOR * modifier);
				cameraXform.rx.setAngle(cameraXform.rx.getAngle() + mouseDeltaY * MODIFIER_FACTOR * modifier);
			} else if (me.isMiddleButtonDown()) {
				double z = ssCamera.get().getTranslateZ();
				double newZ = z - mouseDeltaY * 2 * modifier;
				ssCamera.get().setTranslateZ(newZ);
			}
		});

		subScene.setOnScroll((ScrollEvent se) -> {
			if (!moveCamera) {
				return;
			}
			double z = ssCamera.get().getTranslateZ();
			double newZ = z;
			double modifier = MODIFIER;
			if (se.isControlDown()) {
				modifier = CONTROL_MULTIPLIER;
				newZ += se.getDeltaY() * modifier * 0.5;
			} else if (se.isShiftDown()) {
				modifier = SHIFT_MULTIPLIER;
				newZ += se.getDeltaX() * modifier;
			} else {
				newZ += se.getDeltaY() * MODIFIER;
			}
			ssCamera.get().setTranslateZ(newZ);
		});

	}
	
	@Override
	public void handleKeyboard(SubSceneAdapter subSceneAdapter) {
		SubScene subScene = subSceneAdapter.getSubScene();
		SubSceneCamera ssCamera = subSceneAdapter.getSubSceneCamera();

		subScene.setOnKeyPressed((KeyEvent ke) -> {
			if (!moveCamera) {
				return;
			}
			switch (ke.getCode()) {
			case UP:
				double oldY_UP = ssCamera.get().getTranslateY();
				double newY_UP = oldY_UP - MODIFIER * 8;
				ssCamera.get().setTranslateY(newY_UP);
				break;
			case DOWN:
				double oldY_DOWN = ssCamera.get().getTranslateY();
				double newY_DOWN = oldY_DOWN + MODIFIER * 8;
				ssCamera.get().setTranslateY(newY_DOWN);
				break;
			case RIGHT:
				double oldX_RIGHT = ssCamera.get().getTranslateX();
				double newX_RIGHT = oldX_RIGHT + MODIFIER * 8;
				ssCamera.get().setTranslateX(newX_RIGHT);
				break;
			case LEFT:
				double oldX_LEFT = ssCamera.get().getTranslateX();
				double newX_LEFT = oldX_LEFT - MODIFIER * 8;
				ssCamera.get().setTranslateX(newX_LEFT);
				break;
			default:
				break;
			}
		});
	}
	
}
