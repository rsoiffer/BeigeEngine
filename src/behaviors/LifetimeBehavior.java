package behaviors;

import engine.Behavior;
import static engine.Core.dt;

public class LifetimeBehavior extends Behavior {

    public double lifetime = 0;

    @Override
    public void step() {
        lifetime -= dt();
        if (lifetime < 0) {
            getRoot().destroy();
        }
    }
}
