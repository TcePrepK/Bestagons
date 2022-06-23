package toolbox;

import core.Mouse;
import game.Camera;

import static core.GlobalVariables.chunkManager;

public class MousePicker {
    private static final int RECURSION_COUNT = 200;
    private static final float RAY_RANGE = 600;

    private Vector3D currentRay;

    private final Camera camera;

    private Vector3D currentWorldPoint;

    public MousePicker(final Camera camera) {
        this.camera = camera;
    }

    public Vector3D getCurrentRay() {
        return currentRay;
    }

    public void update() {
        currentRay = calculateMouseRay();
        if (intersectionInRange(0, MousePicker.RAY_RANGE, currentRay)) {
            currentWorldPoint = binarySearch(0, 0, MousePicker.RAY_RANGE, currentRay);
        } else {
            currentWorldPoint = null;
        }
    }

    private Vector3D calculateMouseRay() {
        return camera.getRay(new Vector2D(Mouse.getPosition()));
    }

    private Vector3D getPointOnRay(final Vector3D ray, final float distance) {
        final Vector3D camPos = new Vector3D(camera.getPosition().x, camera.getPosition().y, 10);
        final Vector3D start = new Vector3D(camPos.x, camPos.y, camPos.z);
        final Vector3D scaledRay = new Vector3D(ray.x * distance, ray.y * distance, ray.z * distance);

        return start.add(scaledRay);
    }

    private Vector3D binarySearch(final int count, final float start, final float finish, final Vector3D ray) {
        final float half = start + ((finish - start) / 2);
        if (count >= MousePicker.RECURSION_COUNT) {
            final Vector3D endPoint = getPointOnRay(ray, half);
            if (chunkManager.inBounds((int) Math.floor(endPoint.x), (int) Math.floor(endPoint.z))) {
                return endPoint;
            } else {
                return null;
            }
        }
        if (intersectionInRange(start, half, ray)) {
            return binarySearch(count + 1, start, half, ray);
        } else {
            return binarySearch(count + 1, half, finish, ray);
        }
    }

    private boolean intersectionInRange(final float start, final float finish, final Vector3D ray) {
        final Vector3D startPoint = getPointOnRay(ray, start);
        final Vector3D endPoint = getPointOnRay(ray, finish);
        return !MousePicker.isUnderGround(startPoint) && MousePicker.isUnderGround(endPoint);
    }

    private static boolean isUnderGround(final Vector3D testPoint) {
        return testPoint.y < 0;
    }

    public Vector3D getCurrentWorldPoint() {
        return currentWorldPoint;
    }
}
