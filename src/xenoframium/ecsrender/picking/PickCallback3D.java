package xenoframium.ecsrender.picking;

import xenoframium.ecs.Entity;
import xenoframium.glmath.linearalgebra.Vec3;

/**
 * Created by chrisjung on 1/10/17.
 */
public interface PickCallback3D {
    void onPick(Entity pickedEntity, Vec3 selectionPoint, Vec3 triangleNormal);
}
