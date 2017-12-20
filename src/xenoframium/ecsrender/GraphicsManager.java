package xenoframium.ecsrender;

import org.lwjgl.stb.STBImage;
import xenoframium.ecsrender.gl.Window;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL.*;
import static org.lwjgl.opengl.GL11.*;

/**
 * Created by chrisjung on 29/09/17.
 */
public class GraphicsManager {
    private static Window window;

    public static Window initGlAndContext(String windowTitle, int width, int height) {
        glfwInit();
        STBImage.stbi_set_flip_vertically_on_load(true);

        window = new Window(windowTitle, width, height);
        window.makeContextCurrent();
        window.show();
        createCapabilities();

        glEnable(GL_BLEND);
        glBlendFunc (GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_DEPTH_TEST);
        glDepthFunc(GL_LEQUAL);
        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);

        return window;
    }

    public static Window getWindow() {
        return window;
    }
}
