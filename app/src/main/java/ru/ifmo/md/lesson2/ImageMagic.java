package ru.ifmo.md.lesson2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.Allocation;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

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

    private Bitmap increaseBrightness(Bitmap bitmap) {
        Bitmap bright = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());
        RenderScript brightRs = RenderScript.create(context);
        Allocation inAlloc = Allocation.createFromBitmap(brightRs, bitmap);
        Allocation outAlloc = Allocation.createTyped(brightRs, inAlloc.getType());
        ScriptC_incBright brightScript = new ScriptC_incBright(brightRs);
        brightScript.set_outAlloc(outAlloc);
        brightScript.set_inAlloc(inAlloc);
        brightScript.set_gScript(brightScript);
        brightScript.invoke_filter();
        outAlloc.copyTo(bright);
        return bright;
    }

    private void initImage() {
//        long timeStart = System.currentTimeMillis();
        img = reduceImageFast(img, 405, 434);
//        long timeEnd = System.currentTimeMillis();
//        Toast t = Toast.makeText(context, Integer.toString((int) (timeEnd - timeStart)), Toast.LENGTH_SHORT);
//        t.show();
        img = increaseBrightness(img);
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

    private Bitmap reduceImageFast(Bitmap source, final int newWidth, final int newHeight) {
        final int[] dx = {0, 1, 0, 1},
                    dy = {0, 0, 1, 1};
        final int N = 4;
        Thread[] t = new Thread[N];

        final int width = source.getWidth();
        final int height = source.getHeight();
        final int[] newRaw = new int[newHeight * newWidth],
                raw = new int[width * height];
        source.getPixels(raw, 0, width, 0, 0, width, height);

        for (int i = 0; i < N; i++) {
            final int finalI = i;
            t[i] = new Thread(new Runnable() {
                @Override
                public void run() {
                    int oldX, offset;
                    for(int y = (newHeight >> 1) * dy[finalI]; y < (newHeight >> 1) * (dy[finalI] + 1); y++) {
                        offset = (int)Math.ceil((height - 1) * y / newHeight) * width;
                        for (int x = (newWidth >> 1) * dx[finalI]; x < (newWidth >> 1) * (dx[finalI] + 1); x++) {
                            oldX = (int)Math.ceil((width - 1) * x / newWidth);
                            newRaw[y * newWidth + x] = raw[offset + oldX];
                        }
                    }
                }
            });
        }

        for (int i = 0; i < N; i++)
            t[i].start();
        for (int i = 0; i < N; i++) {
            try {
                t[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return Bitmap.createBitmap(newRaw, newWidth, newHeight, Bitmap.Config.ARGB_8888);
    }
}
