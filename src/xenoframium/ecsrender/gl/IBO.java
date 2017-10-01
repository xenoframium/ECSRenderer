package xenoframium.ecsrender.gl;

import static org.lwjgl.opengl.GL15.*;

/**
 * Created by chrisjung on 30/09/17.
 */
public class IBO implements AutoCloseable {
    private final int id;
    public IBO() {
        id = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, id);
    }

    public void bind() {
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, id);
    }

    public void bufferData(int[] indices) {
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, id);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);
    }

    @Override
    public void close() throws RuntimeException {
        glDeleteBuffers(id);
    }
}
