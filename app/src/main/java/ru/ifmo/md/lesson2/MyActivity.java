package ru.ifmo.md.lesson2;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import static java.lang.Math.min;
import static java.util.Collections.swap;

public class MyActivity extends Activity {
    boolean isGood;
    ImageView iv;
    Bitmap good;
    Bitmap bad;
    final double comp = 1.73;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_layout);
        iv = (ImageView) findViewById(R.id.imageView);
        Bitmap image = BitmapFactory.decodeResource(getResources(), R.drawable.source);
        iv.setImageBitmap(image);

        int w = image.getWidth();
        int h = image.getHeight();
        int[] colors = new int[w * h];
        image.getPixels(colors, 0, w, 0, 0, w, h);

        colors = changeImage(colors, w, h);
        int t = w;
        w = h;
        h = t;

        badCompression(colors, w, h);
        isGood = false;

        goodCompression(colors, w, h);

        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isGood) {
                    isGood = false;
                    iv.setImageBitmap(bad);
                } else {
                    isGood = true;
                    iv.setImageBitmap(good);
                }
            }
        });
    }


    //rotation and lightning
    int[] changeImage(int[] colors, int w, int h) {
        int a, r, g, b, c;
        int[] res = new int[w * h];

        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                c = colors[i * w + j];
                a = Color.alpha(c);
                r = min(255, Color.red(c) * 2);
                g = min(255, Color.green(c) * 2);
                b = min(255, Color.blue(c) * 2);

                res[j * h + (h - i - 1)] = Color.argb(a, r, g, b);
            }
        }

        return res;
    }


    //fast
    void badCompression(int[] colors, int w, int h) {
        int w2 = (int)(w / comp);
        int h2 = (int)(h / comp);
        int[] res = new int[w2 * h2];
        int i, j;

        for (int i2 = 0; i2 < h2; i2++) {
            for (int j2 = 0; j2 < w2; j2++) {
                i = (int) (i2 * comp);
                j = (int) (j2 * comp);

                if (i * w + j < w * h) {
                    res[i2 * w2 + j2] = colors[i * w + j];
                }
            }
        }

        bad = Bitmap.createBitmap(res, 0, w2, w2, h2, Bitmap.Config.ARGB_8888);
    }


    //slow
    void goodCompression(int[] colors, int w, int h) {
        int w2 = (int)(w / comp);
        int h2 = (int)(h / comp);

        int[] res = new int[w2 * h2];
        int[] a = new int[w2 * h2];
        int[] r = new int[w2 * h2];
        int[] g = new int[w2 * h2];
        int[] b = new int[w2 * h2];
        int[] kol = new int[w2 * h2];
        int x2, i2, j2, oldC, k, i, j;

        for (i = 0; i < w2 * h2; i++) {
            res[i] = 0;
            a[i] = 0;
            r[i] = 0;
            g[i] = 0;
            b[i] = 0;
            kol[i] = 0;
        }

        for (i = 0; i < h; i++) {
            for (j = 0; j < w; j++) {
                i2 = (int) (i / comp);
                j2 = (int) (j / comp);

                x2 = i2 * w2 + j2;
                if (x2 < w2 * h2) {
                    oldC = colors[i * w + j];
                    a[x2] += Color.alpha(oldC);
                    r[x2] += Color.red(oldC);
                    g[x2] += Color.green(oldC);
                    b[x2] += Color.blue(oldC);
                    kol[x2]++;
                }
            }
        }

        for (i2 = 0; i2 < h2; i2++) {
            for (j2 = 0; j2 < w2; j2++) {
                x2 = i2 * w2 + j2;
                k = kol[x2];
                res[x2] = Color.argb(a[x2] / k, r[x2] / k, g[x2] / k, b[x2] / k);
            }
        }

        good = Bitmap.createBitmap(res, 0, w2, w2, h2, Bitmap.Config.ARGB_8888);
    }
}
