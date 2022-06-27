package game.resources;

import core.RawModel;
import models.CubeModel;
import models.HexagonModel;
import toolbox.Color;
import toolbox.Vector2D;
import toolbox.Vector3D;

import java.util.ArrayList;
import java.util.List;

import static core.GlobalVariables.loader;
import static core.GlobalVariables.mapChunkSize;

public class ResourcesModel {
    public static RawModel createModel(final int[] resourceGrid) {
        final int cW = mapChunkSize;
        final int cH = mapChunkSize;

        final float tileW = 0.8660f;
        final float tileH = 0.75f;

        final List<Float> positions = new ArrayList<>();
        final List<Integer> indices = new ArrayList<>();
        final List<Byte> colors = new ArrayList<>();

        int lastIndex = 0;
        for (int y = 0; y < cH; y++) {
            for (int x = 0; x < cW; x++) {
                final int idx = x + y * cW;
                final int resource = resourceGrid[idx];
                if (resource == 0) {
                    continue;
                }

                final float hexX = (x + (y % 2) * 0.5f) * tileW;
                final float hexY = y * tileH;

                if (resource == 1) {
                    final float depth = 5;
                    final Color color = Color.randomColor();
                    for (final Vector3D corner : CubeModel.calculatePositions(0.5f, 0.5f, depth)) {
                        positions.add(hexX + corner.x + tileW / 2f);
                        positions.add(hexY + corner.y + 1 / 2f);
                        positions.add(corner.z + depth / 2);

                        colors.add((byte) (0xFF * color.r));
                        colors.add((byte) (0xFF * color.g));
                        colors.add((byte) (0xFF * color.b));
                    }

                    for (int i = 0; i < CubeModel.indices.length; i++) {
                        indices.add(CubeModel.indices[i] + lastIndex);
                    }
                    lastIndex += CubeModel.positions.length;

                    continue;
                }

                for (int j = 0; j < 6; j++) {
                    final Vector2D corner = HexagonModel.positions[j];

                    final float cornerX = hexX + corner.x;
                    final float cornerY = hexY + corner.y;

                    positions.add(cornerX);
                    positions.add(cornerY);
                    positions.add(1f);

                    colors.add((byte) 0);
                    colors.add((byte) (0xFF * (resource == 2 ? 1 : 0)));
                    colors.add((byte) 0x00);
                }

                for (int i = 0; i < HexagonModel.indices.length; i++) {
                    indices.add(HexagonModel.indices[i] + lastIndex);
                }
                lastIndex += HexagonModel.positions.length;
            }
        }

        final float[] fixedPositions = new float[positions.size()];
        final int[] fixedIndices = new int[indices.size()];
        final byte[] fixedColors = new byte[colors.size()];

        for (int i = 0; i < fixedPositions.length; i++) {
            fixedPositions[i] = positions.get(i);
        }
        for (int i = 0; i < fixedIndices.length; i++) {
            fixedIndices[i] = indices.get(i);
        }
        for (int i = 0; i < fixedColors.length; i++) {
            fixedColors[i] = colors.get(i);
        }

        return loader.loadToVAO(fixedPositions, fixedColors, fixedIndices, 3);
    }
}
