package ru.ifmo.md.lesson2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by gshark on 24.09.14.
 */
public class MyView extends SurfaceView {
    Thread thread;

    public MyView(Context context) {
        super(context);
    }

    public void resume() {
        thread = new DrawThread(getHolder());
        thread.start();
    }

    Bitmap pictureGet() {
        return BitmapFactory.decodeResource(getContext().getResources(), R.drawable.source);
    }

    int min(int a, int b) {
        if (a < b)
            return a;
        return b;
    }

    Bitmap fast(Bitmap bmp) {
        int[] pixels;
        int height = bmp.getHeight();
        int width = bmp.getWidth();
        pixels = new int[height * width];
        bmp.getPixels(pixels, 0, width, 1, 1, width - 1, height -1);
        int newHeight = (int) (height / 1.73);
        int newWidth = (int) (width / 1.73);
        int[] newPixels = new int[newHeight * newWidth];
        for (int i = 0; i < newHeight; i++) {
            for (int j = 0; j < newWidth; j++) {
                newPixels[i * newWidth + j] = pixels[(int)(1.73 * i) * width + (int)(1.73 * j)];
                int red = (newPixels[i * newWidth + j] & (255 * 256 * 256)) / 256 / 256;
                int green = (newPixels[i * newWidth + j] & (255 * 256)) / 256;
                int blue = (newPixels[i * newWidth + j] & 255) ;
                int alpha = (newPixels[i * newWidth + j] & (255 * 256 * 256 * 256));
                red += 128;
                green += 128;
                blue += 128;
                red = min(255, red);
                green = min(255, green);
                blue = min(255, blue);
                newPixels[i * newWidth + j] = alpha + red * 256 * 256 + green * 256 + blue;
            }
        }
        int[] rotPixels = new int[newHeight * newWidth];
        for (int i = 0; i < newHeight; i++) {
            for (int j = 0; j < newWidth; j++) {
                rotPixels[j * newHeight + i] = newPixels[i * newWidth + j];
            }
        }
        return Bitmap.createBitmap(rotPixels, newHeight, newWidth, Bitmap.Config.ARGB_8888);
    }

    Bitmap slow(Bitmap bmp) {
        int[] pixels;
        int height = bmp.getHeight();
        int width = bmp.getWidth();
        pixels = new int[height * width];
        bmp.getPixels(pixels, 0, width, 1, 1, width - 1, height -1);
        int newHeight = (int) (height / 1.73);
        int newWidth = (int) (width / 1.73);
        int[] newPixels = new int[newHeight * newWidth];
        for (int i = 0; i < newHeight; i++) {
            for (int j = 0; j < newWidth; j++) {
                newPixels[i * newWidth + j] = pixels[(int)(1.73 * i) * width + (int)(1.73 * j)];
                int red = (newPixels[i * newWidth + j] & (255 * 256 * 256)) / 256 / 256;
                int green = (newPixels[i * newWidth + j] & (255 * 256)) / 256;
                int blue = (newPixels[i * newWidth + j] & 255) ;
                int alpha = (newPixels[i * newWidth + j] & (255 * 256 * 256 * 256));
                red += 128;
                green += 128;
                blue += 128;
                red = min(255, red);
                green = min(255, green);
                blue = min(255, blue);
                newPixels[i * newWidth + j] = alpha + red * 256 * 256 + green * 256 + blue;
            }
        }
        /*int[] rotPixels = new int[newHeight * newWidth];
        for (int i = 0; i < newHeight; i++) {
            for (int j = 0; j < newWidth; j++) {
                rotPixels[j * newHeight + i] = newPixels[i * newWidth + j];
            }
        }*/
        return Bitmap.createBitmap(newPixels, newWidth, newHeight, Bitmap.Config.ARGB_8888);
    }



    boolean isFastMode = true;

    void changeMode() {
        isFastMode = !isFastMode;
    }

    public class DrawThread extends Thread {
        SurfaceHolder holder;

        public DrawThread(SurfaceHolder holder) {
            this.holder = holder;
        }
        @Override
        public void run() {
            Bitmap bmp = pictureGet();
            while (true) {
                if (holder.getSurface().isValid()) {
                    Canvas canvas = holder.lockCanvas();
                    if (!isFastMode) {
                        canvas.drawBitmap(fast(bmp), 0, 0, null);
                    } else {
                        canvas.drawBitmap(slow(bmp), 0, 0, null);
                    }
                    holder.unlockCanvasAndPost(canvas);
                    break;
                }
            }
        }
    }

}
