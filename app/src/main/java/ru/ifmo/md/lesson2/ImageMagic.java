package ru.ifmo.md.lesson2;

import android.graphics.Bitmap;
import android.graphics.Color;


/**
 * Created by siziyman on 24.09.2014.
 */
public class ImageMagic {
    int width;
    int height;
    int[] pixels;

    public ImageMagic(Bitmap bmp) {
        this.width = bmp.getWidth();
        this.height = bmp.getHeight();
        pixels = new int[width * height];
        bmp.getPixels(pixels, 0, width, 0, 0, width, height);


    }

    protected void nearestNeighborInterpolation(double scale) { //fast&simple method, but very "ugly" results

        int scaledHeight = (int) Math.ceil((double) width / scale);
        int scaledWidth = (int) Math.ceil((double) width / scale);
        int[] newPixels = new int[scaledHeight * scaledWidth];

        for (int x = 0; x < scaledWidth; x++) {
            for (int y = 0; y < scaledHeight; y++) {

                newPixels[x + y * scaledWidth] = pixels[(int) (x * scale) + (int) (y * scale) * width];

            }
        }

        pixels = newPixels;
        width = scaledWidth;
        height = scaledHeight;
    }

    protected void bilinearInterpolation(double scale) {//slower, but better quality
        int scaledHeight = (int) Math.ceil((double) width / scale);
        int scaledWidth = (int) Math.ceil((double) width / scale);
        int[] newPixels = new int[scaledHeight * scaledWidth];
        int a,
                b,
                c,
                d,
                x,
                y,
                index,
                alpha,
                blue,
                red,
                green;
        double xDiff,
                yDiff;

        int offset = 0;
        for (int i = 0; i < scaledHeight; i++) {
            for (int j = 0; j < scaledWidth; j++) {
                x = (int) (scale * j);
                y = (int) (scale * i);
                xDiff = (scale * j) - x;
                yDiff = (scale * i) - y;
                index = (y * width + x);
                a = pixels[index];
                b = pixels[index + 1];
                c = pixels[index + width];
                d = pixels[index + width + 1];

                blue = (int) Math.round((a & 0xff) * (1 - xDiff) * (1 - yDiff) + (b & 0xff) * xDiff * (1 - yDiff) +
                        (c & 0xff) * yDiff * (1 - xDiff) + (d & 0xff) * (xDiff * yDiff));

                green = (int) Math.round(((a >> 8) & 0xff) * (1 - xDiff) * (1 - yDiff) + ((b >> 8) & 0xff) * xDiff *
                        (1 - yDiff) + ((c >> 8) & 0xff) * yDiff * (1 - xDiff) + ((d >> 8) & 0xff) *
                        (xDiff * yDiff));

                red = (int) Math.round(((a >> 16) & 0xff) * (1 - xDiff) * (1 - yDiff) + ((b >> 16) & 0xff) * xDiff *
                        (1 - yDiff) + ((c >> 16) & 0xff) * yDiff * (1 - xDiff) + ((d >> 16) & 0xff) *
                        (xDiff * yDiff));
                alpha = (int) Math.round(((a >> 24) & 0xff) * (1 - xDiff) * (1 - yDiff) + ((b >> 24) & 0xff) * xDiff *
                        (1 - yDiff) + ((c >> 24) & 0xff) * yDiff * (1 - xDiff) + ((d >> 24) & 0xff) *
                        (xDiff * yDiff));

                newPixels[offset++] = Color.argb(alpha, red, green, blue);
            }
        }
        pixels = newPixels;
        width = scaledWidth;
        height = scaledHeight;
    }

    protected Bitmap rotate() {
        int[] newPixels = new int[width * height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {

                newPixels[x * height + (height - y - 1)] = pixels[x + y * width];

            }
        }
        pixels = newPixels;
        swap(width, height);
        return Bitmap.createBitmap(pixels, width, height, Bitmap.Config.ARGB_8888);
    }

    protected void increaseBrightness(int margin) {
        for (int i = 0; i < pixels.length; i++) {

            int red = getR(pixels[i]);
            red = red + margin;

            if (red > 255) {
                red = 255;
            }

            int green = getG(pixels[i]);
            green = green + margin;

            if (green > 255) {
                green = 255;
            }


            int blue = getB(pixels[i]);
            blue = blue + margin;

            if (blue > 255) {
                blue = 255;
            }

            pixels[i] = recreatePixel(getA(pixels[i]), red, green, blue);
        }
    }

    private int getA(int pixel) {
        return ((pixel >> 24) & 255);
    }

    private int getR(int pixel) {
        return ((pixel >> 16) & 255);
    }

    private int getG(int pixel) {
        return ((pixel >> 8) & 255);
    }

    private int getB(int pixel) {
        return (pixel & 255);
    }

    private int recreatePixel(int A, int R, int G, int B) {
        return ((A << 24) + (R << 16) + (G << 8) + B);
    }

    protected Bitmap createBitmap() {
        return Bitmap.createBitmap(pixels, width, height, Bitmap.Config.ARGB_8888);
    }

    private void swap(int a, int b) {
        int c = a;
        a = b;
        b = c;
    }
}