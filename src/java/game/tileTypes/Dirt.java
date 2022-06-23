package game.tileTypes;

public class Dirt {
    public static TileTypes update(final int[] neighbors) {
        final int dirtAmount = neighbors[0];
        final int grassAmount = neighbors[1];
        final int sandAmount = neighbors[2];
        final int waterAmount = neighbors[3];

        if (waterAmount >= 3) {
            return TileTypes.Water;
        }

        if (waterAmount > 0) {
            return TileTypes.Grass;
        }

        if (grassAmount > dirtAmount) {
            return TileTypes.Grass;
        }

        return TileTypes.Dirt;
    }
}
