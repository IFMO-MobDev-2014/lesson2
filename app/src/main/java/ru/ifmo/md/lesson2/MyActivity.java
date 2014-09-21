package ru.ifmo.md.lesson2;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class MyActivity extends Activity {
    ImageView image;
    Bitmap b1, b2, bMap;
    int chooseBitmap = 0;
    final static int newWeight = 405;
    final static int newHeight = 434;

    public void run() {
        image = (ImageView) findViewById(R.id.imageView1);
        bMap = BitmapFactory.decodeResource(getResources(), R.drawable.source);
        bMap = bMap.copy(Bitmap.Config.RGB_565, true);

        int width = bMap.getWidth();
        int height = bMap.getHeight();
        upBrightness(bMap, width, height);

        int[] intArray = new int[width * height + 1];
        int[] intArrayNew = new int[width * height + 1];

        bMap.getPixels(intArray, 0, width, 0, 0, width, height);

        rotate(intArray, intArrayNew);

        bMap = Bitmap.createBitmap(intArrayNew, height, width,
                Bitmap.Config.RGB_565);


        int[] arrResGood = resizeBilinear(intArrayNew, height, width, newHeight,
                newWeight);
        int[] arrResQuick = resizePixels(intArrayNew, height, width, newHeight, newWeight);

        b1 = Bitmap.createBitmap(arrResGood, newHeight, newWeight, Bitmap.Config.RGB_565);
        b2 = Bitmap
                .createBitmap(arrResQuick, newHeight, newWeight, Bitmap.Config.RGB_565);

        image.setImageBitmap(b2);

        image.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                draw();
            }
        });

    }

    public void rotate(int[] intArray, int[] intArrayNew) {
        int width = bMap.getWidth();
        int height = bMap.getHeight();
        for (int i = 0; i < intArray.length; i++)
            intArrayNew[i] = intArray[(height - (i % height) - 1) * width + i
                    / height];
    }


    public void draw() {
        switch (chooseBitmap) {
            case 0:
                image.setImageBitmap(b1);
                chooseBitmap = 1;
                break;
            case 1:
                image.setImageBitmap(b2);
                chooseBitmap = 0;
                break;
        }
    }

    public void upBrightness(Bitmap src, int w, int h) {
        int red, green, blue, pixel;

        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                pixel = src.getPixel(i, j);
                red = (pixel >> 16) & 0xFF;
                green = (pixel >> 8) & 0xFF;
                blue = pixel & 0xFF;
                red = changeColor(red);
                green = changeColor(green);
                blue = changeColor(blue);


                pixel = 0xFF000000 | (red << 16) | (green << 8) | blue;
                src.setPixel(i, j, pixel);
            }
        }
    }

    public int changeColor(int color) {
        int tmp = color;
        tmp += tmp;
        if (tmp > 255)
            tmp = 255;
        return  tmp;
    }

    public int[] resizeBilinear(int[] pixels, int oldW, int oldH, int w, int h) {
        int[] temp = new int[w * h + 1];
        int a, b, c, d, x, y, index, red, green, blue, count = 0;
        float xRatio = ((float) (oldW - 1)) / w;
        float yRatio = ((float) (oldH - 1)) / h;
        float xDiff, yDiff;
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                x = (int) (xRatio * j);
                y = (int) (yRatio * i);
                xDiff = (xRatio * j) - x;
                yDiff = (yRatio * i) - y;
                index = (y * oldW + x);

                a = pixels[index];
                b = pixels[index + 1];
                c = pixels[index + oldW];
                d = pixels[index + oldW + 1];

                blue = (int) ((a & 0xff) * (1 - xDiff) * (1 - yDiff)
                        + (b & 0xff) * (xDiff) * (1 - yDiff) + (c & 0xff)
                        * (yDiff) * (1 - xDiff) + (d & 0xff) * (xDiff * yDiff));

                green = (int) (((a >> 8) & 0xff) * (1 - xDiff) * (1 - yDiff)
                        + ((b >> 8) & 0xff) * (xDiff) * (1 - yDiff)
                        + ((c >> 8) & 0xff) * (yDiff) * (1 - xDiff) + ((d >> 8) & 0xff)
                        * (xDiff * yDiff));

                red = (int) (((a >> 16) & 0xff) * (1 - xDiff) * (1 - yDiff)
                        + ((b >> 16) & 0xff) * (xDiff) * (1 - yDiff)
                        + ((c >> 16) & 0xff) * (yDiff) * (1 - xDiff) + ((d >> 16) & 0xff)
                        * (xDiff * yDiff));

                temp[count++] = 0xff000000 | ((red << 16) & 0xff0000)
                        | ((green << 8) & 0xff00) | blue;
            }
        }
        return temp;
    }

    public int[] resizePixels(int[] pixels, int oldW, int oldH, int w, int h) {
        int[] temp = new int[w * h + 1];
        int xRatio = ((oldW << 16) / w) + 1;
        int yRatio = ((oldH << 16) / h) + 1;
        int x, y;
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                x = ((j * xRatio) >> 16);
                y = ((i * yRatio) >> 16);
                temp[(i * w) + j] = pixels[(y * oldW) + x];
            }
        }
        return temp;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        run();
    }

}
