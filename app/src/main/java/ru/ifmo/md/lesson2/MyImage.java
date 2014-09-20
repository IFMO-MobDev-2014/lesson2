package ru.ifmo.md.lesson2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.view.View;


public class MyImage extends View {
    BitmapFactory.Options options = new BitmapFactory.Options();
    Bitmap bitmap, initBitmap;
    Matrix m = new Matrix();
    boolean state;
    float factor = 1.73f;

    public MyImage(Context context) {
        super(context);
        init();
        this.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                bitmap = state ? beautyScale(initBitmap, factor) : fastScale(initBitmap, factor);
                state = !state;
                invalidate();
            }
        });
    }

    private void init() {
        options.inScaled = false;
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.source, options);
        initBitmap = bright(rotate(bitmap));
        bitmap = fastScale(initBitmap, factor);
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
            int newRed = (((c[i] >> 8 * 2) & white) + white) >> 1;
            int newGreen = (((c[i] >> 8) & white) + white) >> 1;
            int newBlue = ((c[i] & white) + white) >> 1;
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
        int w = b.getWidth();
        int h = b.getHeight();
        int nw = (int) (w / factor);
        int nh = (int) (h / factor);
        int[] newColors = new int[nw * nh];
        for (int y = 0; y < nh; y++) {
            for (int x = 0; x < nw; x++) {
                int x1 = (int) (x * factor);
                int y1 = (int) (y * factor);
                int x2 = (int) ((x + 1) * factor - 0.01f);
                int y2 = (int) ((y + 1) * factor - 0.01f);
                int dx = x2 - x1 + 1;
                int dy = y2 - y1 + 1;
                int[] c = new int[dx * dy];
                int k = 0;
                for (int xi = x1; xi <= x2; xi++) {
                    for (int yi = y1; yi <= y2; yi++) {
                        c[k++] = b.getPixel(xi, yi);
                    }
                }
                int red = 0;
                int green = 0;
                int blue = 0;
                for (int i = 0; i < k; i++) {
                    red += Color.red(c[i]);
                    green += Color.green(c[i]);
                    blue += Color.blue(c[i]);
                }
                newColors[y * nw + x] = Color.rgb(red / k, green / k, blue / k);
            }
        }
        return Bitmap.createBitmap(newColors, nw, nh, Bitmap.Config.ARGB_8888);
    }

    @Override
    public void onDraw(Canvas canvas) {
        canvas.drawBitmap(bitmap, m, null);
    }
}
