package xenoframium.ecsrender;

/**
 * Created by chrisjung on 1/10/17.
 */
public class Mesh {
    public float[] coords;
    public float[] uvs;
    public int drawType;
    public Mesh(float[] coords, float[] uvs, int drawType) {
        this.coords = coords;
        this.uvs = uvs;
        this.drawType = drawType;
    }
}
