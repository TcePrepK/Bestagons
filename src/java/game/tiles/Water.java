package game.tiles;

public class Water {
    public static TileTypes update(final int[] neighbors) {
        final int dirtAmount = neighbors[0];
        final int grassAmount = neighbors[1];
        final int sandAmount = neighbors[2];
        final int waterAmount = neighbors[3];

        if (waterAmount <= 2) {
            return TileTypes.Dirt;
        }

        return TileTypes.Water;
    }
}
