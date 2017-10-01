package xenoframium.ecsrender.pickable;

import xenoframium.ecs.*;
import xenoframium.ecsrender.PositioningComponent;
import xenoframium.ecsrender.gl.Camera;
import xenoframium.ecsrender.gl.Projection;
import xenoframium.ecsrender.system.CursorPosition;
import xenoframium.ecsrender.system.Input;
import xenoframium.ecsrender.system.NDCCoord;
import xenoframium.ecsrender.system.Window;
import xenoframium.ecsrender.util.BucketedMap;
import xenoframium.glmath.GLM;
import xenoframium.glmath.linearalgebra.*;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by chrisjung on 1/10/17.
 */
public class PickingSystem implements BaseSystem {
    private class PositioningObserver implements Observer {
        Entity e;

        public PositioningObserver(Entity e) {
            this.e = e;
        }

        @Override
        public void update(Component component) {
            PositioningComponent comp = (PositioningComponent) component;
            Vec4 pos = comp.getModelMatrix().mult(new Vec4(0, 0, 0, 1));
            entityMapping.remove(e);
            entityMapping.add(new Vec3(pos), e);
        }
    }

    private static final int BUCKET_SIZE = 8;

    private Set<Entity> entities = new HashSet<>();
    private BucketedMap<Entity> entityMapping = new BucketedMap<>(BUCKET_SIZE);
    private final Window window;
    private final float reach;
    private final Input inp;
    private final Projection projection;
    private final Camera camera;

    public PickingSystem(Window window, Input input, float reach, Projection p, Camera c) {
        this.window = window;
        this.reach = reach;
        this.projection = p;
        this.camera = c;
        this.inp = input;
    }

    @Override
    public void notifyEntityAddition(Entity entity) {
        entities.add(entity);
        Vec4 pos = new Vec4(0, 0, 0, 1);
        if (entity.hasComponents(PositioningComponent.class)) {
            PositioningComponent pc = entity.getComponent(PositioningComponent.class);
            pos = pc.getModelMatrix().mult(new Vec4(0, 0, 0, 1));
            pc.addObserver(new PositioningObserver(entity));
        }
        entityMapping.add(new Vec3(pos), entity);
    }

    @Override
    public void notifyEntityRemoval(Entity entity) {
        entities.remove(entity);
        entityMapping.remove(entity);
    }

    @Override
    public void update(EntityManager entityManager, long deltaTime, long time) {
        CursorPosition cp = inp.getCursorPosition();
        Vec4 ndc = NDCCoord.getAsNDC(window, cp.x, cp.y, -1);
        Vec4 ndc2 = NDCCoord.getAsNDC(window, cp.x, cp.y, 1);
        Mat4 ivp = GLM.mult(projection.getMat(), camera.getMat()).inv();
        Vec4 ws1 = ivp.mult(ndc);
        Vec4 ws2 = ivp.mult(ndc2);
        ws1 = ws1.div(ws1.w);
        ws2 = ws2.div(ws2.w);
        Line3 lin = GLM.lineFromPoints(new Vec3(ws1.x, ws1.y, ws1.z), new Vec3(ws2.x, ws2.y, ws2.z));
        Mat4 invCam = camera.getMat().inv();
        Vec3 cameraPos = new Vec3(invCam.m[3][0], invCam.m[3][1], invCam.m[3][2]);
        Set<Entity> collidables = entityMapping.getInRange(cameraPos, reach);

        PickableComponent collisionComponent = null;
        float minZ = Float.MAX_VALUE;

        for (Entity collidable : collidables) {
            PickableComponent comp = collidable.getComponent(PickableComponent.class);
            Triangle[] mesh = comp.triangles;
            for (Triangle t : mesh) {
                Vec4 intersection = new Vec4(GLM.findLinePlaneIntersection(lin, GLM.planeFromTriangle(t)), 1);
                if (collidable.hasComponents(PositioningComponent.class)) {
                    intersection = collidable.getComponent(PositioningComponent.class).getModelMatrix().inv().mult(intersection);
                }
                if (GLM.isPointInTriangle(t, new Vec3(intersection))) {
                    if (intersection.z < minZ) {
                        minZ = intersection.z;
                        collisionComponent = comp;
                    }
                }
            }
        }

        if (collisionComponent != null && minZ < reach) {
            collisionComponent.callback.onPick();
        }

        Triangle tr = new Triangle(new Vec3(0, 0, 0), new Vec3(0, 1, 0), new Vec3(-1, 0, 0));
    }
}
