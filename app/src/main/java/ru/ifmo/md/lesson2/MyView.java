package ru.ifmo.md.lesson2;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import static java.lang.Math.sqrt;

import static android.graphics.BitmapFactory.decodeResource;

/**
 * Created by kano_vas on 23.09.14.
 */

public class MyView extends SurfaceView implements Runnable{

    SurfaceHolder holder;
    Thread thread = null;
    volatile boolean running = true;
    Bitmap pic;
    int c1, height, width, b, g, r;
    int[] pixels;
    Paint paint;
    Canvas canvas;

    public MyView(Context context) {
        super(context);
        holder = getHolder();
        
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

                pic = decodeResource(getResources(), R.drawable.source);
                width = pic.getWidth();
                height = pic.getHeight();
                pixels = new int[height * width];
                pic.getPixels(pixels, 0, width, 0, 0, width, height);
                paint = new Paint();

                long startTime = System.nanoTime();
                canvas = holder.lockCanvas();
                onDraw(canvas);
                holder.unlockCanvasAndPost(canvas);
                long finishTime = System.nanoTime();

                Log.i("TIME", "Circle: " + (finishTime - startTime) / 1000000);
                try {
                    Thread.sleep(1);
                } catch (InterruptedException ignore) {}
            }
        }
    }

    private int incBrightness(int x, int y) {
        c1 = pixels[x + y * width];

        b = c1 & 255;
        g = (c1 >> 8) & 255;
        r = (c1 >> 16) & 255;

        b = (int) (sqrt(b) * sqrt(255));
        g = (int) (sqrt(g) * sqrt(255));
        r = (int) (sqrt(r) * sqrt(255));

        return(0xff000000) | b | (g << 8) | (r << 16);
    }

    @Override
    public void onSizeChanged(int w, int h, int oldW, int oldH) {

    }

    @Override
    public void onDraw(Canvas canvas) {
        canvas.drawBitmap(pixels, 0, width, 0, 0, width, height, false, paint);
    }
}
