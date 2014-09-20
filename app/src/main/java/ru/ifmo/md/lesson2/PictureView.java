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

import java.lang.reflect.Array;

/**
 * Created by vi34 on 20.09.14.
 */
public class PictureView extends View {
    private int width;
    private int height;
    private int nwidth;
    private int nheight;
    private int p_image[];
    private int image[];
    private int image2[];
    private Bitmap bmp;
    private Resources res = getResources();
    double perc = 1.0;
    boolean step = true;

    public PictureView (Context context){
        super(context);
        BitmapFactory.Options op = new BitmapFactory.Options();
        op.inScaled = false;
        bmp = BitmapFactory.decodeResource(res, R.drawable.source, op);
        width = bmp.getWidth();
        height = bmp.getHeight();
        image = new int[width * height];
        image2 = new int[width * height];

        for(int i = 0; i < 100; i++)
            image[100 + i] = Color.WHITE;
        bmp.getPixels(image, 0, width, 0, 0, width,height);
        System.arraycopy(image,0,image2,0,image.length);


        OnClickListener listener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(step){
                    p_image = image;
                }
                else {
                    p_image = image2;
                }
                step = !step;
                invalidate();
            }
        };

        setOnClickListener(listener);
        nwidth = (int)Math.ceil(width/1.73);
        nheight = (int)Math.ceil(height/1.73);
        image = fastScale(image,width,height,nwidth, nheight);

        changeBrithness(image, 2.0);
        p_image = image;
    }

    void changeBrithness(int array[], double percent){
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

    int[] fastScale(int array[],int oldWidth, int oldHeight, int newWidth, int newHeight) {
        int[] nImage = new int[newWidth * newHeight];
        double w_ratio = oldWidth / (double)newWidth;
        double h_ratio = oldHeight / (double)newHeight;
        double px, py;
        for(int i = 0; i < newHeight; i++){
            for(int j = 0; j < newWidth; j++){
                px = Math.floor(j * w_ratio) ;
                py = Math.floor(i * h_ratio) ;
                nImage[(i * newWidth) + j] = array[(int)((py * oldWidth)+px)] ;
            }

        }
        return nImage;
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        //width = w;
        //height = h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(p_image,0,nwidth,0,0,nwidth,nheight,true,null);
    }
}

