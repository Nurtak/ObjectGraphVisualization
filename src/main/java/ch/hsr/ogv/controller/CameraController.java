package ch.hsr.ogv.controller;

import ch.hsr.ogv.view.SubSceneAdapter;
import ch.hsr.ogv.view.SubSceneCamera;
import ch.hsr.ogv.view.Xform;

import java.util.Observable;
import java.util.Observer;

public class CameraController implements Observer {

    private RotationCamera rotationCamera = new RotationCamera();

    private CameraBase baseCamera = rotationCamera;

    public void setLockedTopView(boolean lockedTopView) {
        this.rotationCamera.setLockedTopView(lockedTopView);
    }

    public void setMoveCamera(boolean moveCamera) {
        this.rotationCamera.setMoveCamera(moveCamera);
    }

    public CameraBase getBaseCamera() {
        return baseCamera;
    }

    public void handleCenterView(SubSceneCamera ssCamera) {
        Xform cameraXform = ssCamera.getCameraXform();
        Xform cameraXform2 = ssCamera.getCameraXform2();
        cameraXform.ry.setAngle(0.0);
        cameraXform.rx.setAngle(90.0);
        cameraXform2.t.setX(0.0);
        cameraXform2.t.setY(0.0);
        ssCamera.get().setTranslateZ(-SubSceneCamera.CAMERA_DISTANCE);
    }

    public void handleLockedTopView(SubSceneCamera ssCamera, boolean isLockedTopView) {
        Xform cameraXform = ssCamera.getCameraXform();
        if (isLockedTopView) {
            cameraXform.ry.setAngle(0.0);
            cameraXform.rx.setAngle(90.0);
        }
        else {
            cameraXform.ry.setAngle(320.0);
            cameraXform.rx.setAngle(40.0);
        }
        setLockedTopView(isLockedTopView);
    }

    public void enableCamera(SubSceneAdapter subSceneAdapter) {
        baseCamera.handleMouse(subSceneAdapter);
        baseCamera.handleKeyboard(subSceneAdapter);
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof DragController) {
            DragController dragController = (DragController) o;
            setMoveCamera(!dragController.isDragInProgress());
        }
    }

}
