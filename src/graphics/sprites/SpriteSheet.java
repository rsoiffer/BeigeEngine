/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package graphics.sprites;

import graphics.Color;
import graphics.opengl.BufferObject;
import static graphics.opengl.GLObject.bindAll;
import graphics.opengl.Shader;
import graphics.opengl.Texture;
import graphics.opengl.VertexArrayObject;
import static graphics.sprites.Sprite.SPRITE_SHADER;
import static graphics.sprites.Sprite.drawTexture;
import java.util.HashMap;
import java.util.Map;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TRIANGLE_FAN;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import util.math.Transformation;
import util.math.Vec2d;

/**
 *
 * @author TARS
 */
public class SpriteSheet {
    
    public static final int SHEET_DEPTH = 32;
    
    private static final Map<String, SpriteSheet> SPRITE_SHEET_CACHE = new HashMap();

    public static SpriteSheet load(String fileName) {
        if (!SPRITE_SHEET_CACHE.containsKey(fileName)) {
            SpriteSheet s = new SpriteSheet(fileName);
            SPRITE_SHEET_CACHE.put(fileName, s);
        }
        return SPRITE_SHEET_CACHE.get(fileName);
    }

    public static final Shader SPRITE_SHEET_SHADER = Shader.load("sprite_sheet");

    public static final VertexArrayObject SPRITE_SHEET_VAO = VertexArrayObject.createVAO(() -> {
        BufferObject vbo = new BufferObject(GL_ARRAY_BUFFER, new float[]{
            0.5f, 0.5f, 0, 1, 1,
            0.5f, -0.5f, 0, 1, 0,
            -0.5f, -0.5f, 0, 0, 0,
            -0.5f, 0.5f, 0, 0, 1
        });
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 20, 0);
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, 20, 12);
        glEnableVertexAttribArray(1);
    });

    private final Texture texture;

    private SpriteSheet(String fileName) {
        this.texture = Texture.load(fileName);
    }
    
    public void draw(Transformation t, int id, Color color) {
        drawTexture(texture, t, id, color);
    }

    public static void drawTexture(Texture texture, Transformation t, int id, Color color) {
        SPRITE_SHEET_SHADER.setMVP(t);
        SPRITE_SHEET_SHADER.setUniform("color", color);
        SPRITE_SHEET_SHADER.setUniform("subCoords", new Vec2d(id % SHEET_DEPTH, 15 - id / SHEET_DEPTH));
        bindAll(texture, SPRITE_SHEET_SHADER, SPRITE_SHEET_VAO);
        glDrawArrays(GL_TRIANGLE_FAN, 0, 4);
    }

    public int getHeight() {
        return texture.getHeight();
    }

    public int getWidth() {
        return texture.getWidth();
    }
}
