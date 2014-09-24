package ru.ifmo.md.lesson2;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.MotionEvent;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;

public class MyActivity extends Activity {

    public final int width = 700, hight=750, Wshort = 405, Hshort = 434, capacity=175770; // means capacity of compressed image
    int redPart, greenPart, bluePart, max; // three streams of RGB color and the strongest stream
    int originImage[] = new int[width*hight]; // first version of image
    int helper[] = new int[capacity];
    int actualImage[]; // compressed image
    boolean click = false;
    Paint text = new Paint();

    class Image extends View {

        public Image(Context context) {

            super(context);
            Bitmap my = BitmapFactory.decodeResource(getResources(),R.drawable.source);
            my.getPixels(originImage, 0, width, 0, 0, width, hight);
            qualityCompression();    // to initialize actualImage
            turn();
            lighter();

            text.setColor(Color.BLUE);
            text.setTextSize(25);

        }

        void turn() {

            int step=0;
            for (int j = capacity-Wshort; j < capacity; j++) {
                for (int i = j; i >=0; i-=Wshort) {
                    helper[step]=actualImage[i];
                    step++;
                }
            }
            System.arraycopy(helper, 0, actualImage, 0, capacity);

        }

        void qualityCompression() {

            int number[] = new int[capacity];
            int middle, axisJ, axisI;

            for (int i = 0; i < hight; i++) {
                for (int j = 0; j < width; j++) {
                    axisJ=(int)(j/1.73);
                    axisI=(int)(i/1.73);
                    number[axisJ + Wshort*axisI]++;
                }
            }

            for (int i=0; i<capacity; i++){
                if (number[i]==0){
                    number[i]++;                    //to prevent division by zero
                }
            }

            actualImage = new int[capacity];

            for (int t = 0; t < 3; t++) {
                int currentColor = t*8;
                int currentSum[] = new int[capacity];
                for (int i = 0; i < hight; i++) {
                    for (int j = 0; j < width; j++) {
                        int sum = originImage[width*i + j]>>currentColor & 255;
                        axisJ=(int)(j/1.73);
                        axisI=(int)(i/1.73);
                        currentSum[axisJ + Wshort*axisI] += sum;
                    }
                }
                for (int i = 0; i < Hshort; i++) {
                    for (int j = 0; j < Wshort; j++) {
                        middle=currentSum[Wshort*i + j]/number[Wshort*i + j];
                        actualImage[Wshort*i + j] = (middle << currentColor) | actualImage[Wshort*i + j];
                    }
                }
            }

        }

        void lighter() {

            for (int i = 0; i < capacity; i++) {
                greenPart = actualImage[i]>>8 & 255;
                redPart = actualImage[i]>>16 & 255;

                if (greenPart>redPart){
                    max=greenPart;
                } else {
                    max=redPart;
                }

                bluePart = actualImage[i] & 255;

                if (bluePart>max){
                    max=bluePart;
                }
                if (max>63){
                    double a = Math.sqrt(255.0/max);
                    redPart*=a;
                    greenPart*=a;
                    bluePart*=a;
                    actualImage[i] = (redPart<<16) | (greenPart<<8) | (bluePart);
                } else {
                    actualImage[i] = (redPart<<17) | (greenPart<<9) | (bluePart<<1);
                }
            }

        }

        void speedyCompression() {

            for (int i=0; i<Hshort ; i++) {
                for (int j=0; j<Wshort; j++) {
                    actualImage[j+Wshort*i] = originImage[((width-1)*j/(Wshort-1))+width*((hight-1)*i/(Hshort-1))];
                }
            }

        }

        @Override
        public void onDraw(Canvas canvas) {

            canvas.drawBitmap(actualImage, 0, Hshort, 10, 10, Hshort, Wshort, false, null);

            if(click) {
                canvas.drawText("Tap to improve quality" , 125, 500, text);
            } else {
                canvas.drawText("Tap to use fast compression", 125, 500, text);
            }

            invalidate();

        }

        public boolean onTouchEvent(MotionEvent tap) {

            if (tap.getAction() == MotionEvent.ACTION_DOWN) {
                if (click){
                    qualityCompression();
                } else {
                    speedyCompression();
                }
                turn();
                lighter();
            }
            click = !click;
            return super.onTouchEvent(tap);

        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(new Image(this));

    }
}

