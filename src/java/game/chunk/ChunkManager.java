package game.chunk;

import toolbox.Points.Point2D;
import toolbox.Vector2D;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static core.GlobalVariables.mapChunkSize;

public class ChunkManager {
    public static int OUT_OF_BOUNDS = -1;

    public final int WIDTH, HEIGHT;
    public final int CHUNK_AMOUNT;

    private final Map<String, Chunk> chunksById = new HashMap<>();
    private final List<Chunk> chunkList = new ArrayList<>();
    private final List<Chunk> chunkUpdateList = new ArrayList<>();

    private final Chunk[] chunkArray;

    private final long[] voxelBufferIDArray;
    private final long[] bitmaskBufferIDArray;

    public ChunkManager(final int width, final int height) {
        WIDTH = width;
        HEIGHT = height;
        CHUNK_AMOUNT = WIDTH * HEIGHT;

        chunkArray = new Chunk[width * height];

        voxelBufferIDArray = new long[width * height];
        bitmaskBufferIDArray = new long[width * height];
    }

    public Chunk createChunkChunkSpace(final Point2D chunkPos) {
        final String id = chunkPos.toString();
        final Chunk chunk = new Chunk(chunkPos.x, chunkPos.y);
        chunksById.put(id, chunk);
        chunkList.add(chunk);

        final int idx = getIDX(chunkPos);
        if (idx != ChunkManager.OUT_OF_BOUNDS) {
            chunkArray[idx] = chunk;
            chunkUpdateList.add(chunk);
        }

        return chunk;
    }

    public static Point2D getChunkPosition(final int x, final int y) {
        return new Vector2D(x, y).div(mapChunkSize).toPoint2D();
    }

    public static Point2D getChunkPosition(final Point2D tile) {
        return new Vector2D(tile).div(mapChunkSize).toPoint2D();
    }

    public Chunk getChunkWithId(final String id) {
        return chunksById.get(id);
    }

    public Chunk getChunkWorldSpace(final Point2D pos, final boolean createIfNull) {
        return getChunkWorldSpace(pos.x, pos.y, createIfNull);
    }

    public Chunk getChunkWorldSpace(final int x, final int y, final boolean createIfNull) {
        final Point2D chunkPos = ChunkManager.getChunkPosition(x, y);
        final Chunk chunk = getChunkWithId(chunkPos.toString());

        if (chunk != null) {
            return chunk;
        }

        if (!createIfNull) {
            return null;
        }

        return createChunkChunkSpace(chunkPos);
    }

    public Chunk getChunkChunkSpace(final int x, final int y, final boolean createIfNull) {
        final Point2D chunkPos = new Point2D(x, y);
        final Chunk chunk = getChunkWithId(chunkPos.toString());

        if (chunk != null) {
            return chunk;
        }

        if (!createIfNull) {
            return null;
        }

        return createChunkChunkSpace(chunkPos);
    }

    public boolean inBounds(final int x, final int y) {
        return !(x < 0 || x >= WIDTH || y < 0 || y >= HEIGHT);
    }

    public int getIDX(final int x, final int y) {
        if (!inBounds(x, y)) {
            return ChunkManager.OUT_OF_BOUNDS;
        }

        return x + y * WIDTH;
    }

    public int getIDX(final Point2D pos) {
        return getIDX(pos.x, pos.y);
    }

    public long[] getVoxelBufferIDArray() {
        return voxelBufferIDArray;
    }

    public long[] getBitmaskBufferIDArray() {
        return bitmaskBufferIDArray;
    }

    public List<Chunk> getChunkList() {
        return chunkList;
    }

    public List<Chunk> getChunkUpdateList() {
        return chunkUpdateList;
    }
}
