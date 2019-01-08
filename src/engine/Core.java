package engine;

import static engine.Layer.ALL_LAYERS;
import graphics.Window;
import java.util.Collection;
import java.util.LinkedList;

public abstract class Core {

    public static Thread MAIN_THREAD;

    private static long prevTime;
    private static double dt;
    private static final Collection<Runnable> TO_RUN = new LinkedList();
    private static boolean shouldClose;

    private static Collection<Runnable> clearToRun() {
        synchronized (TO_RUN) {
            Collection<Runnable> r = new LinkedList<>(TO_RUN);
            TO_RUN.clear();
            return r;
        }
    }

    public static double dt() {
        return dt;
    }

    public static void init() {
        MAIN_THREAD = Thread.currentThread();
        Window.initGLFW();
        Input.init();
    }

    public static void onMainThread(Runnable toRun) {
        if (toRun == null) {
            throw new RuntimeException("toRun cannot be null");
        }
        if (Thread.currentThread() != MAIN_THREAD) {
            synchronized (TO_RUN) {
                TO_RUN.add(toRun);
            }
        } else {
            toRun.run();
        }
    }

    public static void run() {
        while (!shouldClose && !Window.window.shouldClose()) {
            Input.nextFrame();
            Window.window.nextFrame();

            long time = System.nanoTime();
            dt = Math.min((time - prevTime) / 1e9, .1);
            prevTime = time;

            clearToRun().forEach(r -> r.run());
            ALL_LAYERS.forEach(Layer::stepAll);
        }
        Window.cleanupGLFW();
        System.exit(0);
    }

    public static void stopGame() {
        shouldClose = true;
    }
}
