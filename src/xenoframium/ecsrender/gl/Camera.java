package xenoframium.ecsrender.gl;

import xenoframium.glmath.GLM;
import xenoframium.glmath.linearalgebra.Mat4;
import xenoframium.glmath.linearalgebra.Vec3;

/**
 * Created by chrisjung on 29/09/17.
 */
public class Camera {
    Mat4 viewMatrix;

    public Camera(Vec3 pos, Vec3 target, Vec3 up) {
        viewMatrix = GLM.cameraLookAt(pos, target, up);
    }

    public Camera(Vec3 pos, Vec3 target) {
        viewMatrix = GLM.cameraLookAt(pos, target, new Vec3(0, 1, 0));
    }

    public void transform(Mat4 mat) {
        viewMatrix = GLM.mult(mat, viewMatrix);
    }

    public void move(Vec3 vec) {
        viewMatrix.translate(vec);
    }

    public void reposition(Vec3 pos, Vec3 target, Vec3 up) {
        viewMatrix = GLM.cameraLookAt(pos, target, up);
    }

    public void reposition(Vec3 pos, Vec3 target) {
        viewMatrix = GLM.cameraLookAt(pos, target, new Vec3(0, 1, 0));
    }

    public Mat4 getMat() {
        return new Mat4(viewMatrix);
    }
}
