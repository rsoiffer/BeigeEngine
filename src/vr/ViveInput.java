package vr;

import java.util.LinkedList;
import java.util.List;
import org.joml.Matrix4d;
import org.joml.Vector4d;
import org.lwjgl.openvr.TrackedDevicePose;
import org.lwjgl.openvr.VR;
import org.lwjgl.openvr.VRControllerState;
import org.lwjgl.openvr.VRSystem;
import util.math.Vec2d;
import util.math.Vec3d;
import static vr.Vive.read4x3Matrix;

public class ViveInput {

    public static final int MENU = 1, GRIP = 2, TRACKPAD = 32, TRIGGER = 33;
    public static ViveController RIGHT, LEFT;

    private static final Matrix4d COORD_CHANGE = new Matrix4d(
            0, 0, -1, 0,
            -1, 0, 0, 0,
            0, 1, 0, 0,
            0, 0, 0, 1);

    public static void init() {
        for (int i = 0; i < 64; i++) {
            if (VRSystem.VRSystem_IsTrackedDeviceConnected(i)) {
            int deviceClass = VRSystem.VRSystem_GetTrackedDeviceClass(i);
            if (deviceClass == VR.ETrackedDeviceClass_TrackedDeviceClass_Controller) {
                ViveController vc = new ViveController(i);
                vc.update();
                if (RIGHT == null) {
                    RIGHT = vc;
                } else if (LEFT == null) {
                    LEFT = vc;
                }}
            }
        }
    }

    public static void resetRightLeft() {
        Vector4d rightPos = new Vector4d(0, 0, 0, 1).mul(RIGHT.pose());
        Vector4d leftPos = new Vector4d(0, 0, 0, 1).mul(LEFT.pose());
        if (rightPos.y > leftPos.y) {
            ViveController temp = RIGHT;
            RIGHT = LEFT;
            LEFT = temp;
        }
    }

    public static void update() {
        RIGHT.update();
        LEFT.update();
    }

    public static class ViveController {

        private final int id;
        private final List<Vec2d> axes = new LinkedList();
        private final List<Integer> buttons = new LinkedList();
        private final List<Vec2d> prevAxes = new LinkedList();
        private final List<Integer> prevButtons = new LinkedList();
        private Matrix4d pose = null;
        private Matrix4d prevPose = null;

        private ViveController(int id) {
            this.id = id;
        }

        public boolean buttonDown(int button) {
            return buttons.contains(button);
        }

        public boolean buttonJustPressed(int button) {
            return buttons.contains(button) && !prevButtons.contains(button);
        }

        public boolean buttonJustReleased(int button) {
            return !buttons.contains(button) && prevButtons.contains(button);
        }

        public Vec3d deltaPosition() {
            Vector4d position = new Vector4d(0, 0, 0, 1).mul(pose());
            Vector4d prevPosition = new Vector4d(0, 0, 0, 1).mul(prevPose());
            return new Vec3d(position.x, position.y, position.z).sub(new Vec3d(prevPosition.x, prevPosition.y, prevPosition.z));
        }

        public Vec3d forwards() {
            return transform(new Vec3d(1, 0, -.5));
        }

        public String getPropertyString(int prop) {
            return VRSystem.VRSystem_GetStringTrackedDeviceProperty(id, prop, null);
        }

        public Matrix4d pose() {
            return new Matrix4d(pose);
        }

        public Vec3d position() {
            Vector4d position = new Vector4d(0, 0, 0, 1).mul(pose());
            return new Vec3d(position.x, position.y, position.z);
        }

        public Matrix4d prevPose() {
            return new Matrix4d(prevPose);
        }

        public Vec3d sideways() {
            return transform(new Vec3d(0, 1, 0));
        }

        @Override
        public String toString() {
            return "ViveController{" + "id=" + id + ", axes=" + axes + ", buttons=" + buttons + ", prevAxes=" + prevAxes + ", prevButtons=" + prevButtons + ", pose=" + pose + '}';
        }

        public Vec2d trackpad() {
            return axes.get(0);
        }

        public Vec3d transform(Vec3d dir) {
            Vector4d v = new Vector4d(dir.x, dir.y, dir.z, 0).mul(pose());
            return new Vec3d(v.x, v.y, v.z).normalize();
        }

        public double trigger() {
            return axes.get(1).x;
        }

        private void update() {
            prevAxes.clear();
            prevAxes.addAll(axes);
            prevButtons.clear();
            prevButtons.addAll(buttons);
            prevPose = pose;

            VRControllerState vcs = VRControllerState.create();
            TrackedDevicePose tdp = TrackedDevicePose.create();
            VRSystem.VRSystem_GetControllerStateWithPose(VR.ETrackingUniverseOrigin_TrackingUniverseStanding,
                    id, vcs, vcs.sizeof(), tdp);
            axes.clear();
            vcs.rAxis().forEach(vrca -> axes.add(new Vec2d(vrca.x(), vrca.y())));
            long buttonState = vcs.ulButtonPressed();
            buttons.clear();
            for (int j = 0; j < 64; j++) {
                if (((buttonState >> j) & 1) > 0) {
                    buttons.add(j);
                }
            }
            pose = read4x3Matrix(tdp.mDeviceToAbsoluteTracking().m());
            pose = new Matrix4d(COORD_CHANGE).invert().mul(pose).mul(COORD_CHANGE);
        }
    }
}
