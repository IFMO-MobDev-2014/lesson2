package ru.ifmo.md.lesson2;

public class BicubicInterpolator {
    private static float[] a = new float[4];

    private static float get(float[] p, float x) {
        return p[1] + 0.5f *
                x * (p[2] - p[0] +
                x * (2.0f * p[0] - 5.0f * p[1] + 4.0f * p[2] - p[3] +
                        x * (3.0f * (p[1] - p[2]) + p[3] - p[0])));
    }

    public static float getValue(float[][] p, float x, float y) {
        a[0] = get(p[0], y);
        a[1] = get(p[1], y);
        a[2] = get(p[2], y);
        a[3] = get(p[3], y);
        return get(a, x);
    }
}
