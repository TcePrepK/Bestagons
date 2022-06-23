package core;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;

public class Loader {
    private final List<Integer> vaos = new ArrayList<>();
    private final List<Integer> vbos = new ArrayList<>();
    private final List<Integer> textures = new ArrayList<>();

    private final HashMap<Integer, List<Integer>> vaoToVbos = new HashMap<>();

    public Loader() {
        GlobalVariables.loader = this;
    }

    public RawModel loadToVAO(final float[] positions, final int dimensions) {
        final int vaoID = createVAO();

        final List<Integer> vboList = new ArrayList<>();
        vaoToVbos.put(vaoID, vboList);

        storeDataInAttributeList(vboList, 0, dimensions, positions);

        Loader.unbindVAO();
        return new RawModel(vaoID, positions.length / dimensions);
    }

    public RawModel loadToVAO(final float[] positions, final int[] indices) {
        final int vaoID = createVAO();

        final List<Integer> vboList = new ArrayList<>();
        vaoToVbos.put(vaoID, vboList);

        bindIndicesBuffer(vboList, indices);

        storeDataInAttributeList(vboList, 0, 3, positions);

        Loader.unbindVAO();
        return new RawModel(vaoID, indices.length);
    }

    public RawModel loadToVAO(final float[] positions, final int[] indices, final int dimensions) {
        final int vaoID = createVAO();

        final List<Integer> vboList = new ArrayList<>();
        vaoToVbos.put(vaoID, vboList);

        bindIndicesBuffer(vboList, indices);

        storeDataInAttributeList(vboList, 0, dimensions, positions);

        Loader.unbindVAO();
        return new RawModel(vaoID, indices.length);
    }

    public RawModel loadToVAO(final float[] positions, final byte[] colors, final int[] indices) {
        final int vaoID = createVAO();

        final List<Integer> vboList = new ArrayList<>();
        vaoToVbos.put(vaoID, vboList);

        bindIndicesBuffer(vboList, indices);

        storeDataInAttributeList(vboList, 0, 3, positions);
        storeDataInAttributeList(vboList, 1, 4, colors);

        Loader.unbindVAO();
        return new RawModel(vaoID, indices.length);
    }

    public RawModel loadToVAO(final float[] positions, final byte[] colors, final int[] indices, final int dimensions) {
        final int vaoID = createVAO();

        final List<Integer> vboList = new ArrayList<>();
        vaoToVbos.put(vaoID, vboList);

        bindIndicesBuffer(vboList, indices);

        storeDataInAttributeList(vboList, 0, dimensions, positions);
        storeDataInAttributeList(vboList, 1, 3, colors);

        Loader.unbindVAO();
        return new RawModel(vaoID, indices.length);
    }

    public void cleanModel(final RawModel model) {
        final int vaoID = model.getVaoID();
        GL30.glDeleteVertexArrays(vaoID);
        vaos.remove((Object) vaoID);

        final List<Integer> vboList = vaoToVbos.get(vaoID);
        for (final int vbo : vboList) {
            glDeleteBuffers(vbo);
            vbos.remove((Object) vbo);
        }

        vaoToVbos.remove(vaoID);
    }

    public void cleanUp() {
        for (final int vao : vaos) {
            GL30.glDeleteVertexArrays(vao);
        }

        for (final int vbo : vbos) {
            glDeleteBuffers(vbo);
        }

        for (final int texture : textures) {
            glDeleteTextures(texture);
        }
    }

    private int createVAO() {
        final int vaoID = GL30.glGenVertexArrays();

        vaos.add(vaoID);

        GL30.glBindVertexArray(vaoID);

        return vaoID;
    }

    private void storeDataInAttributeList(final List<Integer> vboList, final int attributeNumber, final int coordinateSize, final float[] data) {
        final int vboID = glGenBuffers();
        final FloatBuffer buffer = Loader.storeDataInFloatBuffer(data);

        vbos.add(vboID);
        vboList.add(vboID);

        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
        GL20.glVertexAttribPointer(attributeNumber, coordinateSize, GL_FLOAT, false, 0, 0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    private void storeDataInAttributeList(final List<Integer> vboList, final int attributeNumber, final int coordinateSize, final int[] data) {
        final int vboID = glGenBuffers();
        final IntBuffer buffer = Loader.storeDataInIntBuffer(data);

        vbos.add(vboID);
        vboList.add(vboID);

        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
        GL20.glVertexAttribPointer(attributeNumber, coordinateSize, GL_INT, true, 0, 0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    private void storeDataInAttributeList(final List<Integer> vboList, final int attributeNumber, final int coordinateSize, final byte[] data) {
        final int vboID = glGenBuffers();
        final ByteBuffer buffer = Loader.storeDataInByteBuffer(data);

        vbos.add(vboID);
        vboList.add(vboID);

        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
        GL20.glVertexAttribPointer(attributeNumber, coordinateSize, GL_UNSIGNED_BYTE, true, 0, 0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    private static void unbindVAO() {
        GL30.glBindVertexArray(0);
    }

    private void bindIndicesBuffer(final List<Integer> vboList, final int[] indices) {
        final int vboID = glGenBuffers();
        vbos.add(vboID);
        vboList.add(vboID);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboID);
        final IntBuffer buffer = Loader.storeDataInIntBuffer(indices);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
    }

    private static ByteBuffer storeDataInByteBuffer(final byte[] data) {
        final ByteBuffer buffer = BufferUtils.createByteBuffer(data.length);
        buffer.put(data);
        buffer.flip();

        return buffer;
    }

    private static IntBuffer storeDataInIntBuffer(final int[] data) {
        final IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
        buffer.put(data);
        buffer.flip();

        return buffer;
    }

    private static FloatBuffer storeDataInFloatBuffer(final float[] data) {
        final FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);

        buffer.put(data);
        buffer.flip();

        return buffer;
    }
}
