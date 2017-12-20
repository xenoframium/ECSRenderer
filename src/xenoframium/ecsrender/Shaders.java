package xenoframium.ecsrender;

import xenoframium.glwrapper.GlProgram;
import xenoframium.glwrapper.GlShader;

import java.io.File;

import static org.lwjgl.system.MemoryUtil.*;
import static org.lwjgl.system.MemoryStack.*;
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
 * Created by chrisjung on 5/12/17.
 */
public class Shaders {
    public static GlProgram colourShaderProgram;
    public static GlProgram alphaTexShaderProgram;
    public static GlProgram textShaderProgram;

    static {
        GlShader vert = new GlShader(GL_VERTEX_SHADER, new File("shaders/alphaTexVert.vert"));
        GlShader frag = new GlShader(GL_FRAGMENT_SHADER, new File("shaders/alphaTexFrag.frag"));
        alphaTexShaderProgram = new GlProgram(vert, frag);
        vert.delete();
        frag.delete();

        vert = new GlShader(GL_VERTEX_SHADER, new File("shaders/textVert.vert"));
        frag = new GlShader(GL_FRAGMENT_SHADER, new File("shaders/textFrag.frag"));
        textShaderProgram = new GlProgram(vert, frag);
        vert.delete();
        frag.delete();

        vert = new GlShader(GL_VERTEX_SHADER, new File("shaders/colourVert.vert"));
        frag = new GlShader(GL_FRAGMENT_SHADER, new File("shaders/colourFrag.frag"));
        colourShaderProgram = new GlProgram(vert, frag);
        vert.delete();
        frag.delete();
    }
}
