package behaviors._3d;

import engine.Behavior;
import engine.Layer;
import static engine.Layer.POSTUPDATE;
import util.math.Vec3d;

public class PreviousPositionBehavior3d extends Behavior {

    public final PositionBehavior3d position = require(PositionBehavior3d.class);

    public Vec3d prevPos;

    @Override
    public void createInner() {
        prevPos = position.position;
    }

    @Override
    public Layer layer() {
        return POSTUPDATE;
    }

    @Override
    public void step() {
        prevPos = position.position;
    }
}
