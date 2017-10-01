package xenoframium.ecsrender;

import xenoframium.ecs.Component;
import xenoframium.glmath.linearalgebra.Vec3;
import xenoframium.glmath.quaternion.Quat;

/**
 * Created by chrisjung on 29/09/17.
 */
public class RotationComponent implements Component {
    public Quat rot = new Quat();

    public RotationComponent(Quat q) {
        rot = new Quat(q);
    }
}
