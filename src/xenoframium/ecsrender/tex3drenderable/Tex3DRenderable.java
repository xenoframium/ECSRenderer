package xenoframium.ecsrender.tex3drenderable;

import xenoframium.ecs.Component;
import xenoframium.ecsrender.Mesh;
import xenoframium.ecsrender.gl.IBO;
import xenoframium.ecsrender.gl.Texture;
import xenoframium.ecsrender.gl.VAO;
import xenoframium.ecsrender.gl.VBO;
import xenoframium.glwrapper.GlProgram;
import xenoframium.glwrapper.GlUniform;

import static org.lwjgl.opengl.GL11.*;

/**
 * Created by chrisjung on 29/09/17.
 */
public class Tex3DRenderable implements Component, AutoCloseable {
    final VAO vao;
    final IBO indexBuffer;
    final int renderMode;
    final int numVertices;
    final Texture texture;
    final boolean isIndexed;

    private final VBO coordVBO = new VBO();
    private final VBO uvVBO = new VBO();

    public Tex3DRenderable(Mesh mesh, int[] indices, Texture texture) {
        isIndexed = true;
        vao = new VAO();
        this.renderMode = mesh.drawType;
        this.texture = texture;
        this.numVertices = indices.length;

        coordVBO.bufferData(mesh.coords);
        uvVBO.bufferData(mesh.uvs);
        indexBuffer = new IBO();
        indexBuffer.bufferData(indices);
        vao.addAttribPointer(coordVBO, 0, 3, GL_FLOAT);
        vao.addAttribPointer(uvVBO, 1, 2, GL_FLOAT);
    }

    public Tex3DRenderable(Mesh mesh, Texture texture) {
        indexBuffer = null;
        isIndexed = false;
        vao = new VAO();
        this.renderMode = mesh.drawType;
        this.texture = texture;
        this.numVertices = mesh.coords.length / 3;

        coordVBO.bufferData(mesh.coords);
        uvVBO.bufferData(mesh.uvs);
        vao.addAttribPointer(coordVBO, 0, 3, GL_FLOAT);
        vao.addAttribPointer(uvVBO, 1, 2, GL_FLOAT);
    }

    @Override
    public void close() throws Exception {
        vao.close();
        indexBuffer.close();
        coordVBO.close();
        uvVBO.close();
    }
}
