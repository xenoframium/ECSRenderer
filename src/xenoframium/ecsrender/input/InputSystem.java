package xenoframium.ecsrender.input;

import org.lwjgl.glfw.GLFW;
import xenoframium.ecs.*;

import java.util.Set;

/**
 * Created by chrisjung on 18/12/17.
 */
public class InputSystem implements BaseSystem {
    EntityFilter inputFilter = new BasicFilter(InputComponent.class);

    @Override
    public void update(Space space, double delta, double time) {
        GLFW.glfwPollEvents();
        Set<Entity> entities = space.getEntities();
        for (Entity entity : entities) {
            InputComponent component = entity.getComponent(InputComponent.class);
            for (int i = 0; i < component.listeners.length; i++) {
                boolean status = InputManager.isEventActive(component.listeners[i].event);
                if (status == true && !component.isActive[i]) {
                    component.listeners[i].command.onPress(component.listeners[i].event);
                    component.isActive[i] = true;
                } else if (status == false && component.isActive[i]) {
                    component.listeners[i].command.onRelease(component.listeners[i].event);
                    component.isActive[i] = false;
                }
            }
        }
    }
}
