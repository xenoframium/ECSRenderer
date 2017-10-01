package xenoframium.ecsrender.util;

import xenoframium.ecsrender.gl.Camera;
import xenoframium.ecsrender.gl.Projection;
import xenoframium.ecsrender.system.Window;
import xenoframium.glmath.linearalgebra.Line3;

/**
 * Created by chrisjung on 1/10/17.
 */
public class MouseRayCaster {
    final Window window;
    final Camera cam;
    final Projection proj;

    public  MouseRayCaster(Window window, Camera cam, Projection proj) {
        this.window = window;
        this.cam = cam;
        this.proj = proj;
    }

    public Line3 getRay(float windowX, float windowY) {
        return null;
    }
}
