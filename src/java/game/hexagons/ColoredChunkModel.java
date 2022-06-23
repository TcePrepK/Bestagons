package game.hexagons;

import core.RawModel;
import toolbox.Color;
import toolbox.Noise;
import toolbox.Points.Point2D;
import toolbox.Vector2D;

import static core.GlobalVariables.loader;
import static core.GlobalVariables.mapChunkSize;

public class ColoredChunkModel {
    public static final float[] heightGradiant = new float[]{
            -1f,
            -0.25f,
            0f,
            0.0625f,
            0.125f,
            0.375f,
            0.75f,
            1f,
    };

    public static final Color[] colorGradiant = new Color[]{
            new Color(0, 0, 128), // deeps
            new Color(0, 0, 255), // shallow
            new Color(0, 128, 255), // shore
            new Color(240, 240, 64), // sand
            new Color(32, 160, 0), // grass
            new Color(224, 224, 0), // dirt
            new Color(128, 128, 128), // rock
            new Color(255, 255, 255) // snow
    };

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
                for (int j = 0; j < 6; j++) {
                    final int idx = hexIndex * 18 + j * 3;
                    final Vector2D corner = HexagonModel.hexagon[j];

                    final float cornerX = hexX + corner.x;
                    final float cornerY = hexY + corner.y;

                    positions[idx] = cornerX;
                    positions[idx + 1] = cornerY;
                    positions[idx + 2] = 0;

                    indices[hexIndex * 12 + j * 2] = HexagonModel.indices[j * 2] + hexIndex * 6;
                    indices[hexIndex * 12 + j * 2 + 1] = HexagonModel.indices[j * 2 + 1] + hexIndex * 6;

                    // Colors and height
                    final Color noiseData = ColoredChunkModel.calculateNoise(chunkPos, cornerX, cornerY);

                    positions[idx + 2] = noiseData.a;

                    colors[hexIndex * 18 + j * 3] = (byte) noiseData.r;
                    colors[hexIndex * 18 + j * 3 + 1] = (byte) noiseData.g;
                    colors[hexIndex * 18 + j * 3 + 2] = (byte) noiseData.b;
                }
            }
        }

        return loader.loadToVAO(positions, colors, indices, 3);
    }

    private static Color calculateNoise(final Point2D chunkPos, final float x, final float y) {
        final float scale = 50;
        final float maxHeight = 50;

        final float chunkX = chunkPos.x * 0.8660f;
        final float chunkY = chunkPos.y * 0.75f;

        final float scaledX = (x + chunkX) / scale;
        final float scaledY = (y + chunkY) / scale;

        final float noise =
                (float) Noise.noise(scaledX * 1, scaledY * 1) * 0.5f +
                        (float) Noise.noise(scaledX * 2, scaledY * 2) * 0.25f +
                        (float) Noise.noise(scaledX * 3, scaledY * 3) * 0.15f +
                        (float) Noise.noise(scaledX * 4, scaledY * 4) * 0.1f;

        Color color = new Color();
        for (int i = 0; i < ColoredChunkModel.heightGradiant.length; i++) {
            final float targetHeight = ColoredChunkModel.heightGradiant[i];
            if (noise > targetHeight) {
                continue;
            }

            if (noise == targetHeight) {
                color = ColoredChunkModel.colorGradiant[i];
                break;
            }

            final float previousHeight = ColoredChunkModel.heightGradiant[i - 1];
            final float t = (noise - previousHeight) / (targetHeight - previousHeight);

            final Color previousColor = ColoredChunkModel.colorGradiant[i - 1];
            final Color targetColor = ColoredChunkModel.colorGradiant[i];

            color = new Color(
                    t * previousColor.r + (1 - t) * targetColor.r,
                    t * previousColor.g + (1 - t) * targetColor.g,
                    t * previousColor.b + (1 - t) * targetColor.b
            );
            break;
        }

        return new Color(color.r, color.g, color.b, noise * maxHeight);
    }
}
