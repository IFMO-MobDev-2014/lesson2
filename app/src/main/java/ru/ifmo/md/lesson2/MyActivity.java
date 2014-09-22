package ru.ifmo.md.lesson2;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

/**
 * Created by lightning95 on 22/09/14.
 */

public class MyActivity extends Activity {
    private static final int SRC_HEIGHT = 700;
    private static final int SRC_WIDTH = 750;
    private static final int RES_HEIGHT = 405;
    private static final int RES_WIDTH = 434;

    private int[] defPixels;
    private int[] qualPixels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initialization();

        setContentView(new MyView(this));
    }

    private void initialization() {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.source);

        int[] srcPixels = new int[SRC_WIDTH * SRC_HEIGHT];

        for (int i = 0; i < SRC_HEIGHT; ++i) {
            for (int j = 0; j < SRC_WIDTH; ++j) {
                srcPixels[SRC_WIDTH * i + SRC_WIDTH - j - 1] = bitmap.getPixel(i, j);
            }
        }

        increaseBrightness(srcPixels);

        defectiveCompression(srcPixels);

        bicubicCompression(srcPixels);
    }

    private void increaseBrightness(int[] src) {
        for (int i = 0; i < src.length; ++i) {
            src[i] = Color.argb(Color.alpha(src[i]),
                    Math.min(Color.red(src[i]) * 2, 255),
                    Math.min(Color.green(src[i]) * 2, 255),
                    Math.min(Color.blue(src[i]) * 2, 255));
        }
    }

    private void defectiveCompression(int[] src) {
        defPixels = new int[RES_WIDTH * RES_HEIGHT];

        for (int i = 0; i < RES_WIDTH; ++i) {
            int x = (int) (1. * i * SRC_WIDTH / RES_WIDTH);
            for (int j = 0; j < RES_HEIGHT; ++j) {
                int y = (int) (1. * j * SRC_HEIGHT / RES_HEIGHT);
                defPixels[i + j * RES_WIDTH] = src[x + y * SRC_WIDTH];
            }
        }
    }

    private void bicubicCompression(int[] src) {
        qualPixels = new int[RES_WIDTH * RES_HEIGHT];
        float sx = (float) SRC_WIDTH / RES_WIDTH;
        float sy = (float) SRC_HEIGHT / RES_HEIGHT;
        float[][] matrix = new float[4][4];
        int values[] = new int[3];

        for (int i = 0; i < RES_WIDTH; ++i) {
            int x = (int) (sx * i);
            for (int j = 0; j < RES_HEIGHT; ++j) {
                int y = (int) (sy * j);

                for (int k = 0; k < 3; ++k) {
                    for (int ii = -1; ii < 3; ++ii) {
                        int cx = ii + x;
                        for (int jj = -1; jj < 3; ++jj) {
                            int cy = jj + y;
                            int id = SRC_WIDTH * cy + cx;
                            if (id >= 0 && id < src.length) {
                                matrix[ii + 1][jj + 1] = getColor(src[id], k);
                            } else {
                                matrix[ii + 1][jj + 1] = 0;
                            }
                        }
                    }
                    values[k] = BicubicInterpolator.get(matrix, 0.1f, 0.1f);
                }
                qualPixels[i + RES_WIDTH * j] = Color.rgb(values[0], values[1], values[2]);
            }
        }
    }

    private int getColor(int color, int id) {
        if (id == 0) {
            return Color.red(color);
        } else if (id == 1) {
            return Color.green(color);
        } else {
            return Color.blue(color);
        }
    }

    private class MyView extends View {
        private int state;

        public MyView(Context context) {
            super(context);
            state = 0;

            this.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    state ^= 1;
                    invalidate();
                }
            });
        }

        @Override
        public void onDraw(Canvas canvas) {
            if (state == 0) {
                canvas.drawBitmap(defPixels, 0, RES_WIDTH, 0, 0, RES_WIDTH, RES_HEIGHT, false, null);
            } else {
                canvas.drawBitmap(qualPixels, 0, RES_WIDTH, 0, 0, RES_WIDTH, RES_HEIGHT, false, null);
            }
        }
    }

    static class BicubicInterpolator {
        private static float[] c = new float[4];

        private static float innerGet(float[] a, float p) {
            return a[1] + 0.5f * p * (a[2] - a[0] + p * (2.0f * a[0] - 5.0f * a[1] + 4.0f * a[2] - a[3] +
                    p * (3.0f * (a[1] - a[2]) + a[3] - a[0])));
        }

        public static int get(float[][] a, float x, float y) {
            c[0] = innerGet(a[0], y);
            c[1] = innerGet(a[1], y);
            c[2] = innerGet(a[2], y);
            c[3] = innerGet(a[3], y);
            return (int) innerGet(c, x);
        }
    }
}