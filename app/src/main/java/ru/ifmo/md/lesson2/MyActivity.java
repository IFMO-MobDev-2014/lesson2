package ru.ifmo.md.lesson2;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.content.Context;
import android.graphics.Canvas;

public class MyActivity extends Activity {

    Bitmap fast;
    Bitmap slow;
    Paint paint;
    double scaleX = 1.73;
    double scaleY = 1.73;
    boolean image = false;

    MyView myview;

    public class MyView extends View {


        public MyView(Context context) {
            super(context);
            this.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    invalidate();
                }
            });
        }

        @Override
        public void onDraw(Canvas canvas) {
            if (image) {
                canvas.drawBitmap(fast, 0, 0, paint);
            } else {
                canvas.drawBitmap(slow, 0, 0, paint);
            }
            image = !image;
        }


    }

    int color(int a, int red, int green, int blue) {
        return (a << 24) + (red << 16) + (green << 8) + blue;
    }
    int getRed(int color) {
        return (color << 8) >>> 24;
    }
    int getGreen(int color) {
        return (color << 16) >>> 24;
    }
    int getBlue(int color) {
        return (color << 24) >>> 24;
    }

    public Bitmap fastCompress(Bitmap bitmap) {
        int newHeight = (int) Math.round(bitmap.getHeight() / scaleX);
        int newWidth = (int) Math.round(bitmap.getWidth() / scaleY);
        Bitmap newBitmap = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.ARGB_8888);
        for (int i = 0; i < newWidth; i++) {
            for (int j = 0; j < newHeight; j++) {
                int pix = bitmap.getPixel((int) (i * scaleX), (int) (j * scaleY));
                newBitmap.setPixel(i, j, pix);
            }
        }
        return newBitmap;
    }

    public Bitmap slowCompress(Bitmap bitmap) {
        int newHeight = (int) Math.round(bitmap.getHeight() / scaleX);
        int newWidth = (int) Math.round(bitmap.getWidth() / scaleY);
        Bitmap newBitmap = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.ARGB_8888);
        for (int i = 0; i < newWidth; i++) {
            for (int j = 0; j < newHeight; j++) {
                int ci = (int) (i * scaleX);
                int cj = (int) (j * scaleY);
                double ar = 1;
                double ag = 1;
                double ab = 1;
                int k = 0;
                for (int x = Math.max(0, ci - 1); x < Math.min(bitmap.getWidth(), ci + 2); x++) {
                    for (int y = Math.max(0, cj - 1); y < Math.min(bitmap.getHeight(), cj + 2); y++) {
                        k += (3 - Math.abs(ci - x) + Math.abs(cj - y));
                        ar *= Math.pow(getRed(bitmap.getPixel(x, y)), (3 - Math.abs(ci - x) + Math.abs(cj - y)));
                        ag *= Math.pow(getGreen(bitmap.getPixel(x, y)), (3 - Math.abs(ci - x) + Math.abs(cj - y)));
                        ab *= Math.pow(getBlue(bitmap.getPixel(x, y)), (3 - Math.abs(ci - x) + Math.abs(cj - y)));
                    }
                }
                ar = Math.pow(ar, 1./k);
                ag = Math.pow(ag, 1./k);
                ab = Math.pow(ab, 1./k);
                newBitmap.setPixel(i, j, color(255, (int)ar, (int)ag, (int)ab));
            }
        }
        return newBitmap;
    }


    public Bitmap rotate(Bitmap bitmap) {
        int height = bitmap.getHeight();
        int width = bitmap.getWidth();
        Bitmap newBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                newBitmap.setPixel(i, j, bitmap.getPixel(i, height - 1 - j));
            }
        }
        return newBitmap;
    }

    public Bitmap increaseBrightness(Bitmap bitmap) {
        int height = bitmap.getHeight();
        int width = bitmap.getWidth();
        Bitmap newBitmap = Bitmap.createBitmap(height, width, Bitmap.Config.ARGB_8888);
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int pix = bitmap.getPixel(i, j);
                int r = Math.min(getRed(pix) * 2, 255);
                int g = Math.min(getGreen(pix) * 2, 255);
                int b = Math.min(getBlue(pix) * 2, 255);
                newBitmap.setPixel(j, i, color(255, r, g, b));
            }
        }
        return newBitmap;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        paint = new Paint();
        Bitmap img = BitmapFactory.decodeResource(getResources(), R.drawable.source);
        img = rotate(img);

        img = increaseBrightness(img);
        fast = fastCompress(img);
        slow = slowCompress(img);

        myview = new MyView(this);
        setContentView(myview);
    }


}
