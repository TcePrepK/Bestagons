package game.hexagons;

import core.RawModel;
import toolbox.Color;
import toolbox.Points.Point2D;
import toolbox.Vector2D;

import static core.GlobalVariables.loader;
import static core.GlobalVariables.mapChunkSize;

public class ChunkModel {
    public static RawModel createModel(final Point2D chunkPos, final int[] colorGrid) {
        final int cW = mapChunkSize;
        final int cH = mapChunkSize;

        final float[] positions = new float[6 * 3 * cW * cH];
        final int[] indices = new int[6 * 2 * cW * cH];
        final byte[] colors = new byte[6 * 3 * cW * cH];

        for (int y = 0; y < cH; y++) {
            for (int x = 0; x < cW; x++) {
                final int hexIndex = x + y * cW;

                final float hexX = (x + (y % 2) * 0.5f) * 0.8660f;
                final float hexY = y * 0.75f;

                final Color color = Color.randomColor();
                for (int j = 0; j < 6; j++) {
                    final int idx = hexIndex * 18 + j * 3;
                    final Vector2D corner = HexagonModel.hexagon[j];

                    final float cornerX = hexX + corner.x;
                    final float cornerY = hexY + corner.y;

                    positions[idx] = cornerX;
                    positions[idx + 1] = cornerY;
                    positions[idx + 2] = 0;

                    colors[hexIndex * 18 + j * 3] = (byte) (color.r * 256);
                    colors[hexIndex * 18 + j * 3 + 1] = (byte) (color.g * 256);
                    colors[hexIndex * 18 + j * 3 + 2] = (byte) (color.b * 256);

                    indices[hexIndex * 12 + j * 2] = HexagonModel.indices[j * 2] + hexIndex * 6;
                    indices[hexIndex * 12 + j * 2 + 1] = HexagonModel.indices[j * 2 + 1] + hexIndex * 6;
                }
            }
        }

        return loader.loadToVAO(positions, colors, indices, 3);
    }
}
