package ru.ifmo.md.lesson2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.Allocation;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class ImageMagic extends SurfaceView implements Runnable{
    SurfaceHolder holder;
    boolean running = true;
    Thread thread = null;
    Bitmap img;
    int width;
    int height;
    Context context;
    Matrix matrix = new Matrix();

    ImageMagic(Context c, Bitmap image) {
        super(c);
        holder = getHolder();
        img = image;
        width = image.getWidth();
        height = image.getHeight();
        context = c;
        initImage();
    }

    private void initImage() {
        Bitmap bright = Bitmap.createBitmap(img.getWidth(), img.getHeight(), img.getConfig());
        RenderScript brightRs = RenderScript.create(context);
        Allocation inAlloc = Allocation.createFromBitmap(brightRs, img);
        Allocation outAlloc = Allocation.createTyped(brightRs, inAlloc.getType());
        ScriptC_incBright brightScript = new ScriptC_incBright(brightRs);
        brightScript.set_outAlloc(outAlloc);
        brightScript.set_inAlloc(inAlloc);
        brightScript.set_gScript(brightScript);
        brightScript.invoke_filter();
        outAlloc.copyTo(bright);
        img = bright;
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
                Canvas canvas = holder.lockCanvas();
                onDraw(canvas);
                holder.unlockCanvasAndPost(canvas);
            }
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        canvas.drawBitmap(img, matrix, null);
    }
}
