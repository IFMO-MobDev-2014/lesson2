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
        increaseBrightness(30);
        return Bitmap.createBitmap(colors, 0, width, width, height, Bitmap.Config.ARGB_8888);
    }

    private static void fastCompress() {

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
        int cnt = width - newWidth;
        {
            TreeSet<Pair> set = new TreeSet<Pair>();
            for (int y = 0; y < height; y++) {
                set.clear();
                for (int x = 0; x < width - 1; x++) {
                    set.add(new Pair(getLength(colors[y * width + x + 1], colors[y * width + x]), x));
                    if (set.size() > cnt)
                        set.remove(set.last());
                }
                Pair tmp = new Pair(0, 0);
                for (int x = 0, point = 0; x < width - 1; x++) {
                    tmp.first = getLength(colors[y * width + x + 1], colors[y * width + x]);
                    tmp.second = x;
                    if (!set.contains(tmp)) {
                        colors[y * width + point] = colors[y * width + x];
                        point++;
                    }
                }
            }
        }
        cnt = height - newHeight;
        {
            TreeSet<Pair> set = new TreeSet<Pair>();
            for (int x = 0; x < width; x++) {
                set.clear();
                for (int y = 0; y < height - 1; y++) {
                    set.add(new Pair(getLength(colors[(y+1) * width + x], colors[y * width + x]), y));
                    if (set.size() > cnt)
                        set.remove(set.last());
                }
                Pair tmp = new Pair(0, 0);
                for (int y = 0, point = 0; y < height - 1; y++) {
                    tmp.first = getLength(colors[(y+1) * width + x], colors[y * width + x]);
                    tmp.second = y;
                    if (!set.contains(tmp)) {
                        colors[point * width + x] = colors[y * width + x];
                        point++;
                    }
                }
            }
        }
        int[] newColors = new int[newHeight * newWidth];
        for (int x = 0; x < newWidth; x++) {
            for (int y = 0; y < newHeight; y++) {
                newColors[y * newWidth + x] = colors[y * width + x];
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
