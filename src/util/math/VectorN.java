/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util.math;

import java.util.Arrays;
import java.util.Iterator;
import java.util.stream.DoubleStream;

/**
 * An immutable linear algebra vector. Has methods which facilitate with general
 * linear algebra as well as with graphics.
 *
 * @author Kosmic
 */
public class VectorN implements Iterable<Double> {

    /**
     * Doubles nearer to each other than EPSILON are considered the same.
     */
    public static final double EPSILON = 1E-20;

    /**
     * The dimension of the vector.
     */
    public final int dim;

    private final double[] vec;

    private VectorN(int dim) {
        this.dim = dim;
        this.vec = new double[dim];
    }

    /**
     * Adds two vectors together.
     *
     * @param v The other vector.
     * @return A new vector where each index is the sum of the same indeces of
     * the inputs.
     */
    public VectorN add(VectorN v) {
        assertDimMatches(v);
        VectorN nv = new VectorN(dim);
        for (int i = 0; i < dim; i++) {
            nv.vec[i] = vec[i] + v.vec[i];
        }
        return nv;
    }

    /**
     * Asserts that the dimension of the vector is dim.
     *
     * @param dim The dimension that the vector must be equal to.
     */
    public void assertDimEquals(int dim) {
        if (dim < 0) {
            throw new IllegalArgumentException("Dimension cannot be smaller than 0");
        }
        if (this.dim != dim) {
            throw new IllegalArgumentException("Dimensions don't match: " + this.dim + " != " + dim);
        }
    }

    /**
     * Throws an exception if the other vector is not the same dimension.
     *
     * @param v The other vector.
     */
    private void assertDimMatches(VectorN v) {
        if (dim != v.dim) {
            throw new IllegalArgumentException("Dimensions don't match: " + dim + " != " + v.dim);
        }
    }

    /**
     * Creates a vector with a one in index index and zeros everywhere else.
     *
     * @param dim The dimension of the vector.
     * @param index The index which contains a one.
     * @return A dim dimensional vector.
     */
    public static VectorN basisVector(int dim, int index) {
        if (index < 0 || index >= dim) {
            throw new IllegalArgumentException("Index out of bounds: " + index);
        }
        VectorN nv = new VectorN(dim);
        nv.vec[index] = 1;
        return nv;
    }

    /**
     * Returns the crossproduct between two 3D vectors.
     *
     * @param v The other vector, which is to the right in this cross v.
     * @return The 3D crossproduct vector.
     */
    public VectorN cross(VectorN v) {
        assertDimEquals(3);
        v.assertDimEquals(3);
        return VectorN.of(
                vec[1] * v.vec[2] - vec[2] * v.vec[1],
                vec[2] * v.vec[0] - vec[0] * v.vec[2],
                vec[0] * v.vec[1] - vec[1] * v.vec[0]
        );
    }

    /**
     * Divides the vector by a scalar.
     *
     * @param scalar The scalar.
     * @return A new scaled vector.
     */
    public VectorN div(double scalar) {
        VectorN nv = new VectorN(dim);
        for (int i = 0; i < dim; i++) {
            nv.vec[i] = vec[i] / scalar;
        }
        return nv;
    }

    /**
     * Returns the dot product between the two vectors.
     *
     * @param v The other vector.
     * @return The dot product.
     */
    public double dot(VectorN v) {
        assertDimMatches(v);
        double sum = 0;
        for (int i = 0; i < dim; i++) {
            sum += vec[i] * v.vec[i];
        }
        return sum;
    }

