package game.hexagons;

import core.GlobalVariables;
import core.RawModel;
import toolbox.Vector2D;

public class HexagonModel {
    static public RawModel model;

    public static void init() {
        final float[] positions = new float[6 * 2];

        for (int i = 0; i < 6; i++) {
            final Vector2D corner = models.HexagonModel.positions[i];
            positions[2 * i] = corner.x;
            positions[2 * i + 1] = corner.y;
        }

        game.hexagons.HexagonModel.model = GlobalVariables.loader.loadToVAO(positions, models.HexagonModel.indices, 2);
    }
}
