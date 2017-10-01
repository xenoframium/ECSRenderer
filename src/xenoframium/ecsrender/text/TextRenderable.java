package xenoframium.ecsrender.text;

import org.lwjgl.system.MemoryStack;
import xenoframium.ecs.Component;
import xenoframium.ecsrender.gl.IBO;
import xenoframium.ecsrender.gl.Texture;
import xenoframium.ecsrender.gl.VAO;
import xenoframium.ecsrender.gl.VBO;
import xenoframium.glmath.linearalgebra.Vec4;

import java.nio.FloatBuffer;
import java.util.ArrayList;

import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.opengl.GL11.*;

/**
 * Created by chrisjung on 29/09/17.
 */
public class TextRenderable implements Component, AutoCloseable {
    final VAO vao;
    final VBO coordVBO;
    final VBO uvVBO;
    final IBO indexBuffer;
    final int renderMode;
    final int numVertices;
    final Texture texture;
    final boolean isIndexed;
    final Vec4 colour;
    public final float width;
    public final float height;

    public TextRenderable(FontInfo fontInfo, String text, int linespacing, Vec4 colour) {
        try (MemoryStack stack = stackPush()) {
            FloatBuffer xOff = stack.callocFloat(1);
            FloatBuffer yOff = stack.callocFloat(1);
            int currentIndex = 0, lineNumber = 0;
            ArrayList<Float> verts = new ArrayList<>();
            ArrayList<Float> uvs = new ArrayList<>();
            ArrayList<Integer> indices = new ArrayList<>();
            float mx=Integer.MAX_VALUE, Mx=Integer.MIN_VALUE, my=Integer.MAX_VALUE, My=Integer.MIN_VALUE;
            for (int i = 0; i < text.length(); i++) {
                char c = text.charAt(i);
                if (c != '\n') {
                    GlyphInfo gi = fontInfo.getGlyphInfo(c, xOff, yOff);

                    mx = Math.min(mx,gi.verts[0]);
                    mx = Math.min(mx,gi.verts[3]);
                    mx = Math.min(mx,gi.verts[6]);
                    mx = Math.min(mx,gi.verts[9]);

                    Mx = Math.max(Mx,gi.verts[0]);
                    Mx = Math.max(Mx,gi.verts[3]);
                    Mx = Math.max(Mx,gi.verts[6]);
                    Mx = Math.max(Mx,gi.verts[9]);

                    my = Math.min(my,gi.verts[1]);
                    my = Math.min(my,gi.verts[4]);
                    my = Math.min(my,gi.verts[7]);
                    my = Math.min(my,gi.verts[10]);

                    My = Math.max(My,gi.verts[1]);
                    My = Math.max(My,gi.verts[4]);
                    My = Math.max(My,gi.verts[7]);
                    My = Math.max(My,gi.verts[10]);

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

                    indices.add(currentIndex);
                    indices.add(currentIndex+1);
                    indices.add(currentIndex+2);

                    indices.add(currentIndex);
                    indices.add(currentIndex+2);
                    indices.add(currentIndex+3);

                    currentIndex += 4;
                } else {
                    lineNumber++;
                    xOff.clear();
                    yOff.clear();
                    xOff.put(0);
                    yOff.put(lineNumber * (fontInfo.fontSize+linespacing));
                }
            }
            width = Mx-mx;
            height = My-my;

            float[] vs = new float[verts.size()];
            float[] uv = new float[uvs.size()];
            int[] inds = new int[indices.size()];
            for (int i = 0; i < verts.size(); i++) {
                vs[i] = verts.get(i);
            }
            for (int i = 0; i < uvs.size(); i++) {
                uv[i] = uvs.get(i);
            }
            for (int i = 0; i < indices.size(); i++) {
                inds[i] = indices.get(i);
            }

            isIndexed = true;
            vao = new VAO();
            this.colour = colour;
            this.renderMode = GL_TRIANGLES;
            this.texture = fontInfo.fontTexture;
            this.numVertices = inds.length;

            coordVBO = new VBO();
            coordVBO.bufferData(vs);
            uvVBO = new VBO();
            uvVBO.bufferData(uv);
            indexBuffer = new IBO();
            indexBuffer.bufferData(inds);
            vao.addAttribPointer(coordVBO, 0, 3, GL_FLOAT);
            vao.addAttribPointer(uvVBO, 1, 2, GL_FLOAT);
        }
    }

    @Override
    public void close() throws RuntimeException {
        vao.close();
        indexBuffer.close();
        coordVBO.close();
        uvVBO.close();
    }
}
