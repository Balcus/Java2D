package crimson;

/*
The game engine will use scenes for very different reasons such as : main menus, the levels, the cutscenses
so we make it abstract and from it will create the specific scene types
*/

import render.Renderer;

import java.util.ArrayList;
import java.util.List;

public abstract class Scene {

    protected Camera camera;
    private boolean isRunning = false;
    protected List<GameObject> gameObjects = new ArrayList<>();
    protected Renderer renderer = new Renderer();

    public Scene() {}
    public abstract void update(float dt);

    public void start() {
        for( GameObject go : this.gameObjects ) {
            go.start();
            this.renderer.add(go);
        }
        this.isRunning = true;
    }

    public void addGameObjectToScene(GameObject go) {
        if (!this.isRunning) {
            this.gameObjects.add(go);
        }else {
            this.gameObjects.add(go);
            go.start();
            this.renderer.add(go);
        }
    }

    public void init() {

    }

    public Camera getCamera() {
        return this.camera;
    }

}
