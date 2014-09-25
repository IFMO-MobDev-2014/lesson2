package ru.ifmo.md.lesson2;

import static java.lang.Math.sqrt;

/**
 * Created by kano_vas on 23.09.14.
 */
public class Modify implements Runnable{

    int[] a, res;
    double[] sqrt;
    int width, height;
    char cmd;
    private final int w = 434;
    private final int h = 405;

    Modify(int[] pixels, int width, int height, char c) {
        a = pixels;
        this.width = width;
        this.height = height;
        cmd = c; //e for easy
    }



    public void run() {
        sqrt = new double[256];
        for (int i = 0; i < 256; i++) {
            sqrt[i] = sqrt(i);
        }
        res = new int[w * h];
        if(cmd == 'e') {
            lowTier();
        }
        else {
            highTier();
        }
    }

    private int getColor(int x, int y, int c)
    {
        int c1 = a[x + y * width];
        if (c == 0)
            return (c1 & 255);
        if (c == 1)
            return ((c1 >> 8) & 255);
        return ((c1 >> 16));
    }

    private int mixColor(int x, int y)
    {
        int c1 = a[x + y * width];
        int b = c1 & 255;
        int g = (c1 >> 8) & 255;
        int r = (c1 >> 16) & 255;
        b = (int) (sqrt[b] * sqrt[255]);
        g = (int) (sqrt[g] * sqrt[255]);
        r = (int) (sqrt[r] * sqrt[255]);
        return (0xff000000) | b | (g << 8) | (r << 16);
    }


    private void lowTier() {
        int x1, y1;
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                x1 = (int) ((width - 1) * (float) j / (h - 1) + 0.5f);
                y1 = (int) ((height - 1) * (1.0 - (float) i / (w - 1)) + 0.5f);
                res[i + j * w] = a[x1 + y1 * width];
            }
        }

    }

    private void highTier() {
        short [] rs = new short [w * h];
        short [] gs = new short [w * h];
        short [] bs = new short [w * h];
        byte [] count = new byte [w * h];
        int x1, y1, c;
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                x1 = (int) ((w - 1) * (-(float) j / (height - 1) + 1.0f) + 0.5f);
                y1 = (int) ((h - 1) * (float) i / (width - 1) + 0.5f);
                c = x1 + y1 * w;
                rs[c] += getColor(i, j, 2);
                gs[c] += getColor(i, j, 1);
                bs[c] += getColor(i, j, 0);
                count[c]++;
            }
        }
        for (int i = 0; i < w * h; i++)
        {
            res[i] = (0xff000000 | (bs[i] / count[i]) | ((gs[i] / count[i]) << 8) | ((rs[i] / count[i]) << 16));
        }
    }
}
