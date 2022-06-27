package models;

import toolbox.Vector3D;

public class CubeModel {
    public static Vector3D[] positions = new Vector3D[]{
            new Vector3D(-0.5f, -0.5f, 0.5f),
            new Vector3D(0.5f, -0.5f, 0.5f),
            new Vector3D(-0.5f, 0.5f, 0.5f),
            new Vector3D(0.5f, 0.5f, 0.5f),
            new Vector3D(-0.5f, -0.5f, -0.5f),
            new Vector3D(0.5f, -0.5f, -0.5f),
            new Vector3D(-0.5f, 0.5f, -0.5f),
            new Vector3D(0.5f, 0.5f, -0.5f),
    };

    public static int[] indices = new int[]{
            0, 1, 2, 2, 3, 1, // Front
            4, 5, 6, 6, 7, 5, // Back
            0, 2, 4, 4, 6, 2, // Left
            1, 3, 5, 5, 7, 3, // Right
            0, 1, 5, 5, 4, 0, // Top
            2, 3, 6, 6, 7, 3  // Bottom
    };

    public static Vector3D[] calculatePositions(final float w, final float h, final float d) {
        final Vector3D[] scaledPositions = new Vector3D[positions.length];

        for (int i = 0; i < positions.length; i++) {
            scaledPositions[i] = positions[i].mult(w, h, d);
        }

        return scaledPositions;
    }
}
