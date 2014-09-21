package ru.ifmo.md.lesson2;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class MyActivity extends Activity {

    ImageView img = null;
    Bitmap original = null;

    Bitmap transformedFast = null, transformedSlow = null;

    public enum ScalingType {
        FAST,
        BILINEAR
    }

    ScalingType scaling = ScalingType.FAST;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_layout);
        img = (ImageView) findViewById(R.id.imageView);
        original = BitmapFactory.decodeResource(this.getResources(), R.drawable.source);
        onClick(null);
    }

    @Override
    public void onTrimMemory(int level) {
        if (level >= TRIM_MEMORY_UI_HIDDEN) {
            transformedFast = null;
            transformedSlow = null;
        }
    }

    public void onClick(View view) {
        switch (scaling) {
            case FAST:
                scaling = ScalingType.BILINEAR;
                if (transformedSlow == null) {
                    transformedSlow = transformImage(original);
                }
                showBitmap(transformedSlow);
                break;
            case BILINEAR:
                scaling = ScalingType.FAST;
                if (transformedFast == null) {
                    transformedFast = transformImage(original);
                }
                showBitmap(transformedFast);
                break;
        }
    }

    void showBitmap(Bitmap bm) {
        img.setImageBitmap(bm);
    }

    public Bitmap transformImage(Bitmap bm) {
        Bitmap scaledImg = null;
        final float scale = 1.f / 1.73f;
        switch (scaling) {
            case FAST:
                scaledImg = scaleBitmapFast(bm, scale, scale);
                break;
            case BILINEAR:
                scaledImg = scaleBitmapBilinear(bm, scale, scale);
                break;
        }
        final double gamma = 2.0;
        return applyGamma(rotateClockwise(scaledImg), 1. / gamma);
    }


    static public Bitmap scaleBitmapFast(Bitmap bm, float scaleX, float scaleY) {
        int newW = (int) (bm.getWidth() * scaleX),
                newH = (int) (bm.getHeight() * scaleY);
        int[] colors = new int[newW * newH];
        for (int i = 0; i < newW * newH; i++) {
            final int x = i % newW, y = i / newW;
            colors[i] = bm.getPixel((int) Math.round(x / scaleX), (int) Math.round(y / scaleY));
        }
        return Bitmap.createBitmap(colors, newW, newH, bm.getConfig());
    }

    static public Bitmap scaleBitmapBilinear(Bitmap bm, float scaleX, float scaleY) {
        if (bm.getConfig() != Bitmap.Config.ARGB_8888) {
            throw new IllegalArgumentException("Bad bitmap! I need 32-bit ARGB");
        }
        int newW = (int) (bm.getWidth() * scaleX),
                newH = (int) (bm.getHeight() * scaleY);
        int[] colors = new int[newW * newH];

        for (int i = 0; i < newW * newH; i++) {
            int x = i % newW, y = i / newW;
            float tx = x / scaleX, ty = y / scaleY;
            int x1 = (int) tx, x2 = (int) (x1 + 1);
            int y1 = (int) ty, y2 = (int) (y1 + 1);

            int q11 = bm.getPixel(x1, y1), q12 = bm.getPixel(x1, y2), q21 = bm.getPixel(x2, y1), q22 = bm.getPixel(x2, y2);


            // alpha
            colors[i] = (q11 >> 24) & 0xff;
            colors[i] <<= 8;
            // red
            colors[i] |= (int) (BilinearInterpolator.apply(x1, x2, y1, y2,
                    (q11 >> 16) & 0xff, (q12 >> 16) & 0xff, (q21 >> 16) & 0xff, (q22 >> 16) & 0xff,
                    tx, ty));
            colors[i] <<= 8;
            // green
            colors[i] |= (int) (BilinearInterpolator.apply(x1, x2, y1, y2,
                    (q11 >> 8) & 0xff, (q12 >> 8) & 0xff, (q21 >> 8) & 0xff, (q22 >> 8) & 0xff,
                    tx, ty));
            colors[i] <<= 8;
            // blue
            colors[i] |= (int) (BilinearInterpolator.apply(x1, x2, y1, y2,
                    (q11 >> 0) & 0xff, (q12 >> 0) & 0xff, (q21 >> 0) & 0xff, (q22 >> 0) & 0xff,
                    tx, ty));
        }
        return Bitmap.createBitmap(colors, newW, newH, bm.getConfig());
    }

    static public Bitmap rotateClockwise(Bitmap bm) {
        int width = bm.getWidth(),
                height = bm.getHeight();
        int[] pixels = new int[width * height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                final int newIdx = /*x*/(height - 1 - y) + (/*y*/x) * height;

                pixels[newIdx] = bm.getPixel(x, y);
            }
        }
        return Bitmap.createBitmap(pixels, height, width, bm.getConfig());
    }

    // invGamma = 1 / gamma
    static public Bitmap applyGamma(Bitmap bm, double invGamma) {
        if (bm.getConfig() != Bitmap.Config.ARGB_8888) {
            throw new IllegalArgumentException("Bad bitmap! I need 32-bit ARGB");
        }
        int width = bm.getWidth(),
                height = bm.getHeight();
        int[] pixels = new int[width * height];
        bm.getPixels(pixels, 0, width, 0, 0, width, height);
        for (int i = 0; i < width * height; i++) {
            // split
            int A = (pixels[i] >> 24) & 0xff;
            int R = (pixels[i] >> 16) & 0xff;
            int G = (pixels[i] >> 8) & 0xff;
            int B = pixels[i] & 0xff;
            // apply gamma
            R = (int) (255 * Math.pow(R / 255.0, invGamma));
            G = (int) (255 * Math.pow(G / 255.0, invGamma));
            B = (int) (255 * Math.pow(B / 255.0, invGamma));
            // combine
            pixels[i] = A << 24 | R << 16 | G << 8 | B;
        }
        return Bitmap.createBitmap(pixels, width, height, bm.getConfig());
    }
}
