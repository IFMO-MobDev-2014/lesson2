package ru.ifmo.md.lesson2;

import android.annotation.TargetApi;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.os.Build;
import android.view.SurfaceHolder;

/**
 * Created by german on 20.09.14.
 */
public class DrawThread extends Thread {
    private boolean running = false;
    private SurfaceHolder surfaceHolder = null;
    private Bitmap picture = null;

    private int width, height;
    private int [] pixels = null;
    // brightPixels[] can consist of negative rgb (else we can loose our colors when we will
    //                                              make bright down)
    private int [] brightPixels = null;

    public DrawThread(SurfaceHolder surfaceHolder, Resources resources) {
        this.surfaceHolder = surfaceHolder;
        picture = BitmapFactory.decodeResource(resources, R.drawable.source);
        width = picture.getWidth();
        height = picture.getHeight();
        pixels = new int[width * height];
        picture.getPixels(pixels, 0, width, 0, 0, width, height);
        brightPixels = pixels;
        turn90();
        makeBrightUp();
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    @Override
    public void run() {
        while (running) {
            if (surfaceHolder.getSurface().isValid()) {
                Canvas canvas = surfaceHolder.lockCanvas();
                if (canvas != null) {
                    draw(canvas);
                    surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
        }
    }

    private void draw(Canvas canvas) {
        synchronized (surfaceHolder) {
            canvas.drawColor(Color.BLACK);
            if (picture != null)canvas.drawBitmap(picture, 0, 0, null);
        }
    }

    public void makeBrightUp() {
        for (int i = 0; i < width * height; i++) {
            int curPixel = pixels[i];
            int a = (curPixel & 0xff000000) >> 24;
            int r = (curPixel & 0x00ff0000) >> 16;
            int g = (curPixel & 0x0000ff00) >> 8;
            int b = curPixel & 0x000000ff;

            r = r + 128;
            g = g + 128;
            b = b + 128;
            curPixel = (a << 24) | (r << 16) | (g << 8) | b;
            brightPixels[i] = curPixel;

            if (r > 0xff) r = 0xff;
            if (g > 0xff) g = 0xff;
            if (b > 0xff) b = 0xff;
            curPixel = (a << 24) | (r << 16) | (g << 8) | b;
            pixels[i] = curPixel;
        }
        picture = Bitmap.createBitmap(pixels, width, height, picture.getConfig());
    }

    public void turn90() {
        int [] newPixels = new int[width * height];
        int [] newBrightPixels = new int[width * height];
        int ptr = 0;
        for (int i = 0; i < width; i++) {
            for (int j = height - 1; j >= 0; j--) {
                newPixels[ptr] = pixels[j * width + i];
                newBrightPixels[ptr] = brightPixels[j * width + i];
                ptr++;
            }
        }

        int tmp = width;
        width = height;
        height = tmp;

        picture = Bitmap.createBitmap(newPixels, width, height, picture.getConfig());
        pixels = newPixels;
        brightPixels = newBrightPixels;
    }
}
