package xenoframium.ecsrender.picking;

import org.lwjgl.glfw.GLFW;
import xenoframium.ecs.*;
import xenoframium.ecsrender.GraphicsManager;
import xenoframium.ecsrender.components.TransformComponent2D;
import xenoframium.ecsrender.gl.Camera;
import xenoframium.ecsrender.gl.Projection;
import xenoframium.ecsrender.gl.Window;
import xenoframium.ecsrender.input.InputManager;
import xenoframium.glmath.GLM;
import xenoframium.glmath.linearalgebra.*;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by chrisjung on 1/10/17.
*/
public class PickingSystem2D implements BaseSystem {
    private static final EntityFilter filter = new BasicFilter(PickableComponent2D.class);

    private Set<Entity> entities = new HashSet<>();
    private final Window window;
    private final Projection projection;
    private final Camera camera = new Camera(new Vec3(0, 0, -1), new Vec3(0, 0, 0));

    private Entity lastSelected = null;

    public PickingSystem2D(Window window, Projection p) {
        this.window = window;
        this.projection = p;
    }


    @Override
    public void update(Space space, double deltaTime, double time) {
        Set<Entity> entities = space.getEntities(filter);
        Window window = GraphicsManager.getWindow();
        float maxT = Float.NEGATIVE_INFINITY;
        Vec4 ndc = new Vec4(0, 0, -1, 1);
        ndc.x = (float) InputManager.getCursorX() / window.width * 2 - 1;
        ndc.y = (float) -InputManager.getCursorY() / window.height * 2 + 1;
        ndc.z = -1;
        if (window.isCursorDisabled()) {
            ndc = new Vec4(0, 0, -1, 1);
        }
        Mat4 ip = projection.getMat().inv();
        Vec4 ws1 = ip.mult(ndc);
        ws1.div(ws1.w);
        Line3 cameraSpaceline = GLM.lineFromPoints(new Vec3(0, 0, 0), new Vec3(ws1.x, ws1.y, ws1.z));

        Entity currentBest = null;

        Vec2 intPoint = null;

        int tc = 0;
        double currentTime = GLFW.glfwGetTime();
        for (Entity collidable : entities) {
            PickableComponent2D comp = collidable.getComponent(PickableComponent2D.class);
            comp.isSelected = false;
            Mat4 modelMatrix = new Mat4();
            modelMatrix = collidable.getComponent(TransformComponent2D.class).getModelMatrix();

            Mat4 mv = camera.getMat().mult(modelMatrix);
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
                    if (cameraSpaceT < 0 && (currentBest == null || collidable.getComponent(TransformComponent2D.class).compareZ(currentBest.getComponent(TransformComponent2D.class)) <= 0)) {
                        intPoint = new Vec2(modelSpaceIntersection.x, modelSpaceIntersection.y);
                        currentBest = collidable;
                    }
                }
            }
        }
        if (currentBest != null) {
            currentBest.getComponent(PickableComponent2D.class).isSelected = true;
            currentBest.getComponent(PickableComponent2D.class).callback.onPick(currentBest, intPoint);
        }
    }
}
