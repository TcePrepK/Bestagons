package game.tiles;

import toolbox.Color;

import java.util.Arrays;

public enum TileTypes {
    Water,
    Sand,
    Grass,
    Dirt;

    public static Color[] colorGradient = new Color[]{
            new Color(35, 137, 218), // Water
            new Color(239, 221, 111), // Sand
            new Color(0, 154, 23), // Grass
            new Color(124, 94, 66) // Dirt
    };

    public static int size() {
        return values().length;
    }

    public static int typeToIndex(final TileTypes type) {
        return Arrays.binarySearch(TileTypes.values(), type);
    }

    public static TileTypes indexToType(final int index) {
        return TileTypes.values()[index];
    }
}
