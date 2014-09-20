package ru.ifmo.md.lesson2;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by anton on 20/09/14.
 */
public class RotateView extends SurfaceView implements Runnable {
    Thread thread = null;
    volatile boolean running = false;
    Bitmap source = null;
    SurfaceHolder holder;

    public RotateView(Context context) {
        super(context);
        holder = getHolder();

        source = loadDrawable("source.png", context);
    }

    // load drawable file from resources and create a Bitmap out of it
    public static Bitmap loadDrawable(String name, Context context) {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier(name, "drawable", context.getApplicationInfo().packageName);
        return BitmapFactory.decodeResource(resources, resourceId);
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
        } catch (InterruptedException ignore) {
        }
    }

    public void run() {
        /*while (running) {
            if (holder.getSurface().isValid()) {

            }
        }*/
    }

    @Override
    public void onSizeChanged(int w, int h, int oldW, int oldH) {
        initScreen();
    }

    public void initScreen() {

    }

    @Override
    public void onDraw(Canvas canvas) {

    }
}
