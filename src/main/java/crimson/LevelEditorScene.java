package crimson;

import org.joml.Vector2f;
import org.lwjgl.BufferUtils;
import render.Shader;

import java.awt.event.KeyEvent;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.ARBVertexArrayObject.glBindVertexArray;
import static org.lwjgl.opengl.ARBVertexArrayObject.glGenVertexArrays;
import static org.lwjgl.opengl.GL20.*;

public class LevelEditorScene extends Scene {

    private int vaoID, vboID, eboID;

    private Shader defaultShader;

    private float[] vertexArray = {
            // coordinates                // colors
            100.5f,  -0.5f,  0.0f,          1.0f, 0.0f, 0.0f, 1.0f, // BR : 0
            -0.5f,   100.5f,  0.0f,          0.0f, 1.0f, 0.0f, 1.0f, // TL : 1
            100.5f,   100.5f,  0.0f,          0.0f, 0.0f, 1.0f, 1.0f, // TR : 2
            -0.5f,  -0.5f,  0.0f,          1.0f, 1.0f, 0.0f, 1.0f, // BL : 3
    };

    // must be in counter-clockwise order
    private int[] elementArray = {
            2, 1, 0,
            0, 1, 3
    };

    public LevelEditorScene() {
    }

    @Override
    public void init() {
        this.camera = new Camera(new Vector2f());
        defaultShader = new Shader("assets/shaders/default.glsl");
        defaultShader.compile();

        /*
         Generate VAO, VBO and EBO buffer object and send them to the GPU
         VAO - Vertex Array Object
         VBO - Vertex Buffer Object
         EBO - Element Buffer Object
         */

        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);

        // Create a FLoat Buffer of Veritces
        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertexArray.length);
        vertexBuffer.put(vertexArray).flip();

        // Create the VBO and upload the Vertex Buffer
        vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);

        // Create the indicies and upload
        IntBuffer elementBuffer = BufferUtils.createIntBuffer(elementArray.length);
        elementBuffer.put(elementArray).flip();

        eboID = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, elementBuffer, GL_STATIC_DRAW);

        // Add the vertex attributes pointers
        int positionSize = 3;
        int colorSize = 4;
        int floatSizeBytes = 4;
        int vertexSizeBytes = (positionSize + colorSize) * floatSizeBytes;

        // add the pos attr
        glVertexAttribPointer(0, positionSize, GL_FLOAT, false, vertexSizeBytes, 0);
        glEnableVertexAttribArray(0);

        // add the color attr
        glVertexAttribPointer(1, colorSize, GL_FLOAT, false, vertexSizeBytes, floatSizeBytes * positionSize);
        glEnableVertexAttribArray(1);
    }

    @Override
    public void update(float dt) {
        camera.position.x -= dt * 50.0f;

        defaultShader.use();
        defaultShader.uploadMat4f("uProjection", camera.getProjectionMatrix());
        defaultShader.uploadMat4f("uView", camera.getViewMatrix());

        //Bind the VAO we're using
        glBindVertexArray(vaoID);

        // Enable vertex attribute pointer
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glDrawElements(GL_TRIANGLES, elementArray.length, GL_UNSIGNED_INT, 0);

        // Unbind everything
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);

        glBindVertexArray(0);

        defaultShader.detach();
    }
}
