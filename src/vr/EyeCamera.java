package vr;

import graphics.Camera;
import org.joml.Matrix4d;
import org.joml.Vector3d;
import org.joml.Vector4d;
import util.math.Vec3d;

public class EyeCamera extends Camera {

    private static final Matrix4d COORD_CHANGE = new Matrix4d(
            0, 0, -1, 0,
            -1, 0, 0, 0,
            0, 1, 0, 0,
            0, 0, 0, 1);

    private static Matrix4d headPose, headPose2;
    private static Vec3d pos;

    private final boolean leftEye;

    public EyeCamera(boolean leftEye) {
        this.leftEye = leftEye;
    }

    public static Vec3d headPos() {
        Vector4d v = new Vector4d(0, 0, 0, 1).mul(headPose2);
        return new Vec3d(v.x, v.y, v.z);
    }

    public static Vec3d headTransform(Vec3d dir) {
        Vector4d v = new Vector4d(dir.x, dir.y, dir.z, 0).mul(headPose2);
        return new Vec3d(v.x, v.y, v.z).normalize();
    }

    @Override
    public Matrix4d projectionMatrix() {
        return Vive.getEyeProjectionMatrix(leftEye);
    }

    @Override
    public Matrix4d viewMatrix() {
        return Vive.getEyeToHeadTransform(leftEye).mul(headPose)
                .rotate(0 - Math.PI / 2, new Vector3d(1, 0, 0))
                .rotate(Math.PI / 2 - 0, new Vector3d(0, 0, 1))
                .translate(pos.mul(-1).toJOML());
    }

    public static void waitUpdatePos(Vec3d pos) {
        headPose = Vive.waitGetPoses();
        headPose2 = new Matrix4d(COORD_CHANGE).invert().mul(headPose).mul(COORD_CHANGE).invert();
        EyeCamera.pos = pos;
    }
}
