package ru.ifmo.md.lesson2;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class MyActivity extends Activity {

    Bitmap bitmap;
    Bitmap fastBitmap;
    Bitmap highQualityBitmap;
    public static final int width = 700;
    public static final int height = 750;
    public static final int newWidth = 405;
    public static final int newHeight = 434;
    int[] colors = new int[width * height];
    int[] temporaryColors = new int[width * height];
    int[] fastCompressedColors = new int[newWidth * newHeight];
    int[] highQualityCompressedColors = new int[newWidth * newHeight];
    boolean hasAlpha = true;
    boolean change = false;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image);
        imageView = (ImageView) findViewById(R.id.imageView);
        decodeImage();
        makeImageBrighter();
        rotateImageClockwise();
        fastCompressor();
        highQualityCompressor();
        onClick(null);
    }

    private void decodeImage() {
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.source);
        bitmap.getPixels(colors, 0, width, 0, 0, width, height);
    }

    private void makeImageBrighter() {
        if (bitmap.getConfig() == Bitmap.Config.ARGB_8888) {
            hasAlpha = true;
        }

        for (int i = 0; i < colors.length; i++) {

            int alpha = hasAlpha ? (colors[i] & 0xFF000000) : 0;
            alpha >>= 24;
            int red = (colors[i] & 0xFF0000) >> 16;
            int green = (colors[i] & 0xFF00) >> 8;
            int blue = colors[i] & 0xFF;

            if (Math.max(red, Math.max(green, blue)) < 64) {
                red *= 2;
                green *= 2;
                blue *= 2;
            } else {
                red = (int) (red * 1.0 / Math.sqrt(red / 255.0));
                green = (int) (green * 1.0 / Math.sqrt(green / 255.0));
                blue = (int) (blue * 1.0 / Math.sqrt(blue / 255.0));
            }


            colors[i] = (alpha << 24) + (red << 16) + (green << 8) + blue;
        }
    }

    private void rotateImageClockwise() {
        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                temporaryColors[(height - j - 1) + i * height] = colors[i + j * width];
            }
        }

        System.arraycopy(temporaryColors, 0, colors, 0, width * height);
    }

    private void fastCompressor() {
        for (int j = 0; j < newWidth; j++) {
            for (int i = 0; i < newHeight; i++) {
                fastCompressedColors[i + j * newHeight] = colors[i * (height - 1) / (newHeight - 1) + j * (width - 1) / (newWidth - 1) * height];
            }
        }

        fastBitmap = Bitmap.createBitmap(fastCompressedColors, newHeight, newWidth, bitmap.getConfig());
    }

    /*
    private void highQualityCompressor() {
        System.arraycopy(newColors, 0, highQualityCompressedColors, 0, newWidth * newHeight);

        for (int j = 0; j < newWidth; j++) {
            for (int i = 0; i < newHeight; i++) {
                int x = i * (height - 1) / (newHeight - 1);
                int y = j * (width - 1) / (newWidth - 1) * height;

                for (int di = -1; di < 2; di++) {
                    for (int dj = -1; dj < 2; dj++) {

                    }
                }
            }
        }
    }
    */

    private void highQualityCompressor() {
        for (int i = 0; i < highQualityCompressedColors.length; i++) {
            highQualityCompressedColors[i] = 0xFFFFFFFF;
        }
        highQualityBitmap = Bitmap.createBitmap(highQualityCompressedColors, newHeight, newWidth, bitmap.getConfig());
    }

    public void update() {
        imageView.setImageBitmap(change ? fastBitmap : highQualityBitmap);
    }

    public void onClick(View view) {
        change = !change;
        update();
    }
}