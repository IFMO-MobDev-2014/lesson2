package ru.ifmo.md.lesson2;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;



public class MyActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ImageView iv = new ImageView(getApplicationContext());
        iv.setImageBitmap(bright(rotate(fastScale(pictureGet(), 1.73))));
        iv.setOnClickListener(new View.OnClickListener() {
            boolean isFast = true;
            Bitmap bmp;

            @Override
            public void onClick(View view) {
                if (!isFast) {
                   bmp = bright(rotate(fastScale(pictureGet(), 1.73)));
                } else {
                   bmp = bright(rotate(slowScale(pictureGet(), 1.73)));
                }
                isFast = !isFast;
                iv.setImageBitmap(bmp);
            }
        });
        iv.setScaleType(ImageView.ScaleType.CENTER);
        setContentView(iv);

    }

    Bitmap pictureGet() {
        return BitmapFactory.decodeResource(getResources(), R.drawable.source);
    }

    Bitmap rotate(Bitmap bmp) {
        int[] pixels;
        int height = bmp.getHeight();
        int width = bmp.getWidth();
        pixels = new int[height * width];
        bmp.getPixels(pixels, 0, width, 1, 1, width - 1, height - 1);
        int[] rotPixels = new int[height * width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                rotPixels[j * height + i] = pixels[(height - i - 1) * width + j];
            }
        }
        return Bitmap.createBitmap(rotPixels, height, width, Bitmap.Config.ARGB_8888);
    }

    int min(int a, int b) {
        if (a < b)
            return a;
        return b;
    }

    Bitmap bright(Bitmap bmp) {
        int[] pixels;
        int height = bmp.getHeight();
        int width = bmp.getWidth();
        pixels = new int[height * width];
        bmp.getPixels(pixels, 0, width, 1, 1, width - 1, height - 1);
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int red = (pixels[i * width + j] & (255 * 256 * 256)) / 256 / 256;
                int green = (pixels[i * width + j] & (255 * 256)) / 256;
                int blue = (pixels[i * width + j] & 255) ;
                int alpha = (pixels[i * width + j] & (255 * 256 * 256 * 256));
                red += 128;
                green += 128;
                blue += 128;
                red = min(255, red);
                green = min(255, green);
                blue = min(255, blue);
                pixels[i * width + j] = alpha + red * 256 * 256 + green * 256 + blue;
            }
        }
        return Bitmap.createBitmap(pixels, width, height, Bitmap.Config.ARGB_8888);
    }

    Bitmap fastScale(Bitmap bmp, double x) {
        int[] pixels;
        int height = bmp.getHeight();
        int width = bmp.getWidth();
        pixels = new int[height * width];
        bmp.getPixels(pixels, 0, width, 1, 1, width - 1, height - 1);
        int newHeight = (int)(height / x);
        int newWidth = (int)(width / x);
        int[] newPixels = new int[newHeight * newWidth];
        for (int i = 0; i < newHeight; i++) {
            for (int j = 0; j < newWidth; j++) {
                newPixels[i * newWidth + j] = pixels[(int)(x * i) * width + (int)(x * j)];
            }
        }
        return Bitmap.createBitmap(newPixels, newWidth, newHeight, Bitmap.Config.ARGB_8888);
    }

    Bitmap slowScale(Bitmap bmp, double x) {
        int[] pixels;
        int height = bmp.getHeight();
        int width = bmp.getWidth();
        pixels = new int[height * width];
        bmp.getPixels(pixels, 0, width, 1, 1, width - 1, height - 1);
        int newHeight = (int)(height / x);
        int newWidth = (int)(width / x);
        int[] newPixels = new int[newHeight * newWidth];
        int[] count = new int[newHeight * newWidth];
        int[] red = new int[newHeight * newWidth];
        int[] green = new int[newHeight * newWidth];
        int[] blue = new int[newHeight * newWidth];
        int[] alpha = new int[newHeight * newWidth];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int k = (int)(i / x) * newWidth + (int)(j / x);
                if (k > newHeight * newWidth - 1) {
                    continue;
                }
                int color = pixels[i * width + j];
                count[k]++;
                alpha[k] += (color >> 24) & 0xFF;
                red[k] += (color >> 16) & 0xFF;
                green[k] += (color >> 8) & 0xFF;
                blue[k] += (color >> 0) & 0xFF;
            }
        }
        for (int i = 0; i < newHeight; i++) {
            for (int j = 0; j < newWidth; j++) {
                int k = i * newWidth + j;
                int a = min(255, alpha[k] / count[k]) << 24;
                int r = min(255, red[k] / count[k]) << 16;
                int g = min(255, green[k] / count[k]) << 8;
                int b = min(255, blue[k] / count[k]);
                newPixels[k] = a + r + g + b;
            }
        }
        return Bitmap.createBitmap(newPixels, newWidth, newHeight, Bitmap.Config.ARGB_8888);
    }

}
