package xenoframium.ecsrender.system;

import org.lwjgl.glfw.*;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.NativeType;

import java.awt.*;
import java.nio.DoubleBuffer;
import java.util.HashSet;
import java.util.Set;

import static org.lwjgl.glfw.GLFW.*;

/**
 * Created by chrisjung on 29/09/17.
 */
public class Input {
    private Set<KeyPressCallback> keyPressCallbacks = new HashSet<>();
    private Set<CharCallback> charCallbacks = new HashSet<>();
    private Set<CursorMovementCallback> cursorMvmntCallbacks = new HashSet<>();
    private Set<MouseButtonCallback> mouseButtonCallbacks = new HashSet<>();
    private Set<ScrollCallback> scrollCallbacks = new HashSet<>();

    private Window window;

    public Input(Window window) {
        glfwSetKeyCallback(window.getId(), new GLFWKeyCallback() {
            @Override
            public void invoke(@NativeType("GLFWwindow *") long win, int key, int scancode, int action, int mods) {
                for (KeyPressCallback callback : keyPressCallbacks) {
                    callback.invoke(window, key, scancode, action, mods);
                }
            }
        });

        glfwSetCharModsCallback(window.getId(), new GLFWCharModsCallback() {
            @Override
            public void invoke(@NativeType("GLFWwindow *") long win, @NativeType("unsigned int") int codepoint, int mods) {
                for (CharCallback callback : charCallbacks) {
                    callback.invoke(window, (char) codepoint, mods);
                }
            }
        });

        glfwSetCursorPosCallback(window.getId(), new GLFWCursorPosCallback() {
            @Override
            public void invoke(@NativeType("GLFWwindow *") long win, double xpos, double ypos) {
                for (CursorMovementCallback cmc : cursorMvmntCallbacks) {
                    cmc.invoke(window, (float) xpos, (float) ypos);
                }
            }
        });

        glfwSetMouseButtonCallback(window.getId(), new GLFWMouseButtonCallback() {
            @Override
            public void invoke(@NativeType("GLFWwindow *") long win, int button, int action, int mods) {
                for (MouseButtonCallback callback : mouseButtonCallbacks) {
                    callback.invoke(button, action, mods);
                }
            }
        });

        glfwSetScrollCallback(window.getId(), new GLFWScrollCallback() {
            @Override
            public void invoke(@NativeType("GLFWwindow *") long win, double xoffset, double yoffset) {
                for (ScrollCallback callback : scrollCallbacks) {
                    callback.invoke(window, xoffset, yoffset);
                }
            }
        });

        this.window = window;
    }

    public int getKeyStatus(int key) {
        return glfwGetKey(window.getId(), key);
    }

    public CursorPosition getCursorPosition() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            DoubleBuffer x = stack.mallocDouble(1);
            DoubleBuffer y = stack.mallocDouble(1);
            glfwGetCursorPos(window.getId(), x, y);
            return new CursorPosition((float) x.get(), (float) y.get());
        }
    }

    public int getMouseButtonStatus(int button) {
        return glfwGetMouseButton(window.getId(), button);
    }

    public void subscribeKeyPressCallback(KeyPressCallback callback) {
        keyPressCallbacks.add(callback);
    }

    public void unsubscribeKeyPressCallback(KeyPressCallback callback) {
        keyPressCallbacks.remove(callback);
    }

    public void subscribeCharCallback(CharCallback callback) {
        charCallbacks.add(callback);
    }

    public void unsubscribeCharCallback(CharCallback callback) {
        charCallbacks.remove(callback);
    }

    public void subscribeCursorMovementCallback(CursorMovementCallback callback) {
        cursorMvmntCallbacks.add(callback);
    }

    public void unsubscribeCursorMovementCallback(CursorMovementCallback callback) {
        cursorMvmntCallbacks.remove(callback);
    }

    public void subscribeMouseButtonCallback(MouseButtonCallback callback) {
        mouseButtonCallbacks.add(callback);
    }

    public void unsubscribeMouseButtonCallback(MouseButtonCallback callback) {
        mouseButtonCallbacks.remove(callback);
    }

    public void subscribeScrollCallback(ScrollCallback callback) {
        scrollCallbacks.add(callback);
    }

    public void unsubscribeScrollCallback(ScrollCallback callback) {
        scrollCallbacks.remove(callback);
    }
}
