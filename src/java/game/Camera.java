package game;

import core.GlobalVariables;
import core.Keyboard;
import core.Mouse;
import core.Signal;
import display.DisplayManager;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import toolbox.Points.Point2D;
import toolbox.Vector2D;
import toolbox.Vector3D;

import static core.GlobalVariables.camera;
import static display.DisplayManager.*;

public class Camera {
    public static final float FOV = 90;
    public static final float NEAR_PLANE = 1;
    public static final float FAR_PLANE = 5000;

    public static float CAMERA_PITCH = -45;
    public static float CAMERA_ROLL = 0;
    public static float SPEED = 25;

    public static float zoom = 50;
    public static float desiredZoom = 50;

    private final Signal matrixWatcher = new Signal();

    private final Matrix4f viewMatrix = new Matrix4f();
    private final Matrix4f projectionMatrix = new Matrix4f();
    private final Matrix4f projectionViewMatrix = new Matrix4f();

    private final float cameraAspectRatio = WIDTH / (float) HEIGHT;
    private final float viewportWidth = 2f * (float) Math.tan(Math.toRadians(Camera.FOV / 2f));
    private final float viewportHeight = viewportWidth / cameraAspectRatio;

    private Vector3D topLeftCorner;
    private Vector3D xIncrement;
    private Vector3D yIncrement;

    private Vector2D position = new Vector2D();
    private final Vector2D velocity = new Vector2D();

    public Camera() {
        createProjectionMatrix();

//        Mouse.mouseMiddleMove.add(this::calculateZoom);
//        Mouse.mouseRightDown.add(this::calculatePitch);

//        matrixWatcher.add(() -> {
//            renderer.loadCameraVariablesNextFrame();
//        });

        screenSizeChange.add(this::screenResize);
    }

    public void update() {
        Camera.updateZoom();

        checkInputs();
        updatePosition();

        calculateMatrices();
        calculateRayVariables();
    }

    private void updatePosition() {
        final float dt = DisplayManager.getDelta();

        position = position.add(velocity.mult(dt));
    }

    private static void updateZoom() {
        final float dt = DisplayManager.getDelta();
        final float dz = Camera.desiredZoom - Camera.zoom;

        Camera.zoom = Camera.zoom + dz * dt;
    }

    public Point2D getCameraTile() {
        final float scale = GlobalVariables.hexagonScale;
        final Vector2D tilePosition = position.clone().add(0, (float) Math.tan(Math.toRadians(Camera.CAMERA_PITCH)) * Camera.zoom).div((float) Math.sin(Math.toRadians(60)), 3 / 4f);
        tilePosition.x += (((int) Math.abs(Math.floor(tilePosition.y / scale))) % 2) * scale / 2;
        final Vector2D scaledPosition = tilePosition.div(scale);

        float modX = Math.abs(scaledPosition.x % 1);
        float modY = Math.abs(scaledPosition.y % 1);

        if (scaledPosition.y >= 0) {
            modY = 1 - modY;
        }
        if (scaledPosition.x <= 0) {
            modX = 1 - modX;
        }

        if (modY > 2 / 3f) {
            modY = (modY - 2 / 3f) * 3;
            if (modY > 1 - 2 * Math.abs(modX - 0.5f)) {
                if (Math.abs(Math.floor(scaledPosition.y + 1)) % 2 == 1) {
                    scaledPosition.x++;
                }

                if (modX <= 0.5f) {
                    scaledPosition.x--;
                }

                scaledPosition.y--;
            }
        }

        return scaledPosition.toPoint2D();
    }

    public static Point2D getMouseTile() {
        return Camera.getTileAt(new Vector2D(Mouse.getPosition()));
    }

    public static Point2D getTileAt(final Vector2D position) {
        final float scale = GlobalVariables.hexagonScale;
        final Vector3D ray = camera.getRay(position);

        final float time = Camera.zoom / ray.z;
        final Vector2D rayPosition = new Vector2D(-ray.x, ray.y).mult(time).add(camera.position).div((float) Math.sin(Math.toRadians(60)), 3 / 4f);
        rayPosition.x += (((int) Math.abs(Math.floor(rayPosition.y / scale))) % 2) * scale / 2;
        final Vector2D scaledPosition = rayPosition.div(scale);

        float modX = Math.abs(scaledPosition.x % 1);
        float modY = Math.abs(scaledPosition.y % 1);

        if (scaledPosition.y >= 0) {
            modY = 1 - modY;
        }
        if (scaledPosition.x <= 0) {
            modX = 1 - modX;
        }

        if (modY > 2 / 3f) {
            modY = (modY - 2 / 3f) * 3;
            if (modY > 1 - 2 * Math.abs(modX - 0.5f)) {
                if (Math.abs(Math.floor(scaledPosition.y + 1)) % 2 == 1) {
                    scaledPosition.x++;
                }

                if (modX <= 0.5f) {
                    scaledPosition.x--;
                }

                scaledPosition.y--;
            }
        }

        return scaledPosition.toPoint2D();
    }

