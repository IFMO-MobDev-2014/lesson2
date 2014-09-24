package ru.ifmo.md.lesson2;

import android.graphics.Bitmap;
import android.graphics.Color;

/**
 * Created by dimatomp on 23.09.14.
 */
public abstract class ImageLoader {
    public static final int WIDTH = 434;
    public static final int HEIGHT = 405;

    int maxZero(int a) {
        return a & ~-(a >>> 31);
    }

    int minFF(int a) {
        return (a | -(a >> 8)) & 0xff;
    }

    protected int convertColor(int r, int g, int b) {
        // Based on ICT used in JPEG; multiplies Cb and Cr by 2
        return Color.rgb(minFF(maxZero((r * 45 - g * 17 - b * 3) / 25)),
                minFF(maxZero((r * -30 + g * 141 - b * 11) / 100)),
                minFF(maxZero((r * -27 - g * 58 + b * 185) / 100)));
    }

    public abstract Bitmap transformBitmap(Bitmap from);
}
