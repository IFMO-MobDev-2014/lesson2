package com.dmitry2537.rotator;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import java.util.Collections;


public class RotatorAndShrinker{

    Bitmap bitmap, fastShr, shr;
    int height, width, newWidth, newHeight;
    final float factor = 1.73f;

    int[] picture;

    boolean goodShrink = true;

    public Bitmap next() {
        goodShrink = !goodShrink;
        if (goodShrink) {
            if (shr == null) {
                shr = shrink();
                return  shr;
            } else {
                return shr;
            }
        } else {
            if (fastShr == null) {
                fastShr = fastShrink();
                return fastShr;
            } else {
                return fastShr;
            }

        }
    }

    Bitmap fastShrink() {
        int r, g, b;
        int[] fastShrp = new int[newWidth * newHeight];
        int[] fastShrpRotate = new int[newWidth * newHeight];

        for (int i = 0; i < newHeight; i++) {
            for (int j = 0; j < newWidth; j++) {
                fastShrp[i * newWidth + j] = picture[(int) (i * factor) * width + (int) (j * factor)];
                b = (fastShrp[i * newWidth + j]) & 0xFF;
                g = (fastShrp[i * newWidth + j] >> 8) & 0xFF;
                r = (fastShrp[i * newWidth + j] >> 16) & 0xFF;
                if(r * 2 > 255){
                    r = 255;
                }
                else{
                    r *= 2;
                }
                if(g * 2 > 255){
                    g = 255;
                }
                else{
                    g *= 2;
                }
                if(b * 2 > 255){
                    b = 255;
                }
                else{
                    b *= 2;
                }
                fastShrp[i * newWidth + j] = (255 << 24) | (r << 16) | (g << 8) | b;
                fastShrpRotate[j * newHeight + newHeight - i - 1] = fastShrp[i * newWidth + j];

            }
        }
        fastShr = Bitmap.createBitmap(newHeight, newWidth, Bitmap.Config.RGB_565);
        fastShr.setPixels(fastShrpRotate, 0, newHeight, 0, 0, newHeight, newWidth);
        return fastShr;
    }

    Bitmap shrink() {
        int r, g, b;
        int[] slowShr = new int[newWidth * newHeight];
        int[] slowShrpRotate = new int[newWidth * newHeight];
        for (int i = 0; i < newHeight; i++) {
            for (int j = 0; j < newWidth; j++) {
                slowShr[i * newWidth + j] = bilinearInterpolation(j, i);
                b = (slowShr[i * newWidth + j]) & 0xFF;
                g = (slowShr[i * newWidth + j] >> 8) & 0xFF;
                r = (slowShr[i * newWidth + j] >> 16) & 0xFF;
                if(r * 2 > 255){
                    r = 255;
                }
                else{
                    r *= 2;
                }
                if(g * 2 > 255){
                    g = 255;
                }
                else{
                    g *= 2;
                }
                if(b * 2 > 255){
                    b = 255;
                }
                else{
                    b *= 2;
                }
                slowShr[i * newWidth + j] = (255 << 24) | (r << 16) | (g << 8) | b;
                slowShrpRotate[j * newHeight + newHeight - i - 1] = slowShr[i * newWidth + j];
            }
        }
        shr = Bitmap.createBitmap(newHeight, newWidth, Bitmap.Config.RGB_565);
        shr.setPixels(slowShrpRotate, 0, newHeight, 0, 0, newHeight, newWidth);
        return shr;
    }

    int bilinearInterpolation(int newX, int newY) {
        int ans = 0, tmp;
        int[] c = new int[4];
        double x = newX * factor;
        double y = newY * factor;
        double dx = x - Math.floor(x);
        double dy = y - Math.floor(y);
        int f00 = picture[((int) y * width + (int) x)];
        int f01 = picture[((int) (y + 1) * width + (int) x)];
        int f10 = picture[((int) y * width + (int) (x + 1))];
        int f11 = picture[((int) (y + 1) * width + (int) (x + 1))];

        c[0] = f00 & 0xFF;
        c[1] = f01 & 0xFF;
        c[2] = f10 & 0xFF;
        c[3] = f11 & 0xFF;
        ans = Math.min((int) ((c[0] * (1 - dx) * (1 - dy))
                + (c[1] * (1 - dx) * dy) + (c[2] * dx * (1 - dy)) + (c[3]
                * dx * dy)), 255);

        c[0] = (f00 >> 8) & 0xFF;
        c[1] = (f01 >> 8) & 0xFF;
        c[2] = (f10 >> 8) & 0xFF;
        c[3] = (f11 >> 8) & 0xFF;
        tmp = Math.min((int) ((c[0] * (1 - dx) * (1 - dy))
                + (c[1] * (1 - dx) * dy) + (c[2] * dx * (1 - dy)) + (c[3]
                * dx * dy)), 255);
        ans += (tmp << 8);

        c[0] = (f00 >> 16) & 0xFF;
        c[1] = (f01 >> 16) & 0xFF;
        c[2] = (f10 >> 16) & 0xFF;
        c[3] = (f11 >> 16) & 0xFF;
        tmp = Math.min((int) ((c[0] * (1 - dx) * (1 - dy))
                + (c[1] * (1 - dx) * dy) + (c[2] * dx * (1 - dy)) + (c[3]
                * dx * dy)), 255);
        ans += (tmp << 16);

        c[0] = (f00 >>> 24) & 0xFF;
        c[1] = (f01 >>> 24) & 0xFF;
        c[2] = (f10 >>> 24) & 0xFF;
        c[3] = (f11 >>> 24) & 0xFF;
        tmp = Math.min((int) ((c[0] * (1 - dx) * (1 - dy))
                + (c[1] * (1 - dx) * dy) + (c[2] * dx * (1 - dy)) + (c[3]
                * dx * dy)), 255);
        ans += (tmp << 24);

        return ans;
    }

    public RotatorAndShrinker(Bitmap main) {

        bitmap = main;
        height = bitmap.getHeight();
        width = bitmap.getWidth();
        newHeight = (int)(height / factor);
        newWidth = (int)(width / factor);
        picture = new int[height * width];
        bitmap.getPixels(picture, 0, width, 0, 0, width, height);
        fastShr = null;
        shr = null;
    }

}