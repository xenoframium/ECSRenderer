package xenoframium.ecsrender.text;

import xenoframium.ecs.BaseSystem;
import xenoframium.ecs.Entity;
import xenoframium.ecs.EntityManager;
import xenoframium.ecsrender.PositionComponent;
import xenoframium.ecsrender.PositioningComponent;
import xenoframium.ecsrender.gl.Camera;
import xenoframium.ecsrender.gl.Projection;
import xenoframium.glmath.GLM;
import xenoframium.glmath.linearalgebra.Mat4;
import xenoframium.glmath.linearalgebra.Vec4;
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
public class Transparent3DRenderSystem implements BaseSystem {
    private static GlProgram shaderProgram;
    private static GlProgram textShaderProgram;
    private static GlUniform mvpUniform;
    private static GlUniform textMvpUniform;
    private static GlUniform textColourUniform;

    static {
        GlShader vert = new GlShader(GL_VERTEX_SHADER, new File("shaders/alphaTexVert.vert"));
        GlShader frag = new GlShader(GL_FRAGMENT_SHADER, new File("shaders/alphaTexFrag.frag"));

        shaderProgram = new GlProgram(vert, frag);

        //vert.delete();
        //frag.delete();

        vert = new GlShader(GL_VERTEX_SHADER, new File("shaders/alphaTextVert.vert"));
        frag = new GlShader(GL_FRAGMENT_SHADER, new File("shaders/alphaTextFrag.frag"));

        textShaderProgram = new GlProgram(vert, frag);

        //vert.delete();
        //frag.delete();

        mvpUniform = new GlUniform(shaderProgram, "mvp");
        textMvpUniform = new GlUniform(textShaderProgram, "mvp");
        textColourUniform = new GlUniform(textShaderProgram, "textColour");
    }

    private Set<Entity> entities = new HashSet<>();
    private Camera camera;
    private Projection projection;

    public Transparent3DRenderSystem(Projection projection, Camera camera) {
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
        Mat4 vp = GLM.mult(projection.getMat(), camera.getMat());
        Entity[] zSortedEntites = new Entity[entities.size()];
        Map<Entity, Float> zVals = new HashMap<>();

        int it = 0;
        for (Entity e : entities) {
            Vec4 referencePos = new Vec4(0, 0, 0, 1);
            if (e.hasComponents(PositioningComponent.class)) {
                referencePos = GLM.mult(vp, e.getComponent(PositioningComponent.class).getModelMatrix()).mult(referencePos);
            }
            zVals.put(e, referencePos.z);
            zSortedEntites[it] = e;
            it++;
        }

        Arrays.sort(zSortedEntites, new Comparator<Entity>() {
            @Override
            public int compare(Entity o1, Entity o2) {
                return zVals.get(o1).compareTo(zVals.get(o2));
            }
        });

        for (Entity e : zSortedEntites) {
            Transparent3DRenderable renderable = e.getComponent(Transparent3DRenderable.class);
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
            if (renderable.isText) {
                textShaderProgram.use();
                glUniform4fv(textColourUniform.getLocation(), renderable.textColour.asArr());
                glUniformMatrix4fv(textMvpUniform.getLocation(), false, mvp.asArr());
            } else {
                shaderProgram.use();
                glUniformMatrix4fv(mvpUniform.getLocation(), false, mvp.asArr());
            }

            if (!renderable.isIndexed) {
                glDrawArrays(renderable.renderMode, 0, renderable.numVertices);
            } else {
                renderable.indexBuffer.bind();
                glDrawElements(renderable.renderMode, renderable.numVertices, GL_UNSIGNED_INT, NULL);
            }
        }
    }
}
