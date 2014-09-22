package ru.ifmo.md.lesson2;

import android.graphics.Bitmap;

/**
 * Created by default on 23.09.14.
 */
public class BitmapProcessor {
    int width;
    int height;
    int [] pixelArray;

    public BitmapProcessor(Bitmap bitmap) {
        this.width = bitmap.getWidth();
        this.height = bitmap.getHeight();
        pixelArray = new int [width * height];
        bitmap.getPixels(pixelArray, 0, width, 0, 0, width, height);
    }

    public void nearestNeighbourInterpolation(double scale) {
        int newWidth = (int) ((double) width / scale);
        int newHeight = (int) ((double) height / scale);

        int [] newPixelArray = new int [newWidth * newHeight];

        for (int i = 0; i < newWidth; i++) {
            for (int j = 0; j < newHeight; j++) {
                int pos = (int) (i * scale) + (int) (j * scale) * width;
                newPixelArray[i + j * newWidth] = pixelArray[pos];
            }
        }

        pixelArray = newPixelArray;
        width = newWidth;
        height = newHeight;
    }

    public void bilinearInterpolation(double scale) {
        int newWidth = (int) ((double) width / scale);
        int newHeight = (int) ((double) height / scale);

        int [] newPixelArray = new int [newWidth * newHeight];

        for (int i = 0; i < newWidth; i++) {
            for (int j = 0; j < newHeight; j++) {

                int x1 = (int) (i * scale);
                int x2 = Math.min((int) (i * scale + 1), newWidth - 1);
                int y1 = (int) (j * scale);
                int y2 = Math.min((int) (j * scale + 1), newHeight - 1);

                int q11 = pixelArray[x1 + y1 * width];
                int q12 = pixelArray[x1 + y2 * width];
                int q21 = pixelArray[x2 + y1 * width];
                int q22 = pixelArray[x2 + y2 * width];

                newPixelArray[i + j * newWidth] = interpolate(x1, x2, y1, y2, i * scale, j * scale, q11, q12, q21, q22);
            }
        }

        pixelArray = newPixelArray;
        width = newWidth;
        height = newHeight;

    }

    private int interpolate(int x1, int x2, int y1, int y2, double x, double y, int q11, int q12, int q21, int q22) {

        int alpha1 = (int) (((x2 - x) / (x2 - x1)) * getAlpha(q11) + ((x - x1) / (x2 - x1)) * getAlpha(q21));
        int red1 = (int) (((x2 - x) / (x2 - x1)) * getRed(q11) + ((x - x1) / (x2 - x1)) * getRed(q21));
        int green1 = (int) (((x2 - x) / (x2 - x1)) * getGreen(q11) + ((x - x1) / (x2 - x1)) * getGreen(q21));
        int blue1 = (int) (((x2 - x) / (x2 - x1)) * getBlue(q11) + ((x - x1) / (x2 - x1)) * getBlue(q21));

        int alpha2 = (int) (((x2 - x) / (x2 - x1)) * getAlpha(q12) + ((x - x1) / (x2 - x1)) * getAlpha(q22));
        int red2 = (int) (((x2 - x) / (x2 - x1)) * getRed(q12) + ((x - x1) / (x2 - x1)) * getRed(q22));
        int green2 = (int) (((x2 - x) / (x2 - x1)) * getGreen(q12) + ((x - x1) / (x2 - x1)) * getGreen(q22));
        int blue2 = (int) (((x2 - x) / (x2 - x1)) * getBlue(q12) + ((x - x1) / (x2 - x1)) * getBlue(q22));

        int alpha = (int) (((y2 - y) / (y2 - y1)) * alpha1 + ((y - y1) / (y2 - y1)) * alpha2);
        int red = (int) (((y2 - y) / (y2 - y1)) * red1 + ((y - y1) / (y2 - y1)) * red2);
        int green = (int) (((y2 - y) / (y2 - y1)) * green1 + ((y - y1) / (y2 - y1)) * green2);
        int blue = (int) (((y2 - y) / (y2 - y1)) * blue1 + ((y - y1) / (y2 - y1)) * blue2);

        return (alpha << 24) | (red << 16) | (green << 8) | (blue);
    }

    public Bitmap rotate() {
        int [] newPixelArray = new int[width * height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                newPixelArray[i * height + (height - j - 1)] = pixelArray[j * width + i];
            }
        }
        pixelArray = newPixelArray;
        int aux = width;
        width = height;
        height = aux;

        return Bitmap.createBitmap(pixelArray, width, height, Bitmap.Config.ARGB_8888);
    }

    public void increaseBrightness(int value) {
        for (int i = 0; i < pixelArray.length; i++) {
            int pixel = pixelArray[i];

            int alpha = getAlpha(pixel) + value;
            if (alpha > 255) {
                alpha = 255;
            }
            if (alpha < 0) {
                alpha = 0;
            }

            int red = getRed(pixel) + value;
            if (red > 255) {
                red = 255;
            }
            if (red < 0) {
                red = 0;
            }

            int green = getGreen(pixel) + value;
            if (green > 255) {
                green = 255;
            }
            if (green < 0) {
                green = 0;
            }

            int blue = getBlue(pixel) + value;
            if (blue > 255) {
                blue = 255;
            }
            if (blue < 0) {
                blue = 0;
            }

            pixelArray[i] = (alpha << 24) | (red << 16) | (green << 8) | (blue);
        }
    }

    private int getAlpha(int pixel) {
        return (pixel >> 24) & 0xFF;
    }
    private int getRed(int pixel) {
        return (pixel >> 16) & 0xFF;
    }
    private int getGreen(int pixel) {
        return (pixel >> 8) & 0xFF;
    }
    private int getBlue(int pixel) {
        return (pixel) & 0xFF;
    }

    public Bitmap getBitmap() {
        return Bitmap.createBitmap(pixelArray, width, height, Bitmap.Config.ARGB_8888);
    }
}
