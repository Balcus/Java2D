package render;

import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;

import java.io.IOException;
import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;

public class Shader {
    private int shaderProgramID;
    private String vertexSrc;
    private String fragmentSrc;
    private String filepath;

    public Shader(String filepath) {
        this.filepath = filepath;
        try {
            String source = new String(Files.readAllBytes(Paths.get(filepath)));
            // split by the pattern #type followed by one or more spaces and then one or more lowercase or uppercase letters
            // for example: #type vertex
            String[] splitString = source.split("(#type)( )+([a-zA-Z]+)");

            // find the first pattern after #type 'pattern'
            int idx = source.indexOf("#type") + 6;
            int EOL = source.indexOf("\n");
            // this will return the type from source, for example: vertex
            String firstPattern = source.substring(idx, EOL).trim();

            //same thing for the second pattern after #type
            idx = source.indexOf("#type", EOL) + 6;
            EOL = source.indexOf("\n", idx);
            String secondPattern = source.substring(idx, EOL).trim();

            if (firstPattern.equals("vertex")) {
                vertexSrc = splitString[1];
            }else if (firstPattern.equals("fragment")) {
                fragmentSrc = splitString[1];
            }else {
                throw new IOException("Unexpected token: " + firstPattern);
            }

            if (secondPattern.equals("vertex")) {
                vertexSrc = splitString[2];
            }else if (secondPattern.equals("fragment")) {
                fragmentSrc = splitString[2];
            }else {
                throw new IOException("Unexpected token: " + firstPattern);
            }

            System.out.println(vertexSrc);
            System.out.println(fragmentSrc);


        }catch(IOException e) {
            e.printStackTrace();
            assert false : "Could not open file for shaders: " + filepath;
        }

    }

    public void compile() {
        // Compile and link shaders

        int vertexID, fragmentID;

        // First load and compile the vertex shader
        vertexID = glCreateShader(GL_VERTEX_SHADER);
        // Pass the shader source code to the GPU
        glShaderSource(vertexID, vertexSrc);
        glCompileShader(vertexID);

        // Check for errors
        int success = glGetShaderi(vertexID, GL_COMPILE_STATUS);
        if (success == GL_FALSE) {
            int len = glGetShaderi(vertexID, GL_INFO_LOG_LENGTH);
            System.out.println("ERROR: 'defaultShader.glsl' \n\tVertex shader compilation failed.");
            System.out.println(glGetShaderInfoLog(vertexID, len));
            assert false : "";
        }

        // Same thing for the fragment shaders
        fragmentID = glCreateShader(GL_FRAGMENT_SHADER);
        // Pass the fragment source code to the GPU
        glShaderSource(fragmentID, fragmentSrc);
        glCompileShader(fragmentID);

        // Check for errors
        success = glGetShaderi(fragmentID, GL_COMPILE_STATUS);
        if (success == GL_FALSE) {
            int len = glGetShaderi(fragmentID, GL_INFO_LOG_LENGTH);
            System.out.println("ERROR: " + "'" + filepath + "'" + " \n\tFragment shader compilation failed.");
            System.out.println(glGetShaderInfoLog(fragmentID, len));
            assert false : "";
        }

        // Link shaders and check for errors
        shaderProgramID = glCreateProgram();
        glAttachShader(shaderProgramID, vertexID);
        glAttachShader(shaderProgramID, fragmentID);
        glLinkProgram(shaderProgramID);

        // Check for linking errors
        success = glGetProgrami(shaderProgramID, GL_LINK_STATUS);
        if (success == GL_FALSE) {
            int len = glGetProgrami(shaderProgramID, GL_INFO_LOG_LENGTH);
            System.out.println("ERROR: "+ "'" + filepath + "'" + " \n\tShader linking failed.");
            System.out.println(glGetProgramInfoLog(shaderProgramID, len));
        }
    }

    public void use() {
        // bind shader program
        glUseProgram(shaderProgramID);
    }

    public void detach() {
        glUseProgram(0);
    }

    public void uploadMat4f(String varName, Matrix4f mat4) {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        FloatBuffer matBuffer = BufferUtils.createFloatBuffer(16); // the matrix is 4x4 so the capacity will be 16
        // puts the matrix into the buffer
        mat4.get(matBuffer);
        glUniformMatrix4fv(varLocation, false, matBuffer);
    }
}
