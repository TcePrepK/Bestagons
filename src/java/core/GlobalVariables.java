package core;

import game.Camera;
import game.World;
import game.chunk.ChunkManager;
import renderers.MasterRenderer;
import toolbox.MousePicker;

import java.util.Random;

public class GlobalVariables {
    public static int currentFrame = 0;

    // Debugging
    public static boolean mouseLocked = false;
    public final static boolean creativeMode = true;
    public static boolean freePlayMode = true;
    public static boolean showDirtyRect = true;
    public static boolean renderChunks = true;
    public static boolean noisyWorld = true;
    public static boolean pathTracing = true;
    public static boolean drawBitmaskBorders = false;
    public static boolean updateSun = false;
    // Debugging

    // Core
    public static ImGuiManager imGuiManager = new ImGuiManager();
    public static Loader loader = new Loader();
    public static ThreadManager threadManager = new ThreadManager();
    // Core

    // World
    public final static double mapSeed = new Random().nextGaussian() * 65536;

    public final static int mapChunkSize = 32;

    public final static int chunkViewDistance = 1;
    public final static int hexagonScale = 5;

    public final static World world = new World();
    public final static ChunkManager chunkManager = new ChunkManager(2 * GlobalVariables.chunkViewDistance, 2 * GlobalVariables.chunkViewDistance);
    // World

    // User
    public static MousePicker mousePicker;
    public final static Random rand = new Random((long) GlobalVariables.mapSeed);
    public static Camera camera = new Camera();
    // User

    // Output
    public static MasterRenderer renderer = new MasterRenderer();
    // Output
}
