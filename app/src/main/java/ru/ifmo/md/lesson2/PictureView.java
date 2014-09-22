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

    public PictureView (Context context){
        super(context);
        BitmapFactory.Options option = new BitmapFactory.Options();
        option.inScaled = false;
        bmp = BitmapFactory.decodeResource(res, R.drawable.source, option);
        width = bmp.getWidth();
        height = bmp.getHeight();
        image = new int[width * height];
        image2 = new int[width * height];

        for(int i = 0; i < 100; i++)
            image[100 + i] = Color.WHITE;
        bmp.getPixels(image, 0, width, 0, 0, width, height);

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



        image = rotate(image);
        changeBrithness(image, 2.0);
        System.arraycopy(image,0,image2,0,image.length);


        image = fastScale(image,1.73, 1.73);
        image2 = fastScale(image2,1.73, 1.73);
        width = (int)Math.ceil(width/1.73);
        height = (int)Math.ceil(height/1.73);

        p_image = image;
    }

    void changeBrithness(int array[], double percent){
        for(int i = 0; i < array.length; ++i){
            int alpha = Color.alpha(array[i]);
            int red = Color.red(array[i]);
            int green = Color.green(array[i]);
            int blue = Color.blue(array[i]);
            red = (int)Math.min(red * percent, 255);
            green =(int) Math.min(green * percent, 255);
            blue = (int)Math.min(blue * percent, 255);
            array[i] = Color.argb(alpha,red,green,blue);
        }
    }

    int[] rotate(int array[]){

        int[] narray = new int[array.length];
        for(int i = 0; i < width; ++i){
            for(int j = 0; j < height; ++j) {
                narray[i * height + j] = array[width * (height - j - 1) + i];
            }
        }
        int tmp = width;
        width = height;
        height = tmp;
        return narray;
    }

    int[] fastScale(int array[],double w_scale, double h_scale) {
        int nWidth = (int)Math.ceil(width/1.73);
        int nHeight = (int)Math.ceil(height/1.73);

        int[] nImage = new int[nWidth * nHeight];
        double px, py;
        for(int i = 0; i < nHeight; i++){
            for(int j = 0; j < nWidth; j++){
                px = Math.floor(j * w_scale) ;
                py = Math.floor(i * h_scale) ;
                nImage[(i * nWidth) + j] = array[(int)((py * width) + px)] ;
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
        canvas.drawBitmap(p_image,0,width,0,0,width,height,true,null);
    }
}

