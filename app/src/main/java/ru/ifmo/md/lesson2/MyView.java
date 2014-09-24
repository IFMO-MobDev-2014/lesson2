package ru.ifmo.md.lesson2;

import android.app.Notification;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import java.nio.Buffer;
import java.util.Random;

import ru.ifmo.md.lesson2.R;

public class MyView extends View/* implements Runnable */{
    int width = 0;
    int height = 0;
    int iwidth = 0;
    int iheight = 0;
    static final double scale = 1.73;
    boolean state;
    Bitmap image, badlyScaled, smoothScaled;

    public MyView(Context context) {
        super(context);
        init();
        makeModifications();
    }

    public void init() {
        image = BitmapFactory.decodeResource(getResources(), R.drawable.source);
        state = false;
        iwidth = image.getWidth();
        iheight = image.getHeight();
    }

    public void makeModifications() {
        badScaling();
        badlyScaled = rotateAndMakeBrighterImage(badlyScaled);

        perfectScaling();
        smoothScaled = rotateAndMakeBrighterImage(smoothScaled);
    }

    public void resume() {
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                state = !state;
                invalidate();
                return false;
            }
        });
    }

    public void badScaling() {
        int newWidth = (int)(iwidth / scale);
        int newHeight = (int)(iheight /scale);
        double baseX, baseY;
        int red, green, blue, color;
        badlyScaled = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.RGB_565);
        for(int i = 0; i < newHeight; i++) {
            for(int j = 0; j < newWidth; j++) {
                baseX = Math.floor(i * scale);
                baseY = Math.floor(j * scale);
                color = image.getPixel((int)baseY, (int)baseX);
                red = Math.min(Color.red(color) * 2, 255);
                green = Math.min(Color.green(color) * 2, 255);
                blue = Math.min(Color.blue(color) * 2, 255);
                color = Color.rgb(red, green, blue);
                badlyScaled.setPixel(j, i, color);
            }
        }
    }

    public void perfectScaling() {
        int newWidth = (int)(iwidth / scale);
        int newHeight = (int)(iheight /scale);
        smoothScaled = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.RGB_565);
        int a, b, c, d, x, y, blue, green, red;
        double dx, dy;
        for(int i = 0; i < newHeight; i++) {
            for(int j = 0; j < newWidth; j++) {
                x = (int)(scale * j);
                y = (int)(scale * i);
                dx = (scale * j) - x;
                dy = (scale * i) - y;
                a = image.getPixel(x, y);
                b = image.getPixel(x, y + 1);
                c = image.getPixel(x + 1, y);
                d = image.getPixel(x + 1, y + 1);
                blue = Math.min(Color.blue(calc(a, b, c, d, dx, dy)) * 2, 255);
                green = Math.min(calc(Color.green(a), Color.green(b), Color.green(c), Color.green(d), dx, dy) * 2, 255);
                red = Math.min(calc(Color.red(a), Color.red(b), Color.red(c), Color.red(d), dx, dy) * 2, 255);
                int final_color = Color.rgb(red, green ,blue);
                smoothScaled.setPixel(j, i, final_color);
            }
        }
    }

    public Bitmap rotateAndMakeBrighterImage(Bitmap rotatable) {
        int w = rotatable.getWidth();
        int h = rotatable.getHeight();
        Bitmap temp = Bitmap.createBitmap(h, w, Bitmap.Config.RGB_565);
        for(int i = 0; i < h; i++) {
            for(int j = 0; j < w; j++) {
                temp.setPixel(h - i - 1, j, rotatable.getPixel(j, i));
            }
        }
        return temp;
    }

    private int calc(int a, int b, int c, int d, double dx, double dy) {
        return (int)((a & 0xff) * (1 - dx) * (1 - dy) + (b & 0xff) * dx * (1 - dy) +
                (c & 0xff) * dy *(1 - dx) + (d & 0xff) * (dx * dy));
    }

    @Override
    public void onSizeChanged(int w, int h, int oldW, int oldH) {
        width = w;
        height = h;
    }

    @Override
    public void onDraw(Canvas canvas) {
        Log.i("STATE", state + "");
        if(state)
            canvas.drawBitmap(badlyScaled, 0, 0, null);
        else
            canvas.drawBitmap(smoothScaled, 0, 0, null);
    }
}
