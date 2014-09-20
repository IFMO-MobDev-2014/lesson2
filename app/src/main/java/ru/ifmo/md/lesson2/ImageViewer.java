package ru.ifmo.md.lesson2;

import android.content.Context;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by german on 19.09.14.
 */
public class ImageViewer extends SurfaceView implements SurfaceHolder.Callback {
    private DrawThread drawThread = null;

    public ImageViewer(Context context) {
        super(context);
        getHolder().addCallback(this);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        drawThread = new DrawThread(getHolder(), getResources());
        drawThread.setRunning(true);
        drawThread.start();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        drawThread.setRunning(false);
        try {
            drawThread.join();
        } catch (InterruptedException ignore) {}
    }

    public void changeCompress() {
        drawThread.changeCompress();
    }
}
