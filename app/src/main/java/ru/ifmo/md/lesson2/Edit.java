package ru.ifmo.md.lesson2;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.wifi.p2p.WifiP2pManager;

/**
 * Created by daria on 23.09.14.
 */
public class Edit {
    Bitmap bitmap, bitmap2;
    int width, height;
    int[] pixels;
    double scale = 1.73;

    Edit(Bitmap bmp) {
        width = bmp.getWidth();
        height = bmp.getHeight();
        pixels = new int[width*height];
        bmp.getPixels(pixels, 0, width, 0, 0, width, height);
        bitmap = Bitmap.createBitmap(bmp);
        bitmap2 = Bitmap.createBitmap(height, width, Bitmap.Config.ARGB_8888);
    }


    public void turnRight() {
        for(int x = 0; x < width; x++) {
            for(int y = 0; y < height; y++) {
                //System.err.println(x + " " + y);
                bitmap2.setPixel(height - y - 1, x, bitmap.getPixel(x, y));
            }
        }
        int k = width;
        width = height;
        height = k;

        bitmap = Bitmap.createBitmap(bitmap2);
    }

    public void increaseBrightness() {
        for(int i = 0; i < width * height; i++) {
            int red = Color.red(pixels[i]);
            int green = Color.green(pixels[i]);
            int blue = Color.blue(pixels[i]);

            if (red + 20  >= 256) {
                red = 0xFF;
            }
            else {
                red += 20;
            }
            if (green + 20 >= 256) {
                green = 0xFF;
            }
            else {
                green += 20;
            }
            if (blue + 20 >= 256) {
                blue = 0xFF;
            }
            else {
                blue += 20;
            }
            pixels[i] = Color.argb(Color.alpha(pixels[i]), red, green, blue);
        }
        bitmap = Bitmap.createBitmap(pixels, width, height, Bitmap.Config.ARGB_8888);
    }


    public Bitmap nearestNeighbor() {

        int newWidth = (int) ((double)  width / scale);
        int newHeight = (int) ((double) height / scale);


        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);

        int[] newPixels = new int[newWidth*newHeight];
        int pos;

        for(int i = 0; i < newWidth; i++) {
            for(int j = 0; j < newHeight; j++) {
                pos = (int) (i * scale) + (int) (j * scale) * width;
                newPixels[i + j * newWidth] = pixels[pos];
            }
        }
        return Bitmap.createBitmap(newPixels, newWidth, newHeight, Bitmap.Config.ARGB_8888);
    }

    public Bitmap bilinearInterpolation() {

        int newWidth = (int) ((double) width / scale);
        int newHeight = (int) ((double) height / scale);

        int[] newPixels = new int[newWidth * newHeight];
        int x, y, pos, a, b, c, d;
        double diffX, diffY, red, green, blue;

        for (int i = 0; i < newWidth; i++) {
            for (int j = 0; j < newHeight; j++) {
                x = (int) (scale * i);
                y = (int) (scale * j);
                diffX = scale * i - x;
                diffY = scale * j - y;

                pos = x + y * width;

                a = pixels[pos];
                b = pixels[pos + 1];
                c = pixels[pos + width];
                d = pixels[pos + width + 1];

                red = Color.red(a) * (1 - diffX) * (1 - diffY) + Color.red(b) * diffX * (1 - diffY) + Color.red(c) * (1 - diffX) * diffY + Color.red(d) * diffX * diffY;

                green = Color.green(a) * (1 - diffX) * (1 - diffY) + Color.green(b) * diffX * (1 - diffY) + Color.green(c) * (1 - diffX) * diffY + Color.green(d) * diffX * diffY;

                blue = Color.blue(a) * (1 - diffX) * (1 - diffY) + Color.blue(b) * diffX * (1 - diffY) + Color.blue(c) * (1 - diffX) * diffY + Color.blue(d) * diffX * diffY;

                newPixels[i + j * newWidth] = Color.argb(0xFF, (int) red, (int) green, (int) blue);
            }
        }

        return Bitmap.createBitmap(newPixels, newWidth, newHeight, Bitmap.Config.ARGB_8888);
    }


    public Bitmap getBitmap() {
        increaseBrightness();
        turnRight();
        return bitmap2;
    }
}

