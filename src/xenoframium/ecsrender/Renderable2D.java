package xenoframium.ecsrender;

import xenoframium.ecs.Component;
import xenoframium.ecsrender.gl.IBO;
import xenoframium.ecsrender.gl.Texture;
import xenoframium.ecsrender.gl.VAO;
import xenoframium.ecsrender.gl.VBO;
import xenoframium.glmath.linearalgebra.Vec4;

import static org.lwjgl.opengl.GL11.GL_FLOAT;

enum RenderableType {
    RENDERABLE_TYPE_TEXTURED, RENDERABLE_TYPE_TEXT, RENDERABLE_TYPE_COLOURED;
}

/**
 * Created by chrisjung on 7/10/17.
 */
public class Renderable2D implements Component, AutoCloseable {
    final VAO vao;
    final int renderMode;
    final int numVertices;

    final Texture texture;
    final IBO indexBuffer;
    final TextInfo textInfo;
    final Vec4 colour;

    final boolean isIndexed;

    final RenderableType renderableType;

    private final VBO coordVBO;
    private final VBO uvVBO;

    private boolean isVisible = true;

    public float z;

    public Renderable2D(Mesh mesh, Texture texture, float zValue) {
        this.renderableType = RenderableType.RENDERABLE_TYPE_TEXTURED;
        this.isIndexed = mesh.hasIndices;

        this.texture = texture;
        if (isIndexed) {
            this.numVertices = mesh.indices.length;
            this.indexBuffer = new IBO();
            indexBuffer.bufferData(mesh.indices);
        } else {
            this.indexBuffer = null;
            this.numVertices = mesh.coords.length / 3;
        }

        this.z = zValue;
        this.renderMode = mesh.drawType;
        this.textInfo = null;
        this.colour = null;

        this.vao = new VAO();
        this.coordVBO = new VBO();
        this.uvVBO = new VBO();

        coordVBO.bufferData(mesh.coords);
        uvVBO.bufferData(mesh.uvs);
        vao.addAttribPointer(coordVBO, 0, 3, GL_FLOAT);
        vao.addAttribPointer(uvVBO, 1, 2, GL_FLOAT);
    }

    public Renderable2D(TextInfo info, float zValue) {
        Mesh mesh = info.mesh;

        this.renderableType = RenderableType.RENDERABLE_TYPE_TEXT;
        this.isIndexed = mesh.hasIndices;

        this.texture = info.texture;
        if (isIndexed) {
            this.numVertices = mesh.indices.length;
            this.indexBuffer = new IBO();
            indexBuffer.bufferData(mesh.indices);
        } else {
            this.indexBuffer = null;
            this.numVertices = mesh.coords.length / 3;
        }

        this.z = zValue;
        this.renderMode = mesh.drawType;
        this.textInfo = new TextInfo(info);
        this.colour = null;

        this.vao = new VAO();
        this.coordVBO = new VBO();
        this.uvVBO = new VBO();

        coordVBO.bufferData(mesh.coords);
        uvVBO.bufferData(mesh.uvs);
        vao.addAttribPointer(coordVBO, 0, 3, GL_FLOAT);
        vao.addAttribPointer(uvVBO, 1, 2, GL_FLOAT);
    }

    public Renderable2D(Mesh mesh, Vec4 colour, float zValue) {
        this.renderableType = RenderableType.RENDERABLE_TYPE_COLOURED;
        this.isIndexed = mesh.hasIndices;

        this.colour = colour;
        if (isIndexed) {
            this.numVertices = mesh.indices.length;
            this.indexBuffer = new IBO();
            indexBuffer.bufferData(mesh.indices);
        } else {
            this.indexBuffer = null;
            this.numVertices = mesh.coords.length / 3;
        }

        this.z = zValue;
        this.renderMode = mesh.drawType;
        this.textInfo = null;
        this.texture = null;

        this.vao = new VAO();
        this.coordVBO = new VBO();
        this.uvVBO = null;

        coordVBO.bufferData(mesh.coords);
        vao.addAttribPointer(coordVBO, 0, 3, GL_FLOAT);
    }

    @Override
    public void close() {
        vao.close();
        if (indexBuffer != null) {
            indexBuffer.close();
        }
        coordVBO.close();
        if (uvVBO != null) {
            uvVBO.close();
        }
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
