package ru.ifmo.md.lesson2;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class MyActivity extends Activity {
    private static final int sourceWidth = 750;
    private static final int sourceHeight = 700;
    private static final double scale = 1.73;
    private int newWidth = (int)(sourceWidth / scale);
    private int newHeight = (int)(sourceHeight / scale);

    private int[] pixels = new int[sourceHeight * sourceWidth];
    private int[] lowQuality = new int[newWidth * newHeight];
    private int[] highQuality = new int[newWidth * newHeight];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final ImageView img = new ImageView(getApplicationContext());
        Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.source);
        bmp = Bitmap.createScaledBitmap(bmp, sourceWidth, sourceHeight, true);
        bmp.getPixels(pixels, 0, sourceWidth, 0, 0, sourceWidth, sourceHeight);
        bmp.recycle();

        nearestNeighbour();
        bilinear();
        rotateAndBright();

        final Bitmap compressFast = Bitmap.createBitmap(lowQuality, newWidth, newHeight, Bitmap.Config.RGB_565);
        final Bitmap compressSlow = Bitmap.createBitmap(highQuality, newWidth, newHeight, Bitmap.Config.RGB_565);
        img.setImageBitmap(compressFast);
        View.OnClickListener l = new View.OnClickListener() {
                        boolean fastCompress = false;

                        @Override
                        public void onClick(View v) {
                                if (fastCompress)
                                    img.setImageBitmap(compressFast);
                                else
                                    img.setImageBitmap(compressSlow);
                                fastCompress = !fastCompress;
                            }
        };
        img.setOnClickListener(l);
        setContentView(img);
    }

    private int getColorAround(int index, float distX, float distY) {
        float invDistX = 1 - distX;
        float invDistY = 1 - distY;
        float blue = Color.blue(pixels[index]) * invDistX * invDistY + Color.blue(pixels[index + 1]) * (distX) * invDistY +
                Color.blue(pixels[index + sourceWidth]) * distY * invDistX + Color.blue(pixels[index + sourceWidth + 1]) * distX * distY;
        float green = Color.green(pixels[index]) * invDistX * invDistY + Color.green(pixels[index + 1]) * (distX) * invDistY +
                Color.green(pixels[index + sourceWidth]) * distY * invDistX + Color.green(pixels[index + sourceWidth + 1]) * distX * distY;
        float red = Color.red(pixels[index]) * invDistX * invDistY + Color.red(pixels[index + 1]) * (distX) * invDistY +
                Color.red(pixels[index + sourceWidth]) * distY * invDistX + Color.red(pixels[index + sourceWidth + 1]) * distX * distY;
        return Color.rgb((int) red, (int) green, (int) blue);
    }


    private void bilinear() {
        int[] tmp = new int[newWidth * newHeight];
        float scaleX = (sourceWidth * 1f - 1) / newWidth;
        float scaleY = (sourceHeight * 1f - 1) / newHeight;
        int offset = 0;
        for (int i = 0; i < newHeight; i++) {
            for (int j = 0; j < newWidth; j++) {
                int x = (int) (scaleX * j);
                int y = (int) (scaleY * i);
                float distX = (scaleX * j) - x;
                float distY = (scaleY * i) - y;
                tmp[offset] = getColorAround(y * sourceWidth + x, distX, distY);
                offset++;
            }
        }
        highQuality = tmp;
    }

    private void nearestNeighbour() {
        for (int i = 0; i < newWidth; i++) {
            for (int j = 0; j < newHeight; j++) {
                lowQuality[i + j * newWidth] = pixels[(int)(j * scale) * sourceWidth + (int) (i * scale)];
            }
        }
    }

    private int getColor(int mode, int i) {
        int alpha, red,blue, green;
        if (mode == 1) {
            alpha = Color.alpha(lowQuality[i]);
            red = Math.min(Color.red(lowQuality[i]) * 2, 255);
            blue = Math.min(Color.blue(lowQuality[i]) * 2, 255);
            green = Math.min(Color.green(lowQuality[i]) * 2, 255);
        } else {
            alpha = Color.alpha(highQuality[i]);
            red = Math.min(Color.red(highQuality[i]) * 2, 255);
            blue = Math.min(Color.blue(highQuality[i]) * 2, 255);
            green = Math.min(Color.green(highQuality[i]) * 2, 255);
        }
        return Color.argb(alpha, red, green, blue);
    }

    private void rotateAndBright() {
        int[] rotateAndBrightedPixels2 = new int[newWidth * newHeight];
        int[] rotateAndBrightedPixels = new int[newWidth * newHeight];
        for (int i = 0; i < newHeight * newWidth; i++) {
            rotateAndBrightedPixels[(i % newWidth + 1) * newHeight - (i / newWidth + 1)] = getColor(1, i);
            rotateAndBrightedPixels2[(i % newWidth + 1) * newHeight - (i / newWidth + 1)] = getColor(0, i);
        }

        int tmp = newHeight;
        newHeight = newWidth;
        newWidth = tmp;

        lowQuality = rotateAndBrightedPixels;
        highQuality = rotateAndBrightedPixels2;
    }
}