package xenoframium.ecsrender;

import xenoframium.glmath.linearalgebra.Vec3;
import xenoframium.glmath.linearalgebra.Vec4;

import java.util.Arrays;

/**
 * Created by chrisjung on 11/12/17.
 */
public class Mesh {
    public static class VertexIndexOutOfBounds extends RuntimeException {
        private VertexIndexOutOfBounds(int index) {
            super("Vertex index out of bounds: " + index);
        }
    }

    private final int renderMode;
    private final float[] vertices;
    private final int[] indices;

    public Mesh(float[] vertices, int renderMode) {
        this.vertices = Arrays.copyOf(vertices, vertices.length);
        this.renderMode = renderMode;
        indices = new int[vertices.length / 3];
        for (int i = 0; i < indices.length; i++) {
            indices[i] = i;
        }
    }

    public Mesh(float[] vertices, int[] indices, int renderMode) {
        this.vertices = Arrays.copyOf(vertices, vertices.length);
        this.indices = Arrays.copyOf(indices, indices.length);
        this.renderMode = renderMode;
    }

    public Vec3 getVertex(int index) {
        int vertexIndex = indices[index];
        return new Vec3(vertices[vertexIndex*3], vertices[vertexIndex*3+1], vertices[vertexIndex*3+2]);
    }

    public int getRenderMode() {
        return renderMode;
    }

    public float[] getVertices() {
        return Arrays.copyOf(vertices, vertices.length);
    }

    public int[] getIndices() {
        return Arrays.copyOf(indices, indices.length);
    }

    public int getNumVertices() {
        return indices.length;
    }
}
