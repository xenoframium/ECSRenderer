package xenoframium.ecsrender.system;

import xenoframium.glmath.linearalgebra.Vec3;
import xenoframium.glmath.linearalgebra.Vec4;

/**
 * Created by chrisjung on 1/10/17.
 */
public class NDCCoord {
    public static Vec4 getAsNDC(Window window, float windowX, float windowY, float zco) {
        Vec4 ndc = new Vec4(0, 0, -1, 1);
        ndc.x = windowX / window.width * 2 - 1;
        ndc.y = -windowY / window.height * 2 + 1;
        ndc.z = zco;
        return ndc;
    }
}
