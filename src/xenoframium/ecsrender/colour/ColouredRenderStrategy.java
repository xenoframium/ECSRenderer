package xenoframium.ecsrender.colour;

import xenoframium.ecsrender.Mesh;
import xenoframium.ecsrender.RenderStrategy;
import xenoframium.ecs.Entity;
import xenoframium.ecsrender.Shaders;
import xenoframium.ecsrender.components.RenderComponent3D;
import xenoframium.ecsrender.gl.IBO;
import xenoframium.ecsrender.gl.VAO;
import xenoframium.ecsrender.gl.VBO;
import xenoframium.glmath.linearalgebra.Mat4;
import xenoframium.glmath.linearalgebra.Vec3;
import xenoframium.glmath.linearalgebra.Vec4;
import xenoframium.glwrapper.GlUniform;

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
 * Created by chrisjung on 11/12/17.
 */
public class ColouredRenderStrategy implements RenderStrategy {
    private static final GlUniform mvpUniform = new GlUniform(Shaders.colourShaderProgram, "mvp");
    private static final GlUniform colourUniform = new GlUniform(Shaders.colourShaderProgram, "inColour");

    private final VAO vao;
    private final VBO vertexVBO;
    private final IBO ibo;
    private final int renderMode;
    private final int numVertices;

    private Vec4 colour;

    public ColouredRenderStrategy(Mesh mesh, Vec4 colour) {
        this.colour = new Vec4(colour);

        this.renderMode = mesh.getRenderMode();
        this.numVertices = mesh.getNumVertices();

        vertexVBO = new VBO();
        vertexVBO.bufferData(mesh.getVertices());

        ibo = new IBO();
        ibo.bufferData(mesh.getIndices());

        vao = new VAO();
        vao.addAttribPointer(vertexVBO, 0, 3, GL_FLOAT);
    }

    public void setColour(Vec4 colour) {
        this.colour = new Vec4(colour);
    }

    public Vec4 getColour() {
        return new Vec4(colour);
    }

    @Override
    public void render(Entity e, Mat4 mvp) {
        RenderComponent3D comp = e.getComponent(RenderComponent3D.class);

        Shaders.colourShaderProgram.use();
        vao.bind();
        glUniformMatrix4fv(mvpUniform.getLocation(), false, mvp.asArr());
        glUniform4fv(colourUniform.getLocation(), colour.asArr());

        ibo.bind();
        glDrawElements(renderMode, numVertices, GL_UNSIGNED_INT, NULL);
    }

    @Override
    public void close() {
        vertexVBO.close();
        ibo.close();
        vao.close();
    }
}
