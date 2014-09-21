package ru.ifmo.md.lesson2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Юлия on 19.09.2014.
 */
public class MyPicture extends View {
    Bitmap bitmap1;
    int w = 0;
    int h = 0;
    int nw = 0;
    int nh = 0;
    Bitmap bitmap2;
    Bitmap bitmap = null;
    int[] pixels;
    BitmapFactory.Options option = new BitmapFactory.Options();
    boolean touch = true;

    public MyPicture(Context context) {
        super(context);
    }

    public void run() {
        option.inScaled = false;
        bitmap2 = bitmap1 = BitmapFactory.decodeResource(getResources(), R.drawable.source, option);
        w = bitmap2.getWidth();
        h = bitmap2.getHeight();
        pixels = new int[w * h];
        bitmap2.getPixels(pixels, 0, w, 0, 0, w, h);
        brightness();
        turn();
        nh = Math.round(h / 1.73f);
        nw = Math.round(w / 1.73f);
        bitmap = Bitmap.createBitmap(nw, nh, Bitmap.Config.ARGB_8888);
        changingFirst();
    }

    public void changingFirst() {

        int[] newPixels = new int[nw * nh];
        for (int x = 0; x < nw; x++)
            for (int y = 0; y < nh; y++) {
                newPixels[x + y * nw] = pixels[Math.round(x * 1.73f) + w * Math.round(y * 1.73f)];
            }
        bitmap = Bitmap.createBitmap(newPixels, 0, nw, nw, nh, Bitmap.Config.ARGB_8888);
        invalidate();
    }

    public void changingSecond() {
    int[] newPixels = new int[nw * nh];
        for (int x = 0; x < nw; x++)
            for (int y = 0; y < nh; y++) {
                float oldX = x * 1.73f;
                float oldY = y * 1.73f;
                int oneX = (int) (oldX - (1.73/2) + 0.5);
                if (oneX < 0) oneX = 0;
                int secX = (int) (oldX + (1.73/2) + 0.5);
                if (secX >= w) secX = w - 1;
                int oneY = (int) (oldY - (1.73/2) + 0.5);
                if (oneY < 0) oneY = 0;
                int secY = (int) (oldY + (1.73/2) + 0.5);
                if (secY >= h) secY = h - 1;
                int cr = 0;
                int cg = 0;
                int cb = 0;
                int t = 0;
                for (int i = oneX; i < secX + 1; i++)
                    for (int k = oneY; k < secY + 1; k++) {
                        cr += Color.red(pixels[i + k * w]);
                        cg += Color.green(pixels[i + k * w]);
                        cb += Color.blue(pixels[i + k * w]);
                        t++;
                    }
                newPixels[x + y * nw] = Color.rgb(cr/t, cg/t, cb/t);
            }
        bitmap = Bitmap.createBitmap(newPixels, 0, nw, nw, nh, Bitmap.Config.ARGB_8888);
        invalidate();
    }

    public void turn() {
        int[] newPixels = new int[w*h];
        for (int x = 0; x < w; x++)
            for(int y = 0; y < h; y++) {
                newPixels[h - y - 1 + x * h] = pixels[x + y * w];
            }
        pixels = newPixels;
        int t = w;
        w = h;
        h = t;
    }

    public void brightness() {
        for (int x = 0; x < w; x++)
            for (int y = 0; y < h; y++) {
                int colorRed = (0xff + Color.red(pixels[x + y * w]))/2;
                int colorGreen = (0xff + Color.green(pixels[x + y * w]))/2;
                int colorBlue = (0xff + Color.blue(pixels[x + y * w]))/2;
                pixels[x + y * w] = Color.rgb(colorRed, colorGreen, colorBlue);
            }
    }

    public boolean onTouchEvent(MotionEvent event)
    {
        if(event.getAction() == MotionEvent.ACTION_DOWN) {
            if (touch) {
                touch = false;
                changingSecond();
            } else {
                touch = true;
                changingFirst();
            }
        }
        return true;
    }

    @Override
    public void onDraw(Canvas canvas) {
        canvas.drawBitmap(bitmap, null, new Rect(0,0, nw, nh), null);
    }
}
