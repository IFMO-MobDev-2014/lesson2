package ru.ifmo.md.lesson2;

import android.graphics.Bitmap;

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

    // nearest neighbor for fast mode
    // superscaling for best mode
    public void scale(float scaleX, float scaleY) {
        int newW = (int) (width / scaleX);
        int newH = (int) (height / scaleY);
        int[] newPixels = new int[newW * newH];

        if (fastScaleMode) {
            for (int y = 0; y < newH; y++) {
                int yw = y * newW;
                for (int x = 0; x < newW; x++) {
                    newPixels[yw + x] = pixels[y * width / newW * width + x * height / newH];
                }
            }
        } else {
            for (int y = 0; y < newH; y++) {
                int yw = y * newW;
                for (int x = 0; x < newW; x++) {
                    int x1 = (int) (x * scaleX);
                    int y1 = (int) (y * scaleY);
                    int x2 = (int) ((x + 1) * scaleX - 1e-6);
                    int y2 = (int) ((y + 1) * scaleY - 1e-6);
                    int cnt = (y2 - y1 + 1) * (x2 - x1 + 1);
                    int red = 0;
                    int green = 0;
                    int blue = 0;
                    for (int yy = y1; yy <= y2; yy++) {
                        for (int xx = x1; xx <= x2; xx++) {
                            int color = pixels[yy * width + xx];
                            red += (color >> 16) & 0xFF;
                            green += (color >> 8) & 0xFF;
                            blue += color & 0xFF;
                        }
                    }
                    newPixels[yw + x] = 0xFF000000 | (red / cnt) << 16 | (green / cnt) << 8 | (blue / cnt);
                }
            }
        }

        width = newW;
        height = newH;
        pixels = newPixels;
    }

    public void brighten(float scale) {
        // build brightening transform table
        int[] transformTable = new int[256];
        double coeff = Math.pow(255, (scale - 1) / scale);
        for (int i = 0; i < 256; i++) {
            transformTable[i] = (int) (Math.pow(i, 1.0 / scale) * coeff);
        }

        int wh = width * height;
        for (int i = 0; i < wh; i++) {
            int color = pixels[i];
            int red = transformTable[(color >> 16) & 0xFF];
            int green = transformTable[(color >> 8) & 0xFF];
            int blue = transformTable[color & 0xFF];
            pixels[i] = 0xFF000000 | red << 16 | green << 8 | blue;
        }
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