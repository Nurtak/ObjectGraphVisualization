package ch.hsr.ogv.controller;

import java.util.Observable;
import java.util.Observer;

import ch.hsr.ogv.view.SubScene3D;
import ch.hsr.ogv.view.SubSceneCamera;
import ch.hsr.ogv.view.Xform;
import javafx.scene.Group;
import javafx.scene.SubScene;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;

public class CameraController implements Observer {
	
	private static final double MODIFIER = 2;
	private static final double MODIFIER_FACTOR = 0.1;
	private static final double CONTROL_MULTIPLIER = 1;
	private static final double SHIFT_MULTIPLIER = 10;
	private double mousePosX;
	private double mousePosY;
	private double mouseOldX;
	private double mouseOldY;
	private double mouseDeltaX;
	private double mouseDeltaY;
	
	private volatile boolean moveCamera = true;
	
	public void handle2DClassView(SubSceneCamera ssCamera) {
		Xform cameraXform = ssCamera.getCameraXform();
        cameraXform.ry.setAngle(0.0); // 320
        cameraXform.rx.setAngle(90.0); // 40
	}

    public void handleMouse(SubScene3D subScene3D) {
    	SubScene subScene = subScene3D.getSubScene();
    	SubSceneCamera ssCamera = subScene3D.getSubSceneCamera();
        
    	subScene.setOnMousePressed((MouseEvent me) -> {
    		if(!moveCamera) return;
    		
            mousePosX = me.getSceneX();
            mousePosY = me.getSceneY();
            mouseOldX = me.getSceneX();
            mouseOldY = me.getSceneY();
        });
        
        subScene.setOnMouseDragged((MouseEvent me) -> {
        	if(!moveCamera) return;
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
            	cameraXform2.t.setX(cameraXform2.t.getX() + mouseDeltaX * MODIFIER_FACTOR * modifier * 2.0);
            	cameraXform2.t.setY(cameraXform2.t.getY() + mouseDeltaY * MODIFIER_FACTOR * modifier * 2.0);
            } else if (me.isSecondaryButtonDown()) {
            	cameraXform.ry.setAngle(cameraXform.ry.getAngle() - mouseDeltaX * MODIFIER_FACTOR * modifier);
            	cameraXform.rx.setAngle(cameraXform.rx.getAngle() + mouseDeltaY * MODIFIER_FACTOR * modifier);
            } else if (me.isMiddleButtonDown()) {
				double z = ssCamera.getPerspectiveCamera().getTranslateZ();
				double newZ = z - mouseDeltaY * 2 + MODIFIER_FACTOR * modifier;
				ssCamera.getPerspectiveCamera().setTranslateZ(newZ);
            }
        });
        
        subScene.setOnScroll((ScrollEvent se) -> {
        	if(!moveCamera) return;
            double z = ssCamera.getPerspectiveCamera().getTranslateZ();
            double newZ = z + se.getDeltaY();
            ssCamera.getPerspectiveCamera().setTranslateZ(newZ);
        });

    }

    public void handleKeyboard(SubScene3D subScene3D) {
    	SubScene subScene = subScene3D.getSubScene();
    	SubSceneCamera ssCamera = subScene3D.getSubSceneCamera();

    	subScene.setOnKeyPressed((KeyEvent ke) -> {
        	if(!moveCamera) return;
        	
            Xform cameraXform2 = ssCamera.getCameraXform2();
        	
            switch (ke.getCode()) {
                case Z:
                	handle2DClassView(ssCamera);
                    ssCamera.getPerspectiveCamera().setTranslateZ(-SubSceneCamera.CAMERA_DISTANCE);
                    cameraXform2.t.setX(0.0);
                    cameraXform2.t.setY(0.0);
                    break;
                case X:
                	Group axisGroup = subScene3D.getAxis().getAxisGroup();
                	if (axisGroup.isVisible()) {
                		axisGroup.setVisible(false);
                    } else {
                    	axisGroup.setVisible(true);
                    }
                    break;
                case UP:
                    double oldY_UP = ssCamera.getPerspectiveCamera().getTranslateY();
                    double newY_UP = oldY_UP + MODIFIER * 7;
                    ssCamera.getPerspectiveCamera().setTranslateY(newY_UP);
                    break;
                case DOWN:
                	double oldY_DOWN = ssCamera.getPerspectiveCamera().getTranslateY();
                    double newY_DOWN = oldY_DOWN - MODIFIER * 7;
                    ssCamera.getPerspectiveCamera().setTranslateY(newY_DOWN);
                    break;
                case RIGHT:
                	double oldX_RIGHT = ssCamera.getPerspectiveCamera().getTranslateX();
                    double newX_RIGHT = oldX_RIGHT - MODIFIER * 7;
                    ssCamera.getPerspectiveCamera().setTranslateX(newX_RIGHT);
                    break;
                case LEFT:
                	double oldX_LEFT = ssCamera.getPerspectiveCamera().getTranslateX();
                    double newX_LEFT = oldX_LEFT + MODIFIER * 7;
                    ssCamera.getPerspectiveCamera().setTranslateX(newX_LEFT);
                    break;
                default:
                	break;
            }
        });
    }

	@Override
	public void update(Observable o, Object arg) {
		if(o instanceof PaneBox3DController) {
			PaneBox3DController paneBox3DController = (PaneBox3DController) o;
			moveCamera = !paneBox3DController.editInProgress();
		}
	}
}
