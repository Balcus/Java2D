package render;

import org.lwjgl.BufferUtils;
import org.lwjgl.system.CallbackI;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11C.*;
import static org.lwjgl.opengl.GL13C.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13C.glActiveTexture;
import static org.lwjgl.stb.STBImage.*;

public class Texture {
    private String filePath;
    private int textureID;

    public Texture(String filePath) {
        this.filePath = filePath;
        this.textureID = glGenTextures();

        // Generate texture on GPU
        glBindTexture(GL_TEXTURE_2D, textureID);

        // Set texture parameters :

        // Repeat texture in both directions
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);

        // When stretching image pixelate it
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);

        // When shrinking image pixelate  it
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        IntBuffer width = BufferUtils.createIntBuffer(1);
        IntBuffer height = BufferUtils.createIntBuffer(1);
        // rgb or rgba
        IntBuffer channels = BufferUtils.createIntBuffer(1);

        // load the image
        stbi_set_flip_vertically_on_load(true);
        ByteBuffer image = stbi_load(filePath, width, height, channels, 0);

        if (image != null) {
            if (channels.get(0) == 4) {
                glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width.get(0), height.get(0),
                        0, GL_RGBA, GL_UNSIGNED_BYTE, image);
            }else if (channels.get(0) == 3) {
                glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width.get(0), height.get(0),
                        0, GL_RGB, GL_UNSIGNED_BYTE, image);
            }else {
                assert false : "ERROR : (Texture) Unkown number of channels: " + channels.get(0);
            }
        }else {
            assert false: "ERROR : Failed to load texture: " + filePath;
        }

        // need to free the memory allocated for the image (stbi func does not use the java VM and is written directly in C)
        stbi_image_free(image);

    }

    public void bind() {
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, textureID);
    }

    public void unbind() {
        glBindTexture(GL_TEXTURE_2D, 0);
    }
}

