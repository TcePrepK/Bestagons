package core;

import display.DisplayManager;
import game.Camera;
import imgui.ImGui;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import toolbox.Logger;
import toolbox.Octatree;

import java.util.List;

import static core.GlobalVariables.camera;
import static core.GlobalVariables.renderer;

public class ImGuiManager {
    private final ImGuiImplGlfw imGuiGlfw = new ImGuiImplGlfw();
    private final ImGuiImplGl3 imGuiGl3 = new ImGuiImplGl3();

    public ImGuiManager() {
        ImGui.createContext();
        imGuiGlfw.init(DisplayManager.getWindow(), true);
        imGuiGl3.init("#version 450");

        Logger.out("~ ImGui Initialized Successfully");
    }

    public static void renderBranch(final Octatree branch, final String name, final int id) {
        if (ImGui.treeNode("##" + id)) {
            ImGui.sameLine();
            ImGui.selectable(name, true);
            if (branch.isBranched()) {
                final List<Octatree> branches = branch.getBranches();
                for (int i = 0; i < branches.size(); i++) {
                    ImGuiManager.renderBranch(branches.get(i), "Branch " + (i + 1), i);
                }
            } else {
                ImGui.text(" ~PointAmount: " + branch.getPointAmount());
            }
            ImGui.treePop();
        } else {
            ImGui.sameLine();
            ImGui.selectable(name);
        }
    }

    public void update(final float updateTime) {
        imGuiGlfw.newFrame();
        ImGui.newFrame();
        ImGui.begin("Cool Window");

        // FPS
        ImGui.text("FPS: " + DisplayManager.getFPS());
        ImGui.text("Average FPS: " + DisplayManager.getAverageFPS());
        ImGui.spacing();
        ImGui.text("Update time: " + updateTime + "ms");
        ImGui.text("Binding time: " + renderer.getBindTime() + "ms");
        ImGui.text("Tracing time: " + renderer.getTraceTime() + "ms");
        ImGui.text("Other time: " + renderer.getOtherTime() + "ms");
        ImGui.text("Total time: " + DisplayManager.getDelta() * 1000 + "ms");
        ImGui.spacing();
        ImGui.spacing();
        // FPS

        // CAMERA
        final float[] pitch = new float[]{-Camera.CAMERA_PITCH};
        ImGui.text("Character Tile: " + camera.getCameraTile().toString());
        ImGui.sliderFloat("Camera Pitch", pitch, 0, 60);
        Camera.CAMERA_PITCH = -pitch[0];
        // CAMERA

//        // Output Control
//        if (ImGui.treeNode("Output Options")) {
//            final String[] options = renderer.getAttachmentManager().keys();
//            int selectedOption = 0;
//            for (int i = 0; i < options.length; i++) {
//                if (options[i].equals(outputOption)) {
//                    selectedOption = i;
//                    break;
//                }
//            }
//
//            final ImInt selected = new ImInt(selectedOption);
//            ImGui.listBox("##Options", selected, options, 5);
//            outputOption = options[selected.get()];
//
//            ImGui.treePop();
//        }
//        ImGui.spacing();
//        ImGui.spacing();
//        // Output Control

//        // World Generation
//        if (ImGui.checkbox("Generate World", generateWorld)) {
//            generateWorld = !generateWorld;
//        }
//
//        ImGui.spacing();
//        ImGui.spacing();
//        // World Generation

//        // World
//        final Point3D worldScale = world.getWorldScale();
//        final ChunkGenerationThread generationThread = (ChunkGenerationThread) threadManager.getThread("chunkGenerationThread");
//
//        ImGui.text("World: " + worldScale.x + "x" + worldScale.y + "x" + worldScale.z);
//        ImGui.text("World generation percentage: " + generationThread.getGeneratePercentage() + "%");
//        ImGui.text("World generation time: " + generationThread.getThreadAliveTime() + "sec");
//        ImGui.text("Estimated generation time: " + generationThread.getEstimatedTime() + "sec");
//
//        ImGui.spacing();
//        ImGui.spacing();
//        // World

//        // World Update
//        if (ImGui.checkbox("Update World", updateWorld)) {
//            updateWorld = !updateWorld;
//        }
//
//        ImGui.text("Amount of chunks to update: " + chunkManager.getChunkUpdateList().size());
//        ImGui.text("Chunk update time: " + threadManager.getThread("chunkUpdateThread").getLoopTime() + "ms");
//        // World Update

        ImGui.end();
        ImGui.render();
        imGuiGl3.renderDrawData(ImGui.getDrawData());
    }

    public void cleanUp() {
        imGuiGlfw.dispose();
        imGuiGl3.dispose();
        ImGui.destroyContext();
    }
}
