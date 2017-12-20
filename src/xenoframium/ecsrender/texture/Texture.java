package xenoframium.ecsrender.texture;

import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import xenoframium.glwrapper.GlTexture;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;

/**
 * Created by chrisjung on 29/09/17.
 */
public class Texture implements AutoCloseable {
    final GlTexture glTexture;
    private final int width;
    private final int height;
    private final boolean hasAlpha;

    public Texture(byte[] textureBytes, int width, int height, int stbiComposition) {
        this.width = width;
        this.height = height;
        this.hasAlpha = (stbiComposition == STBImage.STBI_rgb_alpha || stbiComposition == STBImage.STBI_grey_alpha);
        try (MemoryStack stack = stackPush()) {
            ByteBuffer textureBuffer = stackMalloc(textureBytes.length);
            textureBuffer.put(textureBytes);

            glTexture = new GlTexture();
            glTexture.bind(GL_TEXTURE_2D);

            int glComp = 0;

            switch (stbiComposition) {
                case STBImage.STBI_grey:
                    glComp = GL_RED;
                    break;
                case STBImage.STBI_grey_alpha:
                    glComp = GL_RG;
                    break;
                case STBImage.STBI_rgb:
                    glComp = GL_RGB;
                    break;
                case STBImage.STBI_rgb_alpha:
                    glComp = GL_RGBA;
                    break;
            }

            glTexImage2D(GL_TEXTURE_2D, 0, glComp, width, height, 0, glComp, GL_UNSIGNED_BYTE, textureBuffer);

            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        }
    }

    public Texture(ByteBuffer textureBuffer, int width, int height, int stbiComposition) {
        this.width = width;
        this.height = height;
        this.hasAlpha = (stbiComposition == STBImage.STBI_rgb_alpha || stbiComposition == STBImage.STBI_grey_alpha);

        glTexture = new GlTexture();
        glTexture.bind(GL_TEXTURE_2D);

        int glComp = 0;

        switch (stbiComposition) {
            case STBImage.STBI_grey:
                glComp = GL_RED;
                break;
            case STBImage.STBI_grey_alpha:
                glComp = GL_RG;
                break;
            case STBImage.STBI_rgb:
                glComp = GL_RGB;
                break;
            case STBImage.STBI_rgb_alpha:
                glComp = GL_RGBA;
                break;
        }

        glTexImage2D(GL_TEXTURE_2D, 0, glComp, width, height, 0, glComp, GL_UNSIGNED_BYTE, textureBuffer);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
    }

    public Texture(File textureFile, int stbiComposition) {
        try (MemoryStack stack = stackPush()) {
            IntBuffer widthBuffer = stack.mallocInt(1);
            IntBuffer heightBuffer = stack.mallocInt(1);
            IntBuffer compBuffer = stack.mallocInt(1);

            ShortBuffer textureBuffer = STBImage.stbi_load_16(textureFile.getAbsolutePath(), widthBuffer, heightBuffer, compBuffer, stbiComposition);

            width = widthBuffer.get(0);
            height = heightBuffer.get(0);
            stbiComposition = compBuffer.get(0);

            hasAlpha = (compBuffer.get(0) == STBImage.STBI_rgb_alpha || compBuffer.get(0) == STBImage.STBI_grey_alpha);

            glTexture = new GlTexture();
            glTexture.bind(GL_TEXTURE_2D);

            int glComp = 0;

            switch (stbiComposition) {
                case STBImage.STBI_grey:
                    glComp = GL_RED;
                    break;
                case STBImage.STBI_grey_alpha:
                    glComp = GL_RG;
                    break;
                case STBImage.STBI_rgb:
                    glComp = GL_RGB;
                    break;
                case STBImage.STBI_rgb_alpha:
                    glComp = GL_RGBA;
                    break;
            }

            glTexImage2D(GL_TEXTURE_2D, 0, glComp, width, height, 0, glComp, GL_UNSIGNED_SHORT, textureBuffer);

            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
            STBImage.stbi_image_free(textureBuffer);
        }
    }

    public Texture(File textureFile) {
        this(textureFile, STBImage.STBI_default);
    }

    public void bind() {
        glTexture.bind(GL_TEXTURE_2D);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    @Override
    public void close() {
        glDeleteTextures(glTexture.getId());
    }
}
