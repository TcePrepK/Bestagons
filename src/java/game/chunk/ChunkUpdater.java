package game.chunk;

import core.Mouse;
import game.Camera;
import toolbox.Points.Point2D;

import static core.GlobalVariables.*;

public class ChunkUpdater {
    public static void init() {
        Mouse.mouseLeftDown.add(ChunkUpdater::update);
    }

    private static void update() {
        if (currentFrame % 1 != 0) {
            return;
        }

        final Point2D mouseTile = Camera.getMouseTile();
        final int scale = 5;
        for (int i = -scale; i <= scale; i++) {
            for (int j = -scale; j <= scale; j++) {
                final Chunk mouseChunk = chunkManager.getChunkWorldSpace(mouseTile.add(new Point2D(i * mapChunkSize, j * mapChunkSize)), true);
                mouseChunk.update();
            }
        }
    }
}
