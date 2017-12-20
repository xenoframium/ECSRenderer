package xenoframium.ecsrender.renderers;

import xenoframium.ecs.*;
import xenoframium.ecsrender.components.RenderComponent3D;
import xenoframium.ecsrender.components.TransformComponent3D;
import xenoframium.ecsrender.gl.Camera;
import xenoframium.ecsrender.gl.Projection;
import xenoframium.glmath.GLM;
import xenoframium.glmath.linearalgebra.Mat4;
import xenoframium.glmath.linearalgebra.Vec4;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

/**
 * Created by chrisjung on 5/12/17.
 */
class Renderer3D {
    private static EntityFilter filter = new BasicFilter(RenderComponent3D.class);

    public Projection projection;
    public Camera camera;

    public Renderer3D(Projection projection, Camera camera) {
        this.projection = projection;
        this.camera = camera;
    }

    public void render(Space space) {
        Set<Entity> entities = space.getEntities(filter);

        class Pair implements Comparable<Pair> {
            Entity e;
            Mat4 mvp;
            float z;

            Pair(Entity e, Mat4 mvp) {
                this.e = e;
                this.mvp = mvp;
                this.z = mvp.mult(new Vec4(0, 0, 0,1)).z;
            }

            @Override
            public int compareTo(Pair o) {
                return Float.compare(z, o.z);
            }
        }

        Mat4 vp = projection.getMat().mult(camera.getMat());

        ArrayList<Pair> transparents = new ArrayList<>();
        for (Entity e : entities) {
            RenderComponent3D comp = e.getComponent(RenderComponent3D.class);
            TransformComponent3D transformComponent = e.getComponent(TransformComponent3D.class);
            if (!transformComponent.isVisible()) {
                continue;
            }

            if (comp.hasTransparency) {
                transparents.add(new Pair(e, GLM.mult(vp, e.getComponent(TransformComponent3D.class).getModelMatrix())));
            } else {
                Mat4 mvp = GLM.mult(vp, e.getComponent(TransformComponent3D.class).getModelMatrix());
                e.getComponent(RenderComponent3D.class).strategy.render(e, mvp);
            }
        }

        Collections.sort(transparents);

        for (Pair p : transparents) {
            p.e.getComponent(RenderComponent3D.class).strategy.render(p.e, p.mvp);
        }
    }
}
