package xenoframium.ecsrender.tex2drenderable;

import xenoframium.ecs.BaseSystem;
import xenoframium.ecs.Entity;
import xenoframium.ecs.EntityManager;
import xenoframium.ecsrender.PositioningComponent;
import xenoframium.ecsrender.gl.Camera;
import xenoframium.ecsrender.gl.Projection;
import xenoframium.ecsrender.tex3drenderable.Tex3DRenderable;
import xenoframium.glmath.GLM;
import xenoframium.glmath.linearalgebra.Mat4;
import xenoframium.glwrapper.GlProgram;
import xenoframium.glwrapper.GlShader;
import xenoframium.glwrapper.GlUniform;

import java.io.File;
import java.util.*;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * Created by chrisjung on 29/09/17.
 */
public class Tex2DRenderSystem implements BaseSystem {
    private static GlProgram shaderProgram;
    private static GlUniform mvpUniform;

    static {
        GlShader vert = new GlShader(GL_VERTEX_SHADER, new File("shaders/alphaTexVert.vert"));
        GlShader frag = new GlShader(GL_FRAGMENT_SHADER, new File("shaders/alphaTexFrag.frag"));

        shaderProgram = new GlProgram(vert, frag);

        mvpUniform = new GlUniform(shaderProgram, "mvp");
    }

    private Set<Entity> entities = new HashSet<>();
    private Camera camera;
    private Projection projection;

    public Tex2DRenderSystem(Projection projection, Camera camera) {
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
    public void update(EntityManager entityManager, double deltaT, double timestamp) {
        shaderProgram.use();
        Mat4 vp = GLM.mult(projection.getMat(), camera.getMat());
        Entity[] orderedEntites = new Entity[entities.size()];
        Map<Entity, Float> zVals = new HashMap<>();

        int ind = 0;
        for (Entity e : entities) {
            orderedEntites[ind] = e;
            zVals.put(e, e.getComponent(Tex2DRenderable.class).z);
            ind++;
        }

        Arrays.sort(orderedEntites, new Comparator<Entity>() {
            @Override
            public int compare(Entity o1, Entity o2) {
                return zVals.get(o1).compareTo(zVals.get(o2));
            }
        });

        for (Entity e : entities) {
            Tex2DRenderable renderable = e.getComponent(Tex2DRenderable.class);
            if (!renderable.isVisible()) {
                continue;
            }
            renderable.texture.bindTexture();
            renderable.vao.bind();

            Mat4 model = new Mat4();
            PositioningComponent pc = e.getComponent(PositioningComponent.class);
            if (pc != null) {
                model = pc.getModelMatrix();
            }

            Mat4 mvp = GLM.mult(vp, model);
            glDisable(GL_DEPTH_TEST);
            glUniformMatrix4fv(mvpUniform.getLocation(), false, mvp.asArr());
            if (!renderable.isIndexed) {
                glDrawArrays(renderable.renderMode, 0, renderable.numVertices);
            } else {
                renderable.indexBuffer.bind();
                glDrawElements(renderable.renderMode, renderable.numVertices, GL_UNSIGNED_INT, NULL);
            }
            glEnable(GL_DEPTH_TEST);
        }
    }
}
