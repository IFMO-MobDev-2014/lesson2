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
    int[] countPixels = new int[newWidth * newHeight];
    int[][] palette = new int[newWidth * newHeight][4]; // 0 - alpha, 1 - red, 2 - green, 3 - blue
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

        if (bitmap.getConfig() == Bitmap.Config.ARGB_8888) {
            hasAlpha = true;
        }
    }

    private void makeImageBrighter() {
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


            colors[i] = (hasAlpha ? (alpha << 24) : 0) + (red << 16) + (green << 8) + blue;
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

    private void highQualityCompressor() {
        for (int i = 0; i < countPixels.length; i++) {
            countPixels[i] = 0;

            for (int c = 0; c < 4; c++) {
                palette[i][c] = 0;
            }
        }

        for (int j = 0; j < width; j++) {
            for (int i = 0; i < height; i++) {
                int iNew = (int) (i * ((double) (newHeight - 1) / (double) (height - 1)));
                int jNew = (int) (j * ((double) (newWidth - 1) / (double) (width - 1)));
                int currentColor = colors[j * height + i];
                palette[jNew * newHeight + iNew][0] += (hasAlpha ? ((currentColor & 0xFF000000) >> 24) : 0);
                palette[jNew * newHeight + iNew][1] += (currentColor & 0xFF0000) >> 16;
                palette[jNew * newHeight + iNew][2] += (currentColor & 0xFF00) >> 8;
                palette[jNew * newHeight + iNew][3] += currentColor & 0xFF;
                countPixels[jNew * newHeight + iNew]++;
            }
        }

        for (int j = 0; j < newWidth; j++) {
            for (int i = 0; i < newHeight; i++) {
                int divider = Math.max(countPixels[j * newHeight + i], 1);

                for (int c = 0; c < 4; c++) {
                    palette[j * newHeight + i][c] /= divider;
                }

                int alpha = palette[j * newHeight + i][0];
                int red = palette[j * newHeight + i][1];
                int green = palette[j * newHeight + i][2];
                int blue = palette[j * newHeight + i][3];
                highQualityCompressedColors[j * newHeight + i] = (hasAlpha ? (alpha << 24) : 0) + (red << 16) + (green << 8) + blue;
            }
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