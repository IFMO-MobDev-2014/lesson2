package ru.ifmo.md.lesson2;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class MyActivity extends Activity {
    private static final String TAG = "Activity";

    private static final int SRC_HEIGHT = 700;
    private static final int SRC_WIDTH = 750;
    private static final int DST_HEIGHT = 405;
    private static final int DST_WIDTH = 434;

    private boolean coolImage;

    private int[] justPixels;
    private int[] poorPixels;
    private int[] coolPixels;


    private class CatView extends View implements View.OnClickListener {
        private static final String TAG = "CatView";

        public CatView(Context context) {
            super(context);
            setOnClickListener(this);
            Log.d(TAG, "Created CatView");
        }

        @Override
        public void onClick(View view) {
            Log.d(TAG, "onClick() works");
            coolImage = !coolImage;
            invalidate();
        }

        @Override
        public void onDraw(Canvas canvas) {
            Log.d(TAG, "onDraw() works");
            canvas.drawBitmap(coolImage ? coolPixels : poorPixels,
                    0, DST_WIDTH, 0, 0, DST_WIDTH, DST_HEIGHT, false, null);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        long startTime = System.currentTimeMillis();
        super.onCreate(savedInstanceState);

        justPixels = extractPixels();
        rotateBitmap(justPixels, SRC_HEIGHT, SRC_WIDTH);
        increaseBrightness(justPixels);

        poorPixels = doItFast(justPixels, SRC_HEIGHT, SRC_WIDTH, DST_HEIGHT, DST_WIDTH);
        coolPixels = doItPerfect(justPixels, SRC_HEIGHT, SRC_WIDTH, DST_HEIGHT, DST_WIDTH);

        setContentView(new CatView(this)); // meeoooww

        long finishTime = System.currentTimeMillis();
        Log.i(TAG, "onCreate() complete in " + (finishTime - startTime) + ".ms");
    }


    // be sure that your image calls res/drawable-nodpi/source.png
    private int[] extractPixels() {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.source);
        int[] pixels = new int[bitmap.getHeight() * bitmap.getWidth()];
        if (bitmap.getHeight() != SRC_WIDTH) {
            throw new AssertionError(bitmap.getHeight() + " " + SRC_WIDTH); // all right ;)
        }
        if (bitmap.getWidth() != SRC_HEIGHT) {
            throw new AssertionError(bitmap.getWidth() + " " + SRC_HEIGHT);
        }
        bitmap.getPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        return pixels;
    }

    // rotate image by 90 clock wise
    private void rotateBitmap(int[] bitmap, int height, int width) {
        int[] temp = bitmap.clone();
        for (int i = 0, ptr = 0; i < height; ++i) {
            for (int j = width - 1; j >= 0; --j) {
                bitmap[ptr++] = temp[i + height * j];
            }
        }
    }

    // simply multiplies each component by two
    private void increaseBrightness(int[] pixels) {
        for (int i = 0; i < pixels.length; ++i) {
            int color = pixels[i];
            pixels[i] = Color.argb(
                    Color.alpha(color),
                    Math.min(2 * Color.red(color), 0xFF),
                    Math.min(2 * Color.green(color), 0xFF),
                    Math.min(2 * Color.blue(color), 0xFF)
            );
        }
    }

    // fast but bad compression
    private int[] doItFast(int[] src, int srcHeight, int srcWidth, int dstHeight, int dstWidth) {
        int[] result = new int[dstHeight * dstWidth];
        for (int j = 0; j < dstHeight; ++j) {
            for (int i = 0; i < dstWidth; ++i) {
                result[i + dstWidth * j] = src[i * srcWidth / dstWidth + j * srcHeight / dstHeight * srcWidth];
            }
        }
        return result;
    }

    // slow but good compression
    private int[] doItPerfect(int[] src, int srcHeight, int srcWidth, int dstHeight, int dstWidth) {
        int[] temp = new int[srcHeight * dstWidth];
        { // interpolate horizontal
            double ratio = (double) srcWidth / dstWidth;
            for (int i = 0; i < srcHeight; ++i) {
                for (int j = 0; j < dstWidth; ++j) {
                    int from = Math.max(0, (int) Math.floor((j - 0.5) * ratio));
                    int to = Math.min(srcWidth, (int) Math.ceil((j + 0.5) * ratio));
                    if (from >= to) {
                        throw new AssertionError(from + " " + to);
                    }
                    temp[i * dstWidth + j] = calculateAverage(src, i * srcWidth + from, i * srcWidth + to, 1);
                }
            }
        }
        int[] result = new int[dstHeight * dstWidth];
        { // interpolate vertical
            double ratio = (double) srcHeight / dstHeight;
            for (int i = 0; i < dstHeight; ++i) {
                for (int j = 0; j < dstWidth; ++j) {
                    int from = Math.max(0, (int) Math.floor((i - 0.5) * ratio));
                    int to = Math.min(srcHeight, (int) Math.ceil((i + 0.5) * ratio));
                    if (from >= to) {
                        throw new AssertionError(from + " " + to);
                    }
                    result[i * dstWidth + j] = calculateAverage(temp, from * dstWidth + j, to * dstWidth + j, dstWidth);
                }
            }
        }
        return result;
    }

    private int calculateAverage(int[] src, int from, int to, int step) {
        int sumAlpha = 0;
        int sumRed = 0;
        int sumGreen = 0;
        int sumBlue = 0;
        for (int i = from; i < to; i += step) {
            sumAlpha += Color.alpha(src[i]);
            sumRed += Color.red(src[i]);
            sumGreen += Color.green(src[i]);
            sumBlue += Color.blue(src[i]);
        }
        int number = (to - from) / step;
        return Color.argb(
                (int) ((double) sumAlpha / number + 0.5),
                (int) ((double) sumRed / number + 0.5),
                (int) ((double) sumGreen / number + 0.5),
                (int) ((double) sumBlue / number + 0.5)
        );
    }
}
