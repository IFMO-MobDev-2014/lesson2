package ru.ifmo.md.lesson2;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MyActivity extends Activity {

    int WayOfZipping = 1;
    Resources res;
    int hAfter = 405;
    int wAfter = 434;
    double scale = 1.73;


    int[] scaleF(int[] colors, int w, int h) {
        int res[] = new int[wAfter * hAfter];
        for (int k = 0; k < 4; k++) {
            for (int i = 0; i < h; i++) {
                for (int j = 0; j < w; j++) {
                    res[(int)(i / scale) * wAfter + (int)(j / scale)] = colors[i * w + j];
                }
            }
        }
        return res;
    }

    int[] scaleQ(int[] colors, int w, int h) {
        //saw in habr, may not working, so may the God be with us
        //upd: tested, work is correct
        int res[] = new int[wAfter * hAfter];
        int cnt[] = new int[wAfter * hAfter];
        int sum[] = new int[wAfter * hAfter];

        for (int k = 0; k < 4; k++) {
             for (int i = 0; i<wAfter*hAfter; i++) {
                 cnt[i] = 0;
                 sum[i] = 0;
             }
             for (int i = 0; i < h; i++) {
                  for (int j = 0; j < w; j++) {
                       cnt[(int)(i / scale) * wAfter + (int)(j / scale)]++;
                       sum[(int)(i / scale) * wAfter + (int)(j / scale)] += (colors[i * w + j] >> (8 * k)) & 255;
                  }
        }
            for (int i = 0; i < hAfter; i++) {
                 for (int j = 0; j < wAfter; j++) {
                      int x = sum[i * wAfter + j] / Math.max(1, cnt[i * wAfter + j]);
                      res[i * wAfter + j] |= x << (8 * k);
                 }
            }
        }
        return res;
    }

    int[] rotateSimple(int[] img, int w, int h) {
        int[] res = new int[w*h];
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                res[ x * h + (h - y - 1)] = img[y * w + x];
            }
        }
        return res;
    }

    int[] changeBrightnessWT(int[] img, int w, int h) {
        int res[] = new int[w*h];
        int brtTable[] = new int[256]; //Template for brightness changing: improves speed and quality, for some look.
        for (int i = 0; i < 256; i++) {
            brtTable[i] = (int) (Math.sqrt(((float) i) / 255.0f) * 255.0f);
        }

        for (int i = 0; i < w*h; i++) { //Brightness changing
            int red = img[i] & 0xff;      //getting three color channels, improving and pushing back
            int green = (img[i] & 0xff00) >> 8;
            int blue = (img[i] & 0xff0000) >> 16;
            red = brtTable[red];
            green = brtTable[green];
            blue = brtTable[blue];
            res[i] = 0xff000000 | red | (green << 8) | (blue << 16);
        }
        return res;
    }

    void doYourWorkDude(int[] pixelsBefore, int wBefore, int hBefore) {
        int[] scaledPixels;
        int[] brightenedPixels;

        if (WayOfZipping > 0) { //fast way
            scaledPixels =  scaleF(pixelsBefore, wBefore, hBefore);
        }
        else {      //better quality
            scaledPixels =  scaleQ(pixelsBefore, wBefore, hBefore);
        }

        int rotatedPixels[] = rotateSimple(scaledPixels, wAfter, hAfter);

        int tmp = wAfter;
        wAfter = hAfter;
        hAfter = tmp;

        brightenedPixels = changeBrightnessWT(rotatedPixels, wAfter, hAfter);
        final ImageView imgview = (ImageView)findViewById(R.id.img);
        imgview.setImageBitmap(Bitmap.createBitmap(brightenedPixels, wAfter, hAfter, Bitmap.Config.ARGB_8888));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        res = this.getResources();
        Bitmap bmp = BitmapFactory.decodeResource(res, R.drawable.source);

        final ImageView imgview = (ImageView)findViewById(R.id.img);
        final TextView scmodeTextView = (TextView)findViewById(R.id.scmodeTextView);
        scmodeTextView.setText("fast scaling (tap the image to change");
        final Button startButton = (Button)findViewById(R.id.btnStart);
        final Button resetButton = (Button)findViewById(R.id.btnReset);

        imgview.setImageBitmap(bmp);

        final int hBefore = bmp.getHeight();
        final int wBefore = bmp.getWidth();

        final int pixelsBefore[] = new int[hBefore*wBefore];

        bmp.getPixels(pixelsBefore, 0, wBefore, 0, 0, wBefore, hBefore);

        View.OnClickListener img_onClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WayOfZipping *= -1;
                if (WayOfZipping > 0) {
                    scmodeTextView.setText("fast scaling");
                }
                else {
                    scmodeTextView.setText("better but slow scaling");
                }
            }
        };
        imgview.setOnClickListener(img_onClick);

        View.OnClickListener rstBtn_onClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgview.setImageBitmap(BitmapFactory.decodeResource(res, R.drawable.source));
            }
        };
        resetButton.setOnClickListener(rstBtn_onClick);

        View.OnClickListener strBtn_onClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doYourWorkDude(pixelsBefore, wBefore, hBefore);
            }
        };
        startButton.setOnClickListener(strBtn_onClick);
    }
}
