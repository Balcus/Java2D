package crimson;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;


/**
 * <h3>Camera class</h3>
 * <p>Manages the view and projection matrices for a 2D scene</p>
 * <p>The camera tracks its position in the game world and can be
 * used to generate view and projection matrices based on its position.</p>
 * <p>
 *     Fields include:
 *     <ul>
 *         <li>{@code projectionMatrix} : 4x4 matrix used to project 3D points onto a 2D screen </li>
 *         <li>{@code viewMatrix} 4x4 matrix used to represent the camera's view of the scene </li>
 *         <li>{@code position} 2D vector used to represent the camera's position in the game's world</li>
 *     </ul>
 * </p>
 */
public class Camera {
    private Matrix4f projectionMatrix;
    private Matrix4f viewMatrix;
    public Vector2f position;

    Camera(Vector2f position) {
        this.position = position;
        this.projectionMatrix = new Matrix4f();
        this.viewMatrix = new Matrix4f();
        adjustProjection();
    }

    // adjust projection if the window size changes
    public void adjustProjection() {
        // init the projectionMatrix with the identity matrix
        this.projectionMatrix.identity();

        // creates the projection matrix
        projectionMatrix.ortho(0.0f, 32.0f * 40.0f, 0.0f, 32.0f * 21.0f, 0.0f, 100.0f);
    }

    public Matrix4f getViewMatrix() {
        // makes the camera look to the front
        Vector3f cameraFront = new Vector3f(0.0f, 0.0f, -1.0f);
        // makes the camera look up
        Vector3f cameraUp = new Vector3f(0.0f, 1.0f, 0.0f);

        // init the viewMatrix with the identity matrix
        this.viewMatrix.identity();

        // creates the view matrix
        this.viewMatrix.lookAt(new Vector3f(this.position.x, this.position.y, 20.0f),
                cameraFront.add(this.position.x, this.position.y, 0.0f),
                cameraUp);

        return this.viewMatrix;
    }

    public Matrix4f getProjectionMatrix() {
        return this.projectionMatrix;
    }
}
