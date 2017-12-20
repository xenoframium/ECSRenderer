package xenoframium.ecsrender.input;

/**
 * Created by chrisjung on 19/12/17.
 */
public interface RawInputListener {
    void onCursorMovementEvent(double xPos, double yPos);
    void onMouseButtonEvent(int mouseButton, int action, int mods);
    void onKeyEvent(int key, int action, int mods, int scancode);
    void onScrollEvent(double xScroll, double yScroll);
    void onTextEvent(char character);
}
