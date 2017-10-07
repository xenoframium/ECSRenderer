package xenoframium.ecsrender;

/**
 * Created by chrisjung on 1/10/17.
 */
public class Mesh {
    final float[] coords;
    final float[] uvs;
    final int[] indices;
    final int drawType;

    final boolean hasUVs;
    final boolean hasIndices;

    public Mesh(float[] coords, int drawType) {
        this.coords = coords;
        this.drawType = drawType;

        this.hasUVs = false;
        this.hasIndices = false;

        this.uvs = null;
        this.indices = null;
    }

    public Mesh(float[] coords, int drawType, float[] uvs) {
        this.coords = coords;
        this.uvs = uvs;
        this.drawType = drawType;

        this.hasUVs = true;
        this.hasIndices = false;

        this.indices = null;
    }

    public Mesh(float[] coords, int drawType, int[] indices) {
        this.coords = coords;
        this.drawType = drawType;
        this.indices = indices;

        this.hasUVs = false;
        this.hasIndices = true;

        this.uvs = null;
    }

    public Mesh(float[] coords, int drawType, float[] uvs, int[] indices) {
        this.coords = coords;
        this.uvs = uvs;
        this.drawType = drawType;
        this.indices = indices;

        this.hasUVs = true;
        this.hasIndices = true;
    }
}
