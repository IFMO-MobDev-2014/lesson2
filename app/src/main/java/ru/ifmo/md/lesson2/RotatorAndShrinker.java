package com.dmitry2537.rotator;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import java.util.Collections;


public class RotatorAndShrinker extends ImageView{
    final static int WIDTH = 700;
    final static int HEIGHT = 750;
    final static int newHEIGHT = 405;
    final static int newWIDTH = 434;
    final static float factor = 1.73f;

    int[] picture = new int[HEIGHT * WIDTH];
    int[] newPicture = new int[HEIGHT * WIDTH];
    int[] shrinkedPicture = new int[newHEIGHT * newWIDTH];

    boolean goodShrink = true;
    boolean rotated = false;

    private static void swap(int a,int b){
        int c = a;
        a = b;
        b = c;
    }

    public void rotate(){
        for(int i = 0;i < WIDTH;i++){
            for(int j = 0;j < HEIGHT;j++){
                int idx = j * WIDTH + i;
                int newIdx = (WIDTH - i - 1) * HEIGHT + j;
                newPicture[newIdx] = picture[idx];
            }
        }
        swap(HEIGHT,WIDTH);
        picture = newPicture;
        Bitmap tmp;
        if(goodShrink){
            tmp = shrink(newPicture);
            goodShrink = false;
        }
        else{
            tmp = fastShrink(newPicture);
            goodShrink = true;
        }
        setImageBitmap(tmp);
        invalidate();
    }


    private Bitmap fastShrink(int[] tmpPicture){
        int[] shr = new int[newWIDTH * newHEIGHT];
        int r,g,b;
        for(int i = 0;i < WIDTH;i++){
            for(int j = 0;j < HEIGHT;j++){
                int idx = j * WIDTH + i;
                int newIdx = (int) (j / factor) * newWIDTH + (int) (i / factor);
                r = (tmpPicture[idx]) & 255;
                g = (tmpPicture[idx] >> 8) & 255;
                b = (tmpPicture[idx] >> 16) & 255;

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

                tmpPicture[idx] = Color.argb(255, r ,g , b);
                shr[newIdx] = tmpPicture[idx];

            }
        }

        return Bitmap.createBitmap(shr,newWIDTH,newHEIGHT,Bitmap.Config.RGB_565);
    }

    Bitmap shrink(int[] tmpPicture){
        int[] shr = new int[newWIDTH * newHEIGHT];


        return Bitmap.createBitmap(shr,newWIDTH,newHEIGHT,Bitmap.Config.RGB_565);
    }

    public RotatorAndShrinker(Context context, AttributeSet attrs) {
        super(context, attrs);

        setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                rotate();
            }
        });

    }





}