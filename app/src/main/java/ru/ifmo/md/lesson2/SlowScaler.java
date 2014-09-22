package ru.ifmo.md.lesson2;

import android.graphics.Bitmap;

/**
 * Bitmap scaler with good quality.
 *
 * @author Zakhar Voit (zakharvoit@gmail.com)
 */
public class SlowScaler implements Scaler {
    @Override
    public Bitmap scale(Bitmap bitmap, float scale) {
        int newWidth = (int) ((bitmap.getWidth() - 1) / scale + 1);
        int newHeight = (int) ((bitmap.getHeight() - 1) / scale + 1);

        int[] source = new int[bitmap.getWidth() * bitmap.getHeight()];
        bitmap.getPixels(source, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());

        int[] red = new int[newWidth * newHeight];
        int[] green = new int[newWidth * newHeight];
        int[] blue = new int[newWidth * newHeight];
        int[] count = new int[newWidth * newHeight];

        for (int i = 0; i < bitmap.getWidth(); i++) {
            for (int j = 0; j < bitmap.getHeight(); j++) {
                int newI = (int) (i / scale);
                int newJ = (int) (j / scale);
                int pos = newI + newWidth * newJ;
                int color = bitmap.getPixel(i, j);

                red[pos] += (color >> 16) & 0xFF;
                green[pos] += (color >> 8) & 0xFF;
                blue[pos] += color & 0xFF;
                count[pos]++;
            }
        }

        int[] result = new int[newWidth * newHeight];
        for (int pos = 0; pos < newWidth * newHeight; pos++) {
            red[pos] /= count[pos];
            green[pos] /= count[pos];
            blue[pos] /= count[pos];
            result[pos] = (red[pos] << 16) | (green[pos] << 8) | blue[pos];
        }

        return Bitmap.createBitmap(result, newWidth, newHeight, Bitmap.Config.RGB_565);
    }
}
