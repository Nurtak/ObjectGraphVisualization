package ch.hsr.ogv.controller;

import ch.hsr.ogv.view.SubSceneAdapter;

/**
 * 
 * @author Simon Gwerder
 * @version OGV 3.1, May 2015
 *
 */
public abstract class CameraBase {

	protected static final double MODIFIER = 2;
	protected static final double MODIFIER_FACTOR = 0.1;
	protected static final double CONTROL_MULTIPLIER = 1;
	protected static final double SHIFT_MULTIPLIER = 10;
	protected double mousePosX;
	protected double mousePosY;
	protected double mouseOldX;
	protected double mouseOldY;
	protected double mouseDeltaX;
	protected double mouseDeltaY;

	protected volatile boolean moveCamera = true;
	protected volatile boolean lockedTopView = false;

	public void setMoveCamera(boolean moveCamera) {
		this.moveCamera = moveCamera;
	}
	
	public void setLockedTopView(boolean lockedTopView) {
		this.lockedTopView = lockedTopView;
	}
	
	protected abstract void handleMouse(SubSceneAdapter subSceneAdapter);
	protected abstract void handleKeyboard(SubSceneAdapter subSceneAdapter);

}
