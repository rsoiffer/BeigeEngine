package graphics;

import util.math.Vec4d;

public class Color extends Vec4d {

    public static final Color WHITE = new Color(1, 1, 1, 1);

    public final double r, g, b, a;

    public Color(double r, double g, double b, double a) {
        super(r, g, b, a);
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }
}
