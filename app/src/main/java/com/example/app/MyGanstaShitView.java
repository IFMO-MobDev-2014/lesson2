package com.example.app;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

class MyGanstaShitView extends View {
    private static Bitmap easy;
    private static Bitmap hard;
    private int st = 0;
    private int[] pixels;
    private int w, h;

    private double scaleX = 1.73;
    private double scaleY = 1.73;

    public MyGanstaShitView(Context con) {
        super(con);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                st ^= 1;
                invalidate();
            }
        });

        Bitmap pic = BitmapFactory.decodeResource(getResources(), R.drawable.source);
        w = pic.getWidth();
        h = pic.getHeight();
        pixels = new int[w * h];
        pic.getPixels(pixels, 0, w, 0, 0, w, h);
        hard = Bitmap.createBitmap(pixels, w, h, Bitmap.Config.ARGB_8888);
        int newW = (int)(w / scaleX);
        int newH = (int)(h / scaleY);
        int[] nPixEasy = new int[newW * newH];
        int[] nPixHard = new int[newW * newH];

        long l = System.currentTimeMillis();
        for (int j = 0; j < newH; ++j) {
            int bh = (int)((j + 1) * scaleY);
            int clr = getPixel(0, bh);
            int pred = (clr>>16)&255, pgreen = (clr>>8)&255, pblue = clr&255, pal = clr>>24;
            int nxcl = getPixel(0, bh - 1);
            pred += (nxcl>>16)&255;
            pgreen += (nxcl>>8)&255;
            pblue += nxcl&255;
            pal += nxcl>>24;

            for (int i = 0; i < newW; ++i) {
                int bw = (int)((i + 1) * scaleX);
                int cl = getPixel(bw, bh);
                int cnl = getPixel(bw, bh - 1);
                int cal = (cl>>24) + (cnl>>24);
                int cred = ((cl>>16)&255) + ((cnl>>16)&255);
                int cgr = ((cl>>8)&255) + ((cnl>>8)&255);
                int cbl = (cl&255) + (cnl&255);
                nPixEasy[j * newW + i] = cl;
                nPixHard[j * newW + i] = Color.argb((pal + cal) / 4, (pred + cred) / 4, (pgreen + cgr) / 4, (pblue + cbl) / 4);

                if (bw == (int)((i + 2) * scaleX) - 1) {
                    pal = cal;
                    pblue = cbl;
                    pgreen = cgr;
                    pred = cred;
                } else {
                    cl = getPixel((int)((i + 2) * scaleX) - 1, bh);
                    nxcl = getPixel((int)((i + 2) * scaleX) - 1, bh - 1);
                    pal = (cl>>24) + (nxcl>>24);
                    pred = ((cl>>16)&255) + ((nxcl>>16)&255);
                    pgreen = ((cl>>8)&255) + ((nxcl>>8)&255);
                    pblue = (cl&255) + (nxcl&255);
                }
            }
        }
        nPixEasy = al(rotate(nPixEasy, newW, newH));
        Log.i("TIME", "CYCL " + (System.currentTimeMillis() - l) / 1000.0);
        nPixHard = al(rotate(nPixHard, newW, newH));
        easy = Bitmap.createBitmap(nPixEasy, newH, newW, Bitmap.Config.ARGB_8888);
        hard = Bitmap.createBitmap(nPixHard, newH, newW, Bitmap.Config.ARGB_8888);
    }


    public int[] rotate(int[] a, int w, int h) {
        int[] ret = new int[w * h];
        for (int i = 0; i < w; ++i)
            for (int j = 0; j < h; ++j)
                ret[(h - j - 1) + i * h] = a[j * w + i];
        return ret;
    }

    public int[] al(int ar[]) {
        final double MAGIC = 2.0;
        int[] ret = new int[ar.length];
        for (int i = 0; i < ar.length; ++i) {
            int a = Color.alpha(ar[i]);
            int r = Color.red(ar[i]);
            int g = Color.green(ar[i]);
            int b = Color.blue(ar[i]);
            r = (int)Math.min(MAGIC * r, 255);
            g = (int)Math.min(MAGIC * g, 255);
            b = (int)Math.min(MAGIC * b, 255);
            ret[i] = Color.argb(a, r, g, b);
        }
        return ret;
    }

    public int getPixel(int x, int y) {
        if (x < 0 || y < 0 || x >= w || y >= h)
            return 0;
        return pixels[y * w + x];
    }

    @Override
    public void onDraw(Canvas canvas) {
        canvas.save();
        if (st == 0)
            canvas.drawBitmap(easy, null, new Rect(0, 0, easy.getWidth(), easy.getHeight()), null);
        else
            canvas.drawBitmap(hard, null, new Rect(0, 0, hard.getWidth(), hard.getHeight()), null);
        canvas.restore();
    }
}