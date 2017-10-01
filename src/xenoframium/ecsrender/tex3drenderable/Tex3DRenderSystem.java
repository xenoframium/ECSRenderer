package xenoframium.ecsrender.tex3drenderable;

import xenoframium.ecs.BaseSystem;
import xenoframium.ecs.Entity;
import xenoframium.ecs.EntityManager;
import xenoframium.ecsrender.PositionComponent;
import xenoframium.ecsrender.PositioningComponent;
import xenoframium.ecsrender.RotationComponent;
import xenoframium.ecsrender.ScaleComponent;
import xenoframium.ecsrender.gl.Camera;
import xenoframium.ecsrender.gl.Projection;
import xenoframium.glmath.GLM;
import xenoframium.glmath.linearalgebra.Mat4;
import xenoframium.glwrapper.GlProgram;
import xenoframium.glwrapper.GlShader;
import xenoframium.glwrapper.GlUniform;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import static org.lwjgl.system.MemoryUtil.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL14.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL21.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL31.*;
import static org.lwjgl.opengl.GL32.*;
import static org.lwjgl.opengl.GL33.*;
import static org.lwjgl.opengl.GL40.*;
import static org.lwjgl.opengl.GL41.*;
import static org.lwjgl.opengl.GL42.*;
import static org.lwjgl.opengl.GL43.*;
import static org.lwjgl.opengl.GL44.*;
import static org.lwjgl.opengl.GL45.*;

/**
 * Created by chrisjung on 29/09/17.
 */
public class Tex3DRenderSystem implements BaseSystem {
    private static GlProgram shaderProgram;
    private static GlUniform mvpUniform;

    static {
        GlShader vert = new GlShader(GL_VERTEX_SHADER, new File("shaders/texVert.vert"));
        GlShader frag = new GlShader(GL_FRAGMENT_SHADER, new File("shaders/texFrag.frag"));

        shaderProgram = new GlProgram(vert, frag);

        mvpUniform = new GlUniform(shaderProgram, "mvp");
    }

    private Set<Entity> entities = new HashSet<>();
    private Camera camera;
    private Projection projection;

    public Tex3DRenderSystem(Projection projection, Camera camera) {
        this.camera = camera;
        this.projection = projection;
    }

    @Override
    public void notifyEntityAddition(Entity entity) {
        entities.add(entity);
    }

    @Override
    public void notifyEntityRemoval(Entity entity) {
        entities.remove(entity);
    }

    @Override
    public void update(EntityManager entityManager, long deltaT, long timestamp) {
        shaderProgram.use();
        Mat4 vp = GLM.mult(projection.getMat(), camera.getMat());
        for (Entity e : entities) {
            Tex3DRenderable renderable = e.getComponent(Tex3DRenderable.class);
            renderable.texture.bindTexture();
            renderable.vao.bind();

            Mat4 model = new Mat4();
            PositioningComponent pc = e.getComponent(PositioningComponent.class);
            if (pc != null) {
                model = pc.getModelMatrix();
            }

            Mat4 mvp = GLM.mult(vp, model);
            glUniformMatrix4fv(mvpUniform.getLocation(), false, mvp.asArr());
            if (!renderable.isIndexed) {
                glDrawArrays(renderable.renderMode, 0, renderable.numVertices);
            } else {
                renderable.indexBuffer.bind();
                glDrawElements(renderable.renderMode, renderable.numVertices, GL_UNSIGNED_INT, NULL);
            }
        }
    }
}
