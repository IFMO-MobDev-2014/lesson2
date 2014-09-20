package ru.ifmo.md.lesson2;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

/**
 * Created by sultan on 19.09.14.
 */
public class WhirlView extends SurfaceView implements Runnable {

    SurfaceHolder holder;
    Bitmap bitmapSource = null;
    Bitmap bitmap = null;
    Bitmap bitmapCompressed = null;
    Thread thread = null;
    int[] pixels = null;
    int[] pixelsCompressed = null;
    Resources res = null;
    static final int brightnessValue = 2;
    static final double compressValue = 1.73;
    boolean fast = true;
    volatile boolean running = false;
    private OnClickListener mClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            fast = !fast;
            if (fast) {
                compressImageFast();
                Log.i("CLICK", "Turn Fast Mode On");
            } else {
                compressImageSlow();
                Log.i("CLICK", "Turn Fast Mode Off");
            }
        }
    };

    public WhirlView(Context context) {
        super(context);
        this.setOnClickListener(mClickListener);
        initField();
    }

    private void initField() {
        holder = getHolder();
        res = getResources();
        bitmapSource = BitmapFactory.decodeResource(res, R.drawable.source).copy(Bitmap.Config.ARGB_8888, false);
        pixels = new int[bitmapSource.getHeight() * bitmapSource.getWidth()];
        bitmap = Bitmap.createBitmap(bitmapSource.getHeight(), bitmapSource.getWidth(), Bitmap.Config.ARGB_8888);
        bitmapCompressed = Bitmap.createBitmap((int) (bitmap.getWidth()/compressValue), (int)(bitmap.getHeight()/compressValue), Bitmap.Config.ARGB_8888);
        pixelsCompressed = new int[bitmapCompressed.getHeight() * bitmapCompressed.getWidth()];
        turnBitmap();
        increaseBitmapBrightness(bitmap);
        compressImageFast();
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


    private void compressImageFast() {
        int nX;
        int nY;
        int pixel;
        for (int y = 0; y < bitmap.getHeight(); y++) {
            for (int x = 0; x < bitmap.getWidth(); x++) {
                pixel = pixels[x + y*bitmap.getWidth()];
                nX = Math.min((int) (x / compressValue), bitmapCompressed.getWidth() - 1);
                nY = Math.min((int) (y / compressValue), bitmapCompressed.getHeight() - 1);
                pixelsCompressed[nX + nY*bitmapCompressed.getWidth()] = pixel;
            }
        }
        bitmapCompressed.setPixels(pixelsCompressed, 0, bitmapCompressed.getWidth(), 0, 0, bitmapCompressed.getWidth(), bitmapCompressed.getHeight());
    }

    private int getGoodPixel(int x, int y) {
        int nx = (int) (x * compressValue);
        int ny = (int) (y * compressValue);
        int sumPixelsRed = 0;
        int sumPixelsGreen = 0;
        int sumPixelsBlue = 0;
        int sumPixelsAlpha = 0;
        int pixel = 0;
        int countPixels = 0;
        for (int dx = -1; dx != 1; dx++) {
            for (int dy = -1; dy != 1; dy++) {
                if (dx+nx >= 0 && dx+nx < bitmap.getWidth() && dy+ny >= 0 && dy+ny < bitmap.getHeight()) {
                    pixel = bitmap.getPixel(dx+nx, dy+ny);
                    sumPixelsAlpha += Color.alpha(pixel);
                    sumPixelsRed += Color.red(pixel);
                    sumPixelsGreen += Color.green(pixel);
                    sumPixelsBlue += Color.blue(pixel);
                    countPixels++;
                }
            }
        }
        sumPixelsAlpha = (int) ((double)sumPixelsAlpha/countPixels);
        sumPixelsRed = (int) ((double)sumPixelsRed/countPixels);
        sumPixelsGreen = (int) ((double)sumPixelsGreen/countPixels);
        sumPixelsBlue = (int) ((double)sumPixelsBlue/countPixels);
        return Color.argb(sumPixelsAlpha, sumPixelsRed, sumPixelsGreen, sumPixelsBlue);
    }

    private void compressImageSlow() {
        for (int y = 0; y < bitmapCompressed.getHeight(); y++) {
            for (int x = 0; x < bitmapCompressed.getWidth(); x++) {
                pixelsCompressed[x + y*bitmapCompressed.getWidth()] = getGoodPixel(x, y);
            }
        }
        bitmapCompressed.setPixels(pixelsCompressed, 0, bitmapCompressed.getWidth(), 0, 0, bitmapCompressed.getWidth(), bitmapCompressed.getHeight());
    }

    private void turnBitmap() {
        for (int x = 0; x < bitmapSource.getWidth(); x++) {
            for (int y = 0; y < bitmapSource.getHeight(); y++) {
                int pixel = bitmapSource.getPixel(x, y);
                int nX = bitmapSource.getHeight() - y - 1;
                int nY = x;
                pixels[nX + nY*bitmapSource.getHeight()] = pixel;
            }
        }
        bitmap.setPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
    }

    private int checkedColor(int color) {
        if (color > 255) {
            return 255;
        } else if (color < 0) {
            return 0;
        }
        return color;
    }

    private int getNewPixel(int oldPixel) {
        int pixelAlpha = Color.alpha(oldPixel);

        int pixelRed = Color.red(oldPixel) * brightnessValue;
        int pixelGreen = Color.green(oldPixel) * brightnessValue;
        int pixelBlue = Color.blue(oldPixel) * brightnessValue;

        return Color.argb(pixelAlpha, checkedColor(pixelRed), checkedColor(pixelGreen), checkedColor(pixelBlue));
    }

    private void increaseBitmapBrightness(Bitmap src) {
        src.getPixels(pixels, 0, src.getWidth(), 0, 0, src.getWidth(), src.getHeight());
        for (int i = 0; i < src.getWidth() * src.getHeight(); i++) {
            pixels[i] = getNewPixel(pixels[i]);
        }
        src.setPixels(pixels, 0, src.getWidth(), 0, 0, src.getWidth(), src.getHeight());
    }

    public void run() {
        while (running) {
            if (holder.getSurface().isValid()) {
                Canvas canvas = holder.lockCanvas();
                reDraw(canvas, bitmapCompressed);
                holder.unlockCanvasAndPost(canvas);
            }
        }
    }

    public void reDraw(Canvas canvas, Bitmap bitmap) {
        canvas.drawBitmap(bitmap, 0, 0, null);
    }

}
