package ru.ifmo.md.lesson2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import static android.graphics.Color.*;


/**
 * Created by vitalik on 24.09.14.
 */
class MySurfaceView extends SurfaceView implements Runnable {
    private boolean running = true;
    private Bitmap bitmap = null, compressedBitmap1 = null, compressedBitmap2 = null;
    private Thread thread = null;
    private boolean compressType = false;
    private int width = 700, compressedWidth = 434;
    private int height = 750, compressedHeight = 405 ;
    private int[] pixels = new int[width * height];
    private int[] pixels2 = new int[compressedWidth * compressedHeight];
    SurfaceHolder surfaceHolder;

    public void Change() {
        compressType = !compressType;
    }
    public MySurfaceView(Context context) {
        super(context);
        surfaceHolder = getHolder();
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.source);
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        makeBrighter();
        rotate();
        compressStupidWay();
        compressNotSoStupidWay();
    }

    float lerp(float s, float e, float t) {
        return s + (e - s) * t;
    }

    float blerp(float c00, float c10, float c01, float c11, float tx, float ty) {
        return lerp(lerp(c00, c10, tx), lerp(c01, c11, tx), ty);
    }

    private void compressNotSoStupidWay() {
        int x, y;
        for(x= 0, y=0; y < compressedHeight; x++){
            if(x > compressedWidth - 1){
                x = 0; y++;
            }
            float gx = x / (float)(compressedWidth) * (width - 1);
            float gy = y / (float)(compressedHeight) * (height - 1);
            int gxi = (int)gx;
            int gyi = (int)gy;
            if (gxi < 0) gxi = 0;
            if (gyi < 0) gyi = 0;
            if (gxi >= width - 1) gxi = width - 2;
            if (gyi >= height - 1) gyi = height - 2;
            int  result=0;
            int c00 = pixels[gyi * width + gxi];              //  getpixel(src, gxi, gyi);
            int c10 = pixels[(gyi) * width + gxi + 1];        //getpixel(src, gxi+1, gyi);
            int c01 = pixels[(((gyi + 1) * width) + gxi + 1)];          //getpixel(src, gxi, gyi+1);
            int c11 = pixels[(gyi + 1) * width + gxi + 1];    //getpixel(src, gxi+1, gyi+1);
            if (y * compressedWidth + x > 405 * 434 - 1) break;
            pixels2[y * compressedWidth + x] = Color.argb(255
                    , (int) blerp(red(c00), red(c10), red(c01), red(c11), gx - gxi, gy - gyi)
                    , (int) blerp(green(c00), green(c10), green(c01), green(c11), gx - gxi, gy - gyi)
                    , (int) blerp(blue(c00), blue(c10), blue(c01), blue(c11), gx - gxi, gy - gyi));
        }
        compressedBitmap2 = Bitmap.createBitmap(pixels2, compressedWidth, compressedHeight,bitmap.getConfig() );
    }


    public void resume() {
        running = true;
        thread = new Thread(this);
        thread.start();
    }
    private void makeBrighter() {
        for (int i = 0; i < width * height; i++) {
            int r, g, b;
            r = red(pixels[i]);
            g = green(pixels[i]);
            b = blue(pixels[i]);
            r =  Math.min(r * 2, 255);
            g = Math.min(g * 2, 255);
            b = Math.min(b * 2, 255);
            pixels[i] = argb(255, r, g, b);
        }
        bitmap = Bitmap.createBitmap(pixels, width, height, bitmap.getConfig());
        //bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
    }
    private void rotate() {
        int[] pixels2 = new int[width * height];
        for (int i = 0; i < width ; i++) {
            for (int j= 0; j < height; j++) {
                pixels2[height*i + j] = pixels[width * (height - 1 - j) + i];
            }
        }
        pixels = pixels2;
        bitmap = Bitmap.createBitmap(pixels2, height, width, bitmap.getConfig());
        int t = width;
        width = height;
        height = t;
    }

    private void compressStupidWay() {
        for (int i = 0; i < compressedHeight; i++) {
            for (int j = 0; j < compressedWidth; j++) {
                int x = (int) Math.max((j - 1) * 1.73 + 1, 0.);
                int y = (int) Math.max((i - 1) * 1.73 + 1, 0);
//                int color = pixels[y * width + x] + pixels[y * width + x + 1] + pixels[(y + 1) * width + x] + pixels[(y + 1) * width + x + 1];
//                color /= 4;
                int color = pixels[y * width + x];
                pixels2[i * compressedWidth + j] = color;
            }
        }
        compressedBitmap1 = Bitmap.createBitmap(pixels2, compressedWidth, compressedHeight,bitmap.getConfig() );
    }

    public void pause() {
        running = false;
        try {
            thread.join();
        } catch (InterruptedException ignore) {
        }
    }

    public void run() {
        Canvas canvas;
        while (running) {
            canvas = null;
            if (surfaceHolder.getSurface().isValid()) {
                canvas = surfaceHolder.lockCanvas();
                canvas.drawColor(BLACK);
                if (compressedBitmap1 != null && compressType) canvas.drawBitmap(compressedBitmap1, 0, 0, null);
                if (compressedBitmap2 != null && !compressType) canvas.drawBitmap(compressedBitmap2, 0, 0, null);
               // if (bitmap != null) canvas.drawBitmap(bitmap,0 , 1000, null);
                surfaceHolder.unlockCanvasAndPost(canvas);
            }
        }
    }
}
