package xenoframium.ecsrender.pickable;

import xenoframium.ecs.Component;
import xenoframium.ecsrender.Mesh;
import xenoframium.glmath.linearalgebra.Triangle;
import xenoframium.glmath.linearalgebra.Vec3;

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

class UnsupportedDrawTypeException extends RuntimeException {
    public UnsupportedDrawTypeException(String msg) {
        super(msg);
    }
}

/**
 * Created by chrisjung on 1/10/17.
 */
public class PickableComponent implements Component {
    final PickCallback callback;
    final Triangle[] triangles;
    final Vec3 furthestPoint;
    public PickableComponent(Mesh mesh, PickCallback callback, int drawType) {
        this.callback = callback;
        switch (drawType) {
            case GL_TRIANGLES:
                triangles = new Triangle[mesh.coords.length/9];
                for (int i = 0; i < mesh.coords.length; i+=9) {
                    Vec3 v1 = new Vec3(mesh.coords[i], mesh.coords[i+1], mesh.coords[i+2]);
                    Vec3 v2 = new Vec3(mesh.coords[i+3], mesh.coords[i+4], mesh.coords[i+5]);
                    Vec3 v3 = new Vec3(mesh.coords[i+6], mesh.coords[i+7], mesh.coords[i+8]);
                    triangles[i/9] = new Triangle(v1, v2, v3);
                }
                break;
            case GL_TRIANGLE_STRIP:
                triangles = new Triangle[mesh.coords.length/3 - 2];
                Vec3 v1 = new Vec3(mesh.coords[0], mesh.coords[1], mesh.coords[2]);
                Vec3 v2 = new Vec3(mesh.coords[3], mesh.coords[4], mesh.coords[5]);
                for (int i = 0; i < mesh.coords.length/3-2; i++) {
                    Vec3 v3 = new Vec3(mesh.coords[i*3+6], mesh.coords[i*3+7], mesh.coords[i*3+8]);
                    triangles[i] = new Triangle(v1, v2, v3);
                    v1 = v2;
                    v2 = v3;
                }
                break;
            default:
                throw new UnsupportedDrawTypeException("Unsupported draw type: " + drawType);
        }
        Vec3 best = new Vec3(0, 0, 0);
        float bestMagSq = 0;
        for (int i = 0; i < mesh.coords.length; i+=3) {
            Vec3 next = new Vec3(mesh.coords[i], mesh.coords[i+1], mesh.coords[i+2]);
            float nMagSq = next.mag();
            if (nMagSq > bestMagSq) {
                best = next;
                bestMagSq = nMagSq;
            }
        }
        furthestPoint = best;
    }
}
