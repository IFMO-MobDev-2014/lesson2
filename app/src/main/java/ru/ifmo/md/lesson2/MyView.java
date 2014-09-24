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
    Thread modifyLight = null;
    Thread modifyHard = null;
    volatile boolean running = true;
    Bitmap pic;
    int c1, height, width, b, g, r, mode;
    int[] pixels, copy;
    Paint paint;
    Canvas canvas;
    Modify calc1;
    Modify calc2;
    private final int w = 434;
    private final int h = 405;

    public MyView(Context context) {
        super(context);
        holder = getHolder();

        mode = 0;
        pic = decodeResource(getResources(), R.drawable.source);
        width = pic.getWidth();
        height = pic.getHeight();
        pixels = new int[height * width];
        copy = new int[height * width];
        pic.getPixels(pixels, 0, width, 0, 0, width, height);
        //incBrightnessAndRotate();

        calc1 = new Modify(pixels, width, height, 'e');
        calc2 = new Modify(pixels, width, height, 'h');
        modifyLight = new Thread(calc1);
        modifyHard = new Thread(calc2);
        modifyLight.start();
        modifyHard.start();
        try {
            modifyLight.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            modifyHard.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
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

                long startTime = System.nanoTime();
                canvas = holder.lockCanvas();
                paint = new Paint();
                onDraw(canvas);
                holder.unlockCanvasAndPost(canvas);
                long finishTime = System.nanoTime();

                Log.i("TIME", "drawing " + (finishTime - startTime) / 1000000);
                try {
                    Thread.sleep(1);
                } catch (InterruptedException ignore) {}
            }
        }
    }

    private void incBrightnessAndRotate() {
        System.arraycopy(pixels, 0, copy, 0, width * height);
        for(int x = 0; x < width; x++) {
            for(int y = 0; y < height; y++) {
                c1 = pixels[x + y * width];

                b = c1 & 255;
                g = (c1 >> 8) & 255;
                r = (c1 >> 16) & 255;

                b = (int) (sqrt(b) * sqrt(255));
                g = (int) (sqrt(g) * sqrt(255));
                r = (int) (sqrt(r) * sqrt(255));

                copy[(height - y - 1) + x * height] = (0xff000000) | b | (g << 8) | (r << 16);
            }
        }
        c1 = width;
        width = height;
        height = c1;
        pixels = copy;
    }

    @Override
    public void onDraw(Canvas canvas) {
        if(mode < 1) {
            canvas.drawBitmap(calc2.res, 0, w, 0, 0, w, h, false, paint);
        }
        else {
            canvas.drawBitmap(calc1.res, 0, w, 0, 0, w, h, false, paint);
        }
    }
}
