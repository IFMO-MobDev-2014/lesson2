package ru.ifmo.md.lesson2;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

/**
 * Created by vi34 on 20.09.14.
 */
public class PictureView extends View {
    private int width;
    private int height;
    private int pixels[];
    private Bitmap bmp;
    private Resources res = getResources();

    public PictureView (Context context){
        super(context);
        bmp = BitmapFactory.decodeResource(res, R.drawable.source);
        width = bmp.getWidth();
        height = bmp.getHeight();
        pixels = new int[width * height];
        for(int i = 0; i < 100; i++)
            pixels[100 + i] = Color.WHITE;
        bmp.getPixels(pixels, 0, width, 0, 0, width,height);
        change_brithness(pixels, 2.0);

    }

    void change_brithness(int array[], double percent){
        for(int i = 0; i < array.length; ++i)
        {
            int alpha = Color.alpha(array[i]);
            int red = Color.red(array[i]);
            int green = Color.green(array[i]);
            int blue = Color.blue(array[i]);
            red *= percent;
            green *= percent;
            blue *= percent;
            if(red > 255)
                red = 255;
            if(green > 255)
                green = 255;
            if(blue > 255)
                blue = 255;
            array[i] = Color.argb(alpha,red,green,blue);

        }
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        //width = w;
        //height = h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(pixels,0,width,0,0,width,height,true,null);
    }
}

