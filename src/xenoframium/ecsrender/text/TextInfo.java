package xenoframium.ecsrender.text;

import xenoframium.ecsrender.IndexedMesh;
import xenoframium.ecsrender.gl.Texture;
import xenoframium.glmath.linearalgebra.Vec4;

/**
 * Created by chrisjung on 2/10/17.
 */
public class TextInfo {
    public final float width, height;
    public final IndexedMesh mesh;
    public final FontInfo fontInfo;
    public final Texture texture;
    public final Vec4 colour;

    public TextInfo(FontInfo fontInfo, IndexedMesh mesh, float width, float height, Vec4 colour) {
        this.fontInfo = fontInfo;
        this.mesh = mesh;
        this.width = width;
        this.height = height;
        this.texture = fontInfo.fontTexture;
        this.colour = colour;
    }
}
