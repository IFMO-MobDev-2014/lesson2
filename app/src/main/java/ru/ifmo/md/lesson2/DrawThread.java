package ru.ifmo.md.lesson2;

import android.annotation.TargetApi;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.os.Build;
import android.util.Log;
import android.view.SurfaceHolder;

/**
 * Created by german on 20.09.14.
 */
public class DrawThread extends Thread {
    private boolean running = false;
    private SurfaceHolder surfaceHolder = null;
    private Bitmap picture = null;
    private Bitmap originalPicture = null;

    private int width, height;
    private int [] pixels = null;
    private int [] originalPixels = null;
    private int [] smartCompressPixels = null;
    private int [] stupidCompressPixels = null;
    // brightPixels[] can consist of negative rgb (else we can loose our colors when we will
    //                                              make bright down)
    //private int [] brightPixels = null;

    public enum CompressType {STUPID, SMART};
    CompressType compressType;

    public DrawThread(SurfaceHolder surfaceHolder, Resources resources) {
        this.surfaceHolder = surfaceHolder;
        originalPicture = BitmapFactory.decodeResource(resources, R.drawable.source);
        picture = originalPicture;
        width = picture.getWidth();
        height = picture.getHeight();
        originalPixels = new int[width * height];
        picture.getPixels(originalPixels, 0, width, 0, 0, width, height);
        pixels = originalPixels;

        compressSmart(405, 434);
        turn90();
        makeBrightUp();
        smartCompressPixels = pixels;

        pixels = originalPixels;
        width = originalPicture.getWidth();
        height = originalPicture.getHeight();

        compressStupid(405, 434);
        turn90();
        makeBrightUp();
        stupidCompressPixels = pixels;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    @Override
    public void run() {
        while (running) {
            if (surfaceHolder.getSurface().isValid()) {
                Canvas canvas = surfaceHolder.lockCanvas();
                if (canvas != null) {
                    draw(canvas);
                    surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
        }
    }

    private void draw(Canvas canvas) {
        picture = Bitmap.createBitmap(pixels, width, height, picture.getConfig());
        synchronized (surfaceHolder) {
            canvas.drawColor(Color.BLACK);
            if (picture != null)canvas.drawBitmap(picture, 0, 0, null);
        }
    }

    public void makeBrightUp() {
        for (int i = 0; i < width * height; i++) {
            int curPixel = pixels[i];
            int a = (curPixel & 0xff000000) >> 24;
            int r = (curPixel & 0x00ff0000) >> 16;
            int g = (curPixel & 0x0000ff00) >> 8;
            int b = curPixel & 0x000000ff;

            r = r + 128;
            g = g + 128;
            b = b + 128;
            if (r > 0xff) r = 0xff;
            if (g > 0xff) g = 0xff;
            if (b > 0xff) b = 0xff;
            curPixel = (a << 24) | (r << 16) | (g << 8) | b;
            pixels[i] = curPixel;
        }
    }

    public void compressStupid(int width, int height) {
        float scaleX = (float)this.width / (float)width;
        float scaleY = (float)this.height / (float)height;
        System.out.println(scaleX + " " + scaleY);
        int [] newPixels = new int[width * height];
        float curY = 0;
        for (int j = 0; j < height; j++, curY += scaleY) {
            float curX = 0;
            for (int i = 0; i < width; i++, curX += scaleX) {
                newPixels[j * width + i] = pixels[(int)curY * this.width + (int)curX];
            }
        }
        this.width = width;
        this.height = height;
        pixels = newPixels;
        compressType = CompressType.STUPID;
    }

    public void compressSmart(int width, int height) {
        float scaleX = (float)this.width / (float)width;
        float scaleY = (float)this.height / (float)height;
        int [] newPixels = new int[width * height];
        float curY = 0;
        for (int j = 0; j < height; j++, curY += scaleY) {
            float curX = 0;
            for (int i = 0; i < width; i++, curX += scaleX) {
                int x2 = (int)Math.min(this.width, curX + Math.ceil(scaleX));
                int y2 = (int)Math.min(this.height, curY + Math.ceil(scaleY));
                int pixelsCnt = (x2 - (int)curX) * (y2 - (int)curY);

                int na = 0, nr = 0, ng = 0, nb = 0;
                for (int x = (int)curX; x < x2; x++) {
                    for (int y = (int)curY; y < y2; y++) {
                        int curPixel = pixels[y * this.width + x];
                        na += ((curPixel & 0xff000000) >> 24);
                        nr += ((curPixel & 0x00ff0000) >> 16);
                        ng += ((curPixel & 0x0000ff00) >> 8);
                        nb += ( curPixel & 0x000000ff);
                    }
                }
                na /= pixelsCnt;
                nr /= pixelsCnt;
                ng /= pixelsCnt;
                nb /= pixelsCnt;
                newPixels[j * width + i] = (na << 24) | (nr << 16) | (ng << 8) | nb;
            }
        }
        this.width = width;
        this.height = height;
        pixels = newPixels;
        compressType = CompressType.SMART;
    }

    public void changeCompress() {
        if (compressType == CompressType.SMART) {
            pixels = stupidCompressPixels;
            compressType = CompressType.STUPID;
        } else {
            pixels = smartCompressPixels;
            compressType = CompressType.SMART;
        }
    }

    public void turn90() {
        int [] newPixels = new int[width * height];
        int ptr = 0;
        for (int i = 0; i < width; i++) {
            for (int j = height - 1; j >= 0; j--) {
                newPixels[ptr] = pixels[j * width + i];
                ptr++;
            }
        }
        int tmp = width;
        width = height;
        height = tmp;
        pixels = newPixels;
    }
}