    @Override
    public boolean equals(Object v) {
        if (!(v instanceof VectorN)) {
            return false;
        }
        VectorN w = (VectorN) v;
        if (dim != w.dim) {
            return false;
        }
        for (int i = 0; i < dim; i++) {
            if (Math.abs(vec[i] - w.vec[i]) >= EPSILON) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns the vector given by taking the floor of each value.
     *
     * @return A new vector of integers.
     */
    public IntVectorN floor() {
        return IntVectorN.fromStream(stream().mapToInt(MathUtils::floor));
    }

    /**
     * Constructs a new vector from a stream of doubles.
     *
     * @param s The stream of doubles.
     * @return The new vector.
     */
    public static VectorN fromStream(DoubleStream s) {
        return of(s.toArray());
    }

    /**
     * Returns the value at the given index of the vector.
     *
     * @param index The index to poll.
     * @return The value at the given index.
     */
    public double get(int index) {
        if (index < 0 || index >= dim) {
            throw new IllegalArgumentException("Index out of bounds: " + index);
        }
        return vec[index];
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + Arrays.hashCode(round(EPSILON).vec);
        return hash;
    }

    @Override
    public Iterator<Double> iterator() {
        return stream().iterator();
    }

    /**
     * Returns the length of the vector.
     *
     * @return The vector's length.
     */
    public double length() {
        return Math.sqrt(lengthSquared());
    }

    /**
     * Returns the square of the length of the vector.
     *
     * @return The vector's length squared.
     */
    public double lengthSquared() {
        return dot(this);
    }

    /**
     * Multiplies the vector by a scalar.
     *
     * @param scalar The scalar.
     * @return A new scaled vector.
     */
    public VectorN mult(double scalar) {
        VectorN nv = new VectorN(dim);
        for (int i = 0; i < dim; i++) {
            nv.vec[i] = vec[i] * scalar;
        }
        return nv;
    }

    /**
     * Returns a new vector with magnitude 1 which faces in the same direction.
     *
     * @return A new normalized vector.
     */
    public VectorN normalize() {
        double length = length();
        if (length < EPSILON) {
            throw new RuntimeException("Trying to normalize a vector of length 0");
        }
        return mult(1 / length);
    }

    public static VectorN of(double... vec) {
        VectorN v = new VectorN(vec.length);
        System.arraycopy(vec, 0, v.vec, 0, vec.length);
        return v;
    }

    /**
     * Creates a vector with ones in every index.
     *
     * @param dim The dimension of the vector.
     * @return A d dimensional vector.
     */
    public static VectorN ones(int dim) {
        VectorN v = new VectorN(dim);
        for (int i = 0; i < dim; i++) {
            v.vec[i] = 1;
        }
        return v;
    }

    /**
     * Returns the vector given by rounding each value to the nearest integer.
     *
     * @return A new vector of integers.
     */
    public IntVectorN round() {
        return IntVectorN.fromStream(stream().mapToInt(MathUtils::round));
    }

    /**
     * Returns the vector given by rounding each value to the nearest multiple
     * of mod.
     *
     * @param mod The value to round to multiples of.
     * @return The rounded vector.
     */
    public VectorN round(double mod) {
        VectorN nv = new VectorN(dim);
        for (int i = 0; i < dim; i++) {
            nv.vec[i] = MathUtils.round(vec[i], mod);
        }
        return nv;
    }

    /**
     * Creates a new vector with every entry the same except for the entry at
     * index, which has the value value.
     *
     * @param index The index to change.
     * @param value The value to put into index.
     * @return A new vector with the index at index changed to value.
     */
    public VectorN set(int index, double value) {
        if (index < 0 || index >= dim) {
            throw new IllegalArgumentException("Index out of bounds: " + index);
        }
        VectorN nv = new VectorN(dim);
        for (int i = 0; i < dim; i++) {
            nv.vec[index] = i == index ? value : vec[i];
        }
        return nv;
    }

    /**
     * Returns a stream containing the values in this vector.
     *
     * @return The stream of values.
     */
    public DoubleStream stream() {
        return Arrays.stream(vec);
    }

    /**
     * Subtracts v from this vector.
     *
     * @param v The other vector.
     * @return The resultant vector from the subtraction.
     */
    public VectorN sub(VectorN v) {
        assertDimMatches(v);
        VectorN nv = new VectorN(dim);
        for (int i = 0; i < dim; i++) {
            nv.vec[i] = vec[i] - v.vec[i];
        }
        return nv;
    }

    @Override
    public String toString() {
        String r = "[";
        for (int i = 0; i < dim; i++) {
            if (i > 0) {
                r += ", ";
            }
            r += String.format("%.3f", vec[i]);
        }
        return r + "]";
    }

    /**
     * Gets the value at index three.
     *
     * @return The value at index three.
     */
    public double w() {
        return get(3);
    }

    /**
     * Gets the value at index zero.
     *
     * @return The value at index zero.
     */
    public double x() {
        return get(0);
    }

    /**
     * Gets the value at index one.
     *
     * @return The value at index one.
     */
    public double y() {
        return get(1);
    }

    /**
     * Gets the value at index two.
     *
     * @return The value at index two.
     */
    public double z() {
        return get(2);
    }

    /**
     * Creates a d dimensional zero vector.
     *
     * @param dim The dimension of the vector.
     * @return A d dimensional vector.
     */
    public static VectorN zeros(int dim) {
        return new VectorN(dim);
    }
}
