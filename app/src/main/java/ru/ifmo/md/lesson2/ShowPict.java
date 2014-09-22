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
    int[] pix = new int[4];
    double[] coef = new double[4];
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

    int calcScalar(int[] pix, double[] coef, int shift) {
        int res = 0;
        for (int i = 0; i < 4; i++) {
            int pshift = ((pix[i] >> (8 * shift)) & 0xFF);
            int cur = (int) (pshift * coef[i]);
            res += cur;
        }
        res = Math.min(res, 0xFF);
        return res << (8 * shift);
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

    void Compress1(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int[][] a = bitmapToMatrix(bitmap);
        for (int i = 0; i < NEED_WIDTH; i++) {
            double ti;
            ti = i * 1.0 * (width - 1) / (NEED_WIDTH - 1);
            int w = i * (width - 1) / (NEED_WIDTH - 1);
            w = Math.min(w, width - 2);
            double u = ti - w;
            for (int j = 0; j < NEED_HEIGHT; j++) {
                double tj;
                tj = j * 1.0 * (height - 1) / (NEED_HEIGHT - 1);
                int h = j * (height - 1) / (NEED_HEIGHT - 1);
                h = Math.min(h, height - 2);
                double t = tj - h;

                coef[0]= (1 - t) * (1 - u);
                coef[1] = t * (1 - u);
                coef[2] = t * u;
                coef[3] = (1 - t) * u;

                pix[0] = a[w][h];
                pix[1] = a[w][h + 1];
                pix[2] = a[w + 1][h + 1];
                pix[3] = a[w + 1][h];
                int blue = calcScalar(pix, coef, 0);
                int green = calcScalar(pix, coef, 1);
                int red = calcScalar(pix, coef, 2);
                int alpha = calcScalar(pix, coef, 3);
                b[i][j] = blue + green + red + alpha;
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
