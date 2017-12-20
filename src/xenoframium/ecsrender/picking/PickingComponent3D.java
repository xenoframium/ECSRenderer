package xenoframium.ecsrender.picking;

import xenoframium.ecs.Component;
import xenoframium.ecsrender.Mesh;
import xenoframium.glmath.linearalgebra.Triangle;
import xenoframium.glmath.linearalgebra.Vec3;

import static org.lwjgl.opengl.GL11.*;

/**
 * Created by chrisjung on 18/12/17.
 */
public class PickingComponent3D implements Component {
    public static class UnsupportedDrawTypeException extends RuntimeException {
        private UnsupportedDrawTypeException(String msg) {
            super(msg);
        }
    }

    boolean isSelected = false;
    final Triangle[] triangles;
    final PickCallback3D callback;

    public PickingComponent3D(Mesh mesh, PickCallback3D callback) {
        this.callback = callback;

        Vec3[] vertices = new Vec3[mesh.getNumVertices()];
        for (int i = 0; i < vertices.length; i++) {
            vertices[i] = mesh.getVertex(i);
        }

        switch (mesh.getRenderMode()) {
            case GL_TRIANGLES:
                triangles = new Triangle[vertices.length/3];
                for (int i = 0; i < vertices.length/3; i++) {
                    triangles[i] = new Triangle(vertices[i*3], vertices[i*3+1], vertices[i*3+2]);
                }
                break;
            case GL_TRIANGLE_STRIP:
                triangles = new Triangle[vertices.length - 2];
                Vec3 v1 = vertices[0];
                Vec3 v2 = vertices[1];
                for (int i = 0; i < vertices.length-2; i++) {
                    Vec3 v3 = vertices[i+2];
                    if (i%2 == 0) {
                        triangles[i] = new Triangle(v1, v2, v3);
                    } else {
                        triangles[i] = new Triangle(v3, v2, v1);
                    }
                    v1 = v2;
                    v2 = v3;
                }
                break;
            default:
                throw new UnsupportedDrawTypeException("Unsupported render mode: " + mesh.getRenderMode());
        }
    }
}
