package crimson;

import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;
import utils.Time;


import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;


//Singleton Class (Only have one instance of the object at a time)
public class Window {

    public static Window window = null;
    private int width, height;
    private String title;
    private long glfwWindow;
    private static Scene currentScene = null;
    public float r,g,b,a;

    /*
    Default window size : 1920 x 1080
     */
    private Window() {
        this.width = 1920;
        this.height = 1080;
        this.title = "Java2D";
        this.r = 1;
        this.g = 1;
        this.b = 1;
        this.a = 1;
    }

    /*
    Only create one Window when there is no Window
     */
    public static Window getWindow() {
        if(Window.window == null) {
            Window.window = new Window();
        }

        return Window.window;
    }

    public static void changeScenes(int newScene) {
        switch(newScene) {
            case 0:
                currentScene = new LevelEditorScene();
                currentScene.init();
                currentScene.start();
                break;
            case 1:
                currentScene = new LevelScene();
                currentScene.init();
                currentScene.start();
                break;
            default:
                assert false : "Unkown Scene: " + newScene + '\n';
                break;
        }
    }

    /*
    Set up the app's window
     */
    public void run() {

        init();
        loop();

        // Free memory
        glfwFreeCallbacks(glfwWindow);
        glfwDestroyWindow(glfwWindow);

        // Terminate GLFW and free the eroor callbacks
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    public void init() {
        // Setup error callback (where we print errors)
        GLFWErrorCallback.createPrint(System.err).set();

        // Init GLFW
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        // Config GLFW
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE);

        // Create the window
        glfwWindow = glfwCreateWindow(this.width, this.height, this.title, MemoryUtil.NULL, MemoryUtil.NULL);
        if (glfwWindow == MemoryUtil.NULL) {
            throw new RuntimeException("Failed to create the GLFW window");
        }

        // Key listeners for the mouse
        glfwSetCursorPosCallback(glfwWindow, MouseListener::cursorPositionCallback);
        glfwSetMouseButtonCallback(glfwWindow, MouseListener::mouseButtonCallback);
        glfwSetScrollCallback(glfwWindow, MouseListener::scrollCallback);

        // Key listener for the keyboard
        glfwSetKeyCallback(glfwWindow, KeyListener::keyCallback);

        // Make OpenGL context current
        glfwMakeContextCurrent(glfwWindow);

        // Enable V-sync (refresh the window as fast as we can)
        glfwSwapInterval(1);

        // Make the window visible
        glfwShowWindow(glfwWindow);

        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();

        Window.changeScenes(0);
    }

    public void loop() {

        float frameBeginTime = Time.getTime();
        float frameEndTime;
        float dt = -1.0f;

        while (!glfwWindowShouldClose(glfwWindow)) {
            // Poll Events
            glfwPollEvents();

            glClearColor(r, g, b, a);
            glClear(GL_COLOR_BUFFER_BIT);

            if (dt >= 0) {
                currentScene.update(dt);
            }

            glfwSwapBuffers(glfwWindow);

            frameEndTime = Time.getTime();
            dt = frameEndTime - frameBeginTime;
            frameBeginTime = frameEndTime;
        }
    }

    public static Scene getCurrentScene() {
        return getWindow().currentScene;
    }

}
