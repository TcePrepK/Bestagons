package models;

import toolbox.Vector2D;

public class HexagonModel {
    static public final Vector2D[] positions = new Vector2D[]{
            new Vector2D(0.866f, 0.25f),
            new Vector2D(0.433f, 0),
            new Vector2D(0, 0.25f),
            new Vector2D(0, 0.75f),
            new Vector2D(0.433f, 1f),
            new Vector2D(0.866f, 0.75f),
    };

    static public final int[] indices = new int[]{
            0, 1, 2, 5, 4, 3,
            2, 0, 3, 3, 0, 5
    };
}
