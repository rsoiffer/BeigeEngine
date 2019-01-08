package graphics.gui;

import graphics.Color;

public class GUIButton extends GUIRectangle {

    public GUIText text;
    public Runnable onClick;

    public GUIButton(String s) {
        this(s, null);
    }

    public GUIButton(String s, Runnable onClick) {
        if (s != null && !s.isEmpty()) {
            text = new GUIText(s);
            add(text);
        }
        this.onClick = onClick;
    }

    @Override
    protected void onClick() {
        if (onClick != null) {
            onClick.run();
        }
    }

    @Override
    protected void onHoverStart() {
        color = new Color(.6, .6, .6, 1);
    }

    @Override
    protected void onHoverStop() {
        color = new Color(.4, .4, .4, 1);
    }
}
