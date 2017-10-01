package xenoframium.ecsrender.system;

/**
 * Created by chrisjung on 29/09/17.
 */
public interface CursorMovementCallback {
    void invoke(Window window, float xpos, float ypos);
}
