package crimson;

import java.util.ArrayList;
import java.util.List;

/**
 * GameObject class modelating a game object that can hold multiple Components.
 * <br><br>
 * Each component adds functionality to the GameObject such as rendering, physics or some other behaviour
 * <br><br>
 * Visual Representation: <a href="https://upload.wikimedia.org/wikipedia/commons/thumb/2/23/ECS_Simple_Layout.svg/640px-ECS_Simple_Layout.svg.png">image_link</a>
 */
public class GameObject {
    private String name;
    private List<Component> components;
    public Transform transform;

    public GameObject(String name) {
        this.name = name;
        this.components = new ArrayList<>();
        this.transform = new Transform();
    }

    public GameObject(String name, Transform transform) {
        this.name = name;
        this.components = new ArrayList<>();
        this.transform = transform;
    }

    /**
     * <p>Retrieves the <b>first</b> component of the specified class type from this GameObject's list of components.</p>
     * @param componentClass the {@code Class} object of the type of component being requested
     * @return first component of the specified type or {@code null} if no matching component found
     * @param <T> the type of the component to retrieve (MUST extend {@link Component} class)
     * @throws ClassCastException if the component cannot be cast to the specified type
     */
    public <T extends Component> T getComponent(Class<T> componentClass) {
        for (Component c : components) {
            // used to check if componentClass is either the same class as or a superclass or interface of Component
            if (componentClass.isAssignableFrom(c.getClass())) {
                try {
                    return componentClass.cast(c);
                }catch (ClassCastException e) {
                    e.printStackTrace();
                    assert false : "ERROR : Casting components";
                }
            }
        }
        return null;
    }

    /**
     * <p>Removes the <b>first</b> component of the specified class type from this GameObject's list of components.</p>
     * @param componentClass the {@code Class} object of the type of component being requested
     * @param <T> the type of the component to retrieve (MUST extend {@link Component} class)
     */
    public <T extends Component> void removeComponent(Class<T> componentClass) {
        for (int i = 0; i < components.size(); i++) {
            Component c = components.get(i);
            if (componentClass.isAssignableFrom(c.getClass())) {
                components.remove(i);
                return;
            }
        }
    }

    /**
     * <p>Adds component to the GameObject's list of components.</p>
     * @param c {@code Component} object to be added to the GameObject's list of components
     */
    public <T extends Component> void addComponent(Component c) {
        this.components.add(c);
        // makes sure each Component added to the GameObject is linked back to the GameObject that holds it
        c.gameObject = this;
    }

    public void update(float dt) {
        for (int i = 0; i < components.size(); i++) {
            Component c = components.get(i);
            c.update(dt);
        }
    }

    public void start() {
        for (int i = 0; i < components.size(); i++) {
            Component c = components.get(i);
            c.start();
        }
    }
}
