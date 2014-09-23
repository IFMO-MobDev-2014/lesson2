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

    public Bitmap getBitmap() {
        increaseBrightness();
        turnRight();
        return bitmap2;
    }
}

