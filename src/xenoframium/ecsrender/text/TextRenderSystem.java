package xenoframium.ecsrender.text;

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

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * Created by chrisjung on 29/09/17.
 */
public class TextRenderSystem implements BaseSystem {
    static final GlProgram textShader;
    static final GlUniform mvpUniform;
    static final GlUniform textColourUniform;

    static {
        GlShader vert = new GlShader(GL_VERTEX_SHADER, new File("shaders/textVert.vert"));
        GlShader frag = new GlShader(GL_FRAGMENT_SHADER, new File("shaders/textFrag.frag"));

        textShader = new GlProgram(vert, frag);

        mvpUniform = new GlUniform(textShader, "mvp");
        textColourUniform = new GlUniform(textShader, "textColour");
    }

    private Set<Entity> entities = new HashSet<>();
    private Camera camera;
    private Projection projection;

    public TextRenderSystem(Projection projection, Camera camera) {
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
        textShader.use();
        Mat4 vp = GLM.mult(projection.getMat(), camera.getMat());
        for (Entity e : entities) {
            TextRenderable renderable = e.getComponent(TextRenderable.class);
            renderable.texture.bindTexture();
            renderable.vao.bind();

            Mat4 model = new Mat4();
            PositioningComponent pc = e.getComponent(PositioningComponent.class);
            if (pc != null) {
                model = pc.getModelMatrix();
            }

            Mat4 mvp = GLM.mult(vp, model);
            glUniform4fv(textColourUniform.getLocation(), renderable.colour.asArr());
            glUniformMatrix4fv(mvpUniform.getLocation(), false, mvp.asArr());
            renderable.indexBuffer.bind();
            glDrawElements(renderable.renderMode, renderable.numVertices, GL_UNSIGNED_INT, NULL);
        }
    }
}
