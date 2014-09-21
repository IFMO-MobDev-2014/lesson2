package ru.ifmo.md.lesson2;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class MyActivity extends Activity {

    int WayOfZipping = -1;
    Resources res;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        res = this.getResources();
        Bitmap bmp = BitmapFactory.decodeResource(res, R.drawable.source);
        ImageView imgview = (ImageView)findViewById(R.id.img);
//        imgview.setImageBitmap(bmp);


        final int hBefore = bmp.getHeight();
        final int wBefore = bmp.getWidth();

        final int hAfter = 405;
        final int wAfter = 434;

        int pixelsBefore[] = new int[hBefore*wBefore];
        int roundedPixels[] = new int[hAfter*wAfter];

        bmp.getPixels(pixelsBefore, 0, wBefore, 0, 0, wBefore, hBefore);
     //   imgview.setImageBitmap(Bitmap.createBitmap(pixelsBefore, wBefore, hBefore, Bitmap.Config.ARGB_8888));
        if (WayOfZipping > 0) { //fast way
            for (int i = 0; i < hAfter; i++) {
                for (int j = 0; j < wAfter; j++) {
                    roundedPixels[j + i * wAfter] = pixelsBefore[i * (wBefore / hAfter) + (wAfter - 1 - j) * hBefore / wAfter * wBefore];
                }
            }
        }

        else {      //better quality

        }

        int brtTable[] = new int[256]; //Template for brightness changing: improves speed and quality, for some look.
        for (int i = 0; i < 256; i++) {
              brtTable[i] = (int) (Math.sqrt(((float) i) / 255.0f) * 255.0f);
        }

        for (int i = 0; i < wAfter*hAfter; i++) { //Brightness changing
            int red = roundedPixels[i] & 0xff;      //getting three color channels, improving and pushing back
            int green = (roundedPixels[i] & 0xff00) >> 8;
            int blue = (roundedPixels[i] & 0xff0000) >> 16;
            red = brtTable[red];
            green = brtTable[green];
            blue = brtTable[blue];
            roundedPixels[i] = 0xff000000 | red | (green << 8) | (blue << 16);
        }

        imgview.setImageBitmap(Bitmap.createBitmap(roundedPixels, wAfter, hAfter, Bitmap.Config.ARGB_8888));

        View.OnClickListener img_onClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WayOfZipping *= -1;
            }
        };
        imgview.setOnClickListener(img_onClick);
    }
}
