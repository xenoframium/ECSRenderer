package xenoframium.ecsrender.renderers;

import xenoframium.ecs.BaseRenderer;
import xenoframium.ecs.Space;
import xenoframium.ecsrender.gl.Camera;
import xenoframium.ecsrender.gl.Projection;

/**
 * Created by chrisjung on 18/12/17.
 */
public class Renderer implements BaseRenderer {
    private final Renderer2D renderer2D;
    private final Renderer3D renderer3D;

    public Renderer(Camera camera, Projection proj3D, Projection proj2D) {
        renderer2D = new Renderer2D(proj2D);
        renderer3D = new Renderer3D(proj3D, camera);
    }

    @Override
    public void render(Space space) {
        renderer3D.render(space);
        renderer2D.render(space);
    }
}
