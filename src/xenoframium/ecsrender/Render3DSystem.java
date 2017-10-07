package xenoframium.ecsrender;

import xenoframium.ecs.BaseSystem;
import xenoframium.ecs.Entity;
import xenoframium.ecs.EntityManager;
import xenoframium.ecsrender.universalcomponents.PositioningComponent;
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

import static org.lwjgl.system.MemoryUtil.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

/**
 * Created by chrisjung on 29/09/17.
 */
public class Render3DSystem implements BaseSystem {
    private static GlProgram colourShaderProgram;
    private static GlUniform colourMvpUniform;
    private static GlUniform colourUniform;

    private static GlProgram alphaTexShaderProgram;
    private static GlUniform alphaTexMvpUniform;

    private static GlProgram textShaderProgram;
    private static GlUniform textMvpUniform;
    private static GlUniform foregroundTextColourUniform;
    private static GlUniform backgroundTextColourUniform;

    static {
        GlShader vert = new GlShader(GL_VERTEX_SHADER, new File("shaders/colourVert.vert"));
        GlShader frag = new GlShader(GL_FRAGMENT_SHADER, new File("shaders/colourFrag.frag"));

        colourShaderProgram = new GlProgram(vert, frag);

        colourMvpUniform = new GlUniform(colourShaderProgram, "mvp");
        colourUniform = new GlUniform(colourShaderProgram, "inColour");

        vert.delete();
        frag.delete();

        vert = new GlShader(GL_VERTEX_SHADER, new File("shaders/alphaTexVert.vert"));
        frag = new GlShader(GL_FRAGMENT_SHADER, new File("shaders/alphaTexFrag.frag"));

        alphaTexShaderProgram = new GlProgram(vert, frag);

        alphaTexMvpUniform = new GlUniform(alphaTexShaderProgram, "mvp");

        vert.delete();
        frag.delete();

        vert = new GlShader(GL_VERTEX_SHADER, new File("shaders/textVert.vert"));
        frag = new GlShader(GL_FRAGMENT_SHADER, new File("shaders/textFrag.frag"));

        textShaderProgram = new GlProgram(vert, frag);

        textMvpUniform = new GlUniform(textShaderProgram, "mvp");
        foregroundTextColourUniform = new GlUniform(textShaderProgram, "foregroundColour");
        backgroundTextColourUniform = new GlUniform(textShaderProgram, "backgroundColour");

        System.out.println(glGetShaderInfoLog(frag.getId()));

        vert.delete();
        frag.delete();
    }

    private Set<Entity> entities = new HashSet<>();
    private Camera camera;
    private Projection projection;

    public Render3DSystem(Projection projection, Camera camera) {
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
        List<Entity> transparentEntities = new ArrayList<>();
        Map<Entity, Float> zVals = new HashMap<>();
        for (Entity e : entities) {
            Renderable3D renderable = e.getComponent(Renderable3D.class);
            if (!renderable.isVisible()) {
                continue;
            }

            boolean hasTransparency = false;

            switch (renderable.renderableType) {
                case RENDERABLE_TYPE_TEXT:
                    if (renderable.textInfo.backgroundColour.w != 1 || renderable.textInfo.foregroundColour.w != 1) {
                        hasTransparency = true;
                    }
                    break;
                case RENDERABLE_TYPE_COLOURED:
                    if (renderable.colour.w != 1) {
                        hasTransparency = true;
                    }
                    break;
                default:
                    if (renderable.isTransparent) {
                        hasTransparency = true;
                    }
                    break;
            }

            if (hasTransparency) {
                Vec4 referencePos = new Vec4(0, 0, 0, 1);
                if (e.hasComponents(PositioningComponent.class)) {
                    referencePos = GLM.mult(vp, e.getComponent(PositioningComponent.class).getModelMatrix()).mult(referencePos);
                }
                zVals.put(e, referencePos.z);
                transparentEntities.add(e);
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
            GlUniform mvpUniform = null;

            switch (renderable.renderableType) {
                case RENDERABLE_TYPE_TEXTURED:
                    alphaTexShaderProgram.use();
                    renderable.texture.bindTexture();
                    mvpUniform = alphaTexMvpUniform;
                    break;
                case RENDERABLE_TYPE_TEXT:
                    TextInfo textInfo = renderable.textInfo;
                    textShaderProgram.use();
                    renderable.texture.bindTexture();
                    mvpUniform = textMvpUniform;
                    glUniform4fv(foregroundTextColourUniform.getLocation(), textInfo.foregroundColour.asArr());
                    glUniform4fv(backgroundTextColourUniform.getLocation(), textInfo.backgroundColour.asArr());
                    break;
                case RENDERABLE_TYPE_COLOURED:
                    colourShaderProgram.use();
                    mvpUniform = colourMvpUniform;
                    glUniform4fv(colourUniform.getLocation(), renderable.colour.asArr());
                    break;
            }

            renderable.vao.bind();
            glUniformMatrix4fv(mvpUniform.getLocation(), false, mvp.asArr());
            if (!renderable.isIndexed) {
                glDrawArrays(renderable.renderMode, 0, renderable.numVertices);
            } else {
                renderable.indexBuffer.bind();
                glDrawElements(renderable.renderMode, renderable.numVertices, GL_UNSIGNED_INT, NULL);
            }
        }

        Collections.sort(transparentEntities, new Comparator<Entity>() {
            @Override
            public int compare(Entity o1, Entity o2) {
                return zVals.get(o2).compareTo(zVals.get(o1));
            }
        });

        for (Entity e : transparentEntities) {
            Renderable3D renderable = e.getComponent(Renderable3D.class);

            Mat4 model = new Mat4();
            PositioningComponent pc = e.getComponent(PositioningComponent.class);
            if (pc != null) {
                model = pc.getModelMatrix();
            }

            Mat4 mvp = GLM.mult(vp, model);
            GlUniform mvpUniform = null;

            switch (renderable.renderableType) {
                case RENDERABLE_TYPE_TEXTURED:
                    alphaTexShaderProgram.use();
                    renderable.texture.bindTexture();
                    mvpUniform = alphaTexMvpUniform;
                    break;
                case RENDERABLE_TYPE_TEXT:
                    TextInfo textInfo = renderable.textInfo;
                    textShaderProgram.use();
                    renderable.texture.bindTexture();
                    mvpUniform = textMvpUniform;
                    glUniform4fv(foregroundTextColourUniform.getLocation(), textInfo.foregroundColour.asArr());
                    glUniform4fv(backgroundTextColourUniform.getLocation(), textInfo.backgroundColour.asArr());
                    break;
                case RENDERABLE_TYPE_COLOURED:
                    colourShaderProgram.use();
                    mvpUniform = colourMvpUniform;
                    glUniform4fv(colourUniform.getLocation(), renderable.colour.asArr());
                    break;
            }

            renderable.vao.bind();
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
