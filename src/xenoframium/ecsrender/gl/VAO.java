package xenoframium.ecsrender.gl;

import xenoframium.glwrapper.GlVao;

import static org.lwjgl.system.MemoryUtil.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

/**
 * Created by chrisjung on 29/09/17.
 */
public class VAO implements AutoCloseable {
    private static final int NUM_ATTRIB_POINTERS = 16;

    final GlVao glVao;

    public VAO() {
        glVao = new GlVao();
    }

    public void bind() {
        glVao.bind();
    }

    public void addAttribPointer(VBO vbo, int vboTarget, int index, int size, int type, boolean isNormalised, int stride, long offset) {
        bind();
        vbo.bind(vboTarget);
        glEnableVertexAttribArray(index);
        glVertexAttribPointer(index, size, type, isNormalised, stride, offset);
    }

    public void addAttribPointer(VBO vbo, int index, int size, int type) {
        bind();
        vbo.bind(GL_ARRAY_BUFFER);
        glEnableVertexAttribArray(index);
        glVertexAttribPointer(index, size, type, false, 0, NULL);
    }

    @Override
    public void close() throws RuntimeException {
        glDeleteVertexArrays(glVao.getId());
    }
}
