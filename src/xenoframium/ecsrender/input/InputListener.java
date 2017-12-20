package xenoframium.ecsrender.input;

/**
 * Created by chrisjung on 20/12/17.
 */
public class InputListener {
    public final InputEvent event;
    public final InputCommand command;

    public InputListener(InputEvent event, InputCommand command) {
        this.event = event;
        this.command = command;
    }
}
