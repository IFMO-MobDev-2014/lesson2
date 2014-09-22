package ru.ifmo.md.lesson2;

import android.graphics.Bitmap;

/**
 * Fast bitmap scaler.
 *
 * @author Zakhar Voit (zakharvoit@gmail.com)
 */
class FastScaler implements Scaler {
    @Override
    public Bitmap scale(Bitmap bitmap, float scale) {
		int newWidth = (int) ((bitmap.getWidth() - 1) / scale + 1);
		int newHeight = (int) ((bitmap.getHeight() - 1) / scale + 1);
        int[] source = new int[bitmap.getWidth() * bitmap.getHeight()];
        int[] result = new int[newWidth * newHeight];

        bitmap.getPixels(source, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());

		for (int i = 0; i < newWidth; i++) {
			for (int j = 0; j < newHeight; j++) {
				result[i + j * newWidth] = source[(int) (i * scale) + bitmap.getWidth() * ((int) (j * scale))];
			}
		}

		return Bitmap.createBitmap(result, newWidth, newHeight, Bitmap.Config.RGB_565);
    }
}
