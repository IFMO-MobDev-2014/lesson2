package ru.ifmo.md.lesson2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.SurfaceView;

/**
 * Created by anton on 20/09/14.
 */
public class RotateView extends SurfaceView {
    int width;
    int height;
    private Rect dst;
    private Bitmap bitmap;

    public RotateView(Context context) {
        super(context);
        init(context);
    }

    public RotateView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public RotateView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
    }

    public void setSize(int w, int h) {
        width = w;
        height = h;
        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        dst = new Rect(0, 0, width, height);
    }

    public void setPixels(int[] pixels) {
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
    }

    public void drawIt(Canvas canvas) {
        canvas.drawColor(Color.BLACK);
        canvas.drawBitmap(bitmap, null, dst, null);
    }
}