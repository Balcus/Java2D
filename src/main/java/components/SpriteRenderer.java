package components;

import crimson.Component;

public class SpriteRenderer extends Component {

    private boolean firstIt = false;

    @Override
    public void start() {
        System.out.println("SpriteRenderer started");
    }

    @Override
    public void update(float dt) {
        if (!firstIt) {
            System.out.println("SpriteRenderer is updating");
            this.firstIt = true;
        }

    }
}
