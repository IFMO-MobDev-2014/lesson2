package ru.ifmo.md.lesson2;

import java.util.Random;

import android.R.string;
import android.os.Bundle;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class MyActivity extends Activity {

    int height = 0, width = 0, heightNow = 0, widthNow = 0, newheight = 0, newwidth = 0;
    int[][] compress = null, picture = null, pictureTurn = null;
    Bitmap bitmap = null, newBitmap = null;
    int[] h = null, w = null;
    int[] b = null;
    ImageView targetImage = null;
    int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        targetImage = (ImageView) findViewById(R.id.ImageContainer);

        bitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.source);

        heightNow = bitmap.getHeight();
        widthNow = bitmap.getWidth();

        picture = new int[heightNow][widthNow];
        create();
        
        pictureTurn = new int[widthNow][heightNow];
        height = widthNow;
        width = heightNow;
        newheight = (int) (height / 1.73);
        newwidth = (int) (width / 1.73);
        w = new int[newwidth];
        h = new int[newheight];
        b = new int[newwidth * newheight];
        compress = new int[newheight][newwidth];

        start();

        targetImage.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                if (count == 0) {
                    count = 1;
                    slow();
                } else {
                    count = 0;
                    fast();
                }
                bright();
                print();
            }
        });

    }

    private void start() {
        set();
        turn();

        fast();
        bright();
        print();
    }

    private void create() {
        for (int i = 0; i < heightNow; i++)
            for (int j = 0; j < widthNow; j++) {
                picture[i][j] = bitmap.getPixel(j, i);
            }
    }

    private void set() {
        for (int i = 0; i < newwidth; i++) {
            w[i] = (int) (i * 1.73);
        }
        for (int i = 0; i < newheight; i++) {
            h[i] = (int) (i * 1.73);
        }
    }

    private void turn() {
        for (int i = height - 1; i >= 0; i--)
            for (int j = 0; j < width; j++) {
                pictureTurn[i][width - j - 1] = picture[j][i];
            }
    }

    private void fast() {
        for (int i = 0; i < newheight; i++) {
            for (int j = 0; j < newwidth; j++) {
                compress[i][j] = pictureTurn[h[i]][w[j]];
            }
        }
    }

    private void slow() {
        Random t = new Random();
        for (int i = 0; i < newheight - 1; ++i) {
            for (int j = 0; j < newwidth - 1; ++j) {
                compress[i][j] = pictureTurn[h[i] + t.nextInt(h[i + 1] - h[i])][w[j] + t.nextInt(w[j + 1] - w[j])];
            }
        }
    }

    private void bright() {
        int alpha, red, green, blue;
        for (int i = 0; i < newheight; i++) {
            for (int j = 0; j < newwidth; j++) {
                alpha = Color.alpha(compress[i][j]);
                alpha = alpha + (int) (0.25 * (255 - alpha));
                red = Color.red(compress[i][j]);
                red = red + (int) (0.25 * (255 - red));
                green = Color.green(compress[i][j]);
                green = green + (int) (0.25 * (255 - green));
                blue = Color.blue(compress[i][j]);
                blue = blue + (int) (0.25 * (255 - blue));
                compress[i][j] = (alpha << 24) | (red << 16) | (green << 8) | (blue);
            }
        }
    }

    private void print() {
        for (int i = 0; i < newwidth * newheight; i++) {
            b[i] = compress[i / newwidth][i % newwidth];
        }
        newBitmap = Bitmap.createBitmap(newwidth, newheight, Config.RGB_565);
        newBitmap.setPixels(b, 0, newwidth, 0, 0, newwidth, newheight);
        targetImage.setImageBitmap(newBitmap);
    }

}
