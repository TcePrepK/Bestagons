package game.tiles;

public class Grass {
    public static TileTypes update(final int[] neighbors) {
        final int dirtAmount = neighbors[0];
        final int grassAmount = neighbors[1];
        final int sandAmount = neighbors[2];
        final int waterAmount = neighbors[3];

        if (waterAmount >= 3) {
            return TileTypes.Water;
        } else if (waterAmount == 0 && dirtAmount > grassAmount) {
            return TileTypes.Dirt;
        } else if (waterAmount >= 2) {
            return TileTypes.Sand;
        }

        return TileTypes.Grass;
    }
}
