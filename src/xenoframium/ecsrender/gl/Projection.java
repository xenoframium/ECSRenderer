package xenoframium.ecsrender.gl;

import xenoframium.glmath.GLM;
import xenoframium.glmath.linearalgebra.Mat4;

/**
 * Created by chrisjung on 29/09/17.
 */
public class Projection {
    Mat4 perspectiveMatrix;

    public Projection(float fov, float aspect, float near, float far) {
        perspectiveMatrix = GLM.perspective(fov, aspect, near, far);
    }

    public void reproject(float fov, float aspect, float near, float far) {
        perspectiveMatrix = GLM.perspective(fov, aspect, near, far);
    }

    public Mat4 getMat() {
        return new Mat4(perspectiveMatrix);
    }
}
