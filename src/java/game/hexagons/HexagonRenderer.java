package game.hexagons;

import core.GlobalVariables;
import game.Camera;
import game.chunk.Chunk;
import shaders.BaseShader;
import toolbox.Color;
import toolbox.Points.Point2D;

import java.util.ArrayList;
import java.util.List;

import static core.GlobalVariables.*;
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
            glBindVertexArray(chunk.getModel().getVaoID());
            glEnableVertexAttribArray(0);
            glEnableVertexAttribArray(1);

            chunkShader.loadPosition(chunk.getPos());

            glDrawElements(GL_TRIANGLES, chunk.getModel().getVertexCount(), GL_UNSIGNED_INT, 0);

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

        final int scale = 5;
        for (int i = -scale; i <= scale; i++) {
            for (int j = -scale; j <= scale; j++) {
                visibleChunks.add(chunkManager.getChunkWorldSpace(cameraPos.add(i * mapChunkSize, j * mapChunkSize), true));
            }
        }

//        visibleChunks.add(chunkManager.getChunkWorldSpace(camera.getCameraTile(), true));
//        visibleChunks.add(chunkManager.getChunkWorldSpace(Camera.getTileAt(new Vector2D(0, 0)), true));
//        visibleChunks.add(chunkManager.getChunkWorldSpace(Camera.getTileAt(new Vector2D(WIDTH, 0)), true));
//        visibleChunks.add(chunkManager.getChunkWorldSpace(Camera.getTileAt(new Vector2D(0, HEIGHT)), true));
//        visibleChunks.add(chunkManager.getChunkWorldSpace(Camera.getTileAt(new Vector2D(WIDTH, HEIGHT)), true));

        return visibleChunks;
    }

    public void cleanUp() {
        hexagonShader.cleanUp();
    }
}
