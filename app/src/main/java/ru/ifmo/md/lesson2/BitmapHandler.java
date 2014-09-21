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
        //turn();
        increaseBrightness(50);
        return Bitmap.createBitmap(colors, 0, width, width, height, Bitmap.Config.ARGB_8888);
    }

    private static void fastCompress() {
        int newWidth = (int)Math.round(width * 1.0 / k);
        int newHeight = (int)Math.round(height * 1.0 / k);
        int[] newColors = new int[newHeight * newWidth];
        for (int y = 0; y < newHeight; y++) {
            for (int x = 0; x < newWidth; x++) {
                int oldX = (int)Math.round(x * k);
                int oldY = (int)Math.round(y * k);
                newColors[y * newWidth + x] = colors[oldY * width + oldX];
            }
        }
        colors = newColors;
        width = newWidth;
        height = newHeight;
    }

    private static int getColor(int c, int k) {
        if (k == 1)
            return Color.red(c);
        if (k == 2)
            return Color.green(c);
        if (k == 3)
            return Color.blue(c);
        if (k == 0)
            return Color.alpha(c);
        return 0;
    }
    public static double getValue(double[] p, double x) {
        return p[1] + 0.5 * x*(p[2] - p[0] + x*(2.0*p[0] - 5.0*p[1] + 4.0*p[2] - p[3] + x*(3.0*(p[1] - p[2]) + p[3] - p[0])));
    }
    static double[] arr;
    public static double getValue(double[][] p, double x, double y) {
        if (arr == null)
            arr = new double[4];
        arr[0] = getValue(p[0], y);
        arr[1] = getValue(p[1], y);
        arr[2] = getValue(p[2], y);
        arr[3] = getValue(p[3], y);
        return getValue(arr, x);
    }

    private static void qualityCompress() {
        int newWidth = (int)Math.round(width * 1.0 / k);
        int newHeight = (int)Math.round(height * 1.0 / k);
        int[] newColors = new int[newHeight * newWidth];
        double[][] p = new double[4][4];
        int[] c = new int[4];
        for (int i = 0; i < newHeight; i++) {
            for (int j = 0; j < newWidth; j++) {
                int y = (int)(k * i);
                int x = (int)(k * j);
                double dx = k * j - x;
                double dy = k * i - y;
                for (int k = 0; k < 4; k++) {
                    for (int ii = -1; ii < 3; ii++)
                        for (int jj = -1; jj < 3; jj++) {
                            int t = x + ii + (y + jj) * width;
                            if (t > 0 && t < width * height)
                                p[ii + 1][jj + 1] = getColor(colors[t], k);
                            else
                                p[ii + 1][jj + 1] = 0;
                        }
                    c[k] = (int)getValue(p, dx/10, dy/10);
                }
                newColors[i * newWidth + j] = Color.argb(c[0], c[1], c[2], c[3]);
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
