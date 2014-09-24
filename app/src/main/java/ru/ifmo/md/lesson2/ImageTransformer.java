package ru.ifmo.md.lesson2;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.Random;

/**
 * Created by thevery on 11/09/14.
 */
class ImageTransformer extends SurfaceView implements Runnable {

    class MeanColor {
        private int R, G, B, count;

        public MeanColor() {
            R = G = B = count = 0;
        }

        public void add(int color) {
            R += Color.red(color);
            G += Color.green(color);
            B += Color.blue(color);
            count++;
        }

        public int getMeanColor() {
            return Color.rgb(R / count, G / count, B / count);
        }
    }

    SurfaceHolder holder        = null;
    Thread thread               = null;
    volatile boolean running    = false;
    Bitmap bitmapToDraw         = null;
    Bitmap betterBitmapToDraw   = null;
    Rect rect                   = new Rect(0, 0, 434, 405);
    Bitmap sourceImage          = null;
    Boolean switcher            = false;

    public ImageTransformer(Context context) {
        super(context);
        holder = getHolder();
        Resources res = getResources();
        sourceImage = ((BitmapDrawable)res.getDrawable(R.drawable.source)).getBitmap();
        bitmapToDraw = Bitmap.createBitmap(405, 434, sourceImage.getConfig());
        for (int i = 0; i < 405; i++)
            for (int j = 0; j < 434; j++)
                bitmapToDraw.setPixel(i, j, sourceImage.getPixel(i * sourceImage.getWidth() / 405,
                        j * sourceImage.getHeight() / 434));

        betterBitmapToDraw = Bitmap.createBitmap(405, 434, sourceImage.getConfig());
        for (int i = 0; i < 405; i++)
            for (int j = 0; j < 434; j++) {
                MeanColor meanColor = new MeanColor();
                for (int i1 = i * sourceImage.getWidth() / 405; i1 < (i + 1) *
                        sourceImage.getWidth() / 405; i1++)
                    for (int j1 = j * sourceImage.getHeight() / 434; j1 < (j + 1) *
                            sourceImage.getHeight() / 434; j1++)
                        meanColor.add(sourceImage.getPixel(i1, j1));
                betterBitmapToDraw.setPixel(i, j, meanColor.getMeanColor());
            }
        bitmapToDraw = x2Value(bitmapToDraw);
        bitmapToDraw = rotate(bitmapToDraw);

        betterBitmapToDraw = x2Value(betterBitmapToDraw);
        betterBitmapToDraw = rotate(betterBitmapToDraw);

    }

    public Bitmap x2Value(Bitmap bitmap) {
        for (int i = 0; i < bitmap.getWidth(); i++)
            for (int j = 0; j < bitmap.getHeight(); j++) {
                float[] hsv = new float[3];
                Color.colorToHSV(bitmap.getPixel(i, j), hsv);
                hsv[2] *= 2;
                if (hsv[2] > 1) hsv[2] = 1;
                bitmap.setPixel(i, j, Color.HSVToColor(hsv));
            }
        return bitmap;
    }

    public Bitmap rotate(Bitmap bitmap) {
        Bitmap tmp = Bitmap.createBitmap(bitmap.getHeight(), bitmap.getWidth(), bitmap.getConfig());
        for (int i = 0; i < bitmap.getWidth(); i++)
            for (int j = 0; j < bitmap.getHeight(); j++) {
                tmp.setPixel(tmp.getWidth() - 1 - j,  i, bitmap.getPixel(i, j));
            }
        return tmp;
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
                Canvas canvas = holder.lockCanvas();
                draw(canvas);
                holder.unlockCanvasAndPost(canvas);
                long finishTime = System.nanoTime();
                Log.i("TIME", "Circle: " + (finishTime - startTime) / 1000000);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switcher ^= true;
        return super.onTouchEvent(event);
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawBitmap(switcher ? bitmapToDraw : betterBitmapToDraw, null, rect, null);

    }
}