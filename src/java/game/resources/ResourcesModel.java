package game.resources;

import core.RawModel;
import game.hexagons.HexagonModel;
import toolbox.Vector2D;

import java.util.ArrayList;
import java.util.List;

import static core.GlobalVariables.loader;
import static core.GlobalVariables.mapChunkSize;

public class ResourcesModel {
    public static RawModel createModel(final int[] resourceGrid) {
        final int cW = mapChunkSize;
        final int cH = mapChunkSize;

        final List<Float> positions = new ArrayList<>();
        final List<Integer> indices = new ArrayList<>();
        final List<Byte> colors = new ArrayList<>();

        for (int y = 0; y < cH; y++) {
            for (int x = 0; x < cW; x++) {
                final int idx = x + y * cW;
                final int resource = resourceGrid[idx];
                if (resource == 0) {
                    continue;
                }

                final int hexIndex = positions.size() / 18;
                final float hexX = (x + (y % 2) * 0.5f) * 0.8660f;
                final float hexY = y * 0.75f;

                for (int j = 0; j < 6; j++) {
                    final Vector2D corner = HexagonModel.hexagon[j];

                    final float cornerX = hexX + corner.x;
                    final float cornerY = hexY + corner.y;

                    positions.add(cornerX);
                    positions.add(cornerY);
                    positions.add(1f);

                    colors.add((byte) (0xFF * (resource == 1 ? 1 : 0)));
                    colors.add((byte) (0xFF * (resource == 2 ? 1 : 0)));
                    colors.add((byte) 0x00);

                    indices.add(HexagonModel.indices[j * 2] + hexIndex * 6);
                    indices.add(HexagonModel.indices[j * 2 + 1] + hexIndex * 6);
                }
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
