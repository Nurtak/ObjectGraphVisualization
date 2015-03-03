package ch.hsr.ogv.controller;

import ch.hsr.ogv.view.SubScene3D;
import ch.hsr.ogv.view.SubSceneCamera;
import ch.hsr.ogv.view.Xform;
import javafx.event.EventHandler;
import javafx.scene.SubScene;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;

public class CameraController {
	
	private double CONTROL_MULTIPLIER = 0.1;
	private double SHIFT_MULTIPLIER = 0.1;
	private double ALT_MULTIPLIER = 0.5;
	private double mousePosX;
	private double mousePosY;
	private double mouseOldX;
	private double mouseOldY;
	private double mouseDeltaX;
	private double mouseDeltaY;
	
	public void handle2DClassView(SubSceneCamera ssCamera) {
		Xform cameraXform = ssCamera.getCameraXform();
        cameraXform.ry.setAngle(0.0); // 320
        cameraXform.rx.setAngle(90.0); // 40
	}

    public void handleMouse(SubScene3D subScene3D) {
    	SubScene scene = subScene3D.getSubScene();
    	SubSceneCamera ssCamera = subScene3D.getSubSceneCamera();
        scene.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent me) {
                mousePosX = me.getSceneX();
                mousePosY = me.getSceneY();
                mouseOldX = me.getSceneX();
                mouseOldY = me.getSceneY();
            }
        });
        scene.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent me) {
                mouseOldX = mousePosX;
                mouseOldY = mousePosY;
                mousePosX = me.getSceneX();
                mousePosY = me.getSceneY();
                mouseDeltaX = (mousePosX - mouseOldX);
                mouseDeltaY = (mousePosY - mouseOldY);
                Xform cameraXform = ssCamera.getCameraXform();
                Xform cameraXform2 = ssCamera.getCameraXform2();

                double modifier = 2.0;
                double modifierFactor = 0.1;

                if (me.isControlDown()) {
                    modifier = 1;
                }
                if (me.isShiftDown()) {
                    modifier = 10.0;
                }
                if (me.isPrimaryButtonDown()) {
                	cameraXform2.t.setX(cameraXform2.t.getX() + mouseDeltaX * modifierFactor * modifier * 2.0);  // -
                	cameraXform2.t.setY(cameraXform2.t.getY() + mouseDeltaY * modifierFactor * modifier * 2.0);  // -
                } else if (me.isSecondaryButtonDown()) {
                	cameraXform.ry.setAngle(cameraXform.ry.getAngle() - mouseDeltaX * modifierFactor * modifier);  // +
                	cameraXform.rx.setAngle(cameraXform.rx.getAngle() + mouseDeltaY * modifierFactor * modifier);
                } else if (me.isMiddleButtonDown()) {
					double z = ssCamera.getPerspectiveCamera().getTranslateZ();
					double newZ = z - mouseDeltaY * 2 + modifierFactor * modifier;
					ssCamera.getPerspectiveCamera().setTranslateZ(newZ);
                }
            }
            
        });
        scene.setOnScroll(new EventHandler<ScrollEvent>() {
            @Override public void handle(ScrollEvent se) {
                double z = ssCamera.getPerspectiveCamera().getTranslateZ();
                double newZ = z + se.getDeltaY();
                ssCamera.getPerspectiveCamera().setTranslateZ(newZ);
            }
        });

    }

    public void handleKeyboard(SubScene3D subScene3D) {
    	SubScene scene = subScene3D.getSubScene();
    	SubSceneCamera ssCamera = subScene3D.getSubSceneCamera();
        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
            	
                Xform cameraXform = ssCamera.getCameraXform();
                Xform cameraXform2 = ssCamera.getCameraXform2();
            	
                switch (event.getCode()) {
                    case Z:
                        if (event.isShiftDown()) {
                            cameraXform.ry.setAngle(0.0);
                            cameraXform.rx.setAngle(0.0);
                            ssCamera.getPerspectiveCamera().setTranslateZ(-300.0);
                        }
                        cameraXform2.t.setX(0.0);
                        cameraXform2.t.setY(0.0);
                        break;
                    case X:
//                        if (event.isControlDown()) {
//                            if (axisGroup.isVisible()) {
//                                axisGroup.setVisible(false);
//                            } else {
//                                axisGroup.setVisible(true);
//                            }
//                        }
                        break;
                    case UP:
                        if (event.isControlDown() && event.isShiftDown()) {
                            cameraXform2.t.setY(cameraXform2.t.getY() - 10.0 * CONTROL_MULTIPLIER);
                        } else if (event.isAltDown() && event.isShiftDown()) {
                            cameraXform.rx.setAngle(cameraXform.rx.getAngle() - 10.0 * ALT_MULTIPLIER);
                        } else if (event.isControlDown()) {
                            cameraXform2.t.setY(cameraXform2.t.getY() - 1.0 * CONTROL_MULTIPLIER);
                        } else if (event.isAltDown()) {
                            cameraXform.rx.setAngle(cameraXform.rx.getAngle() - 2.0 * ALT_MULTIPLIER);
                        } else if (event.isShiftDown()) {
                            double z = ssCamera.getPerspectiveCamera().getTranslateZ();
                            double newZ = z + 5.0 * SHIFT_MULTIPLIER;
                            ssCamera.getPerspectiveCamera().setTranslateZ(newZ);
                        }
                        break;
                    case DOWN:
                        if (event.isControlDown() && event.isShiftDown()) {
                            cameraXform2.t.setY(cameraXform2.t.getY() + 10.0 * CONTROL_MULTIPLIER);
                        } else if (event.isAltDown() && event.isShiftDown()) {
                            cameraXform.rx.setAngle(cameraXform.rx.getAngle() + 10.0 * ALT_MULTIPLIER);
                        } else if (event.isControlDown()) {
                            cameraXform2.t.setY(cameraXform2.t.getY() + 1.0 * CONTROL_MULTIPLIER);
                        } else if (event.isAltDown()) {
                            cameraXform.rx.setAngle(cameraXform.rx.getAngle() + 2.0 * ALT_MULTIPLIER);
                        } else if (event.isShiftDown()) {
                            double z = ssCamera.getPerspectiveCamera().getTranslateZ();
                            double newZ = z - 5.0 * SHIFT_MULTIPLIER;
                            ssCamera.getPerspectiveCamera().setTranslateZ(newZ);
                        }
                        break;
                    case RIGHT:
                        if (event.isControlDown() && event.isShiftDown()) {
                            cameraXform2.t.setX(cameraXform2.t.getX() + 10.0 * CONTROL_MULTIPLIER);
                        } else if (event.isAltDown() && event.isShiftDown()) {
                            cameraXform.ry.setAngle(cameraXform.ry.getAngle() - 10.0 * ALT_MULTIPLIER);
                        } else if (event.isControlDown()) {
                            cameraXform2.t.setX(cameraXform2.t.getX() + 1.0 * CONTROL_MULTIPLIER);
                        } else if (event.isAltDown()) {
                            cameraXform.ry.setAngle(cameraXform.ry.getAngle() - 2.0 * ALT_MULTIPLIER);
                        }
                        break;
                    case LEFT:
                        if (event.isControlDown() && event.isShiftDown()) {
                            cameraXform2.t.setX(cameraXform2.t.getX() - 10.0 * CONTROL_MULTIPLIER);
                        } else if (event.isAltDown() && event.isShiftDown()) {
                            cameraXform.ry.setAngle(cameraXform.ry.getAngle() + 10.0 * ALT_MULTIPLIER);  // -
                        } else if (event.isControlDown()) {
                            cameraXform2.t.setX(cameraXform2.t.getX() - 1.0 * CONTROL_MULTIPLIER);
                        } else if (event.isAltDown()) {
                            cameraXform.ry.setAngle(cameraXform.ry.getAngle() + 2.0 * ALT_MULTIPLIER);  // -
                        }
                        break;
				default:
					break;
                }
            }
        });
    }
}
