package ru.ifmo.md.lesson2;

import android.graphics.Color;

public class MyImage {
    private int[] pixels;
    private int width;
    private int height;

    public MyImage(int[] pixels, int width, int height) {
        this.pixels = pixels;
        this.width = width;
        this.height = height;
    }

    public MyImage turn90() {
        int[] newPixels = new int[height * width];
        int x = 0, y = 0;
        for (int curPixel = 0; curPixel < newPixels.length; curPixel++) {
            if (x == width) {
                x = 0;
                y++;
            }
            newPixels[x * height + (height - y - 1)] = this.pixels[curPixel];
            x++;
        }
        return new MyImage(newPixels, height, width);
    }

    public int[] getPixels() {
        return this.pixels;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public MyImage fastScale(int newWidth, int newHeight) {
        int[] newPixels = new int[newWidth * newHeight];
        int ScX = (width << 16) / newWidth;
        int ScY = (height << 16) / newHeight;
        for (int i = 0; i < newWidth; i++)
            for (int j = 0; j < newHeight; j++)
                newPixels[j * newWidth + i] = pixels[((j * ScY) >> 16) * width + ((i * ScX) >> 16)];
        return new MyImage(newPixels, newWidth, newHeight);
    }

    public MyImage incBrightness() {
        int[] newPixels = new int[height * width];
        int red, green, blue, alpha;
        int mid = 0;
        for (int i = 0; i < width * height; i++)
            mid += Color.red(pixels[i]) + Color.blue(pixels[i]) + Color.green(pixels[i]);
        mid /= (width * height);
        mid /= 3;
        for (int i = 0; i < height * width; i++) {
            red = Color.red(pixels[i]);
            green = Color.green(pixels[i]);
            blue = Color.blue(pixels[i]);
            alpha = Color.alpha(pixels[i]);

            red += mid;
            green += mid;
            blue += mid;
            if (red > 255) red = 255;
            if (green > 255) green = 255;
            if (blue > 255) blue = 255;

            newPixels[i] = Color.argb(alpha, red, green, blue);
        }
        return new MyImage(newPixels, width, height);
    }

    public MyImage qualityScale(int newWidth, int newHeight) {
        int[] newPixels = new int[newHeight * newWidth];
        float FactorX = (width) / newWidth;
        float FactorY = (height) / newHeight;
        int it = 0;
        for (int i = 0; i < newHeight; i++) {
            for (int j = 0; j < newWidth; j++) {
                int x = (j * ((width << 16) / newWidth)) >> 16;
                int y = (i * ((height << 16) / newHeight)) >> 16;
                float xDiff = (FactorX * j) - (int) (FactorX * j);
                float yDiff = (FactorY * i) - (int) (FactorY * i);
                int id = (y * width + x);
                int a, b, c, d;
                a = pixels[id];
                if (id + height < height * width) b = pixels[id + height]; else b = a;
                if (id + width < height * width) c = pixels[id + width]; else c = a;
                if (id + height + width < height * width) d = pixels[id + height + width]; else d = a;

                float blue = (a & 0xff) * (1 - xDiff) * (1 - yDiff) +
                       (b & 0xff) * (xDiff) * (1 - yDiff) +
                       (c & 0xff) * (yDiff) * (1 - xDiff) +
                       (d & 0xff) * (xDiff * yDiff);

                float green = ((a >> 8) & 0xff) * (1 - xDiff) * (1 - yDiff) +
                        ((b >> 8) & 0xff) * (xDiff) * (1 - yDiff) +
                        ((c >> 8) & 0xff) * (yDiff) * (1 - xDiff) +
                        ((d >> 8) & 0xff) * (xDiff * yDiff);

                float red = ((a >> 16) & 0xff) * (1 - xDiff) * (1 - yDiff) +
                      ((b >> 16) & 0xff) * (xDiff) * (1 - yDiff) +
                      ((c >> 16) & 0xff) * (yDiff) * (1 - xDiff) +
                      ((d >> 16) & 0xff) * (xDiff * yDiff);

                newPixels[it++] = 0xff000000 | (((int) red) << 16) & 0xff0000 | (((int) green) << 8) & 0xff00 | (int) blue;
            }
        }
        return new MyImage(newPixels, newWidth, newHeight);
    }

}
