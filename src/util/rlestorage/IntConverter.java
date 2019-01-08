package util.rlestorage;

import static util.math.MathUtils.round;
import util.math.Vec2d;
import util.math.Vec3d;

public interface IntConverter<T> {

    public T fromInt(int i);

    public static int pack(int[] sizes, int... values) {
        int r = 0;
        int m = 1;
        for (int i = 0; i < sizes.length; i++) {
            r += values[i] * m;
            m <<= sizes[i];
        }
        return r;
    }

    public int toInt(T t);

    public static int[] unpack(int[] sizes, int value) {
        int[] r = new int[sizes.length];
        for (int i = 0; i < sizes.length; i++) {
            r[i] = value & ((1 << sizes[i]) - 1);
            value >>>= sizes[i];
        }
        return r;
    }

    public static class IntegerConverter implements IntConverter<Integer> {

        @Override
        public Integer fromInt(int i) {
            if (i == 0) {
                return null;
            }
            return i - 1;
        }

        @Override
        public int toInt(Integer t) {
            if (t == null) {
                return 0;
            }
            return t + 1;
        }
    }

    public static class Vec2dConverter implements IntConverter<Vec2d> {

        private static final int[] SIZES = {16, 16};
        private static final double C = (1 << 16) - 1;

        @Override
        public Vec2d fromInt(int i) {
            if (i == C) {
                return null;
            }
            int[] values = unpack(SIZES, i);
            return new Vec2d(values[0] / C, values[1] / C);
        }

        @Override
        public int toInt(Vec2d t) {
            if (t == null) {
                return (int) C;
            }
            t = t.clamp(0, 1);
            return pack(SIZES, round(t.x * C), round(t.y * C));
        }
    }

    public static class Vec3dConverter implements IntConverter<Vec3d> {

        private static final int[] SIZES = {10, 10, 10};
        private static final double C = (1 << 10) - 1;

        @Override
        public Vec3d fromInt(int i) {
            if (i == -1) {
                return null;
            }
            int[] values = unpack(SIZES, i);
            return new Vec3d(values[0] / C, values[1] / C, values[2] / C);
        }

        @Override
        public int toInt(Vec3d t) {
            if (t == null) {
                return -1;
            }
            t = t.clamp(0, 1);
            return pack(SIZES, round(t.x * C), round(t.y * C), round(t.z * C));
        }
    }
}
