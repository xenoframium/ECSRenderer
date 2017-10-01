package xenoframium.ecsrender;

import xenoframium.ecs.Component;
import xenoframium.glmath.linearalgebra.Vec3;

/**
 * Created by chrisjung on 29/09/17.
 */
public class ScaleComponent implements Component {
    public Vec3 scale = new Vec3(1,1,1);

    public ScaleComponent(Vec3 v) {
        scale = new Vec3(v);
    }
}
