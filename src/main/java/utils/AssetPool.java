package utils;


import render.Shader;
import render.Texture;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
* The AssetPool class manages assets in memory to ensure they remain accessible throughout the application's runtime.
* By storing assets in a HashMap, we prevent them from being garbage collected, which helps avoid issues with
* resource availability and lag spikes. Additionally, this approach allows us to pass asset by references rather than copy.
 * Helps a lot with Shaders and Textures.
*/

public class AssetPool {
    private static Map<String, Shader> shaders = new HashMap<>();
    private static Map<String, Texture> textures = new HashMap<>();

    public static Shader getShader(String resourceShaderFile) {
        File file = new File(resourceShaderFile);

        if (AssetPool.shaders.containsKey(file.getAbsolutePath())) {
            return AssetPool.shaders.get(file.getAbsolutePath());
        }else {
            Shader shader = new Shader(resourceShaderFile);
            shader.compile();
            AssetPool.shaders.put(file.getAbsolutePath(), shader);
            return shader;
        }
    }

    public static Texture getTexture(String resourceTextureFile) {
        File file = new File(resourceTextureFile);

        if (AssetPool.textures.containsKey(file.getAbsolutePath())) {
            return AssetPool.textures.get(file.getAbsolutePath());
        }else {
            Texture texture = new Texture(resourceTextureFile);
            texture.bind();
            AssetPool.textures.put(file.getAbsolutePath(), texture);
            return texture;
        }
    }
}
