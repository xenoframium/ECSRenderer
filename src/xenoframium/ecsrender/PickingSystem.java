package xenoframium.ecsrender;

import org.lwjgl.glfw.GLFW;
import xenoframium.ecs.*;
import xenoframium.ecsrender.universalcomponents.PositionComponent;
import xenoframium.ecsrender.universalcomponents.PositioningComponent;
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

    private static final int BUCKET_SIZE = 4;

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
            PositionComponent pcc = entity.getComponent(PositionComponent.class);
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
    public void update(EntityManager entityManager, double deltaTime, double time) {
        float maxT = Float.NEGATIVE_INFINITY;
        CursorPosition cp = inp.getCursorPosition();
        Vec4 ndc = NDCCoord.getAsNDC(window, cp.x, cp.y, -1);
        if (window.isCursorDisabled()) {
            ndc = new Vec4(0, 0, -1, 1);
        }
        Mat4 ip = projection.getMat().inv();
        Vec4 ws1 = ip.mult(ndc);
        ws1.div(ws1.w);
        Line3 cameraSpaceline = GLM.lineFromPoints(new Vec3(0, 0, 0), new Vec3(ws1.x, ws1.y, ws1.z));
        Mat4 ic = camera.getMat().inv();
        Vec3 cameraPos = new Vec3(ic.m[3][0], ic.m[3][1], ic.m[3][2]);
        Set<Entity> collidables = entityMapping.getInRange(cameraPos, reach);

        PickableComponent collisionComponent = null;
        Entity pickedEntity = null;
        Vec3 rayIntersection = null;
        Vec3 modelIntersection = null;
        Vec3 normal = null;
        int tc = 0;
        double currentTime = GLFW.glfwGetTime();
        for (Entity collidable : collidables) {
            PickableComponent comp = collidable.getComponent(PickableComponent.class);
            Mat4 modelMatrix = new Mat4();
            if (collidable.hasComponents(PositioningComponent.class)) {
                modelMatrix = collidable.getComponent(PositioningComponent.class).getModelMatrix();
            }

            Mat4 mv = camera.getMat().mult(modelMatrix);

            Vec4 modelOriginPos = new Vec4(mv.m[3][0], mv.m[3][1], mv.m[3][2], 1);
            Vec4 maxDist = mv.mult(new Vec4(comp.furthestPoint, 1)).subt(modelOriginPos);
            float r2 = new Vec3(maxDist).magSq();
            float dist2 = mv.m[3][0]*mv.m[3][0] + mv.m[3][1]*mv.m[3][1] + mv.m[3][2]*mv.m[3][2];
            if (dist2 - r2 > reach*reach) {
                continue;
            }

            float LOC2 = modelOriginPos.z*modelOriginPos.z;
            if (LOC2 - dist2 + r2 < 0) {
                continue;
            }

            Mat4 imv = new Mat4(mv).inv();

            Triangle[] mesh = comp.triangles;

            for (Triangle triangle : mesh) {
                Plane trianglePlane = GLM.planeFromTriangle(triangle);
                Plane cameraSpacePlane = new Plane(trianglePlane).transform(mv);
                if (cameraSpacePlane.n.dot(cameraSpaceline.a) < 0) {
                    continue;
                }
                tc++;
                Vec4 cameraSpaceIntersection = new Vec4(GLM.findLinePlaneIntersection(cameraSpaceline, cameraSpacePlane), 1);
                Vec3 modelSpaceIntersection = new Vec3(imv.mult(cameraSpaceIntersection));
                if (GLM.isPointInTriangle(triangle, modelSpaceIntersection)) {
                    float cameraSpaceT;

                    if (cameraSpaceline.a.z != 0) {
                        cameraSpaceT = new Vec3(cameraSpaceIntersection).subt(cameraSpaceline.r0).z / cameraSpaceline.a.z;
                    } else if (cameraSpaceline.a.y != 0) {
                        cameraSpaceT = new Vec3(cameraSpaceIntersection).subt(cameraSpaceline.r0).y / cameraSpaceline.a.y;
                    } else {
                        cameraSpaceT = new Vec3(cameraSpaceIntersection).subt(cameraSpaceline.r0).x / cameraSpaceline.a.x;
                    }

                    if (maxT < cameraSpaceT && cameraSpaceT < 0) {
                        rayIntersection = new Vec3(cameraSpaceIntersection);
                        modelIntersection = modelSpaceIntersection;
                        maxT = cameraSpaceT;
                        collisionComponent = comp;
                        pickedEntity = collidable;
                        normal = trianglePlane.n;
                    }
                }
            }
        }

        if (collisionComponent != null && rayIntersection.magSq() < reach*reach) {
            collisionComponent.callback.onPick(pickedEntity, modelIntersection, normal);
        }
    }
}
