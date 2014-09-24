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
    int newWidth = 0;
    int newHeight = 0;
    static final double scale = 1.73;
    boolean state;
    int [] imageArray, badlyScaled, smoothScaled, changeBuffer;
    Bitmap image;

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
        imageArray = new int[iwidth * iheight];
        for(int i = 0; i < image.getHeight(); i++) {
            for(int j = 0; j < image.getWidth(); j++) {
                imageArray[i * iwidth + j] = image.getPixel(j, i);
            }
        }
    }

    public void makeModifications() {
        newWidth = (int)(iwidth/scale);
        newHeight = (int)(iheight/scale);

        badScaling();
        badlyScaled = rotateImage(badlyScaled);

        perfectScaling();
        smoothScaled = rotateImage(smoothScaled);
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
        double baseX, baseY;
        int red, green, blue, color;
        badlyScaled = new int[newWidth * newHeight];
        for(int i = 0; i < newHeight; i++) {
            for(int j = 0; j < newWidth; j++) {
                baseX = Math.floor(i * scale);
                baseY = Math.floor(j * scale);
                color = image.getPixel((int)baseY, (int)baseX);
                red = Math.min(Color.red(color) * 2, 255);
                green = Math.min(Color.green(color) * 2, 255);
                blue = Math.min(Color.blue(color) * 2, 255);
                color = Color.rgb(red, green, blue);
                badlyScaled[i * newWidth + j] = color;
            }
        }
    }

    public void perfectScaling() {
        smoothScaled = new int[newWidth * newHeight];
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
                smoothScaled[i * newWidth + j] = final_color;
            }
        }
    }

    public int[] rotateImage(int [] rotatable) {
        changeBuffer = new int[newWidth * newHeight];
        for(int i = 0; i < newHeight; i++) {
            for(int j = 0; j < newWidth; j++) {
                changeBuffer[j * newHeight + (newHeight - i - 1)] = rotatable[i * newWidth + j];
            }
        }
        return changeBuffer;
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
        if(state)
            canvas.drawBitmap(smoothScaled, 0, newHeight, 0, 0, newHeight, newWidth, false, null);
        else
            canvas.drawBitmap(badlyScaled, 0, newHeight, 0, 0, newHeight, newWidth, false, null);
    }
}
