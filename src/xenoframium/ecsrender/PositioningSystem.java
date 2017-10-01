package xenoframium.ecsrender;

import xenoframium.ecs.BaseSystem;
import xenoframium.ecs.Entity;
import xenoframium.ecs.EntityManager;
import xenoframium.glmath.GLM;
import xenoframium.glmath.linearalgebra.Mat4;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by chrisjung on 1/10/17.
 */
public class PositioningSystem implements BaseSystem {
    private Map<Entity, Long> entityUpdateTimes = new HashMap<>();

    @Override
    public void notifyEntityAddition(Entity entity) {
        entityUpdateTimes.put(entity, -1L);
    }

    @Override
    public void notifyEntityRemoval(Entity entity) {
        entityUpdateTimes.remove(entity);
    }

    private Mat4 dfs(Entity e, Long time) {
        if (entityUpdateTimes.get(e) == time) {
            return e.getComponent(PositioningComponent.class).modelMatrix;
        }

        PositioningComponent posComp = e.getComponent(PositioningComponent.class);

        Mat4 model = new Mat4();
        if (posComp == null) {
            return model;
        }

        PositionComponent pos = e.getComponent(PositionComponent.class);
        RotationComponent rot = e.getComponent(RotationComponent.class);
        ScaleComponent scale = e.getComponent(ScaleComponent.class);

        if (pos != null) {
            model.translate(pos.pos);
        }
        if (rot != null) {
            model.rotate(rot.rot);
        }
        if (scale != null) {
            model.scale(scale.scale);
        }

        if (e.hasComponents(AttachmentComponent.class)) {
            model.mult(dfs(e.getComponent(AttachmentComponent.class).parent, time));
        }

        if (posComp.modelMatrix.equals(model)) {
            return model;
        }

        posComp.notifyObservers();
        posComp.modelMatrix = model;
        entityUpdateTimes.replace(e, time);

        return model;
    }

    @Override
    public void update(EntityManager entityManager, long dT, long t) {
        for (Entity e : entityUpdateTimes.keySet()) {
            dfs(e, t);
        }
    }
}
