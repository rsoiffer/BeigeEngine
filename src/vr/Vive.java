package vr;

import static engine.Layer.POSTRENDER;
import static engine.Layer.RENDER3D;
import graphics.Camera;
import graphics.opengl.Framebuffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.joml.Matrix4d;
import org.joml.Matrix4f;
import org.joml.Matrix4x3d;
import org.lwjgl.openvr.HmdMatrix34;
import org.lwjgl.openvr.HmdMatrix44;
import org.lwjgl.openvr.OpenVR;
import org.lwjgl.openvr.Texture;
import org.lwjgl.openvr.TrackedDevicePose;
import org.lwjgl.openvr.VR;
import org.lwjgl.openvr.VRCompositor;
import org.lwjgl.openvr.VRSystem;
import org.lwjgl.openvr.VRTextureBounds;
import org.lwjgl.system.MemoryStack;
import util.math.Vec2d;
import util.math.Vec4d;
import static vr.ViveInput.LEFT;
import static vr.ViveInput.RIGHT;

public class Vive {

    public static double SCALE_FACTOR = 1;
    public static Framebuffer leftEye, rightEye;

    public static void main(String[] args) {
        init();
        System.out.println("Initialized sucessfully!");
        System.out.println("Recommended size is " + getRecommendedRenderTargetSize());
        System.out.println(getEyeProjectionMatrix(true));
        System.out.println(getEyeProjectionMatrix(false));
        System.out.println(getEyeToHeadTransform(true));
        System.out.println(getEyeToHeadTransform(false));
        for (int i = 0; i < 100; i++) {
            System.out.println("Current HMD pose:");
            System.out.println(getDeviceToAbsoluteTrackingPose());
            System.out.println("Current controller states:");
            ViveInput.update();
            System.out.println(RIGHT);
            System.out.println(LEFT);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(Vive.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        shutdown();
    }

    public static boolean canStart() {
        return VR.VR_IsRuntimeInstalled() && VR.VR_IsHmdPresent();
    }

    public static void checkError(int ecode) {
        if (ecode != 0) {
            throw new RuntimeException("VR Error (code " + ecode + ")");
        }
    }

    public static Camera eyeCamera(boolean leftEye, Matrix4d tPose) {
        return new Camera() {
            @Override
            public Matrix4d projectionMatrix() {
                return Vive.getEyeProjectionMatrix(leftEye);
            }

            @Override
            public Matrix4d viewMatrix() {
                return Vive.getEyeToHeadTransform(leftEye).mul(tPose)
                        .mul(Camera.camera3d.viewMatrix());
            }
        };
    }

    public static Matrix4d getDeviceToAbsoluteTrackingPose() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            ByteBuffer bb = stack.malloc(128); // value unknown, 128 is a safe upper bound
            TrackedDevicePose.Buffer tdp = new TrackedDevicePose.Buffer(bb);
            VRSystem.VRSystem_GetDeviceToAbsoluteTrackingPose(
                    VR.ETrackingUniverseOrigin_TrackingUniverseStanding, 0, tdp);
            return read4x3Matrix(tdp.mDeviceToAbsoluteTracking().m()).invert();
        }
    }

    public static Matrix4d getEyeProjectionMatrix(boolean leftEye) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            ByteBuffer bb = stack.malloc(64);
            VRSystem.VRSystem_GetProjectionMatrix(leftEye ? VR.EVREye_Eye_Left : VR.EVREye_Eye_Right, .2f, 2000, new HmdMatrix44(bb));
            return read4x4Matrix(bb.asFloatBuffer());
        }
    }

    public static Matrix4d getEyeToHeadTransform(boolean leftEye) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            ByteBuffer bb = stack.malloc(48);
            VRSystem.VRSystem_GetEyeToHeadTransform(leftEye ? VR.EVREye_Eye_Left : VR.EVREye_Eye_Right, new HmdMatrix34(bb));
            return read4x3Matrix(bb.asFloatBuffer()).invert();
        }
    }

    public static Vec2d getRecommendedRenderTargetSize() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            VRSystem.VRSystem_GetRecommendedRenderTargetSize(w, h);
            return new Vec2d(w.get(), h.get());
        }
    }

    public static void init() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer peError = stack.mallocInt(1);
            int token = VR.VR_InitInternal(peError, VR.EVRApplicationType_VRApplication_Scene);
            checkError(peError.get());

            OpenVR.create(token);
        }
        ViveInput.init();
    }

    public static void initRender(Vec4d clearColor) {
        Vec2d texSize = Vive.getRecommendedRenderTargetSize();
        leftEye = new Framebuffer((int) texSize.x, (int) texSize.y).attachColorBuffer().attachDepthStencilBuffer();
        rightEye = new Framebuffer((int) texSize.x, (int) texSize.y).attachColorBuffer().attachDepthStencilBuffer();

        POSTRENDER.onStep(() -> {
            Matrix4d tPose = Vive.waitGetPoses();
            Camera.camera3d.horAngle = 0;
            Camera.camera3d.vertAngle = 0;

            leftEye.clear(clearColor);
            Camera.current = Vive.eyeCamera(true, tPose);
            RENDER3D.stepAll();
            submit(true, leftEye.colorBuffer);

            rightEye.clear(clearColor);
            Camera.current = Vive.eyeCamera(false, tPose);
            RENDER3D.stepAll();
            submit(false, rightEye.colorBuffer);

            Camera.current = Camera.camera3d;
        });
    }

    static Matrix4d read4x4Matrix(FloatBuffer b) {
        return new Matrix4d(new Matrix4f(b)).transpose();
    }

    static Matrix4d read4x3Matrix(FloatBuffer b) {
        return new Matrix4d(new Matrix4x3d(
                b.get(0), b.get(4), b.get(8),
                b.get(1), b.get(5), b.get(9),
                b.get(2), b.get(6), b.get(10),
                b.get(3) * SCALE_FACTOR, b.get(7) * SCALE_FACTOR, b.get(11) * SCALE_FACTOR
        ));
    }

    public static void resetSeatedZeroPose() {
        VRSystem.VRSystem_ResetSeatedZeroPose();
    }

    public static void shutdown() {
        VR.VR_ShutdownInternal();
    }

    public static void submit(boolean leftEye, graphics.opengl.Texture t) {
        Texture pTexture = Texture.create();
        pTexture.handle(t.id);
        pTexture.eType(VR.ETextureType_TextureType_OpenGL);
        pTexture.eColorSpace(VR.EColorSpace_ColorSpace_Auto);
        VRTextureBounds pBounds = null;
        int ecode = VRCompositor.VRCompositor_Submit(leftEye ? VR.EVREye_Eye_Left : VR.EVREye_Eye_Right,
                pTexture, pBounds, 0);
        checkError(ecode);
    }

    public static Matrix4d waitGetPoses() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            ByteBuffer bb1 = stack.malloc(128); // value unknown, 128 is a safe upper bound
            TrackedDevicePose.Buffer tdp1 = new TrackedDevicePose.Buffer(bb1);
            ByteBuffer bb2 = stack.malloc(128); // value unknown, 128 is a safe upper bound
            TrackedDevicePose.Buffer tdp2 = new TrackedDevicePose.Buffer(bb2);
            int ecode = VRCompositor.VRCompositor_WaitGetPoses(tdp1, tdp2);
            checkError(ecode);
            return read4x3Matrix(tdp1.mDeviceToAbsoluteTracking().m()).invert();
        }
    }
}
