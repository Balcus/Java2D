package crimson;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

// Singleton class implementing the functionallity for the mouse
public class MouseListener {
    private static MouseListener instance;
    private double scrollX, scrollY;
    private double xPos, yPos, lastX, lastY;
    private boolean[] mouseKeyPressed = new boolean[3];
    private boolean isDragging;

    private MouseListener() {
        this.scrollX = 0.0;
        this.scrollY = 0.0;
        this.xPos = 0.0;
        this.yPos = 0.0;
        this.lastX = 0.0;
        this.lastY = 0.0;
    }

    public static MouseListener getMouseListener() {
        if (MouseListener.instance == null) {
            MouseListener.instance = new MouseListener();
        }
        return MouseListener.instance;
    }

    // https://www.glfw.org/docs/3.3/input_guide.html
    public static void cursorPositionCallback(long window, double xpos, double ypos) {
        getMouseListener().lastX = getMouseListener().xPos;
        getMouseListener().lastY = getMouseListener().yPos;
        getMouseListener().xPos = xpos;
        getMouseListener().yPos = ypos;
        getMouseListener().isDragging = getMouseListener().mouseKeyPressed[0] || getMouseListener().mouseKeyPressed[1] || getMouseListener().mouseKeyPressed[2];
    }

    public static void mouseButtonCallback(long window, int button, int action, int mods) {
        if (action == GLFW_PRESS && button < getMouseListener().mouseKeyPressed.length) {
            getMouseListener().mouseKeyPressed[button] = true;
        } else if (action == GLFW_RELEASE && button < getMouseListener().mouseKeyPressed.length) {
            getMouseListener().mouseKeyPressed[button] = false;
            getMouseListener().isDragging = false;
        }
    }

    public static void scrollCallback(long window, double xoffset, double yoffset) {
        getMouseListener().scrollX = xoffset;
        getMouseListener().scrollY = yoffset;
    }

    public static void endFrame() {
        getMouseListener().lastX = getMouseListener().xPos;
        getMouseListener().lastY = getMouseListener().yPos;
        getMouseListener().scrollX = 0.0;
        getMouseListener().scrollY = 0.0;
    }

    public static float getMouseX() {
        return (float)getMouseListener().xPos;
    }

    public static float getMouseY() {
        return (float)getMouseListener().yPos;
    }

    public static float getMouseDeltaX() {
        return (float)(getMouseListener().lastX - getMouseListener().xPos);
    }

    public static float getMouseDeltaY() {
        return (float)(getMouseListener().lastY - getMouseListener().yPos);
    }

    public static float getScrollX() {
        return (float)getMouseListener().scrollX;
    }

    public static float getScrollY() {
        return (float)getMouseListener().scrollY;
    }

    public static boolean isDragging() {
        return getMouseListener().isDragging;
    }

    public static boolean isMouseKeyPressed(int bttn) {
        if (bttn < getMouseListener().mouseKeyPressed.length) {
            return getMouseListener().mouseKeyPressed[bttn];
        }
        return false;
    }
}
