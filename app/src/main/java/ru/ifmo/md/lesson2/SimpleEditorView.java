package ru.ifmo.md.lesson2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * @author volhovm
 *         Created on 9/21/14
 */

public class SimpleEditorView extends SurfaceView implements Runnable {
    private SurfaceHolder holder;
    private Canvas canvas;
    private Rect rect;
    Bitmap bitmap;
    Thread mainThread;
    boolean running = false;
    boolean sleeping = false;
    private int width, height;

    public SimpleEditorView(Context context) {
        super(context);
        holder = getHolder();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(bitmap, null, rect, null);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        width = w;
        height = h;
        initRect();
    }

    private void initRect() {
        double k;
        int shiftX = 0;
        int shiftY = 0;
        if ((double) width / (double) height < (double) bitmap.getWidth() / (double) bitmap.getHeight()) {
            k = (double) width / (double) bitmap.getWidth();
            shiftY = (height - (int) (bitmap.getHeight() * k)) / 2;
        } else {
            k = (double) height / (double) bitmap.getHeight();
            shiftX = (width - (int) (bitmap.getWidth() * k)) / 2;
        }
        rect = new Rect(shiftX, shiftY, (int) (bitmap.getWidth() * k) + shiftX, (int) (bitmap.getHeight() * k) + shiftY);
    }

    void setBitMap(Bitmap bitmap) {
        pause();
        this.bitmap = bitmap;
        initRect();
        resume();
    }

    @Override
    public void run() {
        while (running) {
            if (bitmap != null && holder.getSurface().isValid()) {
                canvas = holder.lockCanvas();
                canvas.drawColor(0xFFFFFFFF);
                onDraw(canvas);
                holder.unlockCanvasAndPost(canvas);
                sleeping = true;
                break;
            }
        }
    }

    public void resume() {
        running = true;
        sleeping = false;
        mainThread = new Thread(this);
        mainThread.start();
    }

    public void pause() {
        running = false;
        sleeping = false;
        try {
            if (mainThread != null) mainThread.join();
        } catch (InterruptedException ignore) {
        }
    }
}
