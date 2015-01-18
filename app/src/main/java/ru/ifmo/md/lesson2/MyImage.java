package ru.ifmo.md.lesson2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.view.View;

public class MyImage extends View {
    Matrix m = new Matrix();
    Bitmap bitmap;
    Bitmap bitmap2;
    boolean flag = true;


    public MyImage(Context context) {
        super(context);
        init();
        this.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                bitmap2 = flag ? QualityScale(bitmap) : FastScale(bitmap);
                flag = !flag;
                invalidate();
            }
        });
    }

    private void init() {
        bitmap = RotateCW(BitmapFactory.decodeResource(getResources(), R.drawable.source)).copy(Bitmap.Config.ARGB_8888, true);
        bitmap = (Brightness(bitmap));
        bitmap2 = FastScale(bitmap);
    }

    public void resume() {
        invalidate();
    }

    Bitmap RotateCW(Bitmap b) {
        int width = b.getWidth();
        int height = b.getHeight();
        int[] pixels = new int[width * height];
        b.getPixels(pixels, 0, width, 0, 0, width, height);
        int[] newColors = new int[width * height];
        for (int y = 0; y < width; y++) {
            for (int x = 0; x < height; x++) {
                newColors[y * height + x] = pixels[y + (height - 1 - x) * width];
            }
        }
        return Bitmap.createBitmap(newColors, height, width, Bitmap.Config.ARGB_8888);
    }
    Bitmap Brightness(Bitmap b) {
        int width = b.getWidth();
        int height = b.getHeight();
        int[] pixels = new int[width * height];
        b.getPixels(pixels, 0, width, 0, 0, width, height);
        for (int i = 0; i < width * height; i++) {
            int white = 0xff;
            int newRed = (((pixels[i] >> 8 * 2) & white) + white) >> 1;
            int newGreen = (((pixels[i] >> 8) & white) + white) >> 1;
            int newBlue = ((pixels[i] & white) + white) >> 1;
            pixels[i] = (white << 8 * 3) | (newRed << 8 * 2) | (newGreen << 8) | newBlue;
        }
        return Bitmap.createBitmap(pixels, width, height, Bitmap.Config.ARGB_8888);
    }

    Bitmap FastScale(Bitmap b) {
        int intFactor = (int) (1.73f * 100);
        int width = b.getWidth();
        int height = b.getHeight();
        int[] pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        int newWidth = width * 100 / intFactor;
        int newHeight = height * 100 / intFactor;
        int[] newColors = new int[newWidth * newHeight];
        for (int y = 0; y < newHeight; y++) {
            for (int x = 0; x < newWidth; x++) {
                newColors[y * newWidth + x] = pixels[(y * intFactor / 100) * width + (x * intFactor / 100)];
            }
        }
        return Bitmap.createBitmap(newColors, newWidth, newHeight, Bitmap.Config.ARGB_8888);
    }

    Bitmap QualityScale(Bitmap b) {
        int width = b.getWidth();
        int height = b.getHeight();
        int New_Width = (int) (width / 1.73f);
        int New_Height = (int) (height / 1.73f);
        int[] newColors = new int[New_Width * New_Height];
        for (int y = 0; y < New_Height; y++) {
            for (int x = 0; x < New_Width; x++) {
                int x1 = (int) (x * 1.73f);
                int y1 = (int) (y * 1.73f);
                int x2 = (int) ((x + 1) * 1.73f - 0.01f);
                int y2 = (int) ((y + 1) * 1.73f - 0.01f);
                int dx = x2 - x1 + 1;
                int dy = y2 - y1 + 1;
                int[] c = new int[dx * dy];
                int k = 0;
                for (int xi = x1; xi <= x2; xi++) {
                    for (int yi = y1; yi <= y2; yi++) {
                        c[k++] = b.getPixel(xi, yi);
                    }
                }
                int red = 0;
                int green = 0;
                int blue = 0;
                for (int i = 0; i < k; i++) {
                    red += Color.red(c[i]);
                    green += Color.green(c[i]);
                    blue += Color.blue(c[i]);
                }
                newColors[y * New_Width + x] = Color.rgb(red / k, green / k, blue / k);
            }
        }
        return Bitmap.createBitmap(newColors, New_Width, New_Height, Bitmap.Config.ARGB_8888);
    }
    @Override
    public void onDraw(Canvas canvas) {
        canvas.drawBitmap(bitmap2, m, null);
    }
}