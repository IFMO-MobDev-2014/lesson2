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


public class ImagePrinter extends View {
    private class DrawThread extends Thread{
        private ImagePrinter printer;
        private boolean isRunning;

        public DrawThread(ImagePrinter printer) {
            this.printer = printer;
        }
        public void setRunning(boolean running) {
            isRunning = running;
        }
        @Override
        public void run() {
            while (isRunning) {
               while (!interrupted()) {
                    try {
                        sleep(1000);
                    } catch (InterruptedException e) {break;}
                }
                synchronized (printer) {
                    printer.postInvalidate();
                }
            }
        }
    }

    private DrawThread drawThread;
    private Bitmap image;
    private Bitmap qualityImage;
    private Bitmap fastImage;
    private boolean isQuality = false;
    public ImagePrinter(Context context) {
        super(context);
        image = BitmapFactory.decodeResource(getResources(), R.drawable.source);
        qualityImage = BitmapHandler.processBitmap(image, false, 1.73);
        fastImage = BitmapHandler.processBitmap(image, true, 1.73);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                isQuality ^= true;
                repaint();
            }
        });
    }

    @Override
    public void onSizeChanged(int w, int h, int oldW, int oldH) {
        repaint();
    }
    @Override
    public void onDraw(Canvas canvas) {
        Bitmap image = isQuality ? qualityImage : fastImage;
        float x = ((float)getWidth() - image.getWidth()) / 2;
        float y = ((float)getHeight() - image.getHeight()) / 2;
        canvas.drawBitmap(image, x, y, null);
    }
    public void resume() {
        drawThread = new DrawThread(this);
        drawThread.setRunning(true);
        drawThread.start();
        repaint();
    }
    public void pause() {
        drawThread.setRunning(false);
        drawThread.interrupt();
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
