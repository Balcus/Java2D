package crimson;

import org.joml.Vector2f;
import org.lwjgl.BufferUtils;
import render.Shader;
import render.Texture;
import utils.Time;

import java.awt.event.KeyEvent;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.ARBVertexArrayObject.glBindVertexArray;
import static org.lwjgl.opengl.ARBVertexArrayObject.glGenVertexArrays;
import static org.lwjgl.opengl.GL20.*;

public class LevelEditorScene extends Scene {

    private int vaoID, vboID, eboID;
    private Shader defaultShader;
    private Texture testTexture;
    private float[] vertexArray = {
            // coordinates                    // colors                   // UV coordinates
            100.5f,   0.0f,    0.0f,          1.0f, 0.0f, 0.0f, 1.0f,     1, 0,               // BR : 0
            0.0f,     100.5f,  0.0f,          0.0f, 1.0f, 0.0f, 1.0f,     0, 1,               // TL : 1
            100.0f,   100.5f,  0.0f,          0.0f, 0.0f, 1.0f, 1.0f,     1, 1,               // TR : 2
            0.0f,     0.0f,    0.0f,          1.0f, 1.0f, 0.0f, 1.0f,     0, 0                // BL : 3
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
        this.defaultShader = new Shader("assets/shaders/default.glsl");
        this.testTexture = new Texture("assets/textures/knight.png");
        this.defaultShader.compile();

        /*
         Generate VAO, VBO and EBO buffer object and send them to the GPU
         VAO - Vertex Array Object
         VBO - Vertex Buffer Object
         EBO - Element Buffer Object
         */

        this.vaoID = glGenVertexArrays();
        glBindVertexArray(this.vaoID);

        // Create a FLoat Buffer of Veritces
        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(this.vertexArray.length);
        vertexBuffer.put(this.vertexArray).flip();

        // Create the VBO and upload the Vertex Buffer
        this.vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, this.vboID);
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);

        // Create the indicies and upload
        IntBuffer elementBuffer = BufferUtils.createIntBuffer(this.elementArray.length);
        elementBuffer.put(this.elementArray).flip();

        this.eboID = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, this.eboID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, elementBuffer, GL_STATIC_DRAW);

        // Add the vertex attributes pointers (specify the attributes for each vertex obj)
        int positionSize = 3;
        int colorSize = 4;
        int uvSize = 2;
        int vertexSizeBytes = (positionSize + colorSize + uvSize) * Float.BYTES;

        // add the pos attrib
        glVertexAttribPointer(0, positionSize, GL_FLOAT, false, vertexSizeBytes, 0);
        glEnableVertexAttribArray(0);

        // add the color attrib
        glVertexAttribPointer(1, colorSize, GL_FLOAT, false, vertexSizeBytes, Float.BYTES * positionSize);
        glEnableVertexAttribArray(1);

        // add uv attrib
        glVertexAttribPointer(2, uvSize, GL_FLOAT, false, vertexSizeBytes, (positionSize + colorSize) * Float.BYTES);
        glEnableVertexAttribArray(2);
    }

    @Override
    public void update(float dt) {

        this.camera.position.x = -150.0f;
        this.camera.position.y = -150.0f;

        this.defaultShader.use();

        // upload texture to shader
        this.defaultShader.uploadTexture("uTEX_SAMPLER", 0);
        glActiveTexture(GL_TEXTURE0);
        this.testTexture.bind();

        this.defaultShader.uploadMat4f("uProjection", this.camera.getProjectionMatrix());
        this.defaultShader.uploadMat4f("uView", this.camera.getViewMatrix());
        this.defaultShader.uploadFloat("uTime", Time.getTime());


        //Bind the VAO we're using
        glBindVertexArray(this.vaoID);

        // Enable vertex attribute pointer
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glDrawElements(GL_TRIANGLES, this.elementArray.length, GL_UNSIGNED_INT, 0);

        // Unbind everything
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);

        glBindVertexArray(0);

        this.defaultShader.detach();
    }
}
