package xenoframium.ecsrender.input;

import org.lwjgl.glfw.*;
import org.lwjgl.system.NativeType;
import xenoframium.ecsrender.GraphicsManager;

import java.util.*;

import static org.lwjgl.glfw.GLFW.*;

/**
 * Created by chrisjung on 19/12/17.
 */
public class InputManager {
    public static class InvalidKeyException extends RuntimeException {
        private InvalidKeyException(int key) {
            super("Invalid key: " + key);
        }
    }

    public static class DuplicateBindingException extends RuntimeException {
        private DuplicateBindingException(String msg) {
            super(msg);
        }
    }

    public static class InvalidModsException extends RuntimeException {
        private InvalidModsException(int mods) {
            super("Not a valid modifier combination: " + mods);
        }
    }

    public static class InputEventNotFoundException extends RuntimeException {
        private InputEventNotFoundException(String msg) {
            super(msg);
        }
    }

    public static class BindingNotFoundException extends RuntimeException {
        private BindingNotFoundException() {
            super("Attempted to remove an event, but the binding to remove it from was not found.");
        }
    }

    private enum BindingType {
        KEY, SCANCODE, MOUSEBUTTON;
    }

    private static class BindingInfo {
        BindingType type;
        int id;
        int mods;

        BindingInfo(BindingType type, int id, int mask) {
            this.type = type;
            this.id = id;
            this.mods = mask;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            BindingInfo that = (BindingInfo) o;

            if (id != that.id) return false;
            if (mods != that.mods) return false;
            return type == that.type;
        }

        @Override
        public int hashCode() {
            int result = type != null ? type.hashCode() : 0;
            result = 31 * result + id;
            result = 31 * result + mods;
            return result;
        }
    }

    public static final RawInputListener NULL_LISTENER = new RawInputListener() {
        @Override
        public void onCursorMovementEvent(double xPos, double yPos) {}

        @Override
        public void onMouseButtonEvent(int mouseButton, int action, int mods) {}

        @Override
        public void onKeyEvent(int key, int action, int mods, int scancode) {}

        @Override
        public void onScrollEvent(double xScroll, double yScroll) {}

        @Override
        public void onTextEvent(char character) {}
    };

    private static HashSet<Integer> activeScancodes = new HashSet<>();
    private static HashMap<BindingInfo, HashSet<InputEvent>> bindings = new HashMap<>();
    private static HashMap<InputEvent, HashSet<BindingInfo>> activeEventBindings = new HashMap<>();

    private static boolean isInputDiverted = false;
    private static RawInputListener rawInputListener = NULL_LISTENER;

    private static double cursorX;
    private static double cursorY;

    //For converting GLFW masks to a more usable version since ctrl takes priority over alt
    private static int[] bitReverseMapping = new int[]{ 0b0000, 0b0001, 0b0100, 0b0101,
                                                        0b0010, 0b0011, 0b0110, 0b0111,
                                                        0b1000, 0b1001, 0b1100, 0b1101,
                                                        0b1010, 0b1011, 0b1110, 0b1111};

    static {
        long windowID = GraphicsManager.getWindow().getId();

        glfwSetCursorPosCallback(windowID, new GLFWCursorPosCallback() {
            @Override
            public void invoke(@NativeType("GLFWwindow *") long window, double xpos, double ypos) {
                cursorX = xpos;
                cursorY = ypos;
                rawInputListener.onCursorMovementEvent(xpos, ypos);
            }
        });

        glfwSetScrollCallback(windowID, new GLFWScrollCallback() {
            @Override
            public void invoke(@NativeType("GLFWwindow *") long window, double xoffset, double yoffset) {
                rawInputListener.onScrollEvent(xoffset, yoffset);
            }
        });

        glfwSetCharCallback(windowID, new GLFWCharCallback() {
            @Override
            public void invoke(@NativeType("GLFWwindow *") long window, @NativeType("unsigned int") int codepoint) {
                rawInputListener.onTextEvent((char) codepoint);
            }
        });

        glfwSetMouseButtonCallback(windowID, new GLFWMouseButtonCallback() {
            @Override
            public void invoke(@NativeType("GLFWwindow *") long window, int button, int action, int mods) {
                handleEvent(new BindingInfo(BindingType.MOUSEBUTTON, button, mods), action);
                rawInputListener.onMouseButtonEvent(button, action, mods);
            }
        });

        glfwSetKeyCallback(windowID, new GLFWKeyCallback() {
            @Override
            public void invoke(@NativeType("GLFWwindow *") long window, int key, int scancode, int action, int mods) {
                if (key == GLFW_KEY_UNKNOWN) {
                    if (action == GLFW_PRESS) {
                        activeScancodes.add(scancode);
                    } else {
                        activeScancodes.remove(scancode);
                    }
                    handleEvent(new BindingInfo(BindingType.SCANCODE, key, mods), action);
                } else {
                    handleEvent(new BindingInfo(BindingType.KEY, key, mods), action);
                }
                rawInputListener.onKeyEvent(key, action, mods, scancode);
            }
        });
    }

