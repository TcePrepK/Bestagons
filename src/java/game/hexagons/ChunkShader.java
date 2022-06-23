package game.hexagons;

import core.GlobalVariables;
import org.joml.Matrix4f;
import shaders.BaseShader;
import toolbox.Points.Point2D;

public class ChunkShader extends BaseShader {
    private static final String VERTEX_FILE = "/shaders/chunkMesh.vert";
    private static final String FRAGMENT_FILE = "/shaders/chunkMesh.frag";

    private int pentagonScale;
    private int pentagonPos;
    private int PVMatrix;

    public ChunkShader() {
        super(ChunkShader.VERTEX_FILE, ChunkShader.FRAGMENT_FILE);
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
        super.bindAttribute(1, "color");
    }

    @Override
    protected void getAllUniformLocations() {
        pentagonScale = super.getUniformLocation("pentagonScale");
        pentagonPos = super.getUniformLocation("pentagonPos");
        PVMatrix = super.getUniformLocation("PVMatrix");
    }

    public void loadScale() {
        BaseShader.loadInt(pentagonScale, GlobalVariables.hexagonScale);
    }

    public void loadPosition(final Point2D pos) {
        BaseShader.load2DVector(pentagonPos, pos);
    }

    public void loadMatrices(final Matrix4f PVM) {
        BaseShader.loadMatrix(PVMatrix, PVM);
    }
}
