package ru.ifmo.md.lesson2;

import android.annotation.TargetApi;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
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

    public DrawThread(SurfaceHolder surfaceHolder, Resources resources) {
        this.surfaceHolder = surfaceHolder;
        picture = BitmapFactory.decodeResource(resources, R.drawable.source);
        width = picture.getWidth();
        height = picture.getHeight();
        turn90();
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

    public void turn90() {
        int [] pixels = new int[width * height];
        picture.getPixels(pixels, 0, width, 0, 0, width, height);

        int [] newPixels = new int[width * height];
        int ptr = 0;
        for (int i = 0; i < width; i++) {
            for (int j = height - 1; j >= 0; j--) {
                newPixels[ptr++] = pixels[j * width + i];
            }
        }

        int tmp = width;
        width = height;
        height = tmp;

        picture = Bitmap.createBitmap(newPixels, width, height, picture.getConfig());
    }
}
