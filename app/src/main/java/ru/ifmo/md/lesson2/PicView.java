package ru.ifmo.md.lesson2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Яна on 23.09.2014.
 */
public class PicView extends View {

    public static final int h = 750;
    public static final int w = 700;

    public static final int neww = 405;
    public static final int newh = 434;

    boolean change_scaling = true;
    boolean first_try = true;

    Bitmap map;
    int[] img = new int[h * w];
    int[] newimg = new int[neww * newh];

    public PicView(Context context) {
        super(context);
        map = BitmapFactory.decodeResource(getResources(), R.drawable.source);
        map.getPixels(img, 0, w, 0, 0, w, h);
    }

    public void rotate() {
        int[] tmp = new int[h * w];
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                tmp[i * h + j] = img[(h - j - 1) * w + i];
            }
        }
        img = tmp;
    }

    public void upBrightness() {

        int value;
        int redv;
        int greenv;
        int bluev;

        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                value = img[i * w + j];
                redv = (value & 0xFF0000) >> 16;
                greenv = (value & 0x00FF00) >> 8;
                bluev = (value & 0x0000FF);

                redv = Math.min(redv * 2, 255);
                greenv = Math.min(greenv * 2, 255);
                bluev = Math.min(bluev * 2, 255);

                img[i * w + j] = (redv << 16) | (greenv << 8) | bluev;
            }
        }
    }

    public void fastScaling() {
        float scale = w / (float)neww;
        for (int i = 0; i < neww; i++) {
            for (int j = 0; j < newh; j++) {
                newimg[i * newh + j] = img[((int)(i * scale) * h + (int)(j * scale))];
            }
        }
    }

    public void coolScaling() {
        float scale = h / (float)newh;

        int[] redv = new int[newh * neww];
        int[] greenv = new int[newh * neww];
        int[] bluev = new int[newh * neww];
        int[] cnt = new int[newh * neww];

        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                int old_coord = i * h + j;
                int new_coord = (int) ((float)i / scale) * newh + (int) ((float)j / scale);

                redv[new_coord] += (img[old_coord] & 0xFF0000) >> 16;
                greenv[new_coord] += (img[old_coord] & 0x00FF00) >> 8;
                bluev[new_coord] += (img[old_coord] & 0x0000FF);
                cnt[new_coord]++;
            }
        }

        for (int i = 0; i < neww; i++) {
            for (int j = 0; j < newh; j++) {
                int coord = i * newh + j;
                newimg[coord] = ((redv[coord] / cnt[coord]) << 16)
                                | ((greenv[coord] / cnt[coord]) << 8)
                                | (bluev[coord] / cnt[coord]);
            }
        }
    }

    public void changePicture() {
        if (change_scaling) {
            fastScaling();
        } else {
            coolScaling();
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        if (first_try) {
            upBrightness();
            rotate();
            changePicture();
            first_try = false;
        }

        canvas.drawBitmap(newimg, 0, newh, 0, 0, newh, neww, false, null);
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent touch) {
        int event_id = touch.getAction();

        if (event_id == MotionEvent.ACTION_DOWN) {
            changePicture();
            change_scaling = !change_scaling;
        }

        return true;
    }

}
