package graphics;

import graphics.opengl.GLState;
import java.nio.IntBuffer;
import org.lwjgl.glfw.*;
import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import org.lwjgl.opengl.GL;
import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import org.lwjgl.opengl.GLUtil;
import org.lwjgl.system.Configuration;
import org.lwjgl.system.MemoryStack;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {

    public static final int ANTI_ALIASING = 1; // Scale from 1 - 16
    public static final boolean SHOW_OPENGL_DEBUG_INFO = false;

    public static final int WIDTH = 1600;
    public static final int HEIGHT = 900;
    public static Window window;

    public static void initGLFW() {
        if (SHOW_OPENGL_DEBUG_INFO) {
            GLFWErrorCallback.createThrow().set();
            Configuration.DEBUG.set(true);
        }

        if (!glfwInit()) {
            throw new RuntimeException("Failed to initialize GLFW");
        }
        window = new Window(true);
        window.createContext();
//          glfwSwapInterval(1); // Enable v-sync
        glfwShowWindow(window.handle);
    }

    public static void cleanupGLFW() {
        window.cleanup();
        glfwTerminate();
        if (SHOW_OPENGL_DEBUG_INFO) {
            glfwSetErrorCallback(null).free();
        }
    }

    private final long handle;

    public Window(boolean mainWindow) {
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        // glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        if (ANTI_ALIASING > 1) {
            glfwWindowHint(GLFW_SAMPLES, ANTI_ALIASING);
        }

        handle = glfwCreateWindow(WIDTH, HEIGHT, "Hello World!", NULL, mainWindow ? NULL : window.handle);
        if (handle == NULL) {
            throw new RuntimeException("Failed to create the GLFW window");
        }

        if (mainWindow) {
            setCursorEnabled(false);
            try (MemoryStack stack = MemoryStack.stackPush()) {
                IntBuffer pWidth = stack.mallocInt(1);
                IntBuffer pHeight = stack.mallocInt(1);
                glfwGetWindowSize(handle, pWidth, pHeight);
                GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
                glfwSetWindowPos(handle, (vidmode.width() - pWidth.get(0)) / 2, (vidmode.height() - pHeight.get(0)) / 2);
            }
        }
    }

    private void cleanup() {
        glfwFreeCallbacks(handle);
        glfwDestroyWindow(handle);
    }

    public void createContext() {
        glfwMakeContextCurrent(handle);
        GL.createCapabilities();
        glfwSwapInterval(0);

        GLState.enable(GL_DEPTH_TEST, GL_BLEND);
        GLState.setBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        if (SHOW_OPENGL_DEBUG_INFO) {
            GLUtil.setupDebugMessageCallback();
        }
    }

    public void nextFrame() {
        glfwSwapBuffers(handle);
        glfwPollEvents();
    }

    public void setCursorEnabled(boolean enabled) {
        if (enabled) {
            glfwSetInputMode(handle, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
        } else {
            glfwSetInputMode(handle, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
        }
    }

    public void setCursorPosCallback(GLFWCursorPosCallbackI cursorPosCallback) {
        glfwSetCursorPosCallback(handle, cursorPosCallback);
    }

    public void setKeyCallback(GLFWKeyCallbackI keyCallback) {
        glfwSetKeyCallback(handle, keyCallback);
    }

    public void setMouseButtonCallback(GLFWMouseButtonCallbackI mouseButtonCallback) {
        glfwSetMouseButtonCallback(handle, mouseButtonCallback);
    }

    public void setScrollCallback(GLFWScrollCallbackI scrollCallback) {
        glfwSetScrollCallback(handle, scrollCallback);
    }

    public void setTitle(String s) {
        GLFW.glfwSetWindowTitle(handle, s);
    }

    public boolean shouldClose() {
        return glfwWindowShouldClose(handle);
    }
}
