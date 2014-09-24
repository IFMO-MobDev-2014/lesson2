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
    ImageView img;
    Bitmap sbm, bm;
    boolean mode;
    int swidth;
    int sheigth;
    double scale;
    int[] scolors;


    private int[] lowResize(int[] colors, int width, int height, double scale) {//low quality, but fast
        int w = 1 + (int)((width - 1) / scale);
        int h = 1 + (int)((height - 1) / scale);
        int[] c = new int[w * h];
        int i1, j1, i, j;
        for (i = 0; i < h; i++) {
            for (j = 0; j < w; j++) {
                i1 = (int)(i * scale);
                j1 = (int)(j * scale);
                c[i * w + j] = colors[i1 * width + j1];
            }
        }
        return c;
    }

    private int[] highResize(int[] colors, int width, int height, double scale) {//high quality
        int w = 1 + (int)((width - 1) / scale);
        int h = 1 + (int)((height - 1) / scale);
        int[] c = new int[w * h];
        int i1, j1, i, j, cnt;
        int[] p = new int[4];
        for (i = 0; i < h; i++) {
            for (j = 0; j < w; j++) {
                c[i * w + j] = 0;
                for (int k = 0; k < 4; k++) {
                    p[k] = 0;
                    cnt = 0;
                    for (i1 = (int)(i * scale); i1 < (int)((i + 1) * scale); i1++) {
                        for (j1 = (int)(j * scale); j1 < (int)((j + 1) * scale); j1++) {
                            p[k] += (((colors[i1 * width + j1] & (255 << (k * 8)))) >> (k * 8));
                            cnt++;
                        }
                    }
                    p[k] = p[k] / cnt;
                    c[i * w + j] += (p[k] << (k * 8));
                }
            }
        }



        return c;
    }

    private int[] rotate(int colors[], int width, int heigth) {
        int[] ncolors = new int[colors.length];
        for (int i = 0; i < heigth; i++) {
            for (int j = 0; j < width; j++) {
                ncolors[(j + 1) * heigth - i - 1] = colors[i * width + j];
            }
        }
        return ncolors;
    }

    private void brightUp(int colors[]) {
        int[] c = new int[3];
        for (int i = 0; i < colors.length; i++) {
            c[0] = (colors[i] & 0x00FF0000) >> 16;
            c[1] = (colors[i] & 0x0000FF00) >> 8;
            c[2] = (colors[i] & 0x000000FF);
            for (int j = 0; j < 3; j++) {
                c[j] += (255 - c[j]) / 3;//why not?
            }
            colors[i] = (colors[i] & 0xFF000000) | (c[0] << 16) | (c[1] << 8) | c[2];
        }
    }

    public void go() {
        long startTime = System.nanoTime();
        int[] colors;
        int width = swidth;
        int heigth = sheigth;
        if (mode) {
            colors = highResize(scolors, width, heigth, scale);
            mode = false;
        }
        else {
            colors = lowResize(scolors, width, heigth, scale);
            mode = true;
        }
        width = 1 + (int)((width - 1) / scale);
        heigth = 1 + (int)((heigth - 1) / scale);

        colors = rotate(colors, width, heigth);
        int tmp = heigth;
        heigth = width;
        width = tmp;

        brightUp(colors);
        bm = Bitmap.createBitmap(colors, 0, width, width, heigth, Bitmap.Config.ARGB_8888);
        img.setImageBitmap(bm);
        setContentView(img);
        long finishTime = System.nanoTime();
        Log.i("OPA", "GO TIME: " + (finishTime - startTime) / 1000000);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mode = false;
        scale = 1.73;

        long startTime = System.nanoTime();
        super.onCreate(savedInstanceState);
        sbm = BitmapFactory.decodeResource(this.getResources(), R.drawable.source);
        swidth = sbm.getWidth();
        sheigth = sbm.getHeight();
        scolors = new int[sheigth * swidth];
        sbm.getPixels(scolors, 0, swidth, 0, 0, swidth, sheigth);

        img = new ImageView(getApplicationContext());
        img.setScaleType(ImageView.ScaleType.CENTER);

        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                go();
            }
        });

        go();

        long finishTime = System.nanoTime();
        Log.i("OPA", "TIME: " + (finishTime - startTime) / 1000000);
    }


}
