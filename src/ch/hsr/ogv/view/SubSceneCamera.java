package ch.hsr.ogv.view;

import javafx.scene.PerspectiveCamera;

/**
 * 
 * @author Simon Gwerder
 *
 */
public class SubSceneCamera {

	private final PerspectiveCamera camera = new PerspectiveCamera(true);
	private final Xform cameraXform = new Xform();
	private final Xform cameraXform2 = new Xform();
	private final Xform cameraXform3 = new Xform();
	public static final int CAMERA_DISTANCE = 1500;
	
	public PerspectiveCamera get() {
		return camera;
	}
	
	public Xform getCameraXform() {
		return cameraXform;
	}

	public Xform getCameraXform2() {
		return cameraXform2;
	}

	public Xform getCameraXform3() {
		return cameraXform3;
	}
	
	public SubSceneCamera() {
        cameraXform.getChildren().add(cameraXform2);
        cameraXform2.getChildren().add(cameraXform3);
        cameraXform3.getChildren().add(camera);
        cameraXform3.setRotateZ(180.0);
        
        camera.setNearClip(1); // 0.1
        camera.setFarClip(10000.0);
        camera.setTranslateZ(-CAMERA_DISTANCE);
        cameraXform.ry.setAngle(0.0); // 320
        cameraXform.rx.setAngle(90.0); // 40
	}
	
}
