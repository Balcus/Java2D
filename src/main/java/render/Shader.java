package render;

import org.joml.*;
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
    private boolean isUsed = false ;

    public Shader(String filepath) {
        this.filepath = filepath;
        try {
            String source = new String(Files.readAllBytes(Paths.get(filepath)));
            // split by the pattern #type followed by one or more spaces and then one or more lowercase or uppercase letters
            String[] splitString = source.split("(#type)( )+([a-zA-Z]+)");

            // find the first pattern after #type 'pattern'
            int idx = source.indexOf("#type") + 6;
            int EOL = source.indexOf("\n");
            // this will return the type from source: vertex or fragment
            String firstPattern = source.substring(idx, EOL).trim();

            //same thing for the second pattern after #type
            idx = source.indexOf("#type", EOL) + 6;
            EOL = source.indexOf("\n", idx);
            String secondPattern = source.substring(idx, EOL).trim();

            if (firstPattern.equals("vertex")) {
                this.vertexSrc = splitString[1];
            }else if (firstPattern.equals("fragment")) {
                this.fragmentSrc = splitString[1];
            }else {
                throw new IOException("Unexpected token: " + firstPattern);
            }

            if (secondPattern.equals("vertex")) {
                this.vertexSrc = splitString[2];
            }else if (secondPattern.equals("fragment")) {
                this.fragmentSrc = splitString[2];
            }else {
                throw new IOException("Unexpected token: " + firstPattern);
            }

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
        glShaderSource(vertexID, this.vertexSrc);
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
        glShaderSource(fragmentID, this.fragmentSrc);
        glCompileShader(fragmentID);

        // Check for errors
        success = glGetShaderi(fragmentID, GL_COMPILE_STATUS);
        if (success == GL_FALSE) {
            int len = glGetShaderi(fragmentID, GL_INFO_LOG_LENGTH);
            System.out.println("ERROR: " + "'" + this.filepath + "'" + " \n\tFragment shader compilation failed.");
            System.out.println(glGetShaderInfoLog(fragmentID, len));
            assert false : "";
        }

        // Link shaders and check for errors
        this.shaderProgramID = glCreateProgram();
        glAttachShader(this.shaderProgramID, vertexID);
        glAttachShader(this.shaderProgramID, fragmentID);
        glLinkProgram(this.shaderProgramID);

        // Check for linking errors
        success = glGetProgrami(this.shaderProgramID, GL_LINK_STATUS);
        if (success == GL_FALSE) {
            int len = glGetProgrami(this.shaderProgramID, GL_INFO_LOG_LENGTH);
            System.out.println("ERROR: "+ "'" + filepath + "'" + " \n\tShader linking failed.");
            System.out.println(glGetProgramInfoLog(this.shaderProgramID, len));
        }
    }

    public void use() {
        if (!this.isUsed) {
            this.isUsed = true;
        }
        // bind shader program
        glUseProgram(this.shaderProgramID);
    }

    public void detach() {
        if (this.isUsed) {
            this.isUsed = false;
        }
        glUseProgram(0);
    }

    public void uploadMat4f(String varName, Matrix4f mat4) {
        int varLocation = glGetUniformLocation(this.shaderProgramID, varName);
        use();

        // will create a liniar representation of the 4x4 matrix
        FloatBuffer matBuffer = BufferUtils.createFloatBuffer(16); // the matrix is 4x4 so the capacity will be 16
        mat4.get(matBuffer);

        glUniformMatrix4fv(varLocation, false, matBuffer);
    }

    public void uploatMat3f(String varName, Matrix3f mat3f) {
        int varLocation = glGetUniformLocation(this.shaderProgramID, varName);
        use();
        FloatBuffer matBuffer = BufferUtils.createFloatBuffer(9);
        mat3f.get(matBuffer);
        glUniformMatrix3fv(varLocation, false, matBuffer);
    }

    public void uploadVec4f(String varName, Vector4f vec4f) {
        int varLocation = glGetUniformLocation(this.shaderProgramID, varName);
        use();
        glUniform4f(varLocation, vec4f.x, vec4f.y, vec4f.z, vec4f.w);
    }

    public void uploadVec3f(String varName, Vector3f vec3f) {
        int varLocation = glGetUniformLocation(this.shaderProgramID, varName);
        use();
        glUniform3f(varLocation, vec3f.x, vec3f.y, vec3f.z);
    }

    public void uploadVec2f(String varName, Vector2f vec2f) {
        int varLocation = glGetUniformLocation(this.shaderProgramID, varName);
        use();
        glUniform2f(varLocation, vec2f.x, vec2f.y);
    }

    public void uploadFloat(String varName, float val) {
        int varLocation = glGetUniformLocation(this.shaderProgramID, varName);
        use();
        glUniform1f(varLocation, val);
    }

    public void uploadInt(String varName, int val) {
        int varLocation = glGetUniformLocation(this.shaderProgramID, varName);
        use();
        glUniform1i(varLocation, val);
    }

    public void uploadTexture(String varName, int slot) {
        int varLocation = glGetUniformLocation(this.shaderProgramID, varName);
        use();
        glUniform1i(varLocation, slot);
    }
}