    private void calculateRayVariables() {
//        final Vector3D cameraDirection = new Vector3D(0, 0, 1);

        final Vector3D cameraDirection = new Vector3D(0, 0, -1)
                .rotateX((float) Math.toRadians(Camera.CAMERA_PITCH))
                .rotateZ((float) Math.toRadians(-Camera.CAMERA_ROLL));
        final Vector3D camRightVector = new Vector3D(viewMatrix.m00(), viewMatrix.m10(), viewMatrix.m20());
        final Vector3D camUpVector = new Vector3D(viewMatrix.m01(), viewMatrix.m11(), viewMatrix.m21());

        topLeftCorner = cameraDirection.sub(camRightVector).mult(viewportWidth / 2).add(camUpVector.mult(viewportHeight / 2));
        xIncrement = camRightVector.mult(viewportWidth).div(WIDTH);
        yIncrement = camUpVector.mult(-viewportHeight).div(HEIGHT);
    }

    public Vector3D getRay(final Vector2D p) {
        return topLeftCorner.add(xIncrement.mult(p.x)).add(yIncrement.mult(p.y)).normalize();
    }

    private void checkInputs() {
        velocity.set(0, 0);
//        final float scale = Keyboard.isKeyDown(Keyboard.LSHIFT) ? 5 : 1;
        final float scale = Keyboard.isKeyDown("C") ? 5 : 1;

        if (Keyboard.isKeyDown("W")) {
            velocity.y = -Camera.SPEED * scale;
        } else if (Keyboard.isKeyDown("S")) {
            velocity.y = Camera.SPEED * scale;
        }

        if (Keyboard.isKeyDown("A")) {
            velocity.x = -Camera.SPEED * scale;
        } else if (Keyboard.isKeyDown("D")) {
            velocity.x = Camera.SPEED * scale;
        }

        if (Keyboard.isKeyDown("Q")) {
            CAMERA_ROLL += -0.1;
        } else if (Keyboard.isKeyDown("E")) {
            CAMERA_ROLL += 0.1;
        }

        final float off = 0.01f;
        if (Keyboard.isKeyDown(Keyboard.SPACE)) {
            Camera.desiredZoom *= 1 + off;
            Camera.zoom *= 1 + off;
            Camera.SPEED *= 1 + off;
        } else if (Keyboard.isKeyDown(Keyboard.LSHIFT)) {
            Camera.desiredZoom *= 1 - off;
            Camera.zoom *= 1 - off;
            Camera.SPEED *= 1 - off;
        }
    }

    private void screenResize() {
        createProjectionMatrix();
    }

    private void calculateMatrices() {
        final Matrix4f oldViewMatrix = new Matrix4f(viewMatrix);

        // ViewMatrix
        viewMatrix.identity();
        viewMatrix.rotate((float) Math.toRadians(CAMERA_PITCH), new Vector3f(1, 0, 0));
//        viewMatrix.rotate((float) Math.toRadians(yaw), new Vector3f(0, 1, 0));
        viewMatrix.rotate((float) Math.toRadians(CAMERA_ROLL), new Vector3f(0, 0, 1));
        viewMatrix.translate(-position.x, position.y, -Camera.zoom);

        // ProjectionViewMatrix
        projectionMatrix.mul(viewMatrix, projectionViewMatrix);

        if (!viewMatrix.equals(oldViewMatrix)) {
            matrixWatcher.dispatch();
        }
    }

    private void createProjectionMatrix() {
        final float aspectRatio = (float) DisplayManager.WIDTH / DisplayManager.HEIGHT;
        final float y_scale = (float) ((1f / Math.tan(Math.toRadians(Camera.FOV / 2f))) * aspectRatio);
        final float x_scale = y_scale / aspectRatio;
        final float frustum_length = Camera.FAR_PLANE - Camera.NEAR_PLANE;

        projectionMatrix.m00(x_scale);
        projectionMatrix.m11(y_scale);
        projectionMatrix.m22(-((Camera.FAR_PLANE + Camera.NEAR_PLANE) / frustum_length));
        projectionMatrix.m23(-1);
        projectionMatrix.m32(-((2 * Camera.NEAR_PLANE * Camera.FAR_PLANE) / frustum_length));
        projectionMatrix.m33(0);
    }

    public Vector2D getPosition() {
        return position;
    }

    public Matrix4f getViewMatrix() {
        return viewMatrix;
    }

    public Matrix4f getProjectionMatrix() {
        return projectionMatrix;
    }

    public Matrix4f getProjectionViewMatrix() {
        return projectionViewMatrix;
    }
}
