package ru.ifmo.md.lesson2;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.widget.ImageView;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

class Image extends SurfaceView implements Runnable{

    Bitmap bitmap;
    public static final int startWidth = 750;
    public static final int startHeight = 700;
    public static final int finalWidth = 434;
    public static final int finalHeight = 405;
    public static final double scale = 1.73;
    boolean fast = false;

    private int[] pixels = new int[startWidth * startHeight];
    private int[] finalPixelsSlow = new int[finalWidth * finalHeight];
    private int[] finalPixelsFast = new int[finalWidth * finalHeight];

    SurfaceHolder holder;
    Canvas canvas = null;
    Thread thread = null;

    public Image(Context context) {
        super(context);
        holder = getHolder();
    }

    public void resume() {
        thread = new Thread(this);
        thread.start();
    }

    public void pause() {
        try {
            thread.join();
        } catch (InterruptedException ignore) {}
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            fast = !fast;
            if (holder.getSurface().isValid()) {
                canvas = holder.lockCanvas();
                if (fast) {
                    //Log.i("new mode:", " fast");
                    draw(canvas, finalPixelsFast);
                } else {
                    //Log.i("new mode:", " slow");
                    draw(canvas, finalPixelsSlow);
                }
                holder.unlockCanvasAndPost(canvas);
            }
        }
        return true;
    }

    public void run() {
        init();
        fastScale();
        slowScale();
        if (holder.getSurface().isValid()) {
            canvas = holder.lockCanvas();
            if (fast) {
                //Log.i("new mode:", " fast");
                draw(canvas, finalPixelsFast);
            } else {
                //Log.i("new mode:", " slow");
                draw(canvas, finalPixelsSlow);
            }
            holder.unlockCanvasAndPost(canvas);
        }
    }

    private void init() {
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.source);
        rotate(startWidth, startHeight);
        brightness(startWidth, startHeight);
    }

    private void brightness(int width, int height) {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pix = pixels[x + y * width];
                int red = Color.red(pix);
                int green = Color.green(pix);
                int blue = Color.blue(pix);
                int alpha = Color.alpha(pix);
                red <<= 1;
                green <<= 1;
                blue <<= 1;
                pixels[x + y * width] = Color.argb(alpha, Math.min(red, 255), Math.min(green, 255), Math.min(blue, 255));
            }
        }
    }

    private void rotate(int width, int height) {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int xx = startWidth - 1 - x;
                pixels[xx + y * startWidth] = bitmap.getPixel(y, x);
            }
        }
    }

    private void fastScale() {
        for (int y = 0; y < finalHeight; y++) {
            for (int x = 0; x < finalWidth; x++) {
                int xx = (int) Math.floor(x * scale);
                int yy = (int) Math.floor(y * scale);
                finalPixelsFast[x + y * finalWidth] = pixels[xx + yy * startWidth];
            }
        }
    }

    private void slowScale() {
        for (int y = 0; y < finalHeight; y++) {
            for (int x = 0; x < finalWidth; x++) {
                int numberOfNeighbors = 0;
                int sumRed = 0;
                int sumGreen = 0;
                int sumBlue = 0;
                int sumAlpha = 0;
                int oldX = (int) (x * scale);
                int oldY = (int) (y * scale);
                for (int dx = -1; dx <= 1; dx++) {
                    for (int dy = -1; dy <= 1; dy++) {
                        if ((oldX + dx >= 0) && (oldX + dx < startWidth) && (oldY + dy >= 0) && (oldY + dy < startHeight)) {
                            numberOfNeighbors++;
                            sumRed += Color.red(pixels[oldX + dx + (oldY + dy) * startWidth]);
                            sumGreen += Color.green(pixels[oldX + dx + (oldY + dy) * startWidth]);
                            sumBlue += Color.blue(pixels[oldX + dx + (oldY + dy) * startWidth]);
                            sumAlpha += Color.alpha(pixels[oldX + dx + (oldY + dy) * startWidth]);
                        }
                    }
                }
                sumRed /= numberOfNeighbors;
                sumGreen /= numberOfNeighbors;
                sumBlue /= numberOfNeighbors;
                sumAlpha /= numberOfNeighbors;
                finalPixelsSlow[x + y * finalWidth] = Color.argb(sumAlpha, sumRed, sumGreen, sumBlue);
            }
        }
    }

    public void draw(Canvas canvas, int[] finalPixels) {
        canvas.drawBitmap(finalPixels, 0, finalWidth, 0, 0, finalWidth, finalHeight, false, null);
    }
}
