package behaviors._3d;

import engine.Behavior;
import static engine.Core.dt;
import util.math.Vec3d;

public class VelocityBehavior3d extends Behavior {

    public final PositionBehavior3d position = require(PositionBehavior3d.class);

    public Vec3d velocity = new Vec3d(0, 0, 0);

    @Override
    public void step() {
        position.position = position.position.add(velocity.mul(dt()));
    }
}
