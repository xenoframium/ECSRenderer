package xenoframium.ecsrender.system;

/**
 * Created by chrisjung on 29/09/17.
 */
public interface ScrollCallback {
    void invoke(Window window, double xOffset, double yOffset);
}
