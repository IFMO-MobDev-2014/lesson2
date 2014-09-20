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

import java.io.ByteArrayInputStream;
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

    private SurfaceHolder holder;
    private DrawThread drawThread;
    private Bitmap image;
    public ImagePrinter(Context context) {
        super(context);
        image = BitmapFactory.decodeResource(getResources(), R.drawable.source);
        image = BitmapHandler.processBitmap(image, true, 1.73);
        holder = getHolder();
    }

    @Override
    public void onSizeChanged(int w, int h, int oldW, int oldH) {
        repaint();
    }
    @Override
    public void onDraw(Canvas canvas) {
        float scaleX = ((float)getWidth()) / image.getWidth();
        float scaleY = ((float)getHeight()) / image.getHeight();
        //canvas.scale(Math.min(scaleX, scaleY), Math.min(scaleX, scaleY));
        canvas.drawBitmap(image, 0, 0, null);
    }
    public void resume() {
        drawThread = new DrawThread(this, holder);
        drawThread.setRunning(true);
        drawThread.start();
        repaint();
    }
    public void pause() {
        Log.i("WIDTH = " + image.getWidth() + "   ", "HEIGHT = " + image.getHeight());
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
