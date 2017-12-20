package xenoframium.ecsrender.text;

import org.lwjgl.stb.*;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import xenoframium.ecsrender.texture.Texture;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.file.Files;

import static org.lwjgl.system.MemoryUtil.*;
import static org.lwjgl.system.MemoryStack.*;

class FontException extends RuntimeException {
    public FontException(String msg) {
        super(msg);
    }
}

class GlyphInfo {
    float[] verts;
    float[] uvs;
    float xOff, yOff;

    GlyphInfo(float[] verts, float[] uvs, float xOff, float yOff) {
        this.verts = verts;
        this.uvs = uvs;
        this.xOff = xOff;
        this.yOff = yOff;
    }
}

/**
 * Created by chrisjung on 30/09/17.
 */
public class Font implements AutoCloseable {
    //Text is way too big, use this to downscale
    private static final float DOWNSCALE_RATIO = 100;

    public final int fontSize;

    final Texture fontTexture;
    final STBTTPackedchar.Buffer charInfo;
    final int atlasWidth;
    final int atlasHeight;
    final char firstCharacter;

    public Font(File fontFile, int atlasWidth, int atlasHeight, char firstCharacter, char lastCharacter, int fontSize) {
        byte[] fontBytes;

        lastCharacter++;
        this.fontSize = fontSize;
        this.atlasWidth = atlasWidth;
        this.atlasHeight = atlasHeight;
        this.firstCharacter = firstCharacter;

        try {
            fontBytes = Files.readAllBytes(fontFile.toPath());
        } catch(IOException e) {
            throw new FontException("Failed to load text file " + fontFile.getAbsolutePath());
        }

        try (MemoryStack stack = stackPush()) {
            ByteBuffer fontData = MemoryUtil.memAlloc(fontBytes.length);
            fontData.put(fontBytes);
            fontData.flip();
            ByteBuffer atlasData = MemoryUtil.memAlloc(atlasWidth * atlasHeight);
            charInfo = new STBTTPackedchar.Buffer(MemoryUtil.memAlloc((lastCharacter - firstCharacter) * STBTTPackedchar.SIZEOF));

            STBTTPackContext context = new STBTTPackContext(stack.malloc(STBTTPackContext.SIZEOF));
            if (!STBTruetype.stbtt_PackBegin(context, atlasData, atlasWidth, atlasHeight, 0, 1, NULL)) {
                throw new FontException("Failed to initialise text. Atlas width and height may be too small.");
            }

            if (!STBTruetype.stbtt_PackFontRange(context, fontData, 0, fontSize, firstCharacter, charInfo)) {
                throw new FontException("Failed to pack text.");
            }

            STBTruetype.stbtt_PackEnd(context);

            fontTexture = new Texture(atlasData, atlasWidth, atlasHeight, STBImage.STBI_grey);
            MemoryUtil.memFree(atlasData);
            MemoryUtil.memFree(fontData);
        }
    }

    GlyphInfo getGlyphInfo(char character, FloatBuffer xOff, FloatBuffer yOff) {

        try (MemoryStack stack = stackPush()) {
            xOff.clear();
            yOff.clear();
            STBTTAlignedQuad quad = new STBTTAlignedQuad(stack.malloc(STBTTAlignedQuad.SIZEOF));
            STBTruetype.stbtt_GetPackedQuad(charInfo, atlasWidth, atlasHeight, character - firstCharacter, xOff, yOff, quad, false);

            float xm=-quad.x0(), xM=-quad.x1(), ym=-quad.y1(), yM=-quad.y0(), u0=quad.s0(), u1=quad.s1(), v0=quad.t0(), v1=quad.t1();

            float[] verts = new float[]{xm, ym, 0,
                    xm, yM, 0,
                    xM, yM, 0,
                    xM, ym, 0};

            for (int i = 0; i < verts.length; i++) {
                verts[i] /= DOWNSCALE_RATIO;
            }

            float[] uvs = new float[]{  u0, v1,
                    u0, v0,
                    u1, v0,
                    u1, v1};

            float a = xOff.get(), b = yOff.get();

            return new GlyphInfo(verts, uvs, DOWNSCALE_RATIO/100, DOWNSCALE_RATIO/100);
        }
    }

    @Override
    public void close() throws RuntimeException {
        charInfo.free();
        fontTexture.close();
    }
}
