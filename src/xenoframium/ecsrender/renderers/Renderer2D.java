package xenoframium.ecsrender.renderers;

import xenoframium.ecs.*;
import xenoframium.ecsrender.components.*;
import xenoframium.ecsrender.gl.Projection;
import xenoframium.glmath.GLM;
import xenoframium.glmath.linearalgebra.Mat4;

import java.util.*;

import static org.lwjgl.opengl.GL11.GL_ALWAYS;
import static org.lwjgl.opengl.GL11.GL_LEQUAL;
import static org.lwjgl.opengl.GL11.glDepthFunc;

/**
 * Created by chrisjung on 5/12/17.
 */
class Renderer2D {
    private static EntityFilter filter = new BasicFilter(RenderComponent2D.class);

    private Map<Entity, ArrayList<EntityComp>> adj = new HashMap<>();
    private Set<EntityComp> roots = new TreeSet<>();

    public Projection projection;

    public Renderer2D(Projection projection) {
        this.projection = projection;
    }


    private class EntityComp implements Comparable<EntityComp> {
        Entity e;
        float z;

        public EntityComp(Entity e) {
            this.e = e;
            this.z = e.getComponent(TransformComponent2D.class).z;
        }

        @Override
        public int compareTo(EntityComp o) {
            return Float.compare(o.z, z);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            EntityComp that = (EntityComp) o;

            return e != null ? e.equals(that.e) : that.e == null;
        }

        @Override
        public int hashCode() {
            return e != null ? e.hashCode() : 0;
        }
    }

    private void buildAdj(Entity entity) {
        if (!adj.containsKey(entity)) {
            adj.put(entity, new ArrayList<>());
            roots.add(new EntityComp(entity));

            TransformComponent2D a = entity.getComponent(TransformComponent2D.class);
            if (a != null && a.getParent() != null) {
                Entity parent = a.getParent();
                buildAdj(parent);
                adj.get(parent).add(new EntityComp(entity));
                roots.remove(new EntityComp(entity));
            }
        }
    }

    private void dfsRender(Entity entity, Mat4 vp) {
        renderEntity(entity, vp);

        ArrayList<EntityComp> dests = adj.get(entity);
        Collections.sort(dests);
        for (EntityComp comp : dests) {
            Entity u = comp.e;
            dfsRender(u, vp);
        }
    }

    private void renderEntity(Entity entity, Mat4 vp) {
        RenderComponent2D renderComponent2D = entity.getComponent(RenderComponent2D.class);
        TransformComponent2D transform = entity.getComponent(TransformComponent2D.class);

        renderComponent2D.strategy.render(entity, GLM.mult(vp, transform.getModelMatrix()));
    }

    public void render(Space space) {
        Set<Entity> entities = space.getEntities(filter);

        for (Entity e : entities) {
            buildAdj(e);
        }

        Mat4 vp = projection.getMat();

        glDepthFunc(GL_ALWAYS);
        for (EntityComp e : roots) {
            dfsRender(e.e, vp);
        }
        glDepthFunc(GL_LEQUAL);
    }
}
