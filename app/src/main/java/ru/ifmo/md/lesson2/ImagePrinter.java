package ru.ifmo.md.lesson2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.media.Image;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;


/**
 * Created by Женя on 20.09.2014.
 */


public class ImagePrinter extends SurfaceView {
    private class DrawThread extends Thread{
        private ImagePrinter printer;
        private SurfaceHolder holder;
        private boolean isRunning;

        public DrawThread(ImagePrinter printer, SurfaceHolder holder) {
            this.printer = printer;
            this.holder = holder;
        }
        public void setRunning(boolean running) {
            isRunning = running;
        }
        @Override
        public void run() {
            while (isRunning) {
               while (!isInterrupted()) {
                    try {
                        sleep(100);
                    } catch (InterruptedException e) {}
                }
               synchronized (printer.colors) {
                synchronized (holder) {
                    if (holder.getSurface().isValid()) {
                        Canvas canvas = holder.lockCanvas();
                        printer.onDraw(canvas);
                        holder.unlockCanvasAndPost(canvas);
                    }
                }
               }
            }
        }
    }

    private Bitmap image;
    private SurfaceHolder holder;
    private DrawThread drawThread;
    private int width;
    private int height;
    private int[] colors;

    public ImagePrinter(Context context) {
        super(context);
        image = BitmapFactory.decodeResource(getResources(), R.drawable.source);
        width = image.getWidth();
        height = image.getHeight();
        int size = image.getWidth() * image.getHeight();
        colors = new int[size];
        image.getPixels(colors, 0, image.getWidth(), 0, 0, image.getWidth(), image.getHeight());
        turnAndIncreaseBrightness(30);
        holder = getHolder();
    }
    private int increaseBrightness(int clr, int value) {
        int A, R, G, B;
        A = Color.alpha(clr);
        B = Color.blue(clr);
        G = Color.green(clr);
        R = Color.red(clr);
        A = Math.min(255, A + value);
        B = Math.min(255, B + value);
        R = Math.min(255, R + value);
        G = Math.min(255, G + value);
        return Color.argb(A, R, G, B);
    }

    private void turnAndIncreaseBrightness(int value) {
        int[] turnColors;
        turnColors = new int[colors.length];
        for (int i = 0; i < colors.length; i++) {
            int x = i % width;
            int y = i / width;
            int nx = height - y - 1;
            int ny = x;
            turnColors[ny * height + nx] = increaseBrightness(colors[i], value);
        }
        int t = width;
        width = height;
        height = t;
        colors = turnColors;
    }
    @Override
    public void onSizeChanged(int w, int h, int oldW, int oldH) {
        repaint();
    }
    @Override
    public void onDraw(Canvas canvas) {
        float scaleX = ((float)getWidth()) / width;
        float scaleY = ((float)getHeight()) / height;
        canvas.scale(Math.min(scaleX, scaleY), Math.min(scaleX, scaleY));
        canvas.drawBitmap(colors, 0, width, 0, 0, width, height, false, null);
    }
    public void resume() {
        drawThread = new DrawThread(this, holder);
        drawThread.setRunning(true);
        drawThread.start();
        repaint();
    }
    public void pause() {
        drawThread.setRunning(false);
        try {
            drawThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public void repaint() {
        drawThread.interrupt();
    }
}
