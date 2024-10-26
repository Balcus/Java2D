package crimson;

import static org.lwjgl.glfw.GLFW.*;

// Singleton class implementing the functionallity for key presses
public class KeyListener {
    private static KeyListener instance;
    // 349 is the highest number a GLFW macro is mapped to
    private boolean[] keyPressed = new boolean[350];

    private KeyListener() {}

    public static KeyListener getKeyListener() {
        if (KeyListener.instance == null) {
            KeyListener.instance = new KeyListener();
        }
        return KeyListener.instance;
    }

    public static void keyCallback(long window, int key, int scancode, int action, int mods) {
        if (action == GLFW_PRESS && key < getKeyListener().keyPressed.length) {
            getKeyListener().keyPressed[key] = true;
        } else if (action == GLFW_RELEASE && key < getKeyListener().keyPressed.length) {
            getKeyListener().keyPressed[key] = false;
        }
    }

    public static boolean isKeyPressed(int key) {
        if (key < getKeyListener().keyPressed.length) {
            return getKeyListener().keyPressed[key];
        }
        return false;
    }
}


