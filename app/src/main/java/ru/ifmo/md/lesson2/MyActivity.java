package ru.ifmo.md.lesson2;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class MyActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Bitmap Img = BitmapFactory.decodeResource(getResources(), R.drawable.photo);
        Change(Bright(Rotate(Compress1(Img))), Bright(Rotate(Compress2(Img))), Img);
    }



    ImageButton button;
    boolean flag = true;

    public void Change(final Bitmap NewImg1, final Bitmap NewImg2, Bitmap Img){
        button = (ImageButton) findViewById(R.id.imageButton);
        button.setImageBitmap(Img);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(flag) {
                    button.setImageBitmap(NewImg1);
                }
                else button.setImageBitmap(NewImg2);
                flag = !flag;
            }
        });

    }

    public Bitmap Bright(Bitmap Img){
        Bitmap NewImg = Bitmap.createBitmap(Img.getWidth(), Img.getHeight(), Bitmap.Config.ARGB_8888);
        for(int j = 0; j < Img.getHeight(); ++j)
            for(int i = 0; i < Img.getWidth(); ++i){
                NewImg.setPixel(i, j, Color.argb(Color.alpha(Img.getPixel(i, j)),
                        Math.min(0xff, (Color.red(Img.getPixel(i, j)) + 10) * 3 / 2),
                        Math.min(0xff, (Color.green(Img.getPixel(i, j)) + 10) * 3 / 2),
                        Math.min(0xff, (Color.blue(Img.getPixel(i, j)) + 10) * 3 / 2)));
            }
        return  NewImg;
    }

    public Bitmap Rotate(Bitmap Img){
    int[] pix = new int[Img.getWidth()*Img.getHeight()];
    int c = 0;
    for(int i = Img.getWidth() - 1; i >=0; i--)
        for(int j = Img.getHeight() - 1; j >= 0; j--){
            pix[c] = Img.getPixel(i, j);
            c++;
        }
    return Bitmap.createBitmap(pix, Img.getHeight(), Img.getWidth(), Bitmap.Config.ARGB_8888);
    }

    public Bitmap Compress1(Bitmap Img){
        int NewWidth = Img.getWidth() * 100 / 173 , NewHeight = Img.getHeight() * 100 / 173, pix[] = new int [NewWidth * NewHeight];
        for(int i = 0; i < NewHeight; ++i)
            for(int j = 0; j < NewWidth; ++j) {
                pix[i * NewWidth + j] = Img.getPixel(Math.min(Img.getWidth() - 1, j * 173 / 100 ), Math.min(Img.getHeight() - 1, i * 173 / 100 ));
            };
    return Bitmap.createBitmap(pix, NewWidth, NewHeight, Bitmap.Config.ARGB_8888);
    }

    public class MyColor{
         int Alpha = 0;
         int Red = 0;
         int Green = 0;
         int Blue =0;
    };

    public Bitmap Compress2(Bitmap Img){
        int NewWidth = Img.getWidth() * 100 / 173 , NewHeight = Img.getHeight() * 100 / 173, pix[] = new int [NewWidth * NewHeight];
        for(int i = 0; i < NewHeight; i++)
            for(int j = 0; j < NewWidth; j++){
                MyColor color = new MyColor();
                int count = 0;
                for(int m = Math.min(Img.getWidth() - 1, j * 173 / 100); m <= Math.min(Img.getWidth() - 1, (j + 1) * 173 / 100); m++)
                    for(int n = Math.min(Img.getHeight() - 1, i * 173 / 100); n <= Math.min(Img.getHeight() - 1, (i +1) * 173 /100); n++){
                        color.Alpha = color.Alpha + Color.alpha(Img.getPixel(m,n));
                        color.Red = color.Red + Color.red(Img.getPixel(m, n));
                        color.Green = color.Green + Color.green(Img.getPixel(m,n));
                        color.Blue = color.Blue + Color.blue(Img.getPixel(m,n));
                        count++;
                    }
                pix[i * NewWidth +j] = Color.argb(color.Alpha / count, color.Red / count, color.Green / count, color.Blue / count);
            }
        return Bitmap.createBitmap(pix, NewWidth, NewHeight, Bitmap.Config.ARGB_8888);
    }
}

//При написании использовались материалы с сайта Александра Климова.