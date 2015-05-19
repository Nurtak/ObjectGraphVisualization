/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fxyz.cameras.controllers;

import org.fxyz.utils.AnimationPreference;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import org.fxyz.utils.MathUtils;

/**
 *
 * @author Dub
 */
public class FPSController extends CameraController {

    private boolean fwd, strafeL, strafeR, back, up, down, shift, mouseLookEnabled;
    private double speed = 1.0;
    private final double maxSpeed = 5.0, minSpeed = 1.0;
    

    public FPSController() {
        super(true, AnimationPreference.TIMER);        
    }

    @Override
    public void update() {

        if (fwd && !back) {
            moveForward();
        }
        if (strafeL) {
            strafeLeft();
        }
        if (strafeR) {
            strafeRight();
        }
        if (back && !fwd) {
            moveBack();
        }
        if (up && !down) {
            moveUp();
        }
        if (down && !up) {
            moveDown();
        }

    }

    @Override
    public void handleKeyEvent(KeyEvent event, boolean handle) {
        if (event.getEventType() == KeyEvent.KEY_PRESSED) {
            switch (event.getCode()) {
                case W:
                    fwd = true;
                    break;
                case S:
                    back = true;
                    break;
                case A:
                    strafeL = true;
                    break;
                case D:
                    strafeR = true;
                    break;
                case SHIFT:
                    shift = true;
                    if(up || down){
                        break;
                    }
                    speed = maxSpeed;                    
                    break;
                case SPACE:
                    if (!shift) {
                        up = true;
                    } else if (shift) {
                        down = true;
                    }
                    break;
            }
        } else if (event.getEventType() == KeyEvent.KEY_RELEASED) {
            switch (event.getCode()) {
                case W:
                    fwd = false;
                    break;
                case S:
                    back = false;
                    break;
                case A:
                    strafeL = false;
                    break;
                case D:
                    strafeR = false;
                    break;
                case SHIFT:
                    speed = minSpeed;
                    shift = false;
                    break;
                case SPACE:
                    up = false;
                    down = false;
                    break;
            }
        }
    }

    @Override
    protected void handlePrimaryMouseDrag(MouseEvent event, Point2D dragDelta, double modifier) {
        if (!mouseLookEnabled) {
            t.setX(getPosition().getX());
            t.setY(getPosition().getY());
            t.setZ(getPosition().getZ());
            
            affine.setToIdentity();
            
            rotateY.setAngle(
                    MathUtils.clamp(((rotateY.getAngle() + dragDelta.getX() * (1.0 * 0.25)) % 360 + 540) % 360 - 180, -360, 360)
            ); // horizontal                
            rotateX.setAngle(
                    MathUtils.clamp(((rotateX.getAngle() - dragDelta.getY() * (1.0 * 0.25)) % 360 + 540) % 360 - 180, -90, 90)
            ); // vertical
            
            affine.prepend(t.createConcatenation(rotateY.createConcatenation(rotateX)));
        }     
    }

    @Override
    protected void handleMiddleMouseDrag(MouseEvent event, Point2D dragDelta, double modifier) {
        // do nothing for now        
    }

    @Override
    protected void handleSecondaryMouseDrag(MouseEvent event, Point2D dragDelta, double modifier) {
        // do nothing for now
    }

    @Override
    protected void handleMouseMoved(MouseEvent event, Point2D moveDelta, double speed) {
        if (mouseLookEnabled) {
            t.setX(getPosition().getX());
            t.setY(getPosition().getY());
            t.setZ(getPosition().getZ());
            
            affine.setToIdentity();
            
            rotateY.setAngle(
                    MathUtils.clamp(((rotateY.getAngle() + moveDelta.getX() * (speed * 0.05)) % 360 + 540) % 360 - 180, -360, 360)
            ); // horizontal                
            rotateX.setAngle(
                    MathUtils.clamp(((rotateX.getAngle() - moveDelta.getY() * (speed * 0.05)) % 360 + 540) % 360 - 180, -90, 90)
            ); // vertical
            
            affine.prepend(t.createConcatenation(rotateY.createConcatenation(rotateX)));
            
        }
    }

    

    @Override
    protected void handleScrollEvent(ScrollEvent event) {
        //do nothing for now, use for Zoom?
    }

    @Override
    protected double getSpeedModifier(KeyEvent event) {
        return speed;
    }

    @Override
    public Node getTransformableNode() {
        if (getCamera() != null) {
            return getCamera();
        } else {
            throw new UnsupportedOperationException("Must have a Camera");
        }
    }

    private void moveForward() {      
        affine.setTx(getPosition().getX() + speed * getForwardMatrixRow().x);
        affine.setTy(getPosition().getY() + speed * getForwardMatrixRow().y);
        affine.setTz(getPosition().getZ() + speed * getForwardMatrixRow().z);
    }

    private void strafeLeft() {
        affine.setTx(getPosition().getX() + speed * -getRightMatrixRow().x);
        affine.setTy(getPosition().getY() + speed * -getRightMatrixRow().y);
        affine.setTz(getPosition().getZ() + speed * -getRightMatrixRow().z);
    }

    private void strafeRight() {
        affine.setTx(getPosition().getX() + speed * getRightMatrixRow().x);
        affine.setTy(getPosition().getY() + speed * getRightMatrixRow().y);
        affine.setTz(getPosition().getZ() + speed * getRightMatrixRow().z);
    }

    private void moveBack() {
        affine.setTx(getPosition().getX() + speed * -getForwardMatrixRow().x);
        affine.setTy(getPosition().getY() + speed * -getForwardMatrixRow().y);
        affine.setTz(getPosition().getZ() + speed * -getForwardMatrixRow().z);
    }

    private void moveUp() {
        affine.setTx(getPosition().getX() + speed * -getUpMatrixRow().x);
        affine.setTy(getPosition().getY() + speed * -getUpMatrixRow().y);
        affine.setTz(getPosition().getZ() + speed * -getUpMatrixRow().z);
    }

    private void moveDown() {
        affine.setTx(getPosition().getX() + speed * getUpMatrixRow().x);
        affine.setTy(getPosition().getY() + speed * getUpMatrixRow().y);
        affine.setTz(getPosition().getZ() + speed * getUpMatrixRow().z);
    }

    public void setMouseLookEnabled(boolean b) {
        mouseLookEnabled = b;
    }

    @Override
    protected void handlePrimaryMouseClick(MouseEvent t) {
        //System.out.println("Primary Button Clicked!");
    }

    @Override
    protected void handleMiddleMouseClick(MouseEvent t) {
        //System.out.println("Middle Button Clicked!");
    }

    @Override
    protected void handleSecondaryMouseClick(MouseEvent t) {
        //System.out.println("Secondary Button Clicked!");
    }

    @Override
    protected void handlePrimaryMousePress(MouseEvent e) {
        
    }

    @Override
    protected void handleSecondaryMousePress(MouseEvent e) {
       
    }

    @Override
    protected void handleMiddleMousePress(MouseEvent e) {
        
    }

    @Override
    protected void updateTransition(double now) {
        
    }

}
