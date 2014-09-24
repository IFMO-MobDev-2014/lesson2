package ru.ifmo.md.lesson2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by nagibator2005 on 2014-09-24.
 */
public class PicView extends SurfaceView implements Runnable {
    private Rect imgRect = new Rect(0, 0, 0, 0);
    private SurfaceHolder holder;
    private int scale = 2;
    volatile private boolean running = false;
    private ImageLoader imageLoader;
    private Thread thread = null;
    public int currentPic = 0;

    public PicView(Context context, ImageLoader loader) {
        super(context);
        holder = getHolder();
        imageLoader = loader;
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
                //updateField();
                draw(canvas);
                holder.unlockCanvasAndPost(canvas);
                long finishTime = System.nanoTime();
                //Log.i("TIME", "Circle: " + (finishTime - startTime) / 1000000);
/*                try {
                    Thread.sleep(16);
                } catch (InterruptedException ignore) {}//*/
            }
        }
    }

    @Override
    public void draw(Canvas canvas) {
        Bitmap bitmap;
        switch (currentPic) {
            case 0:
                bitmap = imageLoader.getFastImage();
            break;
            case 1:
                bitmap = imageLoader.getNiceImage();
            break;
            default:
                bitmap = null;
        }
        if (bitmap != null) {
            imgRect.right = bitmap.getWidth() * scale;
            imgRect.bottom = bitmap.getHeight() * scale;
            canvas.drawBitmap(bitmap, null, imgRect, null);
        }
    }
}
