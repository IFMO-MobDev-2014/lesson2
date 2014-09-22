package ru.ifmo.md.lesson2;

import android.graphics.Bitmap;

public class BitmapEditor {
    int width;
    int height;
    int [] pixels;

    public BitmapEditor(Bitmap bitmap) {
        width = bitmap.getWidth();
        height = bitmap.getHeight();
        pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
    }



    public void rotateClockwise() {
        int newWidth  = height;
        int newHeight = width;
        int [] newPixels = new int[width * height];

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                newPixels[x * height + (height - y - 1)] = pixels[y * width + x];
            }
        }

        width = newWidth;
        height = newHeight;
        pixels = newPixels;
    }

    public void changeBrightness(int x) {
        for (int i = 0; i < pixels.length; i++) {
            int a = getA(pixels[i]);
            int r = getR(pixels[i]);
            int g = getG(pixels[i]);
            int b = getB(pixels[i]);
            r += x;
            g += x;
            b += x;
            if (r > 255) r = 255;
            if (g > 255) g = 255;
            if (b > 255) b = 255;
            if (r < 0) r = 0;
            if (g < 0) g = 0;
            if (b < 0) b = 0;
            pixels[i] = a << 24 | r << 16 | g << 8 | b;
        }
    }

    //works fine
    public Bitmap nearestNeighbor(double ratio) {
        int newWidth  = (int) ((double) width  / ratio);
        int newHeight = (int) ((double) height / ratio);
        int [] newPixels = new int[newWidth * newHeight];

        for (int x = 0; x < newWidth; x++) {
            for (int y = 0; y < newHeight; y++) {
                int current = (int) (x * ratio) + (int) (y * ratio) * width;
                newPixels[x + y * newWidth] = pixels[current];
            }
        }
        return Bitmap.createBitmap(newPixels, newWidth, newHeight, Bitmap.Config.ARGB_8888);
    }

    public Bitmap bilinearInterpolation(double ratio) {
        int newWidth  = (int) ((double) width  / ratio);
        int newHeight = (int) ((double) height / ratio);
        int [] newPixels = new int[newWidth * newHeight];

        for (int x = 0; x < newWidth; x++) {
            for (int y = 0; y < newHeight; y++) {
                double oldX = x * ratio;
                double oldY = y * ratio;
                int x1 = (int) oldX;
                int x2 = Math.min((int) (oldX + 1), width);
                int y1 = (int) oldY;
                int y2 = Math.min((int) (oldY + 1), height);
                int q11 = pixels[x1 + y1 * width];
                int q12 = pixels[x1 + y2 * width];
                int q21 = pixels[x2 + y1 * width];
                int q22 = pixels[x2 + y2 * width];

                newPixels[x + y * newWidth] = bilinearInterpolation(x1, x2, y1, y2, oldX, oldY, q11, q12, q21, q22);
            }
        }
        return Bitmap.createBitmap(newPixels, newWidth, newHeight, Bitmap.Config.ARGB_8888);
    }

    private int bilinearInterpolation(int x1, int x2, int y1, int y2, double x, double y, int q11, int q12, int q21, int q22) {

        //R1
        int r1a = (int) (((x2 - x) / (x2 - x1)) * getA(q11) + ((x - x1) / (x2 - x1)) * getA(q21));
        int r1r = (int) (((x2 - x) / (x2 - x1)) * getR(q11) + ((x - x1) / (x2 - x1)) * getR(q21));
        int r1g = (int) (((x2 - x) / (x2 - x1)) * getG(q11) + ((x - x1) / (x2 - x1)) * getG(q21));
        int r1b = (int) (((x2 - x) / (x2 - x1)) * getB(q11) + ((x - x1) / (x2 - x1)) * getB(q21));

        //R2
        int r2a = (int) (((x2 - x) / (x2 - x1)) * getA(q12) + ((x - x1) / (x2 - x1)) * getA(q22));
        int r2r = (int) (((x2 - x) / (x2 - x1)) * getR(q12) + ((x - x1) / (x2 - x1)) * getR(q22));
        int r2g = (int) (((x2 - x) / (x2 - x1)) * getG(q12) + ((x - x1) / (x2 - x1)) * getG(q22));
        int r2b = (int) (((x2 - x) / (x2 - x1)) * getB(q12) + ((x - x1) / (x2 - x1)) * getB(q22));

        int a = (int) (((y2 - y) / (y2 - y1)) * r1a + ((y - y1) / (y2 - y1)) * r2a);
        int r = (int) (((y2 - y) / (y2 - y1)) * r1r + ((y - y1) / (y2 - y1)) * r2r);
        int g = (int) (((y2 - y) / (y2 - y1)) * r1g + ((y - y1) / (y2 - y1)) * r2g);
        int b = (int) (((y2 - y) / (y2 - y1)) * r1b + ((y - y1) / (y2 - y1)) * r2b);

        return (a << 24 | r << 16 | g << 8 | b);
    }

    private int getA(int color) {
        return (color >> 24) & 0xFF;
    }

    private int getR(int color) {
        return (color >> 16) & 0xFF;
    }

    private int getG(int color) {
        return (color >> 8) & 0xFF;
    }

    private int getB(int color) {
        return color & 0xFF;
    }
}
