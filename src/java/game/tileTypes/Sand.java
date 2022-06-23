package game.tileTypes;

public class Sand {
    public static TileTypes update(final int[] neighbors) {
        final int dirtAmount = neighbors[0];
        final int grassAmount = neighbors[1];
        final int sandAmount = neighbors[2];
        final int waterAmount = neighbors[3];

        if (waterAmount >= 4) {
            return TileTypes.Water;
        }

        if (waterAmount == 0) {
            return TileTypes.Dirt;
        }

        if (waterAmount == 1) {
            return TileTypes.Water;
        }

        return TileTypes.Sand;
    }
}
