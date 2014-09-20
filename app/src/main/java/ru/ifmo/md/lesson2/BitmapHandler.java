package ru.ifmo.md.lesson2;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;

/**
 * Created by Женя on 21.09.2014.
 */
public class BitmapHandler {
    private static int width;
    private static int height;
    private static int newWidth;
    private static int newHeight;

    private static int[] colors;

    public static Bitmap processBitmap(Bitmap original, boolean fastCompress, double k) {
        width = original.getWidth();
        height = original.getHeight();
        colors = new int[width * height];
        original.getPixels(colors, 0, width, 0, 0, width, height);
        newWidth = (int)Math.round(width * 1.0 / k);
        newHeight = (int)Math.round(height * 1.0 / k);
        if (fastCompress)
            compress();
        //turnAndIncreaseBrightness(30);
        return Bitmap.createBitmap(colors, 0, width, width, height, Bitmap.Config.ARGB_8888);
    }

    private static void fastCompress() {

    }

    private static int increaseBrightness(int clr, int value) {
        int A, R, G, B;
        A = Color.alpha(clr);
        B = Color.blue(clr);
        G = Color.green(clr);
        R = Color.red(clr);
        A = Math.min(255, A + value);
        B = Math.min(255, B + value);
        R = Math.min(255, R + value);
        G = Math.min(255, G + value);
        return Color.argb(A, R, G, B);
    }
    private static int sqr(int a) {
        return a * a;
    }
    private static int getLength(int a, int b) {
        int x1 = 0, y1 = 0, z1 = 0, k1 = 0;
        int x2 = 0, y2 = 0, z2 = 0, k2 = 0;
        x1 = Color.alpha(a);
        y1 = Color.red(a);
        z1 = Color.green(a);
        k1 = Color.blue(a);

        x2 = Color.alpha(b);
        y2 = Color.red(b);
        z2 = Color.green(b);
        k2 = Color.blue(b);

        return (int)Math.round(Math.sqrt(sqr(x1 - x2) + sqr(y1 - y2) + sqr(z1 - z2) + sqr(k1 - k2)));
    }

    private static void compress() {
        for (;width > newWidth || height > newHeight;) {
            for (int y = 0; width > newWidth && y < height; y++) {
                int best = -1, bestDifference = -1;
                for (int x = 1; x < width - 1; x++) {
                    int len = (getLength(colors[y * width + x - 1], colors[y * width + x])) +
                            (getLength(colors[y * width + x], colors[y * width + x + 1]));
                    if (best == -1 || len < bestDifference) {
                        best = x;
                        bestDifference = len;
                    }
                }
                for (int x = best; x < width - 1; x++)
                    colors[y * width + x] = colors[y * width + x + 1];
            }
            --width;
            for (int x = 0; height > newHeight && x < width; x++) {
                int best = -1, bestDifference = -1;
                for (int y = 1; y < height - 1; y++) {
                    int len = (getLength(colors[(y-1) * width + x], colors[y * width + x])) +
                            (getLength(colors[y * width + x], colors[(y+1)* width + x]));
                    if (best == -1 || len < bestDifference) {
                        best = y;
                        bestDifference = len;
                    }
                }
                for (int y = best; y < height - 1; y++)
                    colors[y * width + x] = colors[(y+1) * width + x];
            }
            height--;
        }
        int[] newColors = new int[newHeight * newWidth];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                newColors[y * width + x] = colors[y * width + x];
            }
        }
        colors = newColors;
    }


    private static void turnAndIncreaseBrightness(int value) {
        int[] turnColors;
        turnColors = new int[colors.length];
        for (int i = 0; i < colors.length; i++) {
            int x = i % width;
            int y = i / width;
            int nx = height - y - 1;
            int ny = x;
            turnColors[ny * height + nx] = increaseBrightness(colors[i], value);
        }
        int t = width;
        width = height;
        height = t;
        colors = turnColors;
    }


}
