import crimson.KeyListener;
import crimson.MouseListener;
import crimson.Window;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_SPACE;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_1;

public class Main {
    public static void main(String[] args) {
        Window window = Window.getWindow();
        window.run();
    }
}
