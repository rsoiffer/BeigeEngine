package examples;

import behaviors.FPSBehavior;
import behaviors.QuitOnEscapeBehavior;
import engine.Core;
import engine.Input;
import static engine.Layer.RENDER2D;
import static engine.Layer.UPDATE;
import engine.Settings;
import graphics.Camera;
import graphics.Color;
import graphics.Graphics;
import graphics.Window;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_A;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_H;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_R;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_S;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_SPACE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_W;
import static util.math.MathUtils.clamp;
import static util.math.MathUtils.floor;
import static util.math.MathUtils.mod;
import util.math.Transformation;
import util.math.Vec2d;

public class GameOfLife {

    private static final int SIZE = 1000;
    private static boolean[][] STATE = new boolean[SIZE][SIZE];

    private static Vec2d viewPos = new Vec2d(0, 0);
    private static double viewZoom = 0;
    private static Vec2d viewSize = new Vec2d(16, 9);
    private static boolean running = false;

    public static void main(String[] args) {
        Core.init();

        new FPSBehavior().create();
        new QuitOnEscapeBehavior().create();

        UPDATE.onStep(() -> {
            double dx = 0, dy = 0;
            
            if (Input.keyDown(GLFW_KEY_W)) {
                dy += 1;
            }
            if (Input.keyDown(GLFW_KEY_A)) {
                dx -= 1;
            }
            if (Input.keyDown(GLFW_KEY_S)) {
                dy -= 1;
            }
            if (Input.keyDown(GLFW_KEY_D)) {
                dx += 1;
            }
            viewPos = viewPos.add(new Vec2d(dx, dy).mul(5 * Math.pow(2, -.5 * viewZoom) * Core.dt()));

            viewZoom += Input.mouseWheel();
            viewZoom = clamp(viewZoom, -14, 0);

            if (Input.mouseDown(0)) {
                set(floor(Input.mouse().x), floor(Input.mouse().y), true);
            }
            if (Input.mouseDown(1)) {
                set(floor(Input.mouse().x), floor(Input.mouse().y), false);
            }
            if (Input.keyJustPressed(GLFW_KEY_SPACE)) {
                running = !running;
            }
            if (Input.keyJustPressed(GLFW_KEY_R)) {
                for (int x = 0; x < SIZE; x++) {
                    for (int y = 0; y < SIZE; y++) {
                        if (Math.random() < .01) {
                            set(x, y, !get(x, y));
                        }
                    }
                }
            }

            if (running) {
                STATE = nextState();
            }
            if(Input.keyJustPressed(GLFW_KEY_H)){
                Window.window.resizeWindow(400, 400);
            }
        });

        RENDER2D.onStep(() -> {
            viewSize = new Vec2d(16, 16.0 * Settings.WINDOW_HEIGHT / Settings.WINDOW_WIDTH);
            Camera.camera2d.setCenterSize(viewPos, viewSize.mul(Math.pow(2, -.5 * viewZoom)));
            for (int x = floor(Camera.camera2d.lowerLeft.x); x < Camera.camera2d.upperRight.x; x++) {
                for (int y = floor(Camera.camera2d.lowerLeft.y); y < Camera.camera2d.upperRight.y; y++) {
                    if (get(x, y)) {
                        Graphics.drawRectangle(Transformation.create(new Vec2d(x, y), 0, 1), Color.WHITE);
                    }
                }
            }
        });

        Core.run();
    }

    private static boolean get(int x, int y) {
        return STATE[mod(x, SIZE)][mod(y, SIZE)];
    }

    private static boolean[][] nextState() {
        boolean[][] nextState = new boolean[SIZE][SIZE];
        for (int x = 0; x < SIZE; x++) {
            for (int y = 0; y < SIZE; y++) {
                int neighborCount = 0;
                for (int i = -1; i <= 1; i++) {
                    for (int j = -1; j <= 1; j++) {
                        neighborCount += get(x + i, y + j) ? 1 : 0;
                    }
                }
                nextState[x][y] = neighborCount == 3 || (neighborCount == 4 && get(x, y));
            }
        }
        return nextState;
    }

    private static void set(int x, int y, boolean val) {
        STATE[mod(x, SIZE)][mod(y, SIZE)] = val;
    }
}
