package ru.ifmo.md.lesson2;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;

import java.util.TreeSet;

/**
 * Created by Женя on 21.09.2014.
 */
public class BitmapHandler {
    private static int width;
    private static int height;

    private static double k;
    private static int[] colors;

    public static Bitmap processBitmap(Bitmap original, boolean fastCompress, double _k) {
        width = original.getWidth();
        height = original.getHeight();
        colors = new int[width * height];
        original.getPixels(colors, 0, width, 0, 0, width, height);
        k = _k;
        if (fastCompress)
            fastCompress();
        else
            qualityCompress();
        turn();
        increaseBrightness(70);
        return Bitmap.createBitmap(colors, 0, width, width, height, Bitmap.Config.ARGB_8888);
    }

    private static void fastCompress() {
        int newWidth = (int)Math.round(width * 1.0 / k);
        int newHeight = (int)Math.round(height * 1.0 / k);
        int[] newColors = new int[newHeight * newWidth];
        for (int y = 0; y < newHeight; y++) {
            int oldY = (int)Math.round(y * k);
            if (oldY >= height)
                oldY = height - 1;
            for (int x = 0; x < newWidth; x++) {
                int oldX = (int)Math.round(x * k);
                if (oldX >= width)
                    oldX = width - 1;
                newColors[y * newWidth + x] = colors[oldY * width + oldX];
            }
        }
        colors = newColors;
        width = newWidth;
        height = newHeight;
    }

    private static int getColor(int x, int y, int k) {
        int c = y * width + x;
        if (c >= width * height || c < 0)
            return 0;
        if (k == 1)
            return Color.red(colors[c]);
        if (k == 2)
            return Color.green(colors[c]);
        if (k == 3)
            return Color.blue(colors[c]);
        if (k == 0)
            return Color.alpha(colors[c]);
        return 0;
    }
    static double intersect(double a, double b, double c, double d) {
        return Math.max(Math.min(b, d) - Math.max(a, c), 0.0);
    }

    private static void qualityCompress() {
        int newWidth = (int)Math.round(width * 1.0 / k);
        int newHeight = (int)Math.round(height * 1.0 / k);
        int[] newColors = new int[newHeight * newWidth];
        int[] c = new int[4];
        for (int i = 0; i < newHeight; i++) {
            double dy = k * i;
            int y = (int)Math.floor(k * i);
            for (int j = 0; j < newWidth; j++) {
                double dx = k * j;
                int x = (int)Math.floor(k * j);
                for (int k = 0; k < 4; k++)
                    c[k] = 0;
                for (int ii = -1; ii < 2; ii++) {
                    for (int jj = -1; jj < 2; jj++) {
                        double s = (intersect((x + ii), (x + ii) + 1, dx, dx + 1) *
                                intersect((y + jj), (y + jj) + 1, dy, dy + 1));
                        for (int col = 0; col < 4; col++) {
                            c[col] += (int)(s * getColor(x + ii, y + jj, col));
                        }
                    }
                }
                newColors[i * newWidth + j] = Color.argb(c[0], c[1], c[2],c[3]);
            }
        }
        colors = newColors;
        width = newWidth;
        height = newHeight;
    }

    private static void turn() {
        int[] turnColors;
        turnColors = new int[colors.length];
        for (int i = 0; i < colors.length; i++) {
            int x = i % width;
            int y = i / width;
            int nx = height - y - 1;
            int ny = x;
            turnColors[ny * height + nx] = colors[i];
        }
        int t = width;
        width = height;
        height = t;
        colors = turnColors;
    }
    private static void increaseBrightness(int value) {
        for (int i = 0; i < colors.length; i++) {
            int x = i % width;
            int y = i / width;
            colors[y * width + x] = increasePixelBrightness(colors[i], value);
        }
    }
    private static int increasePixelBrightness(int clr, int value) {
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




}
