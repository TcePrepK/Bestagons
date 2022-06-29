package game.hexagons;

import core.GlobalVariables;
import game.Camera;
import game.chunk.Chunk;
import game.chunk.ChunkManager;
import shaders.BaseShader;
import toolbox.Color;
import toolbox.Maths;
import toolbox.Points.Point2D;
import toolbox.Vector2D;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static core.GlobalVariables.camera;
import static core.GlobalVariables.chunkManager;
import static display.DisplayManager.HEIGHT;
import static display.DisplayManager.WIDTH;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11C.glDrawElements;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

public class HexagonRenderer {
    private final ChunkShader chunkShader = new ChunkShader();
    private final HexagonShader hexagonShader = new HexagonShader();

    public HexagonRenderer() {
        hexagonShader.start();
        hexagonShader.loadScale();
        HexagonShader.stop();

        chunkShader.start();
        chunkShader.loadScale();
        ChunkShader.stop();
    }

    public void render() {
        // CHUNKS
        chunkShader.start();
        chunkShader.loadMatrices(GlobalVariables.camera.getProjectionViewMatrix());

        final List<Chunk> visibleChunks = HexagonRenderer.calculateVisibleChunks();
        for (final Chunk chunk : visibleChunks) {
            glBindVertexArray(chunk.getTileModel().getVaoID());
            glEnableVertexAttribArray(0);
            glEnableVertexAttribArray(1);

            chunkShader.loadPosition(chunk.getPos());

            glDrawElements(GL_TRIANGLES, chunk.getTileModel().getVertexCount(), GL_UNSIGNED_INT, 0);

            glDisableVertexAttribArray(1);
            glDisableVertexAttribArray(0);

            glBindVertexArray(chunk.getResourceModel().getVaoID());
            glEnableVertexAttribArray(0);
            glEnableVertexAttribArray(1);

            chunkShader.loadPosition(chunk.getPos());

            glDrawElements(GL_TRIANGLES, chunk.getResourceModel().getVertexCount(), GL_UNSIGNED_INT, 0);

            glDisableVertexAttribArray(1);
            glDisableVertexAttribArray(0);
        }

        glBindVertexArray(0);

        BaseShader.stop();
        // CHUNKS

        // HEXAGONS
        hexagonShader.start();
        hexagonShader.loadMatrices(GlobalVariables.camera.getProjectionViewMatrix());

        glBindVertexArray(HexagonModel.model.getVaoID());
        glEnableVertexAttribArray(0);

        // Camera
        hexagonShader.loadColor(new Color(1, 49 / 255f, 49 / 255f));
        hexagonShader.loadPosition(camera.getCameraTile());
        glDrawElements(GL_TRIANGLES, HexagonModel.model.getVertexCount(), GL_UNSIGNED_INT, 0);
        // Camera

        // Mouse
        final Point2D mouseTile = Camera.getMouseTile();

        hexagonShader.loadColor(new Color(57 / 255f, 1, 20 / 255f));
        hexagonShader.loadPosition(mouseTile);
        glDrawElements(GL_TRIANGLES, HexagonModel.model.getVertexCount(), GL_UNSIGNED_INT, 0);
        // Mouse

        glDisableVertexAttribArray(0);
        glBindVertexArray(0);

        BaseShader.stop();
        // HEXAGONS
    }

    private static List<Chunk> calculateVisibleChunks() {
        final Point2D cameraPos = camera.getCameraTile();
        final List<Chunk> visibleChunks = new ArrayList<>();

//        final int scale = 5;
//        for (int i = -scale; i <= scale; i++) {
//            for (int j = -scale * 2; j <= 0; j++) {
//                visibleChunks.add(chunkManager.getChunkWorldSpace(cameraPos.add(i * mapChunkSize, j * mapChunkSize), true));
//            }
//        }

        final Point2D A = ChunkManager.getChunkPosition(Camera.getTileAt(new Vector2D(0, 0)));
        final Point2D B = ChunkManager.getChunkPosition(Camera.getTileAt(new Vector2D(WIDTH, 0)));
        final Point2D C = ChunkManager.getChunkPosition(Camera.getTileAt(new Vector2D(WIDTH, HEIGHT)));
        final Point2D D = ChunkManager.getChunkPosition(Camera.getTileAt(new Vector2D(0, HEIGHT)));

//        final Point2D[] pointList = Arrays.stream(new Point2D[]{A, B, C, D}).sorted(Comparator.comparingInt(p -> p.y)).toArray(Point2D[]::new);

        final int top = Math.min(Math.min(A.y, B.y), Math.min(C.y, D.y));
        final int bottom = Math.max(Math.max(A.y, B.y), Math.max(C.y, D.y));
        final int dist = bottom - top;

        final Point2D[] pointList = new Point2D[]{A, B, C, D};
        System.out.println(Arrays.toString(pointList));
        for (int y = 0; y <= dist; y++) {
            int left = Integer.MAX_VALUE;
            int right = Integer.MIN_VALUE;
            for (int i = 0; i < 4; i++) {
                final Point2D p1 = pointList[i];
                final Point2D p2 = pointList[(i + 1) % 4];
                if (Float.isInfinite(Maths.calculateSlope(p1, p2))) {
                    continue;
                }

                final int hy = y + top;
                if (hy < p1.y && hy < p2.y) {
                    continue;
                }
                if (hy > p1.y && hy > p2.y) {
                    continue;
                }

                final int intersection = Maths.xValueInLine(p1, p2, y);

                left = Math.min(left, intersection);
                right = Math.max(right, intersection);
            }
//                visibleChunks.add(chunkManager.getChunkChunkSpace(intersection, y + Math.min(p1.y, p2.y), true));

            for (int x = left; x <= right; x++) {
                visibleChunks.add(chunkManager.getChunkChunkSpace(x, y + top, true));
            }
        }
//
//        final int scale = 2;
//        for (int i = leftChunk.x; i <= rightChunk.x && (i - leftChunk.x) < scale; i++) {
//            for (int j = topChunk.y; j <= bottomChunk.y && (j - topChunk.y) < scale; j++) {
//                visibleChunks.add(chunkManager.getChunkChunkSpace(i, j, true));
//            }
//        }

        return visibleChunks;
    }

    public void cleanUp() {
        hexagonShader.cleanUp();
        chunkShader.cleanUp();
    }
}
