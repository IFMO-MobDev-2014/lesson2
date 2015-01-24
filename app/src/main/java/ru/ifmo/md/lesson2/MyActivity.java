package ru.ifmo.md.lesson2;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

public class MyActivity extends Activity {

    private int[] source;
    private int w;
    private int h;
    private double scale = 1.73;


    void scaleF() {
        int hAfter = (int)(h/scale);
        int wAfter = (int)(w/scale);
        int res[] = new int[wAfter * hAfter];

        for (int y = 0; y < hAfter; y++) {
            int yw = y * wAfter;
            for (int x = 0; x < wAfter; x++) {
                res[yw + x] = source[y * w / wAfter * w + x * h / hAfter];
            }
        }

        w = wAfter;
        h = hAfter;
        source = res;
    }

    void scaleQ() {

        int hAfter = (int)(h/scale);
        int wAfter = (int)(w/scale);
        int res[] = new int[wAfter * hAfter];

        int newSize = wAfter * hAfter;
        int[] cnt = new int[newSize];
        int[] red = new int[newSize];
        int[] green = new int[newSize];
        int[] blue = new int[newSize];

        for (int i = 0; i < newSize; i++) {
            cnt[i] = red[i] = green[i] = blue[i] = 0;
        }

        for (int y = 0; y < h; y++) {
            int newY = y * hAfter / h;
            int newYW = newY * wAfter;
            int yw = y * w;
            for (int x = 0; x < w; x++) {
                int newX = x * wAfter / w;
                int color = source[yw + x];
                int index = newYW + newX;
                int r = (color >> 16) & 0xFF;
                int g = (color >> 8) & 0xFF;
                int b = color & 0xFF;

                red[index] += r;
                green[index] += g;
                blue[index] += b;
                cnt[index]++;
            }
        }

        for (int i = 0; i < newSize; i++) {
            int count = cnt[i];
            res[i] = 0xFF000000 | (red[i] / count) << 16 | (green[i] / count) << 8 | (blue[i] / count);
        }

        w = wAfter;
        h = hAfter;
        source = res;
    }

    void rotateSimple() {

        int[] res = new int[w * h];
        for (int y = 0; y < h; y++) {
            int yw = y * w;
            int hy = h - y - 1;
            for (int x = 0; x < w; x++) {
                int xh = x * h;
                res[xh + hy] = source[yw + x];
            }
        }
        source = res;
        int t = w;
        w = h;
        h = t;
    }


    void changeBrightnessWT() {
        int brtTable[] = new int[256]; //Template for brightness changing: improves speed and quality, for some look.
        for (int i = 0; i < 256; i++) {
            brtTable[i] = (int) (Math.sqrt(((float) i) / 255.0f) * 255.0f);
        }

        for (int i = 0; i < w*h; i++) { //Brightness changing
            int red = source[i] & 0xff;      //getting three color channels, improving and pushing back
            int green = (source[i] & 0xff00) >> 8;
            int blue = (source[i] & 0xff0000) >> 16;
            red = brtTable[red];
            green = brtTable[green];
            blue = brtTable[blue];
            source[i] = 0xff000000 | red | (green << 8) | (blue << 16);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        final ImageView imageView = (ImageView)findViewById(R.id.img);
        final Button startButton = (Button)findViewById(R.id.btnStart);
        final Button resetButton = (Button)findViewById(R.id.btnReset);
        final RadioButton fRButton = (RadioButton)findViewById(R.id.scale_mode_fast);

        fRButton.setChecked(true);

        Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.source);
        imageView.setImageBitmap(bmp);

        h = bmp.getHeight();
        w = bmp.getWidth();

        source = new int[w*h];

        bmp.getPixels(source, 0, w, 0, 0, w, h);

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.source);
                h = bmp.getHeight();
                w = bmp.getWidth();
                source = new int[w*h];
                bmp.getPixels(source, 0, w, 0, 0, w, h);
                imageView.setImageBitmap(bmp);
                startButton.setEnabled(true);
            }
        });

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fRButton.isChecked()) {
                    scaleF();
                } else {
                    scaleQ();
                }
                rotateSimple();
                changeBrightnessWT();
                imageView.setImageBitmap(Bitmap.createBitmap(source, w, h, Bitmap.Config.ARGB_8888));
                startButton.setEnabled(false);
            }
        });
    }
}
