package ru.ifmo.md.lesson2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.Log;
import android.view.View;

/**
 * Created by Яна on 23.09.2014.
 */
public class PicView extends View {

    public static final int h = 750;
    public static final int w = 700;

    public static final int newh = 434;
    public static final int neww = 405;

    Bitmap map;
    int[] img = new int[h * w];

    public PicView(Context context) {
        super(context);
        map = BitmapFactory.decodeResource(getResources(), R.drawable.source);
        map.getPixels(img, 0, w, 0, 0, w, h);
    }

    public void rotate() {
        int[] tmp = new int[h * w];
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                tmp[i* h + j] = img[(h - j - 1) * w + i];
            }
        }
        img = tmp;
    }
    @Override

    public void onDraw(Canvas canvas) {
        rotate();
        canvas.drawBitmap(img, 0, h, 0, 0, h, w, false, null);
    }

}
