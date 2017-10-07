package xenoframium.ecsrender.universalcomponents;

import xenoframium.ecs.BaseSystem;
import xenoframium.ecs.Entity;
import xenoframium.ecs.EntityManager;
import xenoframium.glmath.linearalgebra.Mat4;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by chrisjung on 1/10/17.
 */
public class PositioningSystem implements BaseSystem {
    private Map<Entity, Double> entityUpdateTimes = new HashMap<>();

    @Override
    public void notifyEntityAddition(Entity entity) {
        entityUpdateTimes.put(entity, -2.0);
        dfs(entity, -1.0);
    }

    @Override
    public void notifyEntityRemoval(Entity entity) {
        entityUpdateTimes.remove(entity);
    }

    private Mat4 dfs(Entity e, Double time) {
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
    public void update(EntityManager entityManager, double dT, double t) {
        for (Entity e : entityUpdateTimes.keySet()) {
            dfs(e, t);
        }
    }
}