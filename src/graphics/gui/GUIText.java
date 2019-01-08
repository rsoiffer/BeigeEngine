package graphics.gui;

import graphics.Color;
import graphics.sprites.Font;
import graphics.sprites.Font.FontText;
import java.util.Objects;

public class GUIText extends GUIItem {

    private String oldText;
    private FontText text;
    public boolean centered = true;
    public double scale = 1;
    public Color color = new Color(1, 1, 1, 1);
    public Color outlineColor = new Color(0, 0, 0, 1);

    public GUIText(String s) {
        setText(s);
    }

    @Override
    protected void render() {
        if (text != null) {
            if (centered) {
                text.draw2dCentered(transformationCenter(), color, outlineColor);
            } else {
                text.draw2d(transformationCenter(), color, outlineColor);
            }
        }
    }

    public final void setText(String s) {
        if (!Objects.equals(s, oldText)) {
            if (s != null && !s.isEmpty()) {
                text = Font.load("arial_outline").renderText(s);
            } else {
                text = null;
            }
            oldText = s;
        }
    }
}
