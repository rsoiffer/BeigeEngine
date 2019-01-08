package behaviors;

import engine.Behavior;
import static engine.Core.dt;
import graphics.Window;
import java.util.LinkedList;
import java.util.Queue;

public class FPSBehavior extends Behavior {

    private final Queue<Double> tList = new LinkedList();
    public double fps;
    private double timeElapsed;

    @Override
    public void step() {
        double t = System.nanoTime() / 1e9;
        tList.add(t);
        while (t - tList.peek() > 5) {
            tList.poll();
        }
        fps = tList.size() / 5;

        timeElapsed += dt();
        if (timeElapsed > .25) {
            timeElapsed -= .25;
            Window.window.setTitle("FPS: " + Math.round(fps));
        }
    }
}
