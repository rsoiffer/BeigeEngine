package behaviors._2d;

import engine.Behavior;
import engine.Layer;
import static engine.Layer.POSTUPDATE;
import util.math.Vec2d;

public class PreviousPositionBehavior2d extends Behavior {

    public final PositionBehavior2d position = require(PositionBehavior2d.class);

    public Vec2d prevPos;

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
