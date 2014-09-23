package ru.ifmo.md.lesson2;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class MyActivity extends Activity {
    private ImageView imgView;

    private Bitmap bitmap;
    private Bitmap scaledBitmap;

    private int width = 700;
    private int height = 750;
    private int newWidth = 434;
    private int newHeight = 405;
    private int newSize = newWidth * newHeight;

    private int[] pixels = new int[width * height];
    private int[] rotAndBrPixels = new int[width * height];
    private int[] scaledPixels = new int[newSize];

    private boolean fast = false;

    public void rotateAndBrighten() {
        int tmp = height;
        height = width;
        width = tmp;

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int pixel = pixels[i + (width - j - 1) * height];
                float[] hsv = new float[3];

                Color.colorToHSV(pixel, hsv);
                hsv[2] = Math.min(1, hsv[2] * 2);
                rotAndBrPixels[j + i * width] = Color.HSVToColor(hsv);
            }
        }
    }

    public void fastScale() {
        for (int i = 0; i < newHeight; i++) {
            for (int j = 0; j < newWidth; j++) {
                scaledPixels[j + i * newWidth] = rotAndBrPixels[Math.round(j * 1.73f) + Math.round(i * 1.73f) * width];
            }
        }
    }

    public void slowScale() {
        for (int i = 0; i < newHeight; i++) {
            for (int j = 0; j < newWidth; j++) {
                int fromI = Math.round(1.73f * i);
                int toI = Math.min(Math.round(1.73f * (i + 1)), height);
                int fromJ = Math.round(1.73f * j);
                int toJ = Math.min(Math.round(1.73f * (j + 1)), width);
                int newRed = 0;
                int newGreen = 0;
                int newBlue = 0;
                int t = 0;

                for (int k = fromI; k < toI; k++) {
                    for (int l = fromJ; l < toJ; l++) {
                        int pixel = rotAndBrPixels[l + k * width];
                        newRed += Color.red(pixel);
                        newBlue += Color.blue(pixel);
                        newGreen += Color.green(pixel);
                        t++;
                    }
                }

                newRed /= t;
                newGreen /= t;
                newBlue /= t;

                scaledPixels[j + i * newWidth] = Color.rgb(newRed, newGreen, newBlue);
            }
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout);

        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.source);
        bitmap = bitmap.createScaledBitmap(bitmap, width, height, true);
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);

        rotateAndBrighten();
        fastScale();

        scaledBitmap = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.RGB_565);
        scaledBitmap.setPixels(scaledPixels, 0, newWidth, 0, 0, newWidth, newHeight);
        imgView = (ImageView) findViewById(R.id.imageView1);
        imgView.setImageBitmap(scaledBitmap);
    }

    public void onClick(View v) {
        if (fast) {
            fastScale();
        }
        else {
            slowScale();
        }
        scaledBitmap.setPixels(scaledPixels, 0, newWidth, 0, 0, newWidth, newHeight);
        imgView.setImageBitmap(scaledBitmap);
        fast = !fast;
    }
}
