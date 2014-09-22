package ru.ifmo.md.lesson2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.File;


/**
 * Created by Alexey on 22.09.2014.
 */
public class WhirlView extends SurfaceView implements Runnable {
    static int width = 700,
            height = 750,
            nWidth = 405,
            nHeight = 434;
    static int[] temp = new int[nWidth * nHeight],
            fast = new int[nWidth * nHeight],
            fancy = new int[width * height];
    static int[] colors = new int[width * height];
    static Canvas canvas = new Canvas();
    SurfaceHolder holder;
    Thread thread = null;
    volatile boolean running = false;

    public WhirlView(Context context) {
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
        } catch (InterruptedException ignore) {
        }
    }

    public void Count() {
        System.out.println();
        getPixels();
        createImages();
    }

    @Override
    public void run() {
        Count();
        while (running) {
            if (holder.getSurface().isValid()) {
                long startTime = System.nanoTime();
                canvas = holder.lockCanvas();
                onDraw(canvas);
                holder.unlockCanvasAndPost(canvas);
                long finishTime = System.nanoTime();
                Log.i("TIME", "Circle: " + (finishTime - startTime) / 1000000);
            }
        }
    }


    int color,
            colorA,
            colorR,
            colorG,
            colorB;

    Bitmap bitmap = null;

    private void getPixels() {
        bitmap = BitmapFactory.decodeFile((new File("/sdcard/Download/source.png")).getAbsolutePath());
        for (int i = 0; i < height; i++)
            for (int j = 0; j < width; j++) {
                color = bitmap.getPixel(j, i);
                colorA = (color >> 24) & 0xff;
                colorR = (color >> 16) & 0xff;
                colorG = (color >> 8) & 0xff;
                colorB = color & 0xff;
                colors[i * width + j] = (colorA << 24) + (increesBrightness(colorR) << 16)
                        + (increesBrightness(colorG) << 8)
                        + increesBrightness(colorB);
            }

    }

    private static int increesBrightness(int c) {
        if (c * 2 > 0xff) return 0xff;
        return c * 2;
    }

    public void createImages() {
        float distanceX = (float) width / nWidth;
        float distanceY = (float) height / nHeight;
        float x = 0;
        float y;
        int counter = 0;
        for (int i = 0; i < nWidth; i++) {
            y = height;
            for (int j = 0; j < nHeight; j++) {
                y -= distanceY;
                fast[counter] = colors[(int) x + (((int) y) * width)];
                fancy[counter] = biPolInt((int) x, (int) y);

                counter++;
            }
            x += distanceX;
        }

    }

    int p0,
            p1,
            p2,
            p3,
            a,
            r,
            g,
            b;

    private int biPolInt(float x, float y) {
        if (x > 0 && y > 0 && x < width - 1 && y < height - 1) {
            p0 = colors[((int) (x - 1 + (y - 1) * width))];
            p1 = colors[((int) (x + 1 + (y - 1) * width))];
            p2 = colors[((int) (x - 1 + (y + 1) * width))];
            p3 = colors[((int) (x + 1 + (y + 1) * width))];
            a = (((p0 >> 24) & 0xff) + ((p1 >> 24) & 0xff) + ((p2 >> 24) & 0xff) + ((p3 >> 24) & 0xff)) / 4;
            r = (((p0 >> 16) & 0xff) + ((p1 >> 16) & 0xff) + ((p2 >> 16) & 0xff) + ((p3 >> 16) & 0xff)) / 4;
            g = (((p0 >> 8) & 0xff) + ((p1 >> 8) & 0xff) + ((p2 >> 8) & 0xff) + ((p3 >> 8) & 0xff)) / 4;
            b = ((p0 & 0xff) + (p1 & 0xff) + (p2 & 0xff) + (p3 & 0xff)) / 4;
            return (a << 24) + (r << 16) + (g << 8) + b;
        }
        return colors[((int) (x + y * width))];
    }

    @Override
    public void onDraw(Canvas canvas) {
        canvas.scale(1, 1);
        if (MyActivity.condition) temp = fast;
        else temp = fancy;
        canvas.drawBitmap(temp, 0, nHeight, 0, 0, nHeight, nWidth, false, null);
    }

}
