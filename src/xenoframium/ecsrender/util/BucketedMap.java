package xenoframium.ecsrender.util;

import javafx.geometry.Pos;
import xenoframium.glmath.linearalgebra.Vec3;

import java.util.*;

/**
 * Created by chrisjung on 1/10/17.
 */
public class BucketedMap<T> {
    private class Pos {
        int x, y, z;

        Pos(int x, int y, int z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Pos pos = (Pos) o;

            if (x != pos.x) return false;
            if (y != pos.y) return false;
            return z == pos.z;
        }

        @Override
        public int hashCode() {
            int result = x;
            result = 31 * result + y;
            result = 31 * result + z;
            return result;
        }
    }

    private int[] xOff = {1, 0, 0, -1, 0, 0};
    private int[] yOff = {0, 1, 0, 0, -1, 0};
    private int[] zOff = {0, 0, 1, 0, 0, -1};
    private int[] xC = {0, 1, 0, 1, 0, 1, 0, 1};
    private int[] yC = {0, 0, 1, 1, 0, 0, 1, 1};
    private int[] zC = {0, 0, 0, 0, 1, 1, 1, 1};

    private final int bucketSize;

    Map<Pos, HashSet<T>> bucketMap = new HashMap<>();
    Map<T, Pos> reverseMap = new HashMap<T, Pos>();

    public BucketedMap(int bucketSize) {
        this.bucketSize = bucketSize;
    }

    public void add(Vec3 pos, T obj) {
        int bX = (int) Math.floor(pos.x / bucketSize);
        int bY = (int) Math.floor(pos.y / bucketSize);
        int bZ = (int) Math.floor(pos.z / bucketSize);
        Pos p = new Pos(bX, bY, bZ);
        reverseMap.put(obj, p);
        if (!bucketMap.containsKey(p)) {
            bucketMap.put(p, new HashSet<T>());
        }
        bucketMap.get(p).add(obj);
    }

    public void remove(T obj) {
        Pos pos = reverseMap.get(obj);
        if (pos == null) {
            return;
        }
        bucketMap.get(pos).remove(obj);
        reverseMap.remove(obj);
    }

    public Set<T> getInRange(Vec3 pos, float dist) {
        int bX = (int) Math.floor(pos.x / bucketSize);
        int bY = (int) Math.floor(pos.y / bucketSize);
        int bZ = (int) Math.floor(pos.z / bucketSize);
        Pos p = new Pos(bX, bY-1, bZ);
        Set<T> st = new HashSet<T>();
        Set<Pos> vis = new HashSet<>();
        Queue<Pos> q = new ArrayDeque<>();
        q.add(p);
        vis.add(p);
        dist += bucketSize;
        dist *= dist;
        //System.out.println();
        while (!q.isEmpty()) {
            Pos v = q.poll();
            if (bucketMap.containsKey(v)) {
                st.addAll(bucketMap.get(v));
            }
            for (int i = 0; i < xOff.length; i++) {
                Pos next = new Pos(v.x+xOff[i], v.y+yOff[i], v.z+zOff[i]);
                if (vis.contains(next)) {
                    continue;
                }
                int xCo = next.x * bucketSize;
                int yCo = next.y * bucketSize;
                int zCo = next.z * bucketSize;
                for (int j = 0; j < xC.length; j++) {
                    float xt = xCo + xC[j] * bucketSize - pos.x;
                    float yt = yCo + yC[j] * bucketSize - pos.y;
                    float zt = zCo + zC[j] * bucketSize - pos.z;
                    if (xt*xt+yt*yt+zt*zt < dist) {
                        q.add(next);
                        vis.add(next);
                        break;
                    }
                }
            }
        }
        return st;
    }
}
