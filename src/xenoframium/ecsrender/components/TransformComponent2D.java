package xenoframium.ecsrender.components;

import xenoframium.ecs.Component;
import xenoframium.ecs.Entity;
import xenoframium.glmath.linearalgebra.Mat4;
import xenoframium.glmath.linearalgebra.Vec2;
import xenoframium.glmath.linearalgebra.Vec3;
import xenoframium.glmath.quaternion.Quat;

import java.util.ArrayList;

/**
 * Created by chrisjung on 10/12/17.
 */
public class TransformComponent2D implements Component {
    private static Vec3 zAxis = new Vec3(0, 0, -1);
    private Vec3 pos3 = new Vec3(0, 0, 0);
    private Vec3 scale3 = new Vec3(1, 1, 1);

    private Entity parent = null;
    private boolean isVisible = true;

    public Vec2 pos = new Vec2(0, 0);
    public Vec2 scale = new Vec2(1, 1);
    public float rot = 0;
    public float z = 0;

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
            if (!parent.hasComponent(TransformComponent2D.class)) {
                return;
            }
            lastPar = parent.getComponent(TransformComponent2D.class).getParent();
            if (lastPar == null) {
                return;
            }
            if (lastPar == parent) {
                throw new RuntimeException("Cycle detected in transform components.");
            }
        }
    }

    public int compareZ(TransformComponent2D component) {
        ArrayList<TransformComponent2D> a = new ArrayList<>();
        ArrayList<TransformComponent2D> b = new ArrayList<>();
        a.add(this);
        b.add(component);
        Entity par = parent;
        while (par != null && par.hasComponent(TransformComponent2D.class)) {
            TransformComponent2D p = par.getComponent(TransformComponent2D.class);
            a.add(p);
            par = p.parent;
        }
        par = component.parent;
        while (par != null && par.hasComponent(TransformComponent2D.class)) {
            TransformComponent2D p = par.getComponent(TransformComponent2D.class);
            b.add(p);
            par = p.parent;
        }
        for (int i = 0; i < Math.min(a.size(), b.size()); i++) {
            if (a.get(i) != b.get(i)) {
                return Float.compare(a.get(i).z, b.get(i).z);
            }
        }
        if (a.size() == b.size()) {
            return Float.compare(a.get(a.size()-1).z, b.get(b.size()-1).z);
        } else if (a.size() < b.size()) {
            return Float.compare(0, b.get(b.size()-1).z);
        } else {
            return Float.compare(a.get(a.size()-1).z, 0);
        }
    }

    public Mat4 getModelMatrix() {
        pos3.x = pos.x;
        pos3.y = pos.y;
        scale3.x = scale.x;
        scale3.y = scale.y;
        if (parent != null) {
            TransformComponent2D trans = parent.getComponent(TransformComponent2D.class);
            return new Mat4().translate(pos3).rotate(new Quat(zAxis, rot)).scale(scale3).mult(trans.getModelMatrix());
        }
        return new Mat4().translate(pos3).rotate(new Quat(zAxis, rot)).scale(scale3);
    }

    public boolean isVisible() {
        if (parent != null) {
            TransformComponent2D trans = parent.getComponent(TransformComponent2D.class);
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
