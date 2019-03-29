package behaviors;

import engine.Behavior;
import engine.Core;
import engine.Input;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;

public class QuitOnEscapeBehavior extends Behavior {

    @Override
    public void step() {
        if (Input.keyJustPressed(GLFW_KEY_ESCAPE)) {
            Core.stopGame();
        }
    }
}
