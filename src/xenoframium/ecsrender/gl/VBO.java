package xenoframium.ecsrender.gl;

import org.lwjgl.system.MemoryStack;
import xenoframium.glwrapper.GlVbo;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.opengl.GL15.*;

/**
 * Created by chrisjung on 29/09/17.
 */
public class VBO implements AutoCloseable {
    final GlVbo glVbo;
    public VBO() {
        glVbo = new GlVbo();
    }

    public void bind(int target) {
        glVbo.bind(target);
    }

    public void bind() {
        glVbo.bind(GL_ARRAY_BUFFER);
    }

    public void bufferData(float[] bufferData) {
        try (MemoryStack stack = stackPush()) {
            FloatBuffer fBuffer = stack.mallocFloat(bufferData.length);
            fBuffer.put(bufferData);
            fBuffer.flip();
            glVbo.bind(GL_ARRAY_BUFFER);
            glBufferData(GL_ARRAY_BUFFER, fBuffer, GL_STATIC_DRAW);
        }
    }

    public void bufferData(FloatBuffer bufferData) {
        try (MemoryStack stack = stackPush()) {
            glVbo.bind(GL_ARRAY_BUFFER);
            glBufferData(GL_ARRAY_BUFFER, bufferData, GL_STATIC_DRAW);
        }
    }

    public void bufferData(float[] bufferData, int target, int drawType) {
        try (MemoryStack stack = stackPush()) {
            FloatBuffer fBuffer = stack.mallocFloat(bufferData.length);
            fBuffer.put(bufferData);
            fBuffer.flip();
            glVbo.bind(target);
            glBufferData(target, fBuffer, drawType);
        }
    }

    public void bufferData(byte[] bufferData) {
        try (MemoryStack stack = stackPush()) {
            ByteBuffer buffer = stack.malloc(bufferData.length);
            buffer.put(bufferData);
            buffer.flip();
            glVbo.bind(GL_ARRAY_BUFFER);
            glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
        }
    }

    public void bufferData(byte[] bufferData, int target, int drawType) {
        try (MemoryStack stack = stackPush()) {
            ByteBuffer buffer = stack.malloc(bufferData.length);
            buffer.put(bufferData);
            buffer.flip();
            glVbo.bind(target);
            glBufferData(target, buffer, drawType);
        }
    }
    public void bufferData(int[] bufferData) {
        try (MemoryStack stack = stackPush()) {
            IntBuffer buffer = stack.mallocInt(bufferData.length);
            buffer.put(bufferData);
            buffer.flip();
            glVbo.bind(GL_ARRAY_BUFFER);
            glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
        }
    }

    public void bufferData(int[] bufferData, int target, int drawType) {
        try (MemoryStack stack = stackPush()) {
            IntBuffer buffer = stack.mallocInt(bufferData.length);
            buffer.put(bufferData);
            buffer.flip();
            glVbo.bind(target);
            glBufferData(target, buffer, drawType);
        }
    }

    @Override
    public void close() throws RuntimeException {
        glDeleteBuffers(glVbo.getId());
    }
}
