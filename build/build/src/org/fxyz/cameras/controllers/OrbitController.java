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

/**
 *
 * @author Dub
 */
public class OrbitController extends CameraController{

    public OrbitController() {
        super(true, AnimationPreference.TRANSITION);
    }

    @Override
    protected void update() {
    }

    @Override
    protected void handleKeyEvent(KeyEvent event, boolean handle) {
    }

    @Override
    protected void handlePrimaryMouseDrag(MouseEvent event, Point2D dragDelta, double modifier) {
    }

    @Override
    protected void handleMiddleMouseDrag(MouseEvent event, Point2D dragDelta, double modifier) {
    }

    @Override
    protected void handleSecondaryMouseDrag(MouseEvent event, Point2D dragDelta, double modifier) {
    }

    @Override
    protected void handlePrimaryMouseClick(MouseEvent e) {
    }

    @Override
    protected void handleSecondaryMouseClick(MouseEvent e) {
    }

    @Override
    protected void handleMiddleMouseClick(MouseEvent e) {
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
    protected void handleMouseMoved(MouseEvent event, Point2D moveDelta, double modifier) {
    }

    @Override
    protected void handleScrollEvent(ScrollEvent event) {
    }

    @Override
    protected double getSpeedModifier(KeyEvent event) {
        return 0;
    }

    @Override
    public Node getTransformableNode() {
        return null;
    }

    @Override
    protected void updateTransition(double now) {
    }
    
}
