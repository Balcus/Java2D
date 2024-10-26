package crimson;

/*
The game engine will use scenes for very different reasons such as : main menus, the levels, the cutscenses
so we make it abstract and from it will create the specific scene types
*/

public abstract class Scene {
    public Scene() {}
    public void init() {}
    public abstract void update(float dt);
}
