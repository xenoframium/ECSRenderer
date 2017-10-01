package xenoframium.ecsrender;

import xenoframium.ecs.Component;
import xenoframium.glmath.linearalgebra.Vec3;

/**
 * Created by chrisjung on 29/09/17.
 */
public class PositionComponent implements Component {
    public Vec3 pos = new Vec3(0, 0, 0);

    public PositionComponent(Vec3 v) {
        pos = new Vec3(v);
    }
}
