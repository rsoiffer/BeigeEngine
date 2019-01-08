package behaviors._3d;

import engine.Behavior;
import engine.Layer;
import static engine.Layer.RENDER3D;
import graphics.voxels.VoxelModel;
import util.math.Quaternion;
import util.math.Transformation;
import util.math.Vec3d;
import util.math.Vec4d;

public class ModelBehavior extends Behavior {

    public final PositionBehavior3d position = require(PositionBehavior3d.class);

    public VoxelModel model;
    public double rotation = 0;
    public double scale = 1 / 16.;
    public Vec4d color = new Vec4d(1, 1, 1, 1);
    public boolean useOriginalSize;

    @Override
    public Layer layer() {
        return RENDER3D;
    }

    @Override
    public void step() {
        Transformation t = Transformation.create(position.position, Quaternion.fromAngleAxis(rotation, new Vec3d(0, 0, 1)), scale);
        // model.render(t.translate(useOriginalSize ? model.originalSize().mul(-.5) : model.center().mul(-1)), color);
        model.render(t.translate(model.originalSize().mul(-.5)), color);
    }
}
