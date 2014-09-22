package ru.ifmo.md.lesson2;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

public class MyActivity extends Activity {

    class MyView extends View {
        public static final int Width = 700;
        public static final int Height = 750;
        public static final int New_Width = 405;
        public static final int New_Height = 434;
        int[] picture = new int[Width * Height];

        public MyView(Context context) {
            super(context);
            getPicture();
        }

        void getPicture() {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.source);
            bitmap.getPixels(picture, 0, Width, 0, 0, Width, Height);
        }

        void incBrightness(int[] pic) {
            for (int i = 0; i < pic.length; i++) {
                int red = (pic[i] & 0xFF0000) >> 16;
                int green = (pic[i] & 0xFF00) >> 8;
                int blue = pic[i] & 0xFF;
                red = Math.min(255, (int) ((double) red * 1.5));
                green = Math.min(255, (int) ((double) green * 1.5));
                blue = Math.min(255, (int) ((double) blue * 1.5));
                pic[i] = ((red << 16) + (green << 8) + (blue));
            }
        }

        void rotatePicture(int[] pic) {
            int[] temppic = new int[New_Height * New_Width];
            for (int i = 0; i < New_Height; i++) {
                for (int j = 0; j < New_Width; j++) {
                    temppic[j * New_Height + (New_Height - i - 1)] = pic[i * New_Width + j];
                }
            }
            System.arraycopy(temppic, 0, pic, 0, New_Height * New_Width);
        }

        int[] compressPicFast(int[] pic) {
            int[] temppic = new int[New_Width * New_Height];
            for (int i = 0; i < New_Height; i++) {
                for (int j = 0; j < New_Width; j++) {
                    temppic[i * New_Width + j] = pic[(i*(Height-1)/(New_Height))*Width +j*(Width-1)/(New_Width)];
                }
            }
            return temppic;
        }

        int averageColor(int[] pic,int y,int x){
            int dot00,dot10,dot01,dot11;
            int avred,avgreen,avblue;

            dot00=pic[y*Width+x];
            dot01=pic[y*Width+x+1];
            dot10=pic[(y+1)*Width+x];
            dot11=pic[(y+1)*Width+x+1];

            avred =((((dot00 & 0xFF0000) >> 16) + ((dot01 & 0xFF0000) >> 16) + ((dot10 & 0xFF0000) >> 16) + ((dot11 & 0xFF0000) >> 16))/4);
            avgreen =((((dot00 & 0xFF00) >> 8) + ((dot01 & 0xFF00) >> 8) + ((dot10 & 0xFF00) >> 8) + ((dot11 & 0xFF00) >> 8))/4);
            avblue =(((dot00 & 0xFF) + (dot01 & 0xFF) + (dot10 & 0xFF) + (dot11 & 0xFF))/4);

            return (avred << 16) + (avgreen << 8) + avblue;
        }

        int[] compressPicSlow(int[] pic) {
            int[] temppic = new int[New_Width*New_Height];
            for (int i=0; i<New_Height; i++) {
                for (int j=0; j < New_Width; j++) {
                    temppic[i*New_Width+j] = averageColor(pic, (i*(Height-1)/(New_Height)),j*(Width-1)/(New_Width));
                }
            }
            return temppic;
        }

        boolean flag = true;
        int[] newPic = null;

        void updatePicture() {
            newPic = flag ? compressPicFast(picture) : compressPicSlow(picture);
            rotatePicture(newPic);
            incBrightness(newPic);
        }

        @Override
        public void onDraw(Canvas canvas) {
            if (newPic == null) {
                updatePicture();
            }
            canvas.drawBitmap(newPic, 0, New_Height, 5, 5, New_Height, New_Width, false, null);
            invalidate();
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            int action = event.getAction();
            if (action == MotionEvent.ACTION_DOWN) {
                flag = !flag;
                updatePicture();
            }
            return true;
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new MyView(this));
    }

}
