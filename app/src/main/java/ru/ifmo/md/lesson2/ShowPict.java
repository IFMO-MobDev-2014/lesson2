package ru.ifmo.md.lesson2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by vlad107 on 22.09.14.
 */

public class ShowPict extends SurfaceView implements Runnable {
    static final int NEED_WIDTH = 405;
    static final int NEED_HEIGHT = 434;
    Thread thread;
    volatile boolean running = false;
    SurfaceHolder holder;
    Bitmap comp0;
    Bitmap comp1;
    int[] col;
    int cur_image = 0;
    int[][] b = new int[NEED_WIDTH][NEED_HEIGHT];

    public ShowPict(Context context) {
        super(context);
        holder = getHolder();
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
        } catch (InterruptedException ignore) {

        }
    }

    int[][] rotate(int[][] a) {
        int[][] b = new int[a[0].length][a.length];
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a[0].length; j++) {
                b[j][a.length - i - 1] = a[i][j];
            }
        }
        return b;
    }

    int[] doubleBrightness(int[] colors) {
        int[] col = new int[colors.length];
        for (int i = 0; i < colors.length; i++) {
            int bb = colors[i] & 0xFF;
            int gg = (colors[i] >> 8) & 0xFF;
            int rr = (colors[i] >> 16) & 0xFF;
            int aa = (colors[i] >> 24) & 0xFF;
            bb = Math.min(bb * 2, 0xFF);
            gg = Math.min(gg * 2, 0xFF);
            rr = Math.min(rr * 2, 0xFF);
            aa = Math.min(aa * 2, 0xFF);
            col[i] = bb + (gg << 8) + (rr << 16) + (aa << 24);
        }
        return col;
    }

    int[][] bitmapToMatrix(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int[][] a = new int[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                a[i][j] = bitmap.getPixel(i, j);
            }
        }
        return a;
    }

    int[] getArray(int[][] a) {
        int w = a.length;
        int h = a[0].length;
        col = new int[w * h];
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                col[j * w + i] = a[i][j];
            }
        }
        return col;
    }

    void Compress0(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int[][] a = bitmapToMatrix(bitmap);
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int ni;
                if (i < 2 * (width - NEED_WIDTH)) {
                    ni = i / 2;
                } else {
                    ni = NEED_WIDTH - (width - i);
                }
                int nj;
                if (j < (height - NEED_HEIGHT) * 2) {
                    nj = j / 2;
                } else {
                    nj = NEED_HEIGHT - (height - j);
                }
                b[ni][nj] = a[i][j];
            }
        }
        a = rotate(b);
        int[] colors = getArray(a);
        colors = doubleBrightness(colors);
        comp0 = Bitmap.createBitmap(colors, 0, a.length, a.length, a[0].length, Bitmap.Config.ARGB_8888);
    }

    int getColor(int mask, int x) {
        return (mask >> (8 * x)) & 0xFF;
    }

    void Compress1(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int[][] a = bitmapToMatrix(bitmap);
        int[][] blue = new int[NEED_WIDTH][NEED_HEIGHT];
        int[][] green = new int[NEED_WIDTH][NEED_HEIGHT];
        int[][] red = new int[NEED_WIDTH][NEED_HEIGHT];
        int[][] alpha = new int[NEED_WIDTH][NEED_HEIGHT];
        for (int i = 0; i < NEED_WIDTH; i++) {
            for (int j = 0; j < NEED_HEIGHT; j++) {
                blue[i][j] = green[i][j] = red[i][j] = alpha[i][j] = 0xFF;
            }
        }
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int w = (i < 2 * (width - NEED_WIDTH) ? i / 2 :
                                                       NEED_WIDTH - (width - i));
                int h = (j < 2 * (height - NEED_HEIGHT) ? j / 2:
                                                       NEED_HEIGHT - (height - j));
                blue[w][h] = Math.min(getColor(a[i][j], 0), blue[w][h]);
                green[w][h] = Math.min(getColor(a[i][j], 1), green[w][h]);
                red[w][h] = Math.min(getColor(a[i][j], 2), red[w][h]);
                alpha[w][h] = Math.min(getColor(a[i][j], 3), alpha[w][h]);
            }
        }
        for (int i = 0; i < NEED_WIDTH; i++) {
            for (int j = 0; j < NEED_HEIGHT; j++) {
                int newBlue = blue[i][j];
                int newGreen = green[i][j];
                int newRed = red[i][j];
                int newAlpha = alpha[i][j];
                b[i][j] = newBlue + (newGreen << 8) + (newRed << 16) + (newAlpha << 24);
            }
        }
        a = rotate(b);
        int[] colors = getArray(a);
        colors = doubleBrightness(colors);
        comp1 = Bitmap.createBitmap(colors, 0, a.length, a.length, a[0].length, Bitmap.Config.ARGB_8888);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        cur_image ^= 1;
        return super.onTouchEvent(event);
    }

    public void run() {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.source);
        Compress0(bitmap);
        Compress1(bitmap);
//        System.out.println("Done");
        System.out.println(bitmap.getWidth() + " " + bitmap.getHeight());
        while (running) {
            if (holder.getSurface().isValid()) {
                Canvas canvas = holder.lockCanvas();
                if (cur_image == 0) {
                    canvas.drawBitmap(comp0, 0, 0, null);
                } else {
                    canvas.drawBitmap(comp1, 0, 0, null);
                }
                holder.unlockCanvasAndPost(canvas);
            }
        }
    }
}
