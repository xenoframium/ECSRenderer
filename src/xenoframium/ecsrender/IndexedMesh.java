package xenoframium.ecsrender;

/**
 * Created by chrisjung on 2/10/17.
 */
public class IndexedMesh {
    public int[] indices;
    public float[] coords;
    public float[] uvs;
    public int drawType;

    public IndexedMesh(float[] coords, float[] uvs, int[] indices, int drawType) {
        this.coords = coords;
        this.uvs = uvs;
        this.indices = indices;
        this.drawType = drawType;
    }
}
