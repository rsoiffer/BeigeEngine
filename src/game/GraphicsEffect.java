package game;

import behaviors.LifetimeBehavior;
import engine.Behavior;
import engine.Layer;
import static engine.Layer.RENDER3D;
import java.util.function.Consumer;

public class GraphicsEffect extends Behavior {

    public final LifetimeBehavior lifetime = require(LifetimeBehavior.class);

    public Consumer<Double> onRender;

    public static void createGraphicsEffect(double lifetime, Runnable onRender) {
        createGraphicsEffect(lifetime, t -> onRender.run());
    }

    public static void createGraphicsEffect(double lifetime, Consumer<Double> onRender) {
        GraphicsEffect ge = new GraphicsEffect();
        ge.lifetime.lifetime = lifetime;
        ge.onRender = lt -> onRender.accept(lifetime - lt);
        ge.create();
    }

    @Override
    public Layer layer() {
        return RENDER3D;
    }

    @Override
    public void step() {
        onRender.accept(lifetime.lifetime);
    }
}
