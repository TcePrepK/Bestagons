package game.tileTypes;

import toolbox.Color;
import toolbox.CustomRunnable;

import java.util.Arrays;
import java.util.List;

public enum TileTypes {
    Dirt,
    Grass,
    Sand,
    Water;

    public static Color[] colorGradient = new Color[]{
            new Color(124, 94, 66), // Dirt
            new Color(0, 154, 23), // Grass
            new Color(239, 221, 111), // Sand
            new Color(35, 137, 218) // Water
    };

    public static List<CustomRunnable<TileTypes, int[]>> updateGradient = Arrays.asList(
            game.tileTypes.Dirt::update,
            game.tileTypes.Grass::update,
            game.tileTypes.Sand::update,
            game.tileTypes.Water::update
    );

    public static int typeToIndex(final TileTypes type) {
        return Arrays.binarySearch(TileTypes.values(), type);
    }

    public static TileTypes indexToType(final int index) {
        return TileTypes.values()[index];
    }

    public static int update(final int index, final int[] neighbors) {
        return TileTypes.typeToIndex(TileTypes.updateGradient.get(index).run(neighbors));
    }
}
