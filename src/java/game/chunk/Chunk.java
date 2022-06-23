package game.chunk;

import core.RawModel;
import game.hexagons.HexagonNeighbor;
import game.hexagons.MinecraftChunkModel;
import game.tileTypes.TileTypes;
import toolbox.Color;
import toolbox.Points.Point2D;

import static core.GlobalVariables.*;
import static game.tileTypes.TileTypes.colorGradient;

public class Chunk {
    private final Point2D pos;
    private RawModel model;

    private int[] grid = new int[mapChunkSize * mapChunkSize];

    public Chunk(final int x, final int y) {
        pos = new Point2D(x, y).mult(mapChunkSize);

        build();
    }

    public void build() {
        for (int x = 0; x < mapChunkSize; x++) {
            for (int y = 0; y < mapChunkSize; y++) {
                final int idx = x + y * mapChunkSize;
                grid[idx] = rand.nextInt(4);
            }
        }

        model = MinecraftChunkModel.createModel(pos, getColorGrid());
    }

    public void update() {
        final int[] nextGrid = new int[grid.length];
        for (int x = 0; x < mapChunkSize; x++) {
            for (int y = 0; y < mapChunkSize; y++) {
                checkForTile(x, y, nextGrid);
            }
        }

        grid = nextGrid;

        loader.cleanModel(model);
        model = MinecraftChunkModel.createModel(pos, getColorGrid());
    }

    private void checkForTile(final int x, final int y, final int[] nextGrid) {
        final Point2D[] offsets = HexagonNeighbor.getNeighbors(y);
        final int[] neighbors = new int[TileTypes.values().length];

        for (final Point2D offset : offsets) {
            final int offX = x + offset.x;
            final int offY = y + offset.y;
            if (Chunk.outBounds(offX, offY)) {
                final Chunk chunk = chunkManager.getChunkWorldSpace(pos.x + offX, pos.y + offY, true);
                neighbors[chunk.getTile((offX + 32) % 32, (offY + 32) % 32)]++;
                continue;
            }

            neighbors[getTile(offX, offY)]++;
        }

        nextGrid[Chunk.getIDX(x, y)] = TileTypes.update(getTile(x, y), neighbors);
    }

    public int[] getColorGrid() {
        final int[] colorGrid = new int[mapChunkSize * mapChunkSize];
        for (int x = 0; x < mapChunkSize; x++) {
            for (int y = 0; y < mapChunkSize; y++) {
                final int idx = x + y * mapChunkSize;

                final Color color = colorGradient[grid[idx]];
                colorGrid[idx] = ((int) color.r << 16) + ((int) color.g << 8) + ((int) color.b);
            }
        }

        return colorGrid;
    }

    public int getTile(final int x, final int y) {
        return grid[Chunk.getIDX(x, y)];
    }

    public static int getIDX(final int x, final int y) {
        return x + (y * mapChunkSize);
    }

    public static boolean outBounds(final int x, final int y) {
        return (x < 0 || x >= mapChunkSize || y < 0 || y >= mapChunkSize);
    }

    public Point2D getPos() {
        return pos;
    }

    public RawModel getModel() {
        return model;
    }
}
    
