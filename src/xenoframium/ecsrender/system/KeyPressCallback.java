package xenoframium.ecsrender.system;

/**
 * Created by chrisjung on 29/09/17.
 */
public interface KeyPressCallback {
    void invoke(Window window, int key, int scancode, int action, int mods);
}
