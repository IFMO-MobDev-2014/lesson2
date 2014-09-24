package ru.ifmo.md.lesson2;

import android.graphics.Bitmap;
import android.graphics.Color;

/**
 * Created by dimatomp on 23.09.14.
 */
public class LossyLoader extends ImageLoader {
    @Override
    public Bitmap transformBitmap(Bitmap from) {
        final int fWidth = from.getWidth();
        final int fHeight = from.getHeight();
        int[] defPixels = new int[fWidth * fHeight];
        int[] pixels = new int[WIDTH * HEIGHT];
        from.getPixels(defPixels, 0, fWidth, 0, 0, fWidth, fHeight);
        for (int j = 0; j < HEIGHT; j++) {
            int x = j * fWidth / HEIGHT;
            for (int i = 0; i < WIDTH; i++) {
                int src = defPixels[x + (fHeight - i * fHeight / WIDTH - 1) * fWidth];
                pixels[j * WIDTH + i] = convertColor(Color.red(src), Color.green(src), Color.blue(src));
            }
        }
        Bitmap result = Bitmap.createBitmap(WIDTH, HEIGHT, Bitmap.Config.ARGB_8888);
        result.setPixels(pixels, 0, WIDTH, 0, 0, WIDTH, HEIGHT);
        return result;
    }
}
