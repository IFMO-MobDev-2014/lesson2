package ru.ifmo.md.lesson2;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;

public class MyActivity extends Activity {

    class MyView extends View {
        public static final int Width = 700;
        public static final int Height = 750;
        public static final int New_Width = 405;
        public static final int New_Height = 434;
        int[] picture = new int[Width * Height];
        int[] newpicture = new int[New_Height*New_Width];



        public MyView(Context context) {
            super(context);
            getPicture();
        }

        void getPicture() {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.source);
            bitmap.getPixels(picture, 0, Width,0,0,Width,Height);
        }

        void incBrightness(int[] pic) {
            for (int i=0; i< pic.length; i++) {
                int red = (pic[i] & 0xFF0000) >> 16;
                int green = (pic[i] & 0xFF00) >> 8;
                int blue = pic[i] & 0xFF;
                red = Math.min(255, (int) ((double) red*2.0));
                green = Math.min(255, (int) ((double) green*2.0));
                blue = Math.min(255, (int) ((double) blue*2.0));
                pic[i] = ((red << 16) + (green << 8) + (blue));
            }
        }

        void rotatePicture(int[] pic, int h, int w) {
            int[] temppic = new int[h*w];
            for (int i = 0; i<h; i++) {
                for (int j = 0; i< w; j++) {
                    temppic[j*h + (h-i-1)]=pic[i*w+j];
                }
            }
            System.arraycopy(temppic,0,pic,0,h*w);
        }

        void compressPicFast(int[] pic) {
            int [] temppic = new int[New_Width*New_Height];
            for (int i=0; i<New_Height; i++) {
                for (int j=0; j<New_Width; j++) {
                    temppic[i*New_Width+j] = pic[(i*(Height-1)/(New_Height-1))*Width + (j* (Width - 1)/(New_Width-1))];
                }
            }
            System.arraycopy(temppic,0,newpicture,0,New_Height*New_Width);
        }




        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }
    }
}
