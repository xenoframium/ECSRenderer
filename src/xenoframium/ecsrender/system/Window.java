package xenoframium.ecsrender.system;

import xenoframium.glwrapper.GlfwWindow;
import xenoframium.glwrapper.NullWindowHintApplier;
import xenoframium.glwrapper.WindowBuilder;
import xenoframium.glwrapper.WindowHintApplier;

import java.util.Locale;

import static org.lwjgl.system.MemoryUtil.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL14.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL21.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL31.*;
import static org.lwjgl.opengl.GL32.*;
import static org.lwjgl.opengl.GL33.*;
import static org.lwjgl.opengl.GL40.*;
import static org.lwjgl.opengl.GL41.*;
import static org.lwjgl.opengl.GL42.*;
import static org.lwjgl.opengl.GL43.*;
import static org.lwjgl.opengl.GL44.*;
import static org.lwjgl.opengl.GL45.*;

/**
 * Created by chrisjung on 29/09/17.
 */
public class Window {
    private GlfwWindow window;
    private boolean isCursorDisabled=false;

    public final int width;
    public final int height;

    public Window(String title, int width, int height) {
        this.width = width;
        this.height = height;

        WindowHintApplier applier;

        String os = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);
        if (os.contains("mac")) {
            applier = new WindowHintApplier() {
                @Override
                public void applyWindowHints() {
                    glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 4);
                    glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 1);
                    glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);
                    glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
                }
            };
        } else {
            applier = new NullWindowHintApplier();
        }

        window = new WindowBuilder(title, width, height).setWindowHintApplier(applier).build();
        makeContextCurrent();
    }

    public void show() {
        glfwShowWindow(window.getId());
    }

    public void hide() {
        glfwHideWindow(window.getId());
    }

    public void swapBuffers() {
        glfwSwapBuffers(window.getId());
    }

    public void makeContextCurrent() {
        window.makeContextCurrent();
    }

    public void close() {
        glfwSetWindowShouldClose(window.getId(), true);
    }

    public boolean shouldClose() {
        return glfwWindowShouldClose(window.getId());
    }

    public void enableVsync() {
        glfwSwapInterval(1);
    }

    public void disableVsync() {
        glfwSwapInterval(0);
    }

    public void disableCursor() {
        glfwSetInputMode(window.getId(), GLFW_CURSOR, GLFW_CURSOR_DISABLED);
        isCursorDisabled = true;
    }

    public void normalCursor() {
        glfwSetInputMode(window.getId(), GLFW_CURSOR, GLFW_CURSOR_NORMAL);
        isCursorDisabled = false;
    }

    public boolean isCursorDisabled() {
        return isCursorDisabled;
    }

    public void enableStickyKeys() {
        glfwSetInputMode(window.getId(), GLFW_STICKY_KEYS, 1);
    }

    public void disableStickyKeys() {
        glfwSetInputMode(window.getId(), GLFW_STICKY_KEYS, 0);
    }

    long getId() {
        return window.getId();
    }

    public void destroy() {
        glfwDestroyWindow(window.getId());
    }

    public float getAspect() {
        return ((float) width) / height;
    }
}
