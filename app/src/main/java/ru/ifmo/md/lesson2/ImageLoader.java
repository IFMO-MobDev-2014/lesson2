package ru.ifmo.md.lesson2;

import android.graphics.Bitmap;
import android.graphics.Color;

/**
 * Created by nagibator2005 on 2014-09-24.
 */
public class ImageLoader implements Runnable {
    private Bitmap sourceImage = null;
    private Bitmap resizedFast = null;
    private Bitmap resizedNice = null;

    private int rWidth = 405;
    private int rHeight = 434;

    public ImageLoader(Bitmap source) {
        sourceImage = source;
    }

    public Bitmap getFastImage() {
        return resizedFast;
    }

    public Bitmap getNiceImage() {
        return resizedNice;
    }

    public void run() {
        resizedFast = twiceLighter(rotateRight(resizeFast(sourceImage, rWidth, rHeight)));
        resizedNice = twiceLighter(rotateRight(resizeNice(sourceImage, rWidth, rHeight)));
    }

    public static Bitmap resizeFast(Bitmap source, int rWidth, int rHeight) {
        int sWidth = source.getWidth(), sHeight = source.getHeight();
        Bitmap resizedFast = Bitmap.createBitmap(rWidth, rHeight, source.getConfig());
        int[] srcData = new int[sWidth * sHeight];
        int[] fastData = new int[rWidth * rHeight];
        source.getPixels(srcData, 0, sWidth, 0, 0, sWidth, sHeight);
        int cntr = 0;
        for (int y = 0; y < rHeight; y++) {
            int sx, sy;
            sy = y * sHeight / rHeight;
            for (int x = 0; x < rWidth; x++) {
                sx = x * sWidth / rWidth;
                fastData[cntr] = srcData[sy * sWidth + sx];
                cntr++;
            }
        }
        resizedFast.setPixels(fastData, 0, rWidth, 0, 0, rWidth, rHeight);
        return resizedFast;
    }

    public static Bitmap resizeNice(Bitmap source, int rWidth, int rHeight) {
        int sWidth = source.getWidth(), sHeight = source.getHeight();
        Bitmap resizedNice = Bitmap.createBitmap(rWidth, rHeight, source.getConfig());
        int[] srcData = new int[sWidth * sHeight];
        int[] niceData = new int[rWidth * rHeight];
        source.getPixels(srcData, 0, sWidth, 0, 0, sWidth, sHeight);
        int cntr = 0;
        int sx, sy;
        int ex, ey;
        int sourceX, sourceY;
        for (int y = 0; y < rHeight; y++) {
            sy = y * sHeight;
            ey = (y + 1) * sHeight;
            for (int x = 0; x < rWidth; x++) {
                sx = x * sWidth;
                ex = (x + 1) * sWidth;
                int a = 0, r = 0, g = 0, b = 0;
                int npix = 0;
                int xCoef, yCoef, coef;
                for (int cx = sx; cx < ex; cx += rWidth) {
                    xCoef = rWidth - cx % rWidth;
                    if (ex - cx <= rWidth) {
                        xCoef = ex - cx - 1;
                    }
                    sourceX = cx / rWidth;
                    for (int cy = sy; cy < ey; cy += rHeight) {
                        yCoef = rHeight - cy % rHeight;
                        if (ey - cy <= rHeight) {
                            yCoef = ey - cy - 1;
                        }
                        sourceY = cy / rHeight;
                        int curColor = srcData[sourceY * sWidth + sourceX];
                        coef = xCoef * yCoef;
                        npix += coef;
                        b += (curColor & 0xFF) * coef;
                        g += ((curColor >>> 8) & 0xFF) * coef;
                        r += ((curColor >>> 16) & 0xFF) * coef;
                        a += ((curColor >>> 24) & 0xFF) * coef;
                        if (yCoef != rHeight) {
                            cy = cy / rHeight * rHeight;
                        }
                    }
                    if (xCoef != rWidth) {
                        cx = cx / rWidth * rWidth;
                    }
                }

                niceData[cntr] = (b / npix) | ((g / npix) << 8) | ((r / npix) << 16) | (a << 24);
                cntr++;
            }
        }
        resizedNice.setPixels(niceData, 0, rWidth, 0, 0, rWidth, rHeight);
        return resizedNice;
    }

    public static Bitmap rotateRight(Bitmap source) {
        int sWidth = source.getWidth();
        int sHeight = source.getHeight();
        Bitmap result = Bitmap.createBitmap(sHeight, sWidth, source.getConfig());
        int cntr = 0;
        int srcData[] = new int[sWidth * sHeight];
        int resData[] = new int[sWidth * sHeight];
        source.getPixels(srcData, 0, sWidth, 0, 0, sWidth, sHeight);
        for (int x = 0; x < sWidth; x++) {
            for (int y = sHeight - 1; y >= 0; y--) {
                resData[cntr] = srcData[y * sWidth + x];
                cntr++;
            }
        }
        result.setPixels(resData, 0, sHeight, 0, 0, sHeight, sWidth);
        return result;
    }

    public static Bitmap twiceLighter(Bitmap source) {
        int sWidth = source.getWidth();
        int sHeight = source.getHeight();
        int totalSize = sWidth * sHeight;
        Bitmap result = Bitmap.createBitmap(sWidth, sHeight, source.getConfig());
        int srcData[] = new int[sWidth * sHeight];
        int resData[] = new int[sWidth * sHeight];
        source.getPixels(srcData, 0, sWidth, 0, 0, sWidth, sHeight);
        float HSV[] = new float[3];
        for (int i = 0; i < totalSize; i++) {
            int b = srcData[i] & 0xFF;
            int g = (srcData[i] >>> 8) & 0xFF;
            int r = (srcData[i] >>> 16) & 0xFF;
            Color.RGBToHSV(r, g, b, HSV);
            HSV[2] *= 2.0f;
            if (HSV[2] > 1.0f) {
                HSV[2] = 1.0f;
            }
            resData[i] = Color.HSVToColor(srcData[i] >>> 24, HSV);
        }
        result.setPixels(resData, 0, sWidth, 0, 0, sWidth, sHeight);
        return result;
    }
}
