package ru.ifmo.md.lesson2;

import android.graphics.Bitmap;

/**
 * Created by anton on 20/09/14.
 */
public class RotateHelper {
    public long scaleTime;
    public long rotateTime;
    public long brightenTime;
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
        long startTime = System.currentTimeMillis();

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

        long endTime = System.currentTimeMillis();
        rotateTime = endTime - startTime;
    }

    // nearest neighbor for fast mode
    // superscaling for best mode
    public void scale(float scaleX, float scaleY) {
        long startTime = System.currentTimeMillis();

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
            int newSize = newW * newH;
            int[] cnt = new int[newSize];
            int[] red = new int[newSize];
            int[] green = new int[newSize];
            int[] blue = new int[newSize];

            for (int i = 0; i < newSize; i++) {
                cnt[i] = red[i] = green[i] = blue[i] = 0;
            }

            for (int y = 0; y < height; y++) {
                int newY = y * newH / height;
                int newYW = newY * newW;
                int yw = y * width;
                for (int x = 0; x < width; x++) {
                    int newX = x * newW / width;
                    int color = pixels[yw + x];
                    int index = newYW + newX;
                    int r = (color >> 16) & 0xFF;
                    int g = (color >> 8) & 0xFF;
                    int b = color & 0xFF;

                    red[index] += r;
                    green[index] += g;
                    blue[index] += b;
                    cnt[index]++;
                }
            }

            for (int i = 0; i < newSize; i++) {
                int count = cnt[i];
                newPixels[i] = 0xFF000000 | (red[i] / count) << 16 | (green[i] / count) << 8 | (blue[i] / count);
            }
        }

        width = newW;
        height = newH;
        pixels = newPixels;

        long endTime = System.currentTimeMillis();
        scaleTime = endTime - startTime;
    }

    public void brighten(float scale) {
        long startTime = System.currentTimeMillis();

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

        long endTime = System.currentTimeMillis();
        brightenTime = endTime - startTime;
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