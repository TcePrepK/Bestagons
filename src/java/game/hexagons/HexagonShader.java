package game.hexagons;

import core.GlobalVariables;
import org.joml.Matrix4f;
import shaders.BaseShader;
import toolbox.Color;
import toolbox.Points.Point2D;
import toolbox.Vector3D;

public class HexagonShader extends BaseShader {
    private static final String VERTEX_FILE = "/shaders/hexagonMesh.vert";
    private static final String FRAGMENT_FILE = "/shaders/hexagonMesh.frag";

    private int pentagonScale;
    private int pentagonPos;
    private int PVMatrix;
    private int color;

    public HexagonShader() {
        super(HexagonShader.VERTEX_FILE, HexagonShader.FRAGMENT_FILE);
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
    }

    @Override
    protected void getAllUniformLocations() {
        pentagonScale = super.getUniformLocation("pentagonScale");
        pentagonPos = super.getUniformLocation("pentagonPos");
        PVMatrix = super.getUniformLocation("PVMatrix");

        color = super.getUniformLocation("color");
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

    public void loadColor(final Color c) {
        BaseShader.load3DVector(color, new Vector3D(c.r, c.g, c.b));
    }
}
