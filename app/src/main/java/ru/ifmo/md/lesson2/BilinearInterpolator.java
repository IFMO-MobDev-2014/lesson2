package ru.ifmo.md.lesson2;

/**
 * Created by fotyev on 20-Sep-14.
 */
public class BilinearInterpolator {
    static public float apply(float x1, float x2, float y1, float y2, float q11, float q12, float q21, float q22, float x, float y) {
        return ((q11 * (x2 - x) * (y2 - y)) +
                (q21 * (x - x1) * (y2 - y)) +
                (q12 * (x2 - x) * (y - y1)) +
                (q22 * (x - x1) * (y - y1))) / ((x2 - x1) * (y2 - y1));
    }
}
