package ru.ifmo.md.lesson2;

/**
 * Created by Alex on 21.09.14.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.view.*;

public class ImageCompressor extends View {
    Bitmap bitmap;
    public static final int width = 700;
    public static final int height = 750;
    public static final int newWidth = 405;
    public static final int newHeight = 434;
    int[] colors = new int[width * height];
    int[] newColors = new int[newWidth * newHeight];
    int[] temporaryColors = new int[width * height];
    boolean hasAlpha = true;

    public ImageCompressor(Context context) {
        super(context);
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
                newColors[i + j * newHeight] = colors[i * (height - 1) / (newHeight - 1) + j * (width - 1) / (newWidth - 1) * height];
            }
        }
    }

    @Override
    public void draw(Canvas canvas) {
        decodeImage();
        makeImageBrighter();
        rotateImageClockwise();
        fastCompressor();
        canvas.drawBitmap(newColors, 0, newHeight, 400, 600, newHeight, newWidth, hasAlpha, null);
    }
}
