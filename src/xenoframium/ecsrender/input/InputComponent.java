package xenoframium.ecsrender.input;

import xenoframium.ecs.Component;

import java.util.Arrays;

/**
 * Created by chrisjung on 18/12/17.
 */
public class InputComponent implements Component {
    boolean[] isActive;
    InputListener[] listeners;

    public InputComponent(InputListener[] listeners) {
        this.listeners = Arrays.copyOf(listeners, listeners.length);
        isActive = new boolean[listeners.length];
    }
}
