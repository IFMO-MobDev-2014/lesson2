package ru.ifmo.md.lesson2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
/**
 * Created by vlad on 10.11.14.
 */
public class PhotoView extends SurfaceView implements Runnable {

    private static final int SRC_WIDTH = 700;
    private static final int SRC_HEIGHT = 750;

    private static final int DST_WIDTH = 405;
    private static final int DST_HEIGHT = 434;

    SurfaceHolder holder;

    Bitmap bitmap;

    Bitmap fastBitmap;
    Bitmap qualityBitmap;

    boolean quality = false;

    Thread thread = null;
    volatile boolean running = false;

    public PhotoView(Context context) {
        super(context);
        holder = getHolder();

        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.source);
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

    public void changeQuality() {
        quality = !quality;
        redraw();
    }

    public void run() {
        while (!holder.getSurface().isValid()) {
            try {
                Thread.sleep(16);
            } catch (InterruptedException ignore) {}
        }

        fast();
        quality();
    }

    public void fast() {
        int[] image = new int[SRC_WIDTH * SRC_HEIGHT];
        bitmap.getPixels(image, 0, SRC_WIDTH, 0, 0, SRC_WIDTH, SRC_HEIGHT);

        image = ImageUtils.fastResize(image, SRC_WIDTH, SRC_HEIGHT, DST_WIDTH, DST_HEIGHT);
        image = ImageUtils.rotate(image, DST_WIDTH, DST_HEIGHT);
        image = ImageUtils.doubleBrightness(image);

        fastBitmap = Bitmap.createBitmap(image, DST_HEIGHT, DST_WIDTH, Bitmap.Config.ARGB_8888);

        redraw();
    }

    public void quality() {
        int[] image = new int[SRC_WIDTH * SRC_HEIGHT];
        bitmap.getPixels(image, 0, SRC_WIDTH, 0, 0, SRC_WIDTH, SRC_HEIGHT);

        image = ImageUtils.qualityResize(image, SRC_WIDTH, SRC_HEIGHT, DST_WIDTH, DST_HEIGHT);
        image = ImageUtils.rotate(image, DST_WIDTH, DST_HEIGHT);
        image = ImageUtils.doubleBrightness(image);

        qualityBitmap = Bitmap.createBitmap(image, DST_HEIGHT, DST_WIDTH, Bitmap.Config.ARGB_8888);

        redraw();
    }

    private void redraw() {
        Rect src = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        Log.i("redraw", bitmap.getWidth() + " x " + bitmap.getHeight());
        Canvas canvas = holder.lockCanvas();
        if (quality) {
            if (qualityBitmap != null)
                canvas.drawBitmap(qualityBitmap, src, src, null);
        } else {
            if (fastBitmap != null)
                canvas.drawBitmap(fastBitmap, src, src, null);
        }
        holder.unlockCanvasAndPost(canvas);
    }
}
