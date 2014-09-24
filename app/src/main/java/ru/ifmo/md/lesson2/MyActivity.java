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
import android.widget.ArrayAdapter;
import android.widget.ListView;

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

    public Bitmap fastCompress(int[] pixels, int width, int height) {
        int newWidth = (int) Math.round(width / scaleY);
        int newHeight = (int) Math.round(height / scaleX);
        int[] newPixels = new int[newWidth * newHeight];
        for (int i = 0; i < newHeight; i++) {
            for (int j = 0; j < newWidth; j++) {
                newPixels[i * newWidth + j] = pixels[(int) (i * scaleX) * width + (int) (j * scaleY)];
            }
        }
        return Bitmap.createBitmap(newPixels, newWidth, newHeight, Bitmap.Config.ARGB_8888);
    }

    public Bitmap slowCompress(int[] pixels, int width, int height) {
        int newWidth = (int) Math.round(width / scaleY);
        int newHeight = (int) Math.round(height / scaleX);
        int[] newPixels = new int[newWidth * newHeight];
        for (int i = 0; i < newHeight; i++) {
            for (int j = 0; j < newWidth; j++) {
                int ci = (int) (i * scaleX);
                int cj = (int) (j * scaleY);
                double ar = 1;
                double ag = 1;
                double ab = 1;
                int k = 0;
                for (int x = Math.max(0, ci - 1); x < Math.min(height, ci + 2); x++) {
                    for (int y = Math.max(0, cj - 1); y < Math.min(width, cj + 2); y++) {
                        k++;
                        int c = pixels[x * width + y];
                        ar *= getRed(c);
                        ag *= getGreen(c);
                        ab *= getBlue(c);
                    }
                }
                ar = Math.pow(ar, 1. / k);
                ag = Math.pow(ag, 1. / k);
                ab = Math.pow(ab, 1. / k);
                newPixels[i * newWidth + j] = color(255, (int) ar, (int) ag, (int) ab);
            }
        }
        return Bitmap.createBitmap(newPixels, newWidth, newHeight, Bitmap.Config.ARGB_8888);
    }


    public void rotate(int[] pixels, int width, int height) {
        int[] rotated = new int[pixels.length];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                rotated[i * height + j] = pixels[(height - j - 1) * width + i];
            }

        }
        for (int i = 0; i < pixels.length; i++) {
            pixels[i] = rotated[i];
        }
    }

    public void increaseBrightness(int[] pixels) {
        for (int i = 0; i < pixels.length; i++) {
            int r = Math.min(getRed(pixels[i]) * 2, 255);
            int g = Math.min(getGreen(pixels[i]) * 2, 255);
            int b = Math.min(getBlue(pixels[i]) * 2, 255);
            pixels[i] = color(255, r, g, b);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        paint = new Paint();
        Bitmap img = BitmapFactory.decodeResource(getResources(), R.drawable.source);
        int pixels[] = new int[img.getHeight() * img.getWidth()];
        img.getPixels(pixels, 0, img.getWidth(), 0, 0, img.getWidth(), img.getHeight());
        rotate(pixels, img.getWidth(), img.getHeight());
        increaseBrightness(pixels);
        img = Bitmap.createBitmap(pixels, img.getHeight(), img.getWidth(), Bitmap.Config.ARGB_8888);
        fast = fastCompress(pixels, img.getWidth(), img.getHeight());
        slow = slowCompress(pixels, img.getWidth(), img.getHeight());

        myview = new MyView(this);
        setContentView(myview);

    }


}
