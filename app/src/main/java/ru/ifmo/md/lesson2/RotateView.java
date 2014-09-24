package ru.ifmo.md.lesson2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Adel on 23.09.14.
 */
public class RotateView extends View {
    public final static int width = 700;
    public final static int height = 750;
    public final static int newWidth = 405;
    public final static int newHeight = 434;
    public boolean good = false;
    Bitmap picture;
    int[] originalPicBitMap = new int[width * height];
    int[] compressedFastPicture = new int[newWidth * newHeight];
    int[] compressedGoodPicture = new int[newWidth * newHeight];
    int[] outputPicture = new int[newWidth * newHeight];

    public RotateView(Context context) {
        super(context);
        getImage();
    }

    public void getImage() {
        picture = BitmapFactory.decodeResource(getResources(), R.drawable.source);
        picture.getPixels(originalPicBitMap, 0, width, 0, 0, width, height);
        makeBright();
        fastPicture();
        goodPicture();
        turn();
    }

    public void makeBright() {
        for (int i = 0; i < height; ++i) {
            for (int j = 0; j < width; ++j) {
                int color = picture.getPixel(j, i);
                int b = Math.min(255, 2 * (color & 0xFF));
                int g = Math.min(255, 2 * ((color >> 8) & 0xFF));
                int r = Math.min(255, 2 * ((color >> 16) & 0xFF));
                int alpha = Math.min(255, ((color >> 24) & 0xFF));
                originalPicBitMap[width * i + j] = Color.argb(alpha, r, g, b);
            }
        }
    }

    public void fastPicture() {
        int[] tmp = new int[newHeight * newWidth];
        for (int i = 0; i < newHeight; ++i)
            for (int j = 0; j < newWidth; ++j) {
                tmp[newWidth * i + j] = originalPicBitMap[(int) (j * ((double) width / (double) newWidth))+(int) (i * ((double) height / (double) newHeight)) * width];
            }
        compressedFastPicture = tmp;
    }

    public void goodPicture() {
        int[] cnt = new int[newHeight * newWidth];
        int[][] tmp = new int[newHeight * newWidth][4];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int x = (int) (j * ((double) newWidth / (double) width));
                int y = (int) (i * ((double) newHeight / (double) height));
                int color = originalPicBitMap[i * width + j];
                tmp[y * newWidth + x][0] += color & 0xFF;
                tmp[y * newWidth + x][1] += (color >> 8) & 0xFF;
                tmp[y * newWidth + x][2] += (color >> 16) & 0xFF;
                tmp[y * newWidth + x][3] += (color >> 24) & 0xFF;
                cnt[y * newWidth + x]++;
            }
        }
        for (int i = 0; i < newHeight; ++i) {
            for (int j = 0; j < newWidth; ++j) {
                for (int k = 0; k < 4; ++k)
                    tmp[i * newWidth + j][k] /= Math.max(cnt[i * newWidth + j], 1);
                compressedGoodPicture[i * newWidth + j] = Color.argb(tmp[i * newWidth + j][3], tmp[i * newWidth + j][2],
                        tmp[i * newWidth + j][1], tmp[i * newWidth + j][0]);
            }
        }
    }

    public void turn() {
        int[] tmpFast = new int[newHeight * newWidth];
        int[] tmpGood = new int[newHeight * newWidth];
        for (int i = 0; i < newHeight; ++i)
            for (int j = 0; j < newWidth; ++j) {
                tmpFast[j * newHeight + newHeight - i - 1] = compressedFastPicture[i * newWidth + j];
                tmpGood[j * newHeight + newHeight - i - 1] = compressedGoodPicture[i * newWidth + j];
            }
        compressedFastPicture = tmpFast;
        compressedGoodPicture = tmpGood;
    }

    public void change() {
        if (good)
            outputPicture = compressedGoodPicture;
        else
            outputPicture = compressedFastPicture;
    }

    @Override
    public void onDraw(Canvas canvas) {
        canvas.drawBitmap(outputPicture, 0, newHeight, 0, 0, newHeight, newWidth, (picture.getConfig() == Bitmap.Config.ARGB_8888), null);
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        if (action == MotionEvent.ACTION_DOWN) {
            good ^= true;
            change();
        }
        return true;
    }
}
