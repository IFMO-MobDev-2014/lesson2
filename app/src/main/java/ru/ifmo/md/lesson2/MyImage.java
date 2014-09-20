package ru.ifmo.md.lesson2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;


public class MyImage extends View {
    BitmapFactory.Options options = new BitmapFactory.Options();
    Bitmap bitmap;
    Matrix m = new Matrix();

    public MyImage(Context context) {
        super(context);
        init();
    }

    private void init() {
        options.inScaled = false;
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.source, options);
        Log.i("S:", bitmap.getWidth() + " " + bitmap.getHeight());
        long act0 = System.nanoTime();
        bitmap = bright(bitmap);
        long act1 = System.nanoTime();
        bitmap = rotate(bitmap);
        long act2 = System.nanoTime();
        bitmap = fastScale(bitmap, 1.73f);
        long act3 = System.nanoTime();
        Log.i("bright():", " " + (act1 - act0) / 1000000);
        Log.i("rotate():", " " + (act2 - act1) / 1000000);
        Log.i("fastScale():", " " + (act3 - act2) / 1000000);
        Log.i("all stuff:", " " + (act3 - act0) / 1000000);
    }

    public void resume() {
        invalidate();
    }

    Bitmap rotate(Bitmap b) {
        int w = b.getWidth();
        int h = b.getHeight();
        int[] c = new int[w * h];
        b.getPixels(c, 0, w, 0, 0, w, h);
        int[] newColors = new int[w * h];
        for (int y = 0; y < w; y++) {
            for (int x = 0; x < h; x++) {
                newColors[y * h + x] = c[y + (h - 1 - x) * w];
            }
        }
        return Bitmap.createBitmap(newColors, h, w, Bitmap.Config.ARGB_8888);
    }

    Bitmap bright(Bitmap b) {
        int w = b.getWidth();
        int h = b.getHeight();
        int[] c = new int[w * h];
        b.getPixels(c, 0, w, 0, 0, w, h);
        for (int i = 0; i < w * h; i++) {
            int white = 0xff;
            int newRed = (((c[i] >> 8 * 2) & white) + white) >>> 1;
            int newGreen = (((c[i] >> 8) & white) + white) >>> 1;
            int newBlue = ((c[i] & white) + white) >>> 1;
            c[i] = (white << 8 * 3) | (newRed << 8 * 2) | (newGreen << 8) | newBlue;
        }
        return Bitmap.createBitmap(c, w, h, Bitmap.Config.ARGB_8888);
    }

    Bitmap fastScale(Bitmap b, float factor) {
        int intFactor = (int) (factor * 100);
        int w = b.getWidth();
        int h = b.getHeight();
        int[] c = new int[w * h];
        b.getPixels(c, 0, w, 0, 0, w, h);
        int newWidth = w * 100 / intFactor;
        int newHeight = h * 100 / intFactor;
        int[] newColors = new int[newWidth * newHeight];
        for (int y = 0; y < newHeight; y++) {
            for (int x = 0; x < newWidth; x++) {
                newColors[y * newWidth + x] = c[(y * intFactor / 100) * w + (x * intFactor / 100)];
            }
        }
        return Bitmap.createBitmap(newColors, newWidth, newHeight, Bitmap.Config.ARGB_8888);
    }

    Bitmap beautyScale(Bitmap b, float factor) {
        return b;
    }

    @Override
    public void onDraw(Canvas canvas) {
        canvas.drawBitmap(bitmap, m, null);
    }
}
