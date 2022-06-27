package renderers;

import core.Timer;
import display.DisplayManager;
import game.hexagons.HexagonRenderer;

import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.opengl.GL11C.*;

public class MasterRenderer {
    public final HexagonRenderer hexagonRenderer = new HexagonRenderer();

    private final Timer mainTimer = new Timer();
    private float bindTime;
    private float traceTime;
    private float otherTime;

    public MasterRenderer() {
//        screenSizeChange.add(() -> {
//            renderShader.start();
//            renderShader.loadResolutions();
//            BaseShader.stop();
//
//            displayShader.start();
//            displayShader.loadResolution();
//            BaseShader.stop();
//
//            attachmentManager.updateResolutions(WIDTH, HEIGHT);
//        });

        glEnable(GL_DEPTH_TEST);
    }

    public void render() {
        // Binding Timer
        mainTimer.startTimer();
        // Binding Timer

        // Update Buffers
//        World.updateBuffers();
        // Update Buffers

        // Start Renderer
//        renderShader.start();
        // Start Renderer

        // Unbind Texture Buffer
//        MasterRenderer.unbindFrameBuffer();
//        BaseShader.stop();
        // Unbind Texture Buffer

        // Start Display
//        displayShader.start();
        // Start Display

        // Render Texture To Screen
//        glActiveTexture(GL_TEXTURE0);
//        glBindTexture(GL_TEXTURE_2D, attachmentManager.get(outputOption).getID());
        // Render Texture To Screen

//        glBindVertexArray(quad.getVaoID());
//        glEnableVertexAttribArray(0);
//        glDrawArrays(GL_TRIANGLE_STRIP, 0, quad.getVertexCount());
//        glDisableVertexAttribArray(0);
//        glBindVertexArray(0);

//        BaseShader.stop();

//        attachmentManager.update();

        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glClearColor(150 / 255f, 150 / 255f, 150 / 255f, 1);

        // Pentagons
        hexagonRenderer.render();
        // Pentagons

        // Timer For Others
        otherTime = (float) mainTimer.stopTimer() * 1000;
        // Timer For Others
    }

    public void finishRendering() {
        mainTimer.startTimer();

        glfwSwapBuffers(DisplayManager.getWindow());
        glfwPollEvents();

        traceTime = (float) mainTimer.stopTimer() * 1000;
    }

//    public void bindFrameBuffer() {
//        glBindFramebuffer(GL_FRAMEBUFFER, displayBufferID);
//        glViewport(0, 0, WIDTH, HEIGHT);
//    }

//    public static void unbindFrameBuffer() {
//        glBindFramebuffer(GL_FRAMEBUFFER, 0);
//        glViewport(0, 0, WIDTH, HEIGHT);
//    }

//    private int createDisplayBuffer() {
//        final int frameBuffer = glGenFramebuffers();
//        glBindFramebuffer(GL_FRAMEBUFFER, frameBuffer);
//
//        final int size = attachmentManager.size();
//        final int[] attachments = new int[size];
//        for (int i = 0; i < size; i++) {
//            attachments[i] = GL_COLOR_ATTACHMENT0 + i;
//        }
//
//        glDrawBuffers(attachments);
//        MasterRenderer.unbindFrameBuffer();
//
//        return frameBuffer;
//    }

    public void cleanUp() {
        hexagonRenderer.cleanUp();
    }

    public float getBindTime() {
        return bindTime;
    }

    public float getTraceTime() {
        return traceTime;
    }

    public float getOtherTime() {
        return otherTime;
    }
}