    private static void handleEvent(BindingInfo info, int action) {
        if (action == GLFW_RELEASE) {
            handleBindingRelease(info);
        } else {
            handleBindingPress(info);
        }
    }

    private static boolean checkBinding(BindingInfo info, int currentMods) {
        if ((info.mods&currentMods) != info.mods) {
            return false;
        }
        long window = GraphicsManager.getWindow().getId();
        switch (info.type) {
            case KEY:
                if (glfwGetKey(window, info.id) != GLFW_PRESS) {
                    return false;
                }
                break;
            case SCANCODE:
                if (!activeScancodes.contains(info.id)) {
                    return false;
                }
            case MOUSEBUTTON:
                if (glfwGetMouseButton(window, info.id) != GLFW_PRESS) {
                    return false;
                }
                break;
        }
        return true;
    }

    private static void handleBindingRelease(BindingInfo info) {
        for (InputEvent event : activeEventBindings.keySet()) {
            HashSet<BindingInfo> bindings = activeEventBindings.get(event);
            HashSet<BindingInfo> removals = new HashSet<>();
            for (BindingInfo binding : bindings) {
                if (binding.type != info.type) {
                    continue;
                }
                if (checkBinding(binding, info.mods) != true) {
                    removals.add(binding);
                }
            }
            for (BindingInfo inf : removals) {
                bindings.remove(inf);
            }
        }
    }

    private static void handleBindingPress(BindingInfo info) {
        for (int testMods = info.mods; testMods >= 0; testMods--) {
            if (!doesMatchMods(bitReverseMapping[testMods], info.mods)) {
                continue;
            }
            BindingInfo test = new BindingInfo(info.type, info.id, bitReverseMapping[testMods]);
            if (bindings.containsKey(test)) {
                HashSet<InputEvent> events = bindings.get(test);
                for (InputEvent event : events) {
                    activeEventBindings.get(event).add(test);
                }
                break;
            }
        }
    }

    private static boolean doesMatchMods(int test, int mods) {
        return (mods^test&mods) == 0;
    }

    private static void throwIfInvalidMod(int mods) {
        if (mods >= 16) {
            throw new InvalidModsException(mods);
        }
    }

    public static RawInputListener getRawInputListener() {
        return rawInputListener;
    }

    public static void setRawInputListener(RawInputListener rawInputListener) {
        InputManager.rawInputListener = rawInputListener;
    }

    public static void registerKeyBinding(int key, InputEvent event) {
        registerKeyBinding(key, 0, event);
    }

    public static void registerKeyBinding(int key, int mods, InputEvent event) {
        throwIfInvalidMod(mods);
        if (key == GLFW_KEY_UNKNOWN) {
            throw new InvalidKeyException(key);
        }
        BindingInfo inf = new BindingInfo(BindingType.KEY, key, mods);
        bindings.putIfAbsent(inf, new HashSet<>());
        if (bindings.get(inf).contains(event)) {
            throw new DuplicateBindingException("Attempted to register duplicate key binding.");
        }
        bindings.get(inf).add(event);
        activeEventBindings.putIfAbsent(event, new HashSet<>());
    }

