package ru.ifmo.md.lesson2;

import android.app.Activity;
import android.content.Context;
import android.graphics.*;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

public class MyActivity extends Activity {

    class Lesson2 extends View {
        public static final int wight = 700;
        public static final int height = 750;
        int[] nP = null;
        double task=1.73;
        boolean choose = true;
        public static final int nwight = 405;
        public static final int nheight = 434;
        int[] col = new int[height * wight];

        public Lesson2(Context context) {
            super(context);
            Bitmap sh = BitmapFactory.decodeResource(getResources(), R.drawable.source);
            sh.getPixels(col, 0, wight, 0, 0, wight, height);
        }

        int[] rotPic(int[] col, int height, int wight) {
            int[] nP= new int[height * nwight];
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < wight; j++) {
                    nP[j * height + (height - i - 1)] = col[i * wight + j]; }
            }
            System.arraycopy(nP, 0, col, 0, height * wight);
            return nP;
        }

        int[] CompF(int[] col) {
            int[]nP=new int[nheight * nwight];
            for (int i = 0; i < nheight; i++) {
                for (int j = 0; j < nwight; j++) {
                    nP[i*nwight+j] = col[(i*(height-1)/(nheight-1))*wight+(j*(wight - 1)/(nwight - 1))];}
            }
            return nP;
        }

        int[] CompBet(int[] col) {
            int klv[] = new int[nwight * nheight];
            int pix[] = new int[nwight * nheight];
            int nP[] =  new int[nwight * nheight];
            for(int i = 0; i<height;i++){
                for(int j = 0;j < wight; j++){
                    klv[(int)(i/task)*nwight+(int)(j/task)]++;
                }
            }
            for (int k = 0; k < 3; k++) {
                if (k != 0) {
                    for (int i = 0; i < nwight*nheight; i++) {
                        pix[i] = 0;
                    }
                }
                for (int i = 0; i < height; i++) {
                    for (int j = 0; j < wight; j++) {
                        pix[(int)(i/task)*nwight+(int)(j/task)]+=(col[i*wight+j]>>((8 * k))&255);
                    }
                }
                for (int i = 0; i < nheight; i++) {
                    for (int j = 0; j < nwight; j++) {
                        int c = pix[i * nwight + j];
                        if(klv[i * nwight + j]>1){
                            c/=klv[i * nwight + j];
                        }
                        nP[i * nwight + j] |= c << (8 * k);
                    }
                }
            }
            return nP;
        }
        int[] Bright(int[] col) {
            for (int i = 0; i < col.length; i++) {
                int b = col[i] & 0xFF;
                int r = (col[i] & 0xFF0000) >> 16;
                int g = (col[i] & 0xFF00) >> 8;
                int z=b;
                if(r>z){
                    z=r;
                }
                if(g>z){
                    z=g;
                }
                double dop;
                if(z < 64)
                    dop=2.0;
                else {
                  dop = Math.sqrt(255.0/z);
                }
                col[i] = (int)(b*dop) + ((int)(r*dop) << 16) + ((int)(g*dop)<<8);
            }
            return col;
        }
        void updPic() {
            if(choose){
                nP=CompF(col);
            }
            else{
                nP=CompBet(col);
            }
            nP=rotPic(nP, nheight, nwight);
            nP=Bright(nP);
        }

        @Override
        public void onDraw(Canvas canvas) {
            if (nP == null) {
                updPic();
            }
            canvas.drawBitmap(nP, 0, nheight, 400, 550, nheight, nwight, false, null);
            Paint pica = new Paint();
            pica.setColor(0xFF00FF00);
            pica.setTextSize(50);
            if (choose)
                canvas.drawText("Fast option,click to change", 400, 300, pica);
            else{
                canvas.drawText("Better option,click to change", 400, 300, pica);
            }
            invalidate();
        }

        @Override
        public boolean onTouchEvent(MotionEvent e) {
            int act = e.getAction();
            if (act == MotionEvent.ACTION_DOWN) {
                choose = !choose;
                updPic();
            }
            return true;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new Lesson2(this));
    }
}
