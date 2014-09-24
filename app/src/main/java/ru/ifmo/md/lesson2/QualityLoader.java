package ru.ifmo.md.lesson2;

import android.graphics.Bitmap;
import android.graphics.Color;

/**
 * Created by dimatomp on 24.09.14.
 */
public class QualityLoader extends ImageLoader {
    int[] defPixels;
    int[] pixels = new int[WIDTH * HEIGHT];
    int fWidth, fHeight;

    private int getBicubicChannel(float offset, int a0, int a1, int a2, int a3) {
        int b0 = 2 * a1;
        int b1 = a1 - a0;
        int b2 = 2 * a0 - 5 * a1 + 4 * a2 - a3;
        int b3 = -a0 + 3 * a1 - 3 * a2 + a3;
        return (int) ((b0 + offset * b1 + offset * offset * b2 + offset * offset * offset * b3) / 2f);
    }

    private int getBicubicConverted(float x, float y) {
        float origX = x * (fWidth - 4) + 1.5f;
        float origY = y * (fHeight - 4) + 1.5f;
        int xP = (int) origX - 1;
        int yP = (int) origY - 1;
        float offsetX = (origX - xP) / 3;
        float offsetY = (origY - yP) / 3;
        int b0 = getBicubicChannel(offsetX, Color.red(defPixels[xP + yP * fWidth]),
                Color.red(defPixels[xP + 1 + yP * fWidth]),
                Color.red(defPixels[xP + 2 + yP * fWidth]),
                Color.red(defPixels[xP + 3 + yP * fWidth]));
        int b1 = getBicubicChannel(offsetX, Color.red(defPixels[xP + (yP + 1) * fWidth]),
                Color.red(defPixels[xP + 1 + (yP + 1) * fWidth]),
                Color.red(defPixels[xP + 2 + (yP + 1) * fWidth]),
                Color.red(defPixels[xP + 3 + (yP + 1) * fWidth]));
        int b2 = getBicubicChannel(offsetX, Color.red(defPixels[xP + (yP + 2) * fWidth]),
                Color.red(defPixels[xP + 1 + (yP + 2) * fWidth]),
                Color.red(defPixels[xP + 2 + (yP + 2) * fWidth]),
                Color.red(defPixels[xP + 3 + (yP + 2) * fWidth]));
        int b3 = getBicubicChannel(offsetX, Color.red(defPixels[xP + (yP + 3) * fWidth]),
                Color.red(defPixels[xP + 1 + (yP + 3) * fWidth]),
                Color.red(defPixels[xP + 2 + (yP + 3) * fWidth]),
                Color.red(defPixels[xP + 3 + (yP + 3) * fWidth]));
        int r = getBicubicChannel(offsetY, b0, b1, b2, b3);
        b0 = getBicubicChannel(offsetX, Color.green(defPixels[xP + yP * fWidth]),
                Color.green(defPixels[xP + 1 + yP * fWidth]),
                Color.green(defPixels[xP + 2 + yP * fWidth]),
                Color.green(defPixels[xP + 3 + yP * fWidth]));
        b1 = getBicubicChannel(offsetX, Color.green(defPixels[xP + (yP + 1) * fWidth]),
                Color.green(defPixels[xP + 1 + (yP + 1) * fWidth]),
                Color.green(defPixels[xP + 2 + (yP + 1) * fWidth]),
                Color.green(defPixels[xP + 3 + (yP + 1) * fWidth]));
        b2 = getBicubicChannel(offsetX, Color.green(defPixels[xP + (yP + 2) * fWidth]),
                Color.green(defPixels[xP + 1 + (yP + 2) * fWidth]),
                Color.green(defPixels[xP + 2 + (yP + 2) * fWidth]),
                Color.green(defPixels[xP + 3 + (yP + 2) * fWidth]));
        b3 = getBicubicChannel(offsetX, Color.green(defPixels[xP + (yP + 3) * fWidth]),
                Color.green(defPixels[xP + 1 + (yP + 3) * fWidth]),
                Color.green(defPixels[xP + 2 + (yP + 3) * fWidth]),
                Color.green(defPixels[xP + 3 + (yP + 3) * fWidth]));
        int g = getBicubicChannel(offsetY, b0, b1, b2, b3);
        b0 = getBicubicChannel(offsetX, Color.blue(defPixels[xP + yP * fWidth]),
                Color.blue(defPixels[xP + 1 + yP * fWidth]),
                Color.blue(defPixels[xP + 2 + yP * fWidth]),
                Color.blue(defPixels[xP + 3 + yP * fWidth]));
        b1 = getBicubicChannel(offsetX, Color.blue(defPixels[xP + (yP + 1) * fWidth]),
                Color.blue(defPixels[xP + 1 + (yP + 1) * fWidth]),
                Color.blue(defPixels[xP + 2 + (yP + 1) * fWidth]),
                Color.blue(defPixels[xP + 3 + (yP + 1) * fWidth]));
        b2 = getBicubicChannel(offsetX, Color.blue(defPixels[xP + (yP + 2) * fWidth]),
                Color.blue(defPixels[xP + 1 + (yP + 2) * fWidth]),
                Color.blue(defPixels[xP + 2 + (yP + 2) * fWidth]),
                Color.blue(defPixels[xP + 3 + (yP + 2) * fWidth]));
        b3 = getBicubicChannel(offsetX, Color.blue(defPixels[xP + (yP + 3) * fWidth]),
                Color.blue(defPixels[xP + 1 + (yP + 3) * fWidth]),
                Color.blue(defPixels[xP + 2 + (yP + 3) * fWidth]),
                Color.blue(defPixels[xP + 3 + (yP + 3) * fWidth]));
        int b = getBicubicChannel(offsetY, b0, b1, b2, b3);
        return convertColor(r, g, b);
    }

    @Override
    public Bitmap transformBitmap(Bitmap from) {
        fWidth = from.getWidth();
        fHeight = from.getHeight();
        defPixels = new int[fWidth * fHeight];
        from.getPixels(defPixels, 0, fWidth, 0, 0, fWidth, fHeight);

        pixels = new int[WIDTH * HEIGHT];
        for (int j = 0; j < HEIGHT; j++) {
            float y = (float) j / HEIGHT;
            for (int i = 0; i < WIDTH; i++)
                pixels[j * WIDTH + i] = getBicubicConverted(y, 1 - (float) i / WIDTH);
        }

        Bitmap result = Bitmap.createBitmap(WIDTH, HEIGHT, Bitmap.Config.ARGB_8888);
        result.setPixels(pixels, 0, WIDTH, 0, 0, WIDTH, HEIGHT);
        return result;
    }
}
