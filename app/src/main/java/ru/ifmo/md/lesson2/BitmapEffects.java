package ru.ifmo.md.lesson2;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;

/**
 * Created by flash on 24.09.14.
 */
public class BitmapEffects {
    private int height;
    private int width;

    private int[] pixels;

    public BitmapEffects(Bitmap bitmap) {
        width = bitmap.getWidth();
        height = bitmap.getHeight();
        pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
    }

    public Bitmap getBitmap() {
        return Bitmap.createBitmap(pixels, 0, width, width, height, Bitmap.Config.ARGB_8888);
    }

    public BitmapEffects compressFast(double scale) {
        int newWidth = (int) (width / scale);
        int newHeight = (int) (height / scale);
        int[] newPixels = new int[newWidth * newHeight];
        double oldX = 0;
        double oldY = 0;
        for (int x = 0; x < newWidth; x++) {
            oldY = 0;
            for (int y = 0; y < newHeight; y++) {
                newPixels[newWidth * y + x] = pixels[width * (int) oldY + (int) oldX];
                oldY += scale;
            }
            oldX += scale;
        }
        width = newWidth;
        height = newHeight;
        pixels = newPixels;
        return this;
    }

    public BitmapEffects compressSlow(double scale) {
        int newWidth = (int) (width / scale);
        int newHeight = (int) (height / scale);
        int[] newPixels = new int[newWidth * newHeight];
        double oldX = 0;
        double oldY = 0;
        int a, r, g, b = 0;
        for (int x = 0; x < newWidth; x++, oldX += scale) {
            oldY = 0;
            for (int y = 0; y < newHeight; y++, oldY += scale) {
                a = Color.alpha(pixels[width * (int) oldY + (int) oldX]);
                r = Color.red(pixels[width * (int) oldY + (int) oldX]);
                g = Color.green(pixels[width * (int) oldY + (int) oldX]);
                b = Color.blue(pixels[width * (int) oldY + (int) oldX]);
                for(int x1 = (int)oldX; x1 < width && x1 < (x + scale + 1); x1++) {
                    for(int y1 = (int)oldY; y1 < height && y1 < (y + scale + 1); y1++) {
                        a = a + (Color.alpha(pixels[width * y1 + x1]) - a) / 2;
                        r = r + (Color.red(pixels[width * y1 + x1]) - r) / 2;
                        g = g + (Color.green(pixels[width * y1 + x1]) - g) / 2;
                        b = b + (Color.blue(pixels[width * y1 + x1]) - b) / 2;
                    }
                }
                newPixels[newWidth * y + x] = Color.argb(a, r, g, b);
            }
        }
        width = newWidth;
        height = newHeight;
        pixels = newPixels;
        return this;
    }

    public BitmapEffects lighten() {
        int a, r, g, b = 0;
        for(int i = 0; i < pixels.length; i++) {
            a = Color.alpha(pixels[i]);
            r = Color.red(pixels[i]);
            g = Color.green(pixels[i]);
            b = Color.blue(pixels[i]);
            a = a + (int)(0.5 * (255 - a));
            r = r + (int)(0.5 * (255 - r));
            g = g + (int)(0.5 * (255 - g));
            b = b + (int)(0.5 * (255 - b));
            pixels[i] = Color.argb(a, r, g, b);
        }
        return this;
    }

    public BitmapEffects rotate() {
        int[] newPixel = new int[width * height];
        for(int y = 0; y < height; y++) {
            for(int x = 0; x < width; x++) {
                newPixel[height - y - 1 + x * height] = pixels[width * y + x];
            }
        }
        int tmpHeight = height;
        height = width;
        width = tmpHeight;
        pixels = newPixel;
        return this;
    }
}
