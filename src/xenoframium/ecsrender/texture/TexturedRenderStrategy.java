package xenoframium.ecsrender.texture;

import xenoframium.ecs.Entity;
import xenoframium.ecsrender.Mesh;
import xenoframium.ecsrender.components.RenderComponent3D;
import xenoframium.ecsrender.Shaders;
import xenoframium.ecsrender.gl.*;
import xenoframium.ecsrender.RenderStrategy;
import xenoframium.glmath.linearalgebra.Mat4;
import xenoframium.glmath.linearalgebra.Vec3;
import xenoframium.glwrapper.GlUniform;

import static org.lwjgl.system.MemoryUtil.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

public class TexturedRenderStrategy implements RenderStrategy {
    private static final GlUniform mvpUniform = new GlUniform(Shaders.alphaTexShaderProgram, "mvp");

    private final VAO vao;
    private final VBO vertexVBO;
    private final VBO uvVBO;
    private final IBO ibo;
    private final Texture texture;
    private final int renderMode;
    private final int numVertices;

    public TexturedRenderStrategy(Mesh mesh, float uvs[], Texture texture) {
        this.renderMode = mesh.getRenderMode();
        this.numVertices = mesh.getNumVertices();

        vertexVBO = new VBO();
        vertexVBO.bufferData(mesh.getVertices());
        uvVBO = new VBO();
        uvVBO.bufferData(uvs);
        this.texture = texture;

        ibo = new IBO();
        ibo.bufferData(mesh.getIndices());

        vao = new VAO();
        vao.addAttribPointer(vertexVBO, 0, 3, GL_FLOAT);
        vao.addAttribPointer(uvVBO, 1, 2, GL_FLOAT);
    }

    @Override
    public void render(Entity e, Mat4 mvp) {
        RenderComponent3D comp = e.getComponent(RenderComponent3D.class);

        Shaders.alphaTexShaderProgram.use();
        texture.bind();
        vao.bind();
        glUniformMatrix4fv(mvpUniform.getLocation(), false, mvp.asArr());
        ibo.bind();
        glDrawElements(renderMode, numVertices, GL_UNSIGNED_INT, NULL);
    }

    @Override
    public void close() {
        vao.close();
        vertexVBO.close();
        uvVBO.close();
    }
}
