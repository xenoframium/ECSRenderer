package xenoframium.ecsrender.input;

/**
 * Created by chrisjung on 20/12/17.
 */
public interface InputCommand {
    void onPress(InputEvent event);
    void onRelease(InputEvent event);
}
