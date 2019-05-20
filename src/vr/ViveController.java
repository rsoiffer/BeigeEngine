package vr;

import java.util.LinkedList;
import java.util.List;
import org.joml.Matrix4d;
import org.joml.Vector3d;
import org.lwjgl.openvr.TrackedDevicePose;
import org.lwjgl.openvr.VR;
import org.lwjgl.openvr.VRControllerState;
import org.lwjgl.openvr.VRSystem;
import util.math.Transformation;
import util.math.Vec2d;
import static vr.OpenVRUtils.read4x3Matrix;
import static vr.Vive.vrCoords;

public class ViveController {

    private final int id;
    private final List<Vec2d> axes = new LinkedList();
    private final List<Integer> buttons = new LinkedList();
    private final List<Vec2d> prevAxes = new LinkedList();
    private final List<Integer> prevButtons = new LinkedList();
    private Transformation poseRaw = null;

    ViveController(int id) {
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

    public String getPropertyString(int prop) {
        return VRSystem.VRSystem_GetStringTrackedDeviceProperty(id, prop, null);
    }

    public void hapticPulse(double durationMillis) {
        VRSystem.VRSystem_TriggerHapticPulse(id, 0, (short) (1e3 * durationMillis));
    }

    public Transformation pose() {
        return vrCoords().mul(poseRaw);
    }

    public Transformation poseRaw() {
        return poseRaw;
    }

    @Override
    public String toString() {
        return "ViveController{" + "id=" + id + ", axes=" + axes + ", buttons=" + buttons + ", prevAxes=" + prevAxes + ", prevButtons=" + prevButtons + ", poseRaw=" + poseRaw + '}';
    }

    public Vec2d trackpad() {
        return axes.get(0);
    }

    public double trigger() {
        return axes.get(1).x;
    }

    void update() {
        prevAxes.clear();
        prevAxes.addAll(axes);
        prevButtons.clear();
        prevButtons.addAll(buttons);

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
        Matrix4d m = read4x3Matrix(tdp.mDeviceToAbsoluteTracking().m());
        m.rotate(Math.PI / 2, new Vector3d(0, 0, 1));
        m.rotate(Math.PI / 2, new Vector3d(0, 1, 0));
        poseRaw = new Transformation(m);
    }
}
