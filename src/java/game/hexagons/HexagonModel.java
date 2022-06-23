package game.hexagons;

import core.GlobalVariables;
import core.RawModel;
import toolbox.Vector2D;

public class HexagonModel {
    static public RawModel model;

    static public final Vector2D[] hexagon = new Vector2D[]{
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

    public static void init() {
        final float[] positions = new float[6 * 2];

        for (int i = 0; i < 6; i++) {
            final Vector2D corner = HexagonModel.hexagon[i];
            positions[2 * i] = corner.x;
            positions[2 * i + 1] = corner.y;
        }

        HexagonModel.model = GlobalVariables.loader.loadToVAO(positions, HexagonModel.indices, 2);
    }
}
