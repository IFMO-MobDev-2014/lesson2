package ru.ifmo.md.lesson2;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.view.View;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * Created by nststnchl on 22.09.14.
 */
public class MyImageView extends View {
    public MyImageView(Context context) {
        super(context);
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.source);
        bitmap.getPixels(initialPixels, 0, WIDTH, 0, 0, WIDTH, HEIGHT);
        makingBright();
    }

    Bitmap bitmap;
    public final static int HEIGHT = 750;
    public final static int WIDTH = 700;
    public final static int NEW_HEIGHT = 434;
    public final static int NEW_WIDTH = 405;
    int[] initialPixels = new int[HEIGHT * WIDTH];

    public int[] fastScaling() {
        int[] newPixels = new int[NEW_HEIGHT * NEW_WIDTH];
        for (int i = 0; i < NEW_HEIGHT; i++) {
            for (int j = 0; j < NEW_WIDTH; j++) {
                int x = (int) (i * ((double) HEIGHT / (double) NEW_HEIGHT));
                int y = (int) (j * ((double) WIDTH / (double) NEW_WIDTH));
                newPixels[i * NEW_WIDTH + j] = initialPixels[x * WIDTH + y];
            }
        }
        return rotate(newPixels);
    }

    public int[] smartScaling() {
        int[] newPixels = new int[NEW_HEIGHT * NEW_WIDTH];
        for (int i = 0; i < NEW_HEIGHT; i++) {
            for (int j = 0; j < NEW_WIDTH; j++) {
                int x = (int) (i * ((double) HEIGHT / (double) NEW_HEIGHT));
                int y = (int) (j * ((double) WIDTH / (double) NEW_WIDTH));
                int color1 = initialPixels[x * WIDTH + y];
                int color2 = (y > 0) ? initialPixels[x * WIDTH + y - 1] : 0;
                int color3 = (y + 1 < WIDTH) ? initialPixels[x * WIDTH + y + 1] : 0;
                int cnt = 1;
                cnt += (color2 == 0) ? 0 : 1;
                cnt += (color3 == 0) ? 0 : 1;
                int red = (color1 >> 16) & 0xFF;
                red += (color2 == 0) ? 0 : (color2 >> 16) & 0xFF;
                red += (color3 == 0) ? 0 : (color3 >> 16) & 0xFF;
                int green = (color1 >> 8) & 0xFF;
                green += (color2 == 0) ? 0 : (color2 >> 8) & 0xFF;
                green += (color3 == 0) ? 0 : (color3 >> 8) & 0xFF;
                int blue = color1 & 0xFF;
                blue += (color2 == 0) ? 0 : color2 & 0xFF;
                blue += (color3 == 0) ? 0 : color3 & 0xFF;
                red /= cnt;
                green /= cnt;
                blue /= cnt;
                newPixels[i * NEW_WIDTH + j] = red << 16 | green << 8 | blue;
            }
        }
        return rotate(newPixels);
    }

    public void makingBright() {
        for (int i = 0; i < WIDTH * HEIGHT; i++) {
            int color = initialPixels[i];
            int red = (color >> 16) & 0xFF;
            int green = (color >> 8) & 0xFF;
            int blue = color & 0xFF;
            int k = 50;
            red += 50;
            green += 50;
            blue += 50;
            red = Math.min(red, 255);
            green = Math.min(green, 255);
            blue = Math.min(blue, 255);
            initialPixels[i] = red << 16 | green << 8 | blue;
        }
    }

    public int[] rotate(int[] newPixels) {
        int[] tmpPixels = new int[NEW_HEIGHT * NEW_WIDTH];
        for (int i = 0; i < NEW_WIDTH; i++) {
            for (int j = 0; j < NEW_HEIGHT; j++) {
                tmpPixels[i * NEW_HEIGHT + NEW_HEIGHT - j - 1] = newPixels[j * NEW_WIDTH + i];
            }
        }
        return tmpPixels;
    }

    boolean isFast = true;

    public void modeChange() {
        isFast = !isFast;
        invalidate();
    }

    @Override
    public void onDraw(Canvas canvas) {
        if (isFast) {
            canvas.drawBitmap(fastScaling(), 0, NEW_HEIGHT, 0, 0, NEW_HEIGHT, NEW_WIDTH, false, null);
        } else {
            canvas.drawBitmap(smartScaling(), 0, NEW_HEIGHT, 0, 0, NEW_HEIGHT, NEW_WIDTH, false, null);
        }
    }
}
