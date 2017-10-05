package xenoframium.ecsrender.text;

import xenoframium.ecs.Component;
import xenoframium.ecsrender.IndexedMesh;
import xenoframium.ecsrender.Mesh;
import xenoframium.ecsrender.gl.IBO;
import xenoframium.ecsrender.gl.Texture;
import xenoframium.ecsrender.gl.VAO;
import xenoframium.ecsrender.gl.VBO;
import xenoframium.glmath.linearalgebra.Vec4;

import static org.lwjgl.opengl.GL11.GL_FLOAT;

/**
 * Created by chrisjung on 29/09/17.
 */
public class Transparent3DRenderable implements Component, AutoCloseable {
    final VAO vao;
    final IBO indexBuffer;
    final int renderMode;
    final int numVertices;
    final Texture texture;
    final boolean isIndexed;
    final boolean isText;
    final Vec4 textColour;

    private final VBO coordVBO = new VBO();
    private final VBO uvVBO = new VBO();

    private boolean isVisible = true;

    public Transparent3DRenderable(IndexedMesh indexedMesh, Texture texture) {
        textColour = null;
        isText = false;
        isIndexed = true;
        vao = new VAO();
        this.renderMode = indexedMesh.drawType;
        this.texture = texture;
        this.numVertices = indexedMesh.indices.length;

        coordVBO.bufferData(indexedMesh.coords);
        uvVBO.bufferData(indexedMesh.uvs);
        indexBuffer = new IBO();
        indexBuffer.bufferData(indexedMesh.indices);
        vao.addAttribPointer(coordVBO, 0, 3, GL_FLOAT);
        vao.addAttribPointer(uvVBO, 1, 2, GL_FLOAT);
    }

    public Transparent3DRenderable(Mesh mesh, Texture texture) {
        textColour = null;
        isText = false;
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

    public Transparent3DRenderable(TextInfo info) {
        textColour = info.colour;
        isText = true;
        isIndexed = true;
        vao = new VAO();
        this.renderMode = info.mesh.drawType;
        this.texture = info.texture;
        this.numVertices = info.mesh.indices.length;

        coordVBO.bufferData(info.mesh.coords);
        uvVBO.bufferData(info.mesh.uvs);
        indexBuffer = new IBO();
        indexBuffer.bufferData(info.mesh.indices);
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
