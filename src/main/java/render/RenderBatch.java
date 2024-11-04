package render;

import components.SpriteRenderer;
import crimson.Window;
import org.joml.Vector4f;
import utils.AssetPool;
import utils.Time;

import static org.lwjgl.opengl.ARBVertexArrayObject.glBindVertexArray;
import static org.lwjgl.opengl.ARBVertexArrayObject.glGenVertexArrays;
import static org.lwjgl.opengl.GL20.*;

public class RenderBatch {

    /*
    Vertex
    ================================================
    Pos                  Color
    float, float         float, float, float, float
     */

    private final int POS_SIZE = 2;
    private final int COLOR_SIZE = 4;
    private final int POS_OFFSET = 0;
    private final int COLOR_OFFSET = POS_OFFSET + POS_SIZE * Float.BYTES;
    private final int VERTEX_SIZE = 6;
    private final int VERTEX_SIZE_BYTES = VERTEX_SIZE * Float.BYTES;

    private SpriteRenderer[] sprites;
    private int numSprites;
    private boolean hasRoom;
    private int maxBatchSize;

    private float[] vertices;
    private int vaoID;
    private int vboID;
    private Shader shader;

    public RenderBatch(int maxBatchSize) {
        this.maxBatchSize = maxBatchSize;
        this.sprites = new SpriteRenderer[maxBatchSize];
        this.numSprites = 0;
        this.hasRoom = true;

        this.shader = AssetPool.getShader("assets/shaders/default.glsl");
        this.vertices = new float[this.maxBatchSize * 4 * VERTEX_SIZE];

    }

    public void start() {
        // Generate and bind VAO
        this.vaoID = glGenVertexArrays();
        glBindVertexArray(this.vaoID);

        // Allocate spaces on the GPU for the vertecies
        this.vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferData(GL_ARRAY_BUFFER, (long) this.vertices.length * Float.BYTES, GL_DYNAMIC_DRAW);

        // Create and upload inidices buffer
        int eboID = glGenBuffers();
        int[] indices = generateIndices();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);

        // Enable the buffer attrib pointers
        glVertexAttribPointer(0, POS_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, POS_OFFSET);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, COLOR_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, COLOR_OFFSET);
        glEnableVertexAttribArray(1);
    }

    public void render() {
        // For now, rebuffer all data every frame
        glBindBuffer(GL_ARRAY_BUFFER, this.vboID);
        glBufferSubData(GL_ARRAY_BUFFER, 0, this.vertices);

        // Use shader
        this.shader.use();
        this.shader.uploadMat4f("uProjection", Window.getCurrentScene().getCamera().getProjectionMatrix());
        this.shader.uploadMat4f("uView", Window.getCurrentScene().getCamera().getViewMatrix());
        this.shader.uploadFloat("uTime", Time.getTime());

        glBindVertexArray(this.vaoID);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glDrawElements(GL_TRIANGLES, this.numSprites * 6, GL_UNSIGNED_INT, 0);

        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindVertexArray(0);

        shader.detach();
    }

    public void addSprite(SpriteRenderer sprite) {
        // Get index and add renderObject
        int index = this.numSprites;
        this.sprites[index] = sprite;
        this.numSprites++;

        // Add proprieties to local vertex array
        loadVertexProperties(index);

        if (this.numSprites >= this.maxBatchSize) {
            this.hasRoom = false;
        }
    }

    /**
     * <p>Loads the vertex properties for a sprite at the specified index into the vertex array.</p>
     *
     * <p>This method populates the vertices array with position and color data for a sprite,
     * preparing it for rendering. Each sprite's vertex properties are defined by four vertices,
     * where each vertex includes position and color attributes.</p>
     *
     * <p>The position of each vertex is calculated based on the sprite's position, scale,
     * and specified offset. Color information is loaded from the sprite's color attribute.</p>
     *
     * @param index the index of the sprite within the sprites array for which the vertex properties are to be loaded
     */
    private void loadVertexProperties(int index) {
        SpriteRenderer sprite = this.sprites[index];

        // Find offset within array ( 4 vertices per sprite )
        int offset = index * 4 * VERTEX_SIZE;

        Vector4f color = sprite.getColor();

        // Add vertices with the appropriate proprieties
        float xAdd = 1.0f;
        float yAdd = 1.0f;

        for (int i = 0 ; i < 4 ; i++) {
            if (i == 1) {
                yAdd = 0.0f;
            } else if (i == 2) {
                xAdd = 0.0f;
            } else if (i == 3) {
                yAdd = 1.0f;
            }


            // Load position
            vertices[offset] = sprite.gameObject.transform.position.x + (xAdd * sprite.gameObject.transform.scale.x);
            vertices[offset + 1] = sprite.gameObject.transform.position.y + (yAdd * sprite.gameObject.transform.scale.y);

            // Load color
            this.vertices[offset + 2] = color.x;
            this.vertices[offset + 3] = color.y;
            this.vertices[offset + 4] = color.z;
            this.vertices[offset + 5] = color.w;

            offset += VERTEX_SIZE;
        }
    }

    /**
     * <p>This method will automatically generate the indices array required for generating the quads</p>
     * @return <b>elements</b> : an integer array containing the indices for each quad in the batch, ready for use in rendering the quads.
     */
    private int[] generateIndices() {
        // 6 indecies per quad  (3 per triangle and we have 2 triangles for each quad)
        int[] elements = new int[6 * this.maxBatchSize];
        for (int i = 0; i < this.maxBatchSize; i++) {
            loadElementIndecies(elements, i);
        }
        return elements;
    }

    /**
     * <p>This method fills the specified portion of the elements array with indices to define two triangles forming a quad.</p>
     * <p>Each quad requires six indices (two triangles) and references four unique vertices.</p>
     *
     * @param elements the array to store indices for rendering quads
     * @param index the index of the quad being processed
     */

    private void loadElementIndecies(int[] elements, int index) {
        int offsetArrayIndex = 6 * index;
        int offset = 4 * index;

        /*
        The below code is used to automatically generate the indexes needed to create each quad,
        Triangle 1 and 2 put together will form the quad
         */

        // Triangle 1
        elements[offsetArrayIndex] = offset + 3;
        elements[offsetArrayIndex + 1] = offset + 2;
        elements[offsetArrayIndex + 2] = offset + 0;

        // Triangle 2
        elements[offsetArrayIndex + 3] = offset + 0;
        elements[offsetArrayIndex + 4] = offset + 2;
        elements[offsetArrayIndex + 5] = offset + 1;
    }

    public boolean getHasRoom() {
        return this.hasRoom;
    }
}
