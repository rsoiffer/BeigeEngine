package graphics;

import graphics.opengl.ShaderProgram;
import org.joml.FrustumIntersection;
import org.joml.Matrix4d;
import org.joml.Matrix4f;
import org.joml.Vector3d;
import util.math.Vec2d;
import util.math.Vec3d;

public abstract class Camera {

    public static final Camera2d camera2d = new Camera2d();
    public static final Camera3d camera3d = new Camera3d();

    public static Camera current = camera2d;

    public abstract Matrix4d projectionMatrix();

    public void setShaderMatrices(ShaderProgram shader, Matrix4d modelMatrix) {
        shader.setUniform("projectionMatrix", projectionMatrix());
        shader.setUniform("modelViewMatrix", worldMatrix(modelMatrix));
    }

    public abstract Matrix4d viewMatrix();

    public Matrix4d worldMatrix(Matrix4d modelMatrix) {
        return viewMatrix().mul(modelMatrix);
    }

    public static class Camera2d extends Camera {

        public Vec2d position = new Vec2d(0, 0);
        public double rotation = 0;
        public double zoom = 1;

        public Vec2d lowerLeft = new Vec2d(0, 0);
        public Vec2d upperRight = new Vec2d(1, 1);

        @Override
        public Matrix4d projectionMatrix() {
            Matrix4d projectionMatrix = new Matrix4d();
            projectionMatrix.setOrtho2D(lowerLeft.x, upperRight.x, lowerLeft.y, upperRight.y);
            return projectionMatrix;
        }

        @Override
        public Matrix4d viewMatrix() {
            return new Matrix4d()
                    .scale(zoom)
                    .rotate(rotation, 0, 0, 1)
                    .translate(new Vector3d(-position.x, -position.y, 0));
        }
    }

    public static class Camera3d extends Camera {

        public Vec3d position = new Vec3d(0, 0, 0);
        public double horAngle, vertAngle;
        public Vec3d up = new Vec3d(0, 0, 1);

        public double fov = 90;
        public double zNear = .2;
        public double zFar = 2000;

        public Vec3d facing() {
            return new Vec3d(Math.cos(vertAngle) * Math.cos(horAngle), Math.cos(vertAngle) * Math.sin(horAngle), -Math.sin(vertAngle));
        }

        @Override
        public Matrix4d projectionMatrix() {
            double aspectRatio = (double) Window.WIDTH / Window.HEIGHT;
            Matrix4d projectionMatrix = new Matrix4d();
            projectionMatrix.perspective(fov * Math.PI / 180, aspectRatio, zNear, zFar);
            return projectionMatrix;
        }

        public FrustumIntersection getViewFrustum() {
            FrustumIntersection viewFrustum = new FrustumIntersection();
            viewFrustum.set(new Matrix4f(projectionMatrix().mul(viewMatrix())));
            return viewFrustum;
        }

        @Override
        public Matrix4d viewMatrix() {
            return new Matrix4d()
                    .rotate(vertAngle - Math.PI / 2, new Vector3d(1, 0, 0))
                    .rotate(Math.PI / 2 - horAngle, new Vector3d(0, 0, 1))
                    .translate(position.toJOML().mul(-1, new Vector3d()));
            // Why am I adding/subtracting doubles from the angles? Idk, but it works.
        }
    }
}
