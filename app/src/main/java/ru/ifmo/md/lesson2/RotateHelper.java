package ru.ifmo.md.lesson2;

import android.graphics.Bitmap;
import android.graphics.Color;

/**
 * Created by anton on 20/09/14.
 */
public class RotateHelper {
    private int[] pixels;
    private int width;
    private int height;
    private boolean fastScaleMode = true;

    public RotateHelper(Bitmap source) {
        width = source.getWidth();
        height = source.getHeight();
        pixels = new int[width * height];
        source.getPixels(pixels, 0, width, 0, 0, width, height);
    }

    public void rotateCW90() {
        int[] newPixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            int yw = y * width;
            int hy = height - y - 1;
            for (int x = 0; x < width; x++) {
                int xh = x * height;
                newPixels[xh + hy] = pixels[yw + x];
            }
        }
        int tmp = width;
        width = height;
        height = tmp;
        pixels = newPixels;
    }

    public void scale(float scaleX, float scaleY) {
        if (fastScaleMode) {
            scaleFast(scaleX, scaleY);
        } else {
            scaleBest(scaleX, scaleY);
        }
    }

    public void scaleFast(float scaleX, float scaleY) {
        // should be easy to implement
    }

    public void scaleBest(float scaleX, float scaleY) {
        // should be not so easy to implement
    }

    public void brighten(float scale) {
        int wh = width * height;
        float[] hsv = new float[3];
        for (int i = 0; i < wh; i++) {
            Color.colorToHSV(pixels[i], hsv);
            hsv[2] *= scale;
            pixels[i] = Color.HSVToColor(hsv);
        }
    }

    public boolean getScaleMode() {
        return fastScaleMode;
    }

    public void setScaleMode(boolean newMode) {
        fastScaleMode = newMode;
    }

    public int[] getPixels() {
        return pixels;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}