package examples;

import behaviors.FPSBehavior;
import behaviors.QuitOnEscapeBehavior;
import behaviors._3d.AccelerationBehavior3d;
import behaviors._3d.ModelBehavior;
import behaviors._3d.PositionBehavior3d;
import engine.Behavior;
import static engine.Behavior.track;
import engine.Core;
import static engine.Core.dt;
import static engine.Layer.RENDER3D;
import static engine.Layer.UPDATE;
import engine.Settings;
import graphics.Camera;
import graphics.Color;
import graphics.voxels.VoxelModel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import org.joml.Matrix4d;
import util.math.MathUtils;
import util.math.Transformation;
import util.math.Vec3d;
import util.math.Vec4d;
import vr.Vive;
import vr.ViveInput;
import static vr.ViveInput.MENU;
import static vr.ViveInput.TRACKPAD;
import static vr.ViveInput.TRIGGER;

public class FireballsVR {

    public static List<Fireball> grabbed = new ArrayList();

    public static void main(String[] args) {
        Settings.BACKGROUND_COLOR = new Color(.4, .7, 1, 1);
        Core.init();

        new FPSBehavior().create();
        new QuitOnEscapeBehavior().create();
        Camera.current = Camera.camera3d;
        Vive.init();
        Vive.initRender(new Vec4d(.4, .7, 1, 1));

        UPDATE.onStep(() -> {
            ViveInput.update();
            if (ViveInput.LEFT.buttonDown(MENU) && ViveInput.RIGHT.buttonDown(MENU)) {
                ViveInput.resetRightLeft();
                Vive.resetSeatedZeroPose();
            }

            if (ViveInput.RIGHT.buttonDown(TRACKPAD)) {
                for (int i = 0; i < 10; i++) {
                    Fireball f = new Fireball();
                    f.position.position = ViveInput.RIGHT.position();
                    f.acceleration.velocity.velocity = ViveInput.RIGHT.forwards().setLength(10).add(MathUtils.randomInSphere(new Random()));
                    f.create();
                }
            }

            if (ViveInput.RIGHT.buttonJustPressed(TRIGGER)) {
                for (Fireball f : Fireball.ALL) {
                    Vec3d delta = f.position.position.sub(ViveInput.RIGHT.position());
                    double along = ViveInput.RIGHT.forwards().normalize().dot(delta);
                    double side = Math.sqrt(delta.lengthSquared() - along * along);
                    if (side < along * .2 + 1) {
                        grabbed.add(f);
                    }
                }
            }
            if (ViveInput.RIGHT.buttonDown(TRIGGER)) {
                Vec3d desiredVelocity = ViveInput.RIGHT.deltaPosition().div(dt()).mul(10);
                for (Fireball f : grabbed) {
                    f.acceleration.velocity.velocity = f.acceleration.velocity.velocity.lerp(desiredVelocity, .1);
                }
            }
            if (ViveInput.RIGHT.buttonJustReleased(TRIGGER)) {
                grabbed.clear();
            }
        });

        RENDER3D.onStep(() -> {
            VoxelModel.load("dagger.vox").render(new Transformation(new Matrix4d()
                    .translate(Camera.camera3d.position.toJOML())
                    .mul(ViveInput.RIGHT.pose())
                    .translate(-.125, -.125, -.125)
                    .scale(1 / 32.)
            ), Color.WHITE);
            VoxelModel.load("dagger.vox").render(new Transformation(new Matrix4d()
                    .translate(Camera.camera3d.position.toJOML())
                    .mul(ViveInput.LEFT.pose())
                    .translate(-.125, -.125, -.125)
                    .scale(1 / 32.)
            ), Color.WHITE);
        });

        Core.run();
    }

    public static class Fireball extends Behavior {

        public static final Collection<Fireball> ALL = track(Fireball.class);

        public final PositionBehavior3d position = require(PositionBehavior3d.class);
        public final AccelerationBehavior3d acceleration = require(AccelerationBehavior3d.class);
        public final ModelBehavior model = require(ModelBehavior.class);

        @Override
        public void createInner() {
            model.model = VoxelModel.load("fireball.vox");
            model.scale = 1 / 32.;
        }

        @Override
        public void step() {
            acceleration.acceleration = acceleration.velocity.velocity.mul(-.2);
        }
    }
}
