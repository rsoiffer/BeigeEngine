package graphics.sprites;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import util.Resources;

public class Animation {

    private static final Map<String, Animation> ANIMATION_CACHE = new HashMap();

    public static Animation load(String fileName) {
        if (!ANIMATION_CACHE.containsKey(fileName)) {
            Animation a = new Animation(fileName);
            ANIMATION_CACHE.put(fileName, a);
        }
        return ANIMATION_CACHE.get(fileName);
    }

    public int length;
    public double speed;
    public List<String> modes = Arrays.asList("");
    private Map<String, List<Sprite>> sprites;

    private Animation(String fileName) {
        try {
            String[] animSettings = Resources.loadFileAsString("sprites/" + fileName + "/anim_settings.txt").split("\n");
            for (String setting : animSettings) {
                if (setting.startsWith("length: ")) {
                    length = Integer.parseInt(setting.substring(8));
                }
                if (setting.startsWith("speed: ")) {
                    speed = Double.parseDouble(setting.substring(7));
                }
                if (setting.startsWith("modes: ")) {
                    modes = Arrays.asList(setting.substring(7).split(" "));
                }
            }
            sprites = new HashMap();
            for (String mode : modes) {
                sprites.put(mode, new ArrayList());
                for (int i = 0; i < length; i++) {
                    if (mode.equals("")) {
                        sprites.get(mode).add(Sprite.load(fileName + "/" + i + ".png"));
                    } else {
                        sprites.get(mode).add(Sprite.load(fileName + "/" + mode + "/" + i + ".png"));
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public Sprite getSpriteOrNull(String mode, int index) {
        if (modes.contains(mode) && index >= 0 && index < length) {
            return sprites.get(mode).get(index);
        } else {
            return null;
        }
    }
}
