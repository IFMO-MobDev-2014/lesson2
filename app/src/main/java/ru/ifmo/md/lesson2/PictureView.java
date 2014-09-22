package ru.ifmo.md.lesson2;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.View;

/**
 * Created by vi34 on 20.09.14.
 */
public class PictureView extends View {
    private int width;
    private int height;
    private int p_image[];
    private int image[];
    private int image2[];
    private Bitmap bmp;
    private Resources res = getResources();
    boolean step = true;
    private static final double scale = 1.73;

    public PictureView(Context context) {
        super(context);
        BitmapFactory.Options option = new BitmapFactory.Options();
        option.inScaled = false;
        bmp = BitmapFactory.decodeResource(res, R.drawable.source, option);
        width = bmp.getWidth();
        height = bmp.getHeight();
        image = new int[width * height];
        image2 = new int[width * height];
        bmp.getPixels(image, 0, width, 0, 0, width, height);

        OnClickListener listener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (step) {
                    p_image = image;
                } else {
                    p_image = image2;
                }
                step = !step;
                invalidate();
            }
        };
        setOnClickListener(listener);
        image = rotate(image);
        changeBrightness(image, 2.0);
        System.arraycopy(image, 0, image2, 0, image.length);
        image = fastScale(image, scale, scale);
        image2 = slowScale(image2, scale, scale);
        width = (int) Math.ceil(width / scale);
        height = (int) Math.ceil(height / scale);
        p_image = image;
    }

    void changeBrightness(int array[], double percent) {
        for (int i = 0; i < array.length; ++i) {
            int alpha = Color.alpha(array[i]);
            int red = Color.red(array[i]);
            int green = Color.green(array[i]);
            int blue = Color.blue(array[i]);
            red = (int) Math.min(red * percent, 255);
            green = (int) Math.min(green * percent, 255);
            blue = (int) Math.min(blue * percent, 255);
            array[i] = Color.argb(alpha, red, green, blue);
        }
    }

    int[] rotate(int array[]) {
        int[] narray = new int[array.length];
        for (int i = 0; i < width; ++i) {
            for (int j = 0; j < height; ++j) {
                narray[i * height + j] = array[width * (height - j - 1) + i];
            }
        }
        int tmp = width;
        width = height;
        height = tmp;
        return narray;
    }

    int[] fastScale(int array[], double w_scale, double h_scale) {
        int nWidth = (int) Math.ceil(width / w_scale);
        int nHeight = (int) Math.ceil(height / h_scale);
        int[] nArray = new int[nWidth * nHeight];
        double px, py;
        for (int i = 0; i < nHeight; i++) {
            for (int j = 0; j < nWidth; j++) {
                px = Math.floor(j * w_scale);
                py = Math.floor(i * h_scale);
                nArray[(i * nWidth) + j] = array[(int) ((py * width) + px)];
            }

        }
        return nArray;
    }

    int[] slowScale(int array[], double w_scale, double h_scale) {
        int nWidth = (int) Math.ceil(width / w_scale);
        int nHeight = (int) Math.ceil(height / h_scale);
        int[] nArray = new int[nWidth * nHeight];
        int a, b, c, d, px, py, index;
        double x_ratio = ((double) (width - 1)) / nWidth;
        double y_ratio = ((double) (height - 1)) / nHeight;
        double x_diff, y_diff, diff1, diff2, diff3, diff4;

        int red, green, blue;
        int offset = 0;
        for (int i = 0; i < nHeight; i++) {
            for (int j = 0; j < nWidth; j++) {
                px = (int) (x_ratio * j);
                py = (int) (y_ratio * i);
                x_diff = (x_ratio * j) - px;
                y_diff = (y_ratio * i) - py;
                index = (py * width + px);
                a = array[index];
                b = array[index + 1];
                c = array[index + width];
                d = array[index + width + 1];
                diff1 = (1 - x_diff) * (1 - y_diff);
                diff2 = (1 - y_diff) * x_diff;
                diff3 = y_diff * (1 - x_diff);
                diff4 = x_diff * y_diff;
                red = (int) (Color.red(a) * diff1 + Color.red(b) * diff2 +
                        Color.red(c) * diff3 + Color.red(d) * diff4);
                green = (int) (Color.green(a) * diff1 + Color.green(b) * diff2 +
                        Color.green(c) * diff3 + Color.green(d) * diff4);
                blue = (int) (Color.blue(a) * diff1 + Color.blue(b) * diff2 +
                        Color.blue(c) * diff3 + Color.blue(d) * diff4);
                nArray[offset++] = Color.argb(Color.alpha(a), red, green, blue);
            }
        }
        return nArray;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(p_image, 0, width, 0, 0, width, height, true, null);
    }
}

