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
    private static final String TAG = "MyActivity";

    private static final int SRC_WIDTH = 750;
    private static final int SRC_HEIGHT = 700;
    private static final int DST_WIDTH = 434;
    private static final int DST_HEIGHT = 405;

    private int[] qualityPixels = null;
    private int[] fastPixels = new int[SRC_WIDTH * SRC_WIDTH];
    private int[] pixels = new int[SRC_WIDTH * SRC_WIDTH];
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prepareImages();

        setContentView(new ScaleView(this));
    }

    private void prepareImages() {
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.source);

        for (int i = 0; i < SRC_HEIGHT; i++) {
            for (int j = 0; j < SRC_WIDTH; j++) {
                int color = bitmap.getPixel(i, j);
                int x = SRC_WIDTH - 1 - j;
                int y = i;
                pixels[x + y * SRC_WIDTH] = color;
            }
        }

        bitmap.recycle();
        increaseBrightness(pixels, SRC_WIDTH, SRC_HEIGHT);

        for (int i = 0; i < DST_WIDTH; i++) {
            for (int j = 0; j < DST_HEIGHT; j++) {
                int x = (int)((float)i * SRC_WIDTH / DST_WIDTH);
                int y = (int)((float)j * SRC_HEIGHT / DST_HEIGHT);
                fastPixels[i + j * DST_WIDTH] = pixels[x + y * SRC_WIDTH];
            }
        }
        qualityPixels = bicubicResize(pixels, SRC_WIDTH, SRC_HEIGHT, DST_WIDTH, DST_HEIGHT);
    }

    private void increaseBrightness(int[] pix, int w, int h) {
        for (int i = 0; i < w * h; i++) {
            int color = pix[i];
            int a = Color.alpha(color);
            int r = Color.red(color);
            int g = Color.green(color);
            int b = Color.blue(color);

            r = Math.min(r * 2, 255);
            g = Math.min(g * 2, 255);
            b = Math.min(b * 2, 255);

            color = Color.argb(a, r, g, b);
            pix[i] = color;
        }
    }

    private int getCol(int color, int index) {
        switch (index) {
            case 0: return Color.red(color);
            case 1: return Color.green(color);
            case 2: return Color.blue(color);
        }
        return 0;
    }

    private int[] bicubicResize(int[] src, int srcWidth, int srcHeight, int dstWidth, int dstHeight) {
        int[] dst = new int[dstWidth * dstHeight];
        final float tx = (float)srcWidth / dstWidth;
        final float ty = (float)srcHeight / dstHeight;
        float[][] grid = new float[4][4];
        int c[] = new int[4];

        for (int i = 0; i < dstWidth; i++) {
            for (int j = 0; j < dstHeight; j++) {
                int x = (int) (tx * i);
                int y = (int) (ty * j);
                float dx = tx * j - x;
                float dy = ty * i - y;

                for (int k = 0; k < 3; k++) {
                    for (int ii = -1; ii < 3; ii++) {
                        for (int jj = -1; jj < 3; jj++) {
                            int cx = ii + x;
                            int cy = jj + y;
                            int index = cx + srcWidth * cy;
                            if (index >= 0 && index < src.length) {
                                grid[ii + 1][jj + 1] = getCol(pixels[index], k);
                            } else {
                                grid[ii + 1][jj + 1] = 0;
                            }
                        }
                    }
                    c[k] = (int)BicubicInterpolator.getValue(grid, 0.1f, 0.1f);
                }
                dst[i + dstWidth * j] = Color.rgb(c[0], c[1], c[2]);
            }
        }
        return dst;
    }


    private class ScaleView extends View {
        private static final String TAG = "ScaleView";

        private int state;

        public ScaleView(Context context) {
            super(context);
            state = 0;

            this.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    state ^= 1;
                    Log.d(TAG, "Clicked, current state: " + state);
                    invalidate();
                }
            });

        }

        @Override
        public void onDraw(Canvas canvas) {
            canvas.save();
            if (state == 0) {
                canvas.drawBitmap(fastPixels, 0, DST_WIDTH, 0, 0, DST_WIDTH, DST_HEIGHT, false, null);
            } else {
                canvas.drawBitmap(qualityPixels, 0, DST_WIDTH, 0, 0, DST_WIDTH, DST_HEIGHT, false, null);
            }
            canvas.restore();
        }
    }
}
