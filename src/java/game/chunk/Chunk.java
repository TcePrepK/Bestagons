package game.chunk;

import core.RawModel;
import game.hexagons.MinecraftChunkModel;
import game.resources.ResourceTypes;
import game.resources.ResourcesModel;
import game.tiles.TileTypes;
import toolbox.Color;
import toolbox.Maths;
import toolbox.Noise;
import toolbox.Points.Point2D;

import static core.GlobalVariables.mapChunkSize;
import static game.tiles.TileTypes.colorGradient;

public class Chunk {
    private final Point2D pos;

    private RawModel tileModel;
    private RawModel resourceModel;

    private final int[] tileGrid = new int[mapChunkSize * mapChunkSize];
    private final int[] resourceGrid = new int[mapChunkSize * mapChunkSize];

    public Chunk(final int x, final int y) {
        pos = new Point2D(x, y).mult(mapChunkSize);

        buildTiles();
        buildResources();

        // preWorldUpdate();
    }

    public void buildTiles() {
        for (int x = 0; x < mapChunkSize; x++) {
            for (int y = 0; y < mapChunkSize; y++) {
                final int idx = x + y * mapChunkSize;

                final float noise = getNoise(x, y, 50);
                final float scaledNoise = (noise + 1) / 2;
                tileGrid[idx] = Math.min((int) (Maths.weirdLerp(scaledNoise, 1.5f) * TileTypes.size()), TileTypes.size() - 1);
            }
        }

        tileModel = MinecraftChunkModel.createModel(pos, getColorGrid());
    }

    public void buildResources() {
        for (int x = 0; x < mapChunkSize; x++) {
            for (int y = 0; y < mapChunkSize; y++) {
                final int idx = x + y * mapChunkSize;

                if (tileGrid[idx] != 2) {
                    continue;
                }

                if (getNoise(x, y, 100) < 0) {
                    continue;
                }

                final float scaledNoise = getNoise(x, y, 1);
                if (scaledNoise < 0) {
                    continue;
                }

                resourceGrid[idx] = (int) Math.min(Math.pow(scaledNoise, 2) * (ResourceTypes.size() + 1), ResourceTypes.size() - 1) + 1;
            }
        }

        resourceModel = ResourcesModel.createModel(resourceGrid);
    }

    public float getNoise(final int x, final int y, final float scale) {
        final int tilePositionY = y + pos.y;
        final float tilePositionX = (x + pos.x) + (Math.abs(tilePositionY) % 2) * 0.5f;

        final float tileX = tilePositionX * 0.8660f / scale;
        final float tileY = tilePositionY * 0.75f / scale;

        return (float) Noise.noise(tileX * 1, tileY * 1) * 0.5f +
                (float) Noise.noise(tileX * 2, tileY * 2) * 0.25f +
                (float) Noise.noise(tileX * 3, tileY * 3) * 0.15f +
                (float) Noise.noise(tileX * 4, tileY * 4) * 0.1f;
    }

    public int[] getColorGrid() {
        final int[] colorGrid = new int[mapChunkSize * mapChunkSize];
        for (int x = 0; x < mapChunkSize; x++) {
            for (int y = 0; y < mapChunkSize; y++) {
                final int idx = x + y * mapChunkSize;

                final Color color = colorGradient[tileGrid[idx]];
                colorGrid[idx] = ((int) color.r << 16) + ((int) color.g << 8) + ((int) color.b);
            }
        }

        return colorGrid;
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

    public RawModel getTileModel() {
        return tileModel;
    }

    public RawModel getResourceModel() {
        return resourceModel;
    }
}
    
