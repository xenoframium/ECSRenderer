package xenoframium.ecsrender.text;

import org.lwjgl.system.MemoryStack;
import xenoframium.ecs.Entity;
import xenoframium.ecsrender.RenderStrategy;
import xenoframium.ecsrender.Shaders;
import xenoframium.ecsrender.gl.IBO;
import xenoframium.ecsrender.gl.VAO;
import xenoframium.ecsrender.gl.VBO;
import xenoframium.ecsrender.texture.Texture;
import xenoframium.glmath.linearalgebra.Mat4;
import xenoframium.glmath.linearalgebra.Vec4;
import xenoframium.glwrapper.GlUniform;

import java.nio.FloatBuffer;
import java.util.ArrayList;

import static org.lwjgl.system.MemoryUtil.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;


/**
 * Created by chrisjung on 11/12/17.
 */
public class TextRenderStrategy implements RenderStrategy {
    private static final GlUniform mvpUniform = new GlUniform(Shaders.textShaderProgram, "mvp");
    private static final GlUniform fgUniform = new GlUniform(Shaders.textShaderProgram, "foregroundColour");
    private static final GlUniform bgUniform = new GlUniform(Shaders.textShaderProgram, "backgroundColour");

    private final Texture texture;

    private final VAO vao;
    private final VBO vertexVBO;
    private final VBO uvVBO;
    private final IBO ibo;
    private final int numVertices;

    public final Vec4 bgColour;
    public final Vec4 fgColour;

    public TextRenderStrategy(Font font, String text, int linespacing, Vec4 foregroundColour, Vec4 backgroundColour) {
        text += 'X';
        try (MemoryStack stack = stackPush()) {
            FloatBuffer xOff = stack.callocFloat(1);
            FloatBuffer yOff = stack.callocFloat(1);
            int currentIndex = 0, lineNumber = 0;
            ArrayList<Float> verts = new ArrayList<>();
            ArrayList<Float> uvs = new ArrayList<>();
            ArrayList<Integer> indices = new ArrayList<>();
            float mx = Integer.MAX_VALUE, Mx = Integer.MIN_VALUE, my = Integer.MAX_VALUE, My = Integer.MIN_VALUE;
            for (int i = 0; i < text.length(); i++) {
                char c = text.charAt(i);
                if (c != '\n') {
                    GlyphInfo gi = font.getGlyphInfo(c, xOff, yOff);

                    if (i != text.length() - 1) {
                        mx = Math.min(mx, gi.verts[0]);
                        mx = Math.min(mx, gi.verts[3]);
                        mx = Math.min(mx, gi.verts[6]);
                        mx = Math.min(mx, gi.verts[9]);

                        Mx = Math.max(Mx, gi.verts[0]);
                        Mx = Math.max(Mx, gi.verts[3]);
                        Mx = Math.max(Mx, gi.verts[6]);
                        Mx = Math.max(Mx, gi.verts[9]);

                        my = Math.min(my, gi.verts[1]);
                        my = Math.min(my, gi.verts[4]);
                        my = Math.min(my, gi.verts[7]);
                        my = Math.min(my, gi.verts[10]);

                        My = Math.max(My, gi.verts[1]);
                        My = Math.max(My, gi.verts[4]);
                        My = Math.max(My, gi.verts[7]);
                        My = Math.max(My, gi.verts[10]);
                    } else {
                        mx = Math.min(mx, gi.verts[0]);
                        mx = Math.min(mx, gi.verts[3]);
                    }

                    //Vert 0
                    verts.add(gi.verts[0]);
                    verts.add(gi.verts[1]);
                    verts.add(gi.verts[2]);
                    //Vert 1
                    verts.add(gi.verts[3]);
                    verts.add(gi.verts[4]);
                    verts.add(gi.verts[5]);
                    //Vert 2
                    verts.add(gi.verts[6]);
                    verts.add(gi.verts[7]);
                    verts.add(gi.verts[8]);
                    //Vert 3
                    verts.add(gi.verts[9]);
                    verts.add(gi.verts[10]);
                    verts.add(gi.verts[11]);

                    //Vert 0
                    uvs.add(gi.uvs[0]);
                    uvs.add(gi.uvs[1]);
                    //Vert 1
                    uvs.add(gi.uvs[2]);
                    uvs.add(gi.uvs[3]);
                    //Vert 2
                    uvs.add(gi.uvs[4]);
                    uvs.add(gi.uvs[5]);
                    //Vert 3
                    uvs.add(gi.uvs[6]);
                    uvs.add(gi.uvs[7]);

                    indices.add(currentIndex + 2);
                    indices.add(currentIndex + 1);
                    indices.add(currentIndex);

                    indices.add(currentIndex + 3);
                    indices.add(currentIndex + 2);
                    indices.add(currentIndex);

                    currentIndex += 4;
                } else {
                    lineNumber++;
                    xOff.clear();
                    yOff.clear();
                    xOff.put(0);
                    yOff.put(lineNumber * (font.fontSize + linespacing));
                }
            }

            float width = Math.max(Mx - mx, 0);
            float height = Math.max(My - my, 0);

            float[] vs = new float[verts.size()];
            float[] uv = new float[uvs.size()];
            int[] inds = new int[indices.size()];
            for (int i = 0; i < verts.size(); i++) {
                vs[i] = verts.get(i);
            }
            for (int i = 0; i < uvs.size(); i++) {
                uv[i] = uvs.get(i);
            }
            for (int i = 0; i < indices.size() - 6; i++) {
                inds[i] = indices.get(i);
            }

            numVertices = inds.length;

            this.texture = font.fontTexture;

            this.bgColour = new Vec4(backgroundColour);
            this.fgColour = new Vec4(foregroundColour);

            this.vao = new VAO();

            this.vertexVBO= new VBO();
            this.uvVBO = new VBO();
            vertexVBO.bufferData(vs);
            uvVBO.bufferData(uv);

            this.ibo = new IBO();
            ibo.bufferData(inds);

            vao.addAttribPointer(vertexVBO, 0, 3, GL_FLOAT);
            vao.addAttribPointer(uvVBO, 1, 2, GL_FLOAT);
        }
    }

    @Override
    public void render(Entity e, Mat4 mvp) {
        texture.bind();
        vao.bind();
        Shaders.textShaderProgram.use();

        glUniformMatrix4fv(mvpUniform.getLocation(), false, mvp.asArr());
        glUniform4fv(fgUniform.getLocation(), fgColour.asArr());
        glUniform4fv(bgUniform.getLocation(), bgColour.asArr());

        ibo.bind();
        glDrawElements(GL_TRIANGLES, numVertices, GL_UNSIGNED_INT, NULL);
    }

    @Override
    public void close() {
        vao.close();
        vertexVBO.close();
        uvVBO.close();
        ibo.close();
    }
}
