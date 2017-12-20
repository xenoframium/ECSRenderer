package xenoframium.ecsrender;

import xenoframium.ecs.Entity;
import xenoframium.glmath.linearalgebra.Mat4;

public interface RenderStrategy extends AutoCloseable {
    void render(Entity e, Mat4 mvp);
    void close();
}
