package xenoframium.ecsrender.tex2drenderable;

import xenoframium.ecs.Component;
import xenoframium.ecsrender.IndexedMesh;
import xenoframium.ecsrender.Mesh;
import xenoframium.ecsrender.gl.IBO;
import xenoframium.ecsrender.gl.Texture;
import xenoframium.ecsrender.gl.VAO;
import xenoframium.ecsrender.gl.VBO;

import static org.lwjgl.opengl.GL11.GL_FLOAT;

/**
 * Created by chrisjung on 3/10/17.
 */
public class Tex2DRenderable implements Component, AutoCloseable {
    final VAO vao;
    final IBO indexBuffer;
    final int renderMode;
    final int numVertices;
    final Texture texture;
    final boolean isIndexed;

    private final VBO coordVBO = new VBO();
    private final VBO uvVBO = new VBO();

    private boolean isVisible = true;

    public float z;

    public Tex2DRenderable(IndexedMesh mesh, Texture texture, float zValue) {
        isIndexed = true;
        vao = new VAO();
        this.renderMode = mesh.drawType;
        this.texture = texture;
        this.numVertices = mesh.indices.length;
        this.z = zValue;

        coordVBO.bufferData(mesh.coords);
        uvVBO.bufferData(mesh.uvs);
        indexBuffer = new IBO();
        indexBuffer.bufferData(mesh.indices);
        vao.addAttribPointer(coordVBO, 0, 3, GL_FLOAT);
        vao.addAttribPointer(uvVBO, 1, 2, GL_FLOAT);
    }

    public Tex2DRenderable(Mesh mesh, Texture texture, float zValue) {
        indexBuffer = null;
        isIndexed = false;
        vao = new VAO();
        this.renderMode = mesh.drawType;
        this.texture = texture;
        this.numVertices = mesh.coords.length / 3;
        this.z = zValue;

        coordVBO.bufferData(mesh.coords);
        uvVBO.bufferData(mesh.uvs);
        vao.addAttribPointer(coordVBO, 0, 3, GL_FLOAT);
        vao.addAttribPointer(uvVBO, 1, 2, GL_FLOAT);
    }

    @Override
    public void close() throws Exception {
        vao.close();
        if (indexBuffer != null) {
            indexBuffer.close();
        }
        coordVBO.close();
        uvVBO.close();
    }

    public void show() {
        isVisible = true;
    }

    public void hide() {
        isVisible = false;
    }

    public boolean isVisible() {
        return isVisible;
    }
}
