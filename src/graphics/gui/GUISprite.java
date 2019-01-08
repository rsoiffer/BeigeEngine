package graphics.gui;

import graphics.Color;
import graphics.sprites.Sprite;

public class GUISprite extends GUIItem {

    public Sprite sprite;
    public double rotation;
    public double scale = 1;
    public Color color = new Color(1, 1, 1, 1);

    public GUISprite(String fileName) {
        sprite = Sprite.load(fileName);
    }

    @Override
    protected void render() {
        sprite.draw(transformationCenter(), color);
    }
}