    public static void registerKeyBinding(int key, InputEvent event, boolean isScanCode) {
        registerKeyBinding(key, 0, event, isScanCode);
    }

    public static void registerKeyBinding(int key, int mods, InputEvent event, boolean isScanCode) {
        if (!isScanCode) {
            registerKeyBinding(key, mods, event);
        }
        throwIfInvalidMod(mods);
        BindingInfo inf = new BindingInfo(BindingType.SCANCODE, key, mods);
        bindings.putIfAbsent(inf, new HashSet<>());
        if (bindings.get(inf).contains(event)) {
            throw new DuplicateBindingException("Attempted to register duplicate key binding.");
        }
        bindings.get(inf).add(event);
        activeEventBindings.putIfAbsent(event, new HashSet<>());
    }

    public static void deregisterKeyBinding(int key, InputEvent event) {
        deregisterKeyBinding(key, 0, event);
    }

    public static void deregisterKeyBinding(int key, int mods, InputEvent event) {
        throwIfInvalidMod(mods);
        if (key == GLFW_KEY_UNKNOWN) {
            throw new InvalidKeyException(key);
        }
        if (!activeEventBindings.containsKey(event)) {
            throw new InputEventNotFoundException("Attempted to remove an event, but the event was not found.");
        }
        BindingInfo inf = new BindingInfo(BindingType.KEY, key, mods);
        if (!bindings.containsKey(inf)) {
            throw new BindingNotFoundException();
        }
        activeEventBindings.get(event).remove(inf);
    }

    public static void deregisterKeyBinding(int key, InputEvent event, boolean isScancode) {
        deregisterKeyBinding(key, 0, event, isScancode);
    }

    public static void deregisterKeyBinding(int key, int mods, InputEvent event, boolean isScancode) {
        throwIfInvalidMod(mods);
        if (!isScancode) {
            deregisterKeyBinding(key, mods, event);
            return;
        }
        if (!activeEventBindings.containsKey(event)) {
            throw new InputEventNotFoundException("Attempted to remove an event, but the event was not found.");
        }
        BindingInfo inf = new BindingInfo(BindingType.SCANCODE, key, mods);
        if (!bindings.containsKey(inf)) {
            throw new BindingNotFoundException();
        }
        activeEventBindings.get(event).remove(inf);
    }

    public static void registerMouseButtonBinding(int button, InputEvent event) {
        registerMouseButtonBinding(button, 0, event);
    }

    public static void registerMouseButtonBinding(int button, int mods, InputEvent event) {
        throwIfInvalidMod(mods);
        BindingInfo inf = new BindingInfo(BindingType.MOUSEBUTTON, button, mods);
        bindings.putIfAbsent(inf, new HashSet<>());
        if (bindings.get(inf).contains(event)) {
            throw new DuplicateBindingException("Attempted to register duplicate mouse button binding.");
        }
        bindings.get(inf).add(event);
        activeEventBindings.putIfAbsent(event, new HashSet<>());
    }

    public static void deregisterMouseButtonBinding(int button, InputEvent event) {
        deregisterMouseButtonBinding(button, 0, event);
    }

    public static void deregisterMouseButtonBinding(int button, int mods, InputEvent event) {
        throwIfInvalidMod(mods);
        if (!activeEventBindings.containsKey(event)) {
            throw new InputEventNotFoundException("Attempted to remove an event, but the event was not found.");
        }
        BindingInfo inf = new BindingInfo(BindingType.KEY, button, mods);
        if (!bindings.containsKey(inf)) {
            throw new BindingNotFoundException();
        }
        activeEventBindings.get(event).remove(inf);
    }

    public static boolean isEventActive(InputEvent event) {
        if (!activeEventBindings.containsKey(event)) {
            throw new InputEventNotFoundException("Attempted to query an unregistered event.");
        }
        return activeEventBindings.get(event).size() != 0;
    }

    public static double getCursorX() {
        return cursorX;
    }

    public static double getCursorY() {
        return cursorY;
    }
}
