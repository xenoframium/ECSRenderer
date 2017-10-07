package xenoframium.ecsrender;

import xenoframium.ecsrender.gl.Texture;
import xenoframium.glmath.linearalgebra.Vec4;

/**
 * Created by chrisjung on 2/10/17.
 */
public class TextInfo {
    public final float width, height;
    final Vec4 foregroundColour;
    final Vec4 backgroundColour;
    final Mesh mesh;
    final FontInfo fontInfo;
    final Texture texture;

    public TextInfo(FontInfo fontInfo, Mesh mesh, float width, float height, Vec4 foregroundColour, Vec4 backgroundColour) {
        this.fontInfo = fontInfo;
        this.mesh = mesh;
        this.width = width;
        this.height = height;
        this.texture = fontInfo.fontTexture;
        this.foregroundColour = foregroundColour;
        this.backgroundColour = backgroundColour;
    }

    public TextInfo(TextInfo textInfo) {
        this.width = textInfo.width;
        this.height = textInfo.height;
        this.foregroundColour = new Vec4(textInfo.foregroundColour);
        this.backgroundColour = new Vec4(textInfo.backgroundColour);
        this.mesh = textInfo.mesh;
        this.fontInfo = textInfo.fontInfo;
        this.texture = textInfo.texture;
    }

    public Vec4 getForegroundColour() {
        return new Vec4(foregroundColour);
    }

    public Vec4 getBackgroundColour() {
        return new Vec4(backgroundColour);
    }
}
