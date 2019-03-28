package vr;

import graphics.Camera;
import org.joml.Matrix4d;
import util.math.Vec3d;

public class EyeCamera extends Camera {

    private static Matrix4d tPose;
    private static Vec3d pos;

    private final boolean leftEye;

    public EyeCamera(boolean leftEye) {
        this.leftEye = leftEye;
    }

    @Override
    public Matrix4d projectionMatrix() {
        return Vive.getEyeProjectionMatrix(leftEye);
    }

    @Override
    public Matrix4d viewMatrix() {
        return Vive.getEyeToHeadTransform(leftEye).mul(tPose).translate(pos.toJOML());
    }

    public static void waitUpdatePos(Vec3d pos) {
        tPose = Vive.waitGetPoses();
        EyeCamera.pos = pos;
    }
}
