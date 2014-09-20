package ru.ifmo.md.lesson2;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class ImageMagic extends SurfaceView implements Runnable{
    SurfaceHolder holder;
    boolean running = true;
    Thread thread = null;
    int[] raw;
    int width;
    int height;

    ImageMagic(Context context, int[] rawImg, int w, int h) {
        super(context);
        holder = getHolder();
        raw = rawImg;
        width = w;
        height = h;
        initImage();
    }

    private void initImage() {
        float[] hsv = new float[3];
        for (int i = 0; i < width * height; i++) {
            Color.RGBToHSV((raw[i] >>> 16) & 255, (raw[i] >>> 8) & 255, raw[i] & 255, hsv);
            hsv[2] *= 2;
            raw[i] = Color.HSVToColor(hsv);
        }
    }

    public void resume() {
        running = true;
        thread = new Thread(this);
        thread.start();
    }

    public void pause() {
        running = false;
        try {
            thread.join();
        } catch (InterruptedException ignore) {}
    }

    public void run() {
        while (running) {
            if (holder.getSurface().isValid()) {
                Canvas canvas = holder.lockCanvas();
                onDraw(canvas);
                holder.unlockCanvasAndPost(canvas);
            }
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        canvas.drawBitmap(raw, 0, width, 0, 0, width, height, false, null);
    }
}
