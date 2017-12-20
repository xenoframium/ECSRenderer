package xenoframium.ecsrender.components;

import xenoframium.ecs.Component;
import xenoframium.ecs.Entity;
import xenoframium.glmath.linearalgebra.Mat4;
import xenoframium.glmath.linearalgebra.Vec3;
import xenoframium.glmath.quaternion.Quat;

/**
 * Created by chrisjung on 10/12/17.
 */
public class TransformComponent3D implements Component {
    private Entity parent = null;
    private boolean isVisible = true;

    public Vec3 pos = new Vec3(0, 0, 0);
    public Vec3 scale = new Vec3(1, 1, 1);
    public Quat rot = new Quat();

    public Entity getParent() {
        return parent;
    }

    public void setParent(Entity parent) {
        this.parent = parent;

        if (parent == null) {
            return;
        }

        Entity lastPar = parent;
        while (true) {
            if (!parent.hasComponent(TransformComponent3D.class)) {
                return;
            }
            lastPar = parent.getComponent(TransformComponent3D.class).getParent();
            if (lastPar == null) {
                return;
            }
            if (lastPar == parent) {
                throw new RuntimeException("Cycle detected in transform components.");
            }
        }
    }

    public Mat4 getModelMatrix() {
        if (parent != null) {
            TransformComponent3D trans = parent.getComponent(TransformComponent3D.class);
            return new Mat4().translate(pos).rotate(rot).scale(scale).mult(trans.getModelMatrix());
        }
        return new Mat4().translate(pos).rotate(rot).scale(scale);
    }

    public boolean isVisible() {
        if (parent != null) {
            TransformComponent3D trans = parent.getComponent(TransformComponent3D.class);
            return isVisible && trans.isVisible();
        }
        return isVisible;
    }

    public void show() {
        isVisible = true;
    }

    public void hide() {
        isVisible = false;
    }
}
