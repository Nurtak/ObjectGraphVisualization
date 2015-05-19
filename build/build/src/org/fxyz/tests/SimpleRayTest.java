/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fxyz.tests;

import javafx.animation.Interpolator;
import javafx.animation.PauseTransition;
import javafx.animation.Transition;
import javafx.application.Application;
import javafx.geometry.Point3D;
import javafx.scene.AmbientLight;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.PointLight;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Sphere;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.fxyz.cameras.CameraTransformer;
import org.fxyz.geometry.Ray;

/**
 * A simple app Showing the newly added Ray class.
 * <br><p>Clicking on the Scene will spawn a Sphere at that position (origin of Ray)<br>
 * Mouse buttons target different nodes Targets are breifly highlighted if an intersection occurs.
 * 
 * Hold Control down to Allow Camera movement without firing.
 * 
 * <br><br>
 * RayTest in org.fxyz.tests package has reference on TriangleMesh intersections
 *
 * </p><br>
 *
 * @author Jason Pollastrini aka jdub1581
 */
public class SimpleRayTest extends Application {

    private final StackPane sceneRoot = new StackPane();
    private final PhongMaterial 
            red = new PhongMaterial(Color.DARKRED),
            blue = new PhongMaterial(Color.DARKCYAN),
            highlight = new PhongMaterial(Color.CHARTREUSE);
    private final Group root = new Group();
    private Sphere target1, target2;
    private PerspectiveCamera camera;
    private final CameraTransformer cameraTransform = new CameraTransformer();
    private double mousePosX;
    private double mousePosY;
    private double mouseOldX;
    private double mouseOldY;
    private double mouseDeltaX;
    private double mouseDeltaY;
    private boolean fireRay = true;
    private final AmbientLight rayLight = new AmbientLight();

    @Override
    public void start(Stage primaryStage) throws Exception {
        // add cameraTransform so it doesn't affect all nodes
        rayLight.getScope().add(cameraTransform);
        
        camera = new PerspectiveCamera(true);
        cameraTransform.setTranslate(0, 0, 0);
        cameraTransform.getChildren().addAll(camera);
        camera.setNearClip(0.1);
        camera.setFarClip(1000000.0);
        camera.setFieldOfView(42);
        camera.setTranslateZ(-5000);

        PointLight light = new PointLight(Color.GAINSBORO);
        PointLight light2 = new PointLight(Color.YELLOW);
        light2.setTranslateY(-2000);
        //create a target
        target1 = new Sphere(100);
        target1.setTranslateX(300);
        target1.setTranslateY(300);
        target1.setTranslateZ(1000);
        target1.setMaterial(red);
        // create another target
        target2 = new Sphere(100);
        target2.setTranslateX(800);
        target2.setTranslateY(-1200);
        target2.setTranslateZ(-500);
        target2.setMaterial(blue);

        root.getChildren().addAll(cameraTransform, target1, target2, light, light2, rayLight);
        root.setAutoSizeChildren(false);
        
        Scene scene = new Scene((root), 1200, 800, true, SceneAntialiasing.BALANCED);
        scene.setCamera(camera);
        
        Stop[] stops = new Stop[]{new Stop(0, Color.BLACK), new Stop(0.5, Color.DEEPSKYBLUE.darker()), new Stop(1.0, Color.BLACK)};
        LinearGradient lg = new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, stops);
        
        scene.setFill(lg);

        //First person shooter keyboard movement
        scene.setOnKeyPressed(ke -> {
            double change = 10.0;
            //Add shift modifier to simulate "Running Speed"
            if (ke.isShiftDown()) {
                change = 50.0;
            }
            //What key did the user press?
            KeyCode keycode = ke.getCode();
            //Step 2c: Add Zoom controls
            if (keycode == KeyCode.W) {
                camera.setTranslateZ(camera.getTranslateZ() + change);
            }
            if (keycode == KeyCode.S) {
                camera.setTranslateZ(camera.getTranslateZ() - change);
            }
            //Step 2d: Add Strafe controls
            if (keycode == KeyCode.A) {
                camera.setTranslateX(camera.getTranslateX() - change);
            }
            if (keycode == KeyCode.D) {
                camera.setTranslateX(camera.getTranslateX() + change);
            }

            // add a flag so we can still move the camera
            if (keycode == KeyCode.CONTROL) {
                fireRay = false;
            }

        });

        scene.setOnKeyReleased(ke -> {
            // release flag
            if (ke.getCode().equals(KeyCode.CONTROL)) {
                fireRay = true;
            }
        });

        scene.setOnMousePressed(e -> {
            mousePosX = e.getSceneX();
            mousePosY = e.getSceneY();
            mouseOldX = e.getSceneX();
            mouseOldY = e.getSceneY();

            if (fireRay) {
                // use PickResult because it is already transformed 
                Point3D o = e.getPickResult().getIntersectedPoint();
                if (e.isPrimaryButtonDown()) {
                    // set Target and Direction
                    Point3D t = Point3D.ZERO.add(target2.getTranslateX(), target2.getTranslateY(), target2.getTranslateZ()),
                            d = t.subtract(o);
                    //Build the Ray
                    Ray r = new Ray(o, d);
                    double dist = t.distance(o);
                    // If ray intersects node, spawn and animate
                    if (target2.getBoundsInParent().contains(r.project(dist))) {
                        animateRayTo(r, target2, Duration.seconds(2));
                        System.out.println(
                                "Target Contains Ray!\n"
                                + r + "\nTarget Bounds: " + target2.getBoundsInParent()
                                + "\nDistance: " + dist + "\n"
                        );
                    }
                   
                    e.consume();
                } // repeat for other target as well
                else if (e.isSecondaryButtonDown()) {
                    Point3D tgt = Point3D.ZERO.add(target1.getTranslateX(), target1.getTranslateY(), target1.getTranslateZ()),
                            dir = tgt.subtract(o);

                    Ray r = new Ray(o, dir);
                    double dist = tgt.distance(o);
                    if (target1.getBoundsInParent().contains(r.project(dist))) {
                        animateRayTo(r, target1, Duration.seconds(2));

                        System.out.println(
                                "Target Contains Ray: "
                                + target1.getBoundsInParent().contains(r.project(dist)) + "\n"
                                + r + "\nTarget Bounds: " + target1.getBoundsInParent()
                                + "\nDistance: " + dist + "\n"
                        );
                    }
                    e.consume();
                }
            }
        });
        scene.setOnMouseDragged(e -> {
            if (!fireRay) {
                mouseOldX = mousePosX;
                mouseOldY = mousePosY;
                mousePosX = e.getSceneX();
                mousePosY = e.getSceneY();
                mouseDeltaX = (mousePosX - mouseOldX);
                mouseDeltaY = (mousePosY - mouseOldY);

                double modifier = 10.0;
                double modifierFactor = 0.1;

                if (e.isControlDown()) {
                    modifier = 0.1;
                }
                if (e.isShiftDown()) {
                    modifier = 50.0;
                }
                if (e.isPrimaryButtonDown()) {
                    cameraTransform.ry.setAngle(((cameraTransform.ry.getAngle() + mouseDeltaX * modifierFactor * modifier * 2.0) % 360 + 540) % 360 - 180); // +
                    cameraTransform.rx.setAngle(((cameraTransform.rx.getAngle() - mouseDeltaY * modifierFactor * modifier * 2.0) % 360 + 540) % 360 - 180); // - 

                } else if (e.isSecondaryButtonDown()) {
                    double z = camera.getTranslateZ();
                    double newZ = z + mouseDeltaX * modifierFactor * modifier;
                    camera.setTranslateZ(newZ);
                } else if (e.isMiddleButtonDown()) {
                    cameraTransform.t.setX(cameraTransform.t.getX() + mouseDeltaX * modifierFactor * modifier * 0.3); // -
                    cameraTransform.t.setY(cameraTransform.t.getY() + mouseDeltaY * modifierFactor * modifier * 0.3); // -
                }
            }
        });

        primaryStage.setTitle("Hello Ray! Animated Visual of a Ray casting");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     *  Creates and launches a custom Transition animation
     * 
     * @param r The Ray that holds the info
     * @param tx to x
     * @param ty to y
     * @param tz to z
     * @param dps distance per step to move ray
     * @param time length of animation
     */
    private void animateRayTo(final Ray r, final Sphere target, final Duration time) {

        final Transition t = new Transition() {
            protected Ray ray;
            protected Sphere s;
            protected double dist;

            {
                this.ray = r;

                this.s = new Sphere(5);
                s.setTranslateX((ray.getOrigin()).getX());
                s.setTranslateY((ray.getOrigin()).getY());
                s.setTranslateZ((ray.getOrigin()).getZ());
                s.setMaterial(highlight);
                rayLight.getScope().add(s);
                this.dist = ray.getOrigin().distance(
                        Point3D.ZERO.add(target.getTranslateX(), target.getTranslateY(), target.getTranslateZ())
                );

                setCycleDuration(time);
                this.setInterpolator(Interpolator.LINEAR);
                this.setOnFinished(e -> {
                    if (target.getBoundsInParent().contains(ray.getPosition())) {
                        target.setMaterial(highlight);
                        // PauseTransition for delay
                        PauseTransition t = new PauseTransition(Duration.millis(750));
                        t.setOnFinished(pe -> {
                            reset();
                            root.getChildren().removeAll(s);
                            s = null;
                        });
                        t.playFromStart();
                    }
                });
                root.getChildren().add(s);
            }

            @Override
            protected void interpolate(double frac) {
                // frac-> 0.0 - 1.0 
                // project ray 
                ray.project(dist * frac);
                // set the sphere to ray position
                s.setTranslateX(ray.getPosition().getX());
                s.setTranslateY(ray.getPosition().getY());
                s.setTranslateZ(ray.getPosition().getZ());
            }

        };
        t.playFromStart();
    }

    // resets materisl on targets
    private void reset() {
        target1.setMaterial(red);
        target2.setMaterial(blue);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
