package ru.ifmo.md.lesson2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class MyView extends SurfaceView implements SurfaceHolder.Callback {
    private DrawerThread drawerThread;
    
    public MyView(Context context) {
        super(context);
        getHolder().addCallback(this);
    }


    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        Log.i("Drawing", "Surface created");
        drawerThread = new DrawerThread(R.drawable.source, getHolder());
        drawerThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        Log.d("Drawing", "Surface destroyed");
        boolean trying = true;
        while (trying) {
            try {
                drawerThread.join();
                trying = false;
            } catch (InterruptedException ignore) {

            }
        }
    }

    private class DrawerThread extends Thread {
        private final int drawableId;
        private final SurfaceHolder holder;

        private DrawerThread(int drawableId, SurfaceHolder holder) {
            this.drawableId = drawableId;
            this.holder = holder;
        }

        @Override
        public void run() {
            Bitmap bitmap = BitmapFactory.decodeResource(getContext().getResources(), drawableId);
            Log.d("Drawing", bitmap.getWidth() + " " + bitmap.getHeight());
            Canvas canvas = null;
            try {
                canvas = holder.lockCanvas(null);
                canvas.drawBitmap(bitmap, 0, 0, null);
            } catch (NullPointerException ignore) {

            } finally {
                if (canvas != null) {
                    holder.unlockCanvasAndPost(canvas);
                }
            }
        }
    }
}
