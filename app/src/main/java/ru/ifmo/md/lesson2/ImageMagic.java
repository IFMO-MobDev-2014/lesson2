package ru.ifmo.md.lesson2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.Allocation;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

public class ImageMagic extends SurfaceView implements Runnable{
    SurfaceHolder holder;
    boolean running = true;
    Thread thread = null;
    Bitmap img;
    Bitmap resized;
    int width;
    int height;
    Context context;
    Matrix matrix = new Matrix();
    boolean qualityFine = false;

    ImageMagic(Context c, Bitmap image) {
        super(c);
        holder = getHolder();
        img = image;
        width = image.getWidth();
        height = image.getHeight();
        context = c;
        img = increaseBrightness(img);
        initImage(qualityFine);
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            qualityFine = !qualityFine;
            initImage(qualityFine);
        }

        return true;
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

    private void initImage(boolean fine) {
//        long timeStart = System.currentTimeMillis();
        if (fine)
            resized = reduceImageFine(img, 405, 434);
        else
            resized = reduceImageFast(img, 405, 434);
//        long timeEnd = System.currentTimeMillis();
//        Toast t = Toast.makeText(context, Integer.toString((int) (timeEnd - timeStart)), Toast.LENGTH_SHORT);
        Toast t;
        if (fine)
            t = Toast.makeText(context, "Fine scale", Toast.LENGTH_SHORT);
        else
            t = Toast.makeText(context, "Fast scale", Toast.LENGTH_SHORT);
        t.show();
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
        canvas.drawBitmap(resized, matrix, null);
    }

    // nearest neighbor
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

    // bilinear interpolation
    public Bitmap reduceImageFine(Bitmap source, final int newWidth, final int newHeight) {
        int width = source.getWidth();
        int height = source.getHeight();
        int[] newRaw = new int[newWidth * newHeight],
              raw = new int[width * height];
        source.getPixels(raw, 0, width, 0, 0, width, height);

        int a, b, c, d, x, y, index;
        float x_ratio = ((float)(width - 1)) / newWidth;
        float y_ratio = ((float)(height - 1))/ newHeight;
        float x_diff, y_diff, blue, red, green;
        int offset = 0 ;
        for (int i = 0; i < newHeight;i++) {
            y = (int)(y_ratio * i);
            y_diff = (y_ratio * i) - y;
            for (int j = 0;j < newWidth;j++) {
                x = (int)(x_ratio * j);
                x_diff = (x_ratio * j) - x;
                index = (y * width + x);
                a = raw[index];
                b = raw[index + 1];
                c = raw[index + width];
                d = raw[index + width + 1];

                // Yb = Ab(1-w)(1-h) + Bb(w)(1-h) + Cb(h)(1-w) + Db(wh)
                blue = (a & 0xff) * (1 - x_diff) * (1 - y_diff) + (b & 0xff) * (x_diff) * (1 - y_diff) +
                        (c & 0xff) * (y_diff) * (1 - x_diff) + (d & 0xff) * (x_diff * y_diff);

                // Yg = Ag(1-w)(1-h) + Bg(w)(1-h) + Cg(h)(1-w) + Dg(wh)
                green = ((a >> 8) & 0xff) * (1 - x_diff) * (1 - y_diff) + ((b >> 8) & 0xff) * (x_diff) * (1 - y_diff) +
                        ((c >> 8) & 0xff) * (y_diff) * (1 - x_diff) + ((d >> 8) & 0xff) * (x_diff * y_diff);

                // Yr = Ar(1-w)(1-h) + Br(w)(1-h) + Cr(h)(1-w) + Dr(wh)
                red = ((a >> 16) & 0xff) * (1 - x_diff) * (1 - y_diff) + ((b >> 16) & 0xff) * (x_diff) * (1 - y_diff) +
                        ((c >> 16) & 0xff) * (y_diff) * (1 - x_diff) + ((d >> 16) & 0xff) * (x_diff * y_diff);

                newRaw[offset++] = 0xff000000 |  ((int)red << 16) | ((int)green << 8) | (int)blue;
            }
        }


        return Bitmap.createBitmap(newRaw, newWidth, newHeight, Bitmap.Config.ARGB_8888);
    }
}
