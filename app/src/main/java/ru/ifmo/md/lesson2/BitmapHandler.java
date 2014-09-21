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

    private static int fA(int x, int y) {
        return Color.alpha(colors[y * width + x]);
    }
    private static int fR(int x, int y) {
        return Color.red(colors[y * width + x]);
    }
    private static int fG(int x, int y) {
        return Color.green(colors[y * width + x]);
    }
    private static int fB(int x, int y) {
        return Color.blue(colors[y * width + x]);
    }



    private static int interpolation(double x, double y) {

        double tx = x;
        double ty = y;
        x = x - Math.floor(x);
        y = y - Math.floor(y);
        double b1 = (1.0/4)*(x-1)*(x-2)*(x+1)*(y-1)*(y-2)*(y+1);
        double b2 = -(1.0/4)*(x)*(x-2)*(x+1)*(y-1)*(y-2)*(y+1);
        double b3 = -(1.0/4)*(x - 1)*(x-2)*(x+1)*(y)*(y-2)*(y+1);
        double b4 = (1.0/4)*(x)*(x-2)*(x+1)*(y)*(y-2)*(y+1);

        double b5 = -(1.0/12)*(x)*(x-2)*(x-1)*(y-1)*(y-2)*(y+1);
        double b6 = -(1.0/12)*(x+1)*(x-2)*(x-1)*(y-1)*(y-2)*(y);
        double b7 = (1.0/12)*(x)*(x-2)*(x-1)*(y)*(y-2)*(y+1);
        double b8 = (1.0/12)*(x)*(x-2)*(x+1)*(y-1)*(y-2)*(y);

        double b9 = (1.0/12)*(x)*(x+1)*(x-1)*(y-1)*(y-2)*(y+1);
        double b10 = (1.0/12)*(x+1)*(x-2)*(x-1)*(y-1)*(y)*(y+1);
        double b11 = (1.0/36)*(x)*(x-2)*(x-1)*(y-1)*(y-2)*(y);
        double b12 = -(1.0/12)*(x)*(x+1)*(x-1)*(y)*(y-2)*(y+1);

        double b13 = -(1.0/12)*(x)*(x-2)*(x+1)*(y-1)*(y)*(y+1);
        double b14 = -(1.0/36)*(x)*(x+1)*(x-1)*(y-1)*(y-2)*(y);
        double b15 = -(1.0/36)*(x)*(x-2)*(x-1)*(y-1)*(y)*(y+1);
        double b16 = (1.0/36)*(x)*(x+1)*(x-1)*(y-1)*(y)*(y+1);

        int rx = (int)Math.round(tx);
        int ry = (int)Math.round(ty);
        int[] ax = new int[4];
        int[] ay = new int[4];
        int add = 0;
        if (rx == 0)
            add = 1;
        for (int i = Math.min(width - 1, rx + 2 + add), cnt = 0; cnt < 4; ++cnt, --i) {
            ax[4 - cnt - 1] = i;
        }
        if (ry == 0)
            add = 1;
        for (int i = Math.min(height - 1, ry + 2 + add), cnt = 0; cnt < 4; ++cnt, --i) {
            ay[4 - cnt - 1] = i;
        }
        int A =  (int)Math.round(b1*fA(ax[1], ay[1]) + b2*fA(ax[1], ay[2]) + b3*fA(ax[2], ay[1]) + b4*fA(ax[2], ay[2]) + b5*fA(ax[1], ay[0]) + b6*fA(ax[0], ay[1]) +
                b7*fA(ax[2], ay[0]) + b8*fA(ax[0], ay[2]) + b9*fA(ax[1], ay[3]) + b10*fA(ax[3], ay[1]) + b11*fA(ax[0], ay[0]) + b12*fA(ax[2], ay[3]) + b13*fA(ax[3], ay[2]) +
                b14*fA(ax[0], ay[3]) + b15*fA(ax[3], ay[0]) + b16*fA(ax[3], ay[3]));

        int R = (int)Math.round(b1*fR(ax[1], ay[1]) + b2*fR(ax[1], ay[2]) + b3*fR(ax[2], ay[1]) + b4*fR(ax[2], ay[2]) + b5*fR(ax[1], ay[0]) + b6*fR(ax[0], ay[1]) +
                b7*fR(ax[2], ay[0]) + b8*fR(ax[0], ay[2]) + b9*fR(ax[1], ay[3]) + b10*fR(ax[3], ay[1]) + b11*fR(ax[0], ay[0]) + b12*fR(ax[2], ay[3]) + b13*fR(ax[3], ay[2]) +
                b14*fR(ax[0], ay[3]) + b15*fR(ax[3], ay[0]) + b16*fR(ax[3], ay[3]));

        int G = (int)Math.round(b1*fG(ax[1], ay[1]) + b2*fG(ax[1], ay[2]) + b3*fG(ax[2], ay[1]) + b4*fG(ax[2], ay[2]) + b5*fG(ax[1], ay[0]) + b6*fG(ax[0], ay[1]) +
                b7*fG(ax[2], ay[0]) + b8*fG(ax[0], ay[2]) + b9*fG(ax[1], ay[3]) + b10*fG(ax[3], ay[1]) + b11*fG(ax[0], ay[0]) + b12*fG(ax[2], ay[3]) + b13*fG(ax[3], ay[2]) +
                b14*fG(ax[0], ay[3]) + b15*fG(ax[3], ay[0]) + b16*fG(ax[3], ay[3]));

        int B = (int)Math.round(b1*fB(ax[1], ay[1]) + b2*fB(ax[1], ay[2]) + b3*fB(ax[2], ay[1]) + b4*fB(ax[2], ay[2]) + b5*fB(ax[1], ay[0]) + b6*fB(ax[0], ay[1]) +
                b7*fB(ax[2], ay[0]) + b8*fB(ax[0], ay[2]) + b9*fB(ax[1], ay[3]) + b10*fB(ax[3], ay[1]) + b11*fB(ax[0], ay[0]) + b12*fB(ax[2], ay[3]) + b13*fB(ax[3], ay[2]) +
                b14*fB(ax[0], ay[3]) + b15*fB(ax[3], ay[0]) + b16*fB(ax[3], ay[3]));

        return Color.argb(A, R, G, B);
    }

    private static void qualityCompress() {
        int newWidth = (int)Math.round(width * 1.0 / k);
        int newHeight = (int)Math.round(height * 1.0 / k);
        int[] newColors = new int[newHeight * newWidth];
        for (int y = 0; y < newHeight; y++) {
            for (int x = 0; x < newWidth; x++) {
                newColors[y * newWidth + x] = interpolation(k * x, k * y);
            }
        }
        colors = newColors;
        width = newWidth;
        height = newHeight;
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
