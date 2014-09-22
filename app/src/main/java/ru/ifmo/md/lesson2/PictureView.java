package ru.ifmo.md.lesson2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.View;

/**
 * Created by Евгения on 22.09.2014.
 */
public class PictureView extends View {

    Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.source);
    float scale = 1.73f;
    int[] colorsFast = new int[(int) (bitmap.getWidth() * bitmap.getHeight() / (scale * scale))];
    int[] colorsGood = new int[(int) (bitmap.getWidth() * bitmap.getHeight() / (scale * scale))];
    int[] colorsFirst = new int[bitmap.getWidth() * bitmap.getHeight()];
    int width = bitmap.getWidth();
    int height = bitmap.getHeight();
    int newWidth = (int) (width / scale);
    int newHeight = (int) (height / scale);

    boolean goodQuality = true;

    public PictureView(Context context) {
        super(context);
        bitmap.getPixels(colorsFirst, 0, width, 0, 0, width, height);
        initImage();
    }

    private void initImage() {
        int colorsFastIndex = 0;
        int newY = 0;
        for (float oldY = 0; newY < newHeight; newY++, oldY += scale) {
            int newX = 0;
            for (float colorsXFirstIndex = 0; newX < newWidth; newX++, colorsXFirstIndex += scale) {
                int color = colorsFirst[(int) ((int) oldY * width + colorsXFirstIndex)];
                int b = Color.blue(color) * 2;
                int r = Color.red(color) * 2;
                int g = Color.green(color) * 2;
                colorsFast[colorsFastIndex++] = Color.rgb(r > 255 ? 255 : r, g > 255 ? 255 : g, b > 255 ? 255 : b);
            }
        }
        int colorsGoodIndex = 0;
        newY = 0;
        for (float oldY = 0; newY < newHeight; newY++, oldY += scale) {
            int newX = 0;
            for (float oldX = 0; newX < newWidth; newX++, oldX += scale) {
                int leftColor = colorsFirst[(int) ((int) oldY * width + oldX)];
                int rightColor = colorsFirst[(int) ((int) oldY * width + oldX) + 1];
                float fracLeft = oldX - (int) (oldX);
                float fracRight = 1 - fracLeft;
                int b = 2 * (int) (fracLeft * Color.blue(leftColor) + fracRight * Color.blue(rightColor));
                int g = 2 * (int) (fracLeft * Color.green(leftColor) + fracRight * Color.green(rightColor));
                int r = 2 * (int) (fracLeft * Color.red(leftColor) + fracRight * Color.red(rightColor));
                colorsGood[colorsGoodIndex++] = Color.rgb(r > 255 ? 255 : r, g > 255 ? 255 : g, b > 255 ? 255 : b);
            }
        }
        int[] rotateGood = new int[newWidth * newHeight];
        int[] rotateFast = new int[newWidth * newHeight];
        for (int y = 0; y < newHeight; y++)
            for (int x = 0; x < newWidth; x++) {
                rotateGood[newHeight * x + newHeight - y - 1] = colorsGood[y * newWidth + x];
                rotateFast[newHeight * x + newHeight - y - 1] = colorsFast[y * newWidth + x];
            }
        int t = newHeight;
        newHeight = newWidth;
        newWidth = t;
        colorsFast = rotateFast;
        colorsGood = rotateGood;
    }

    @Override
    public void onDraw(Canvas canvas) {
        if (goodQuality)
            canvas.drawBitmap(colorsGood, 0, newWidth, 0, 0, newWidth, newHeight, false, null);
        else
            canvas.drawBitmap(colorsFast, 0, newWidth, 0, 0, newWidth, newHeight, false, null);
    }

    public void changeQuality() {
        goodQuality = !goodQuality;
        invalidate();
    }
}
