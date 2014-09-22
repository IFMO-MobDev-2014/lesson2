package ru.ifmo.md.lesson2;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.util.Arrays;

public class MyActivity extends Activity {
    ImageView im;
    Bitmap bmp[] = new Bitmap[2]; // 0 -- good, 1 -- fast
    int curBmp;
    final double scale = 1.73;

    // slow and good compressing
    int[] compressSlow(int[] colors, int w, int h, int newW, int newH, double scale) {
        int res[] = new int[newW * newH];
        int cnt[] = new int[newW * newH];
        int sum[] = new int[newW * newH];

        for (int k = 0; k < 4; k++) {
            Arrays.fill(cnt, 0);
            Arrays.fill(sum, 0);
            for (int i = 0; i < h; i++) {
                for (int j = 0; j < w; j++) {
                    int ni = (int)(i / scale);
                    int nj = (int)(j / scale);
                    cnt[ni * newW + nj]++;
                    int x = (colors[i * w + j] >> (8 * k)) & 255;
                    sum[ni * newW + nj] += x;
                }
            }
            for (int i = 0; i < newH; i++) {
                for (int j = 0; j < newW; j++) {
                    int x = sum[i * newW + j] / Math.max(1, cnt[i * newW + j]); // average color
                    res[i * newW + j] |= x << (8 * k);
                }
            }
        }
        return res;
    }

    // fast and bad compressing
    int[] compressFast(int[] colors, int w, int h, int newW, int newH, double scale) {
        int res[] = new int[newW * newH];

        for (int k = 0; k < 4; k++) {
            for (int i = 0; i < h; i++) {
                for (int j = 0; j < w; j++) {
                    int ni = (int)(i / scale);
                    int nj = (int)(j / scale);
                    res[ni * newW + nj] = colors[i * w + j];
                }
            }
        }

        return res;
    }

    // rotating by pi/2 clockwise
    int[] rotate(int[] colors, int w, int h) {
        int[] res = new int[colors.length];
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                res[j * h + (h - i - 1)] = colors[i * w + j];
            }
        }
        return res;
    }

    void brightening(int colors[]) {
        for (int i = 0; i < colors.length; i++) {
            int res = 0;
            for (int j = 0; j < 4; j++) {
                int x = (colors[i] >> (8 * j)) & 255;
                if (j < 3) {
                    x = Math.min((int)(x * 1.6 + 10), 255);
                }
                res |= x << (8 * j);
            }
            colors[i] = res;
        }
    }

    void doAll(Bitmap sourceBmp) {
        int w = sourceBmp.getWidth();
        int h = sourceBmp.getHeight();
        int colors[] = new int[w * h];
        sourceBmp.getPixels(colors, 0, w, 0, 0, w, h);

        colors = rotate(colors, w, h);
        int o = w;
        w = h;
        h = o;
        brightening(colors);

        int newW = (int)(w / scale + 1);
        int newH = (int)(h / scale + 1);
        int[] newColors;
        newColors = compressSlow(colors, w, h, newW, newH, scale);
        bmp[0] = Bitmap.createBitmap(newColors, 0, newW, newW, newH, Bitmap.Config.ARGB_8888);
        newColors = compressFast(colors, w, h, newW, newH, scale);
        bmp[1] = Bitmap.createBitmap(newColors, 0, newW, newW, newH, Bitmap.Config.ARGB_8888);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        long start = System.currentTimeMillis();
        super.onCreate(savedInstanceState);
        im = new ImageView(getApplicationContext());
        im.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                curBmp ^= 1;
                im.setImageBitmap(bmp[curBmp]);
                setContentView(im);
            }
        });

        doAll(BitmapFactory.decodeResource(this.getResources(), R.drawable.source));

        im.setScaleType(ImageView.ScaleType.CENTER);
        im.setImageBitmap(bmp[curBmp]);
        setContentView(im);
        long finish = System.currentTimeMillis();
        Log.v("", Long.toString(finish - start));
    }
}
