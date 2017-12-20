package xenoframium.ecsrender.gl;

import xenoframium.glmath.GLM;
import xenoframium.glmath.linearalgebra.Mat4;

/**
 * Created by chrisjung on 29/09/17.
 */
public class Projection {
    Mat4 perspectiveMatrix;

    private Projection() {};

    public static Projection createPerspectiveProjection(float fov, float aspect, float near, float far) {
        Projection proj = new Projection();
        proj.perspectiveMatrix = GLM.perspective(fov, aspect, near, far);
        return proj;
    }

    public static Projection createOrthoProjection(float width, float height, float near, float far) {
        Projection proj = new Projection();
        proj.perspectiveMatrix = GLM.ortho(width, height, near, far);
        return proj;
    }

    public void reprojectPerspective(float fov, float aspect, float near, float far) {
        perspectiveMatrix = GLM.perspective(fov, aspect, near, far);
    }

    public void reprojectOrtho(float width, float height, float near, float far) {
        perspectiveMatrix = GLM.ortho(width, height, near, far);
    }

    public Mat4 getMat() {
        return new Mat4(perspectiveMatrix);
    }
}
