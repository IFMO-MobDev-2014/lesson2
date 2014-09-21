package ru.ifmo.md.lesson2;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;


public class MyActivity extends Activity {

    private final static int width = 700;
    private final static int height = 750;
    private final static double scale = 1.73;
    private final static int nWidth = (int) (height / scale);
    private final static int nHeight = (int) (width / scale);

    private boolean flag = false;
    private int[] pixels = new int[width * height];
    private int[] pixels2 = new int[width * height];
    private int[] pixels3 = new int[nWidth * nHeight];

    private Bitmap bitmap = null;


    // reverse and increasing brightness
    public void revAndInc() {
        for (int i = 0; i < height * width; i++) {
            int alpha = Color.alpha(pixels[i]);
            int red = Math.min(Color.red(pixels[i]) * 2, 255);
            int blue = Math.min(Color.blue(pixels[i]) * 2, 255);
            int green = Math.min(Color.green(pixels[i]) * 2, 255);

            int idOfColumnReverse = i % width + 1;
            int offset = i / width + 1;
            pixels2[idOfColumnReverse * height - offset] = Color.argb(alpha, red, green, blue);
        }
    }

    public int getGoodColor(int i, int j) {
        int red = 0;
        int blue = 0;
        int green = 0;
        int cnt = 0;
        for (int di = -1; di < 2; di++) {
            for (int dj = -1; dj < 2; dj++) {
                int ni = i + di;
                int nj = j + dj;
                int nCoord =(int) (ni * scale) * height + (int)(nj * scale);
                if (nCoord > width * height || nCoord < 0)
                    continue;
                red += Color.red(pixels2[nCoord]);
                green += Color.green(pixels2[nCoord]);
                blue += Color.blue(pixels2[nCoord]);
                cnt++;
            }
        }
        return Color.rgb(red / cnt, green / cnt, blue / cnt);
    }

    public void fastScale() {
        for (int i = 0; i < nHeight; i++) {
            for (int j = 0; j < nWidth; j++) {
                pixels3[i * nWidth + j] = getGoodColor(i, j);
            }
        }
    }

    public void slowScale() {
        for (int i = 0; i < nHeight; i++) {
            for (int j = 0; j < nWidth; j++) {
                pixels3[i * nWidth + j] = pixels2[(int) (i * scale) * height + (int)(j * scale)];
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);

        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.source);
        bitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);

        revAndInc();
        slowScale();

        Bitmap bitmap3 = Bitmap.createBitmap(nWidth, nHeight, Bitmap.Config.ARGB_8888);
        bitmap3.setPixels(pixels3, 0, nWidth, 0, 0, nWidth, nHeight);
        ImageView myImage = (ImageView) findViewById(R.id.imageView);
        myImage.setImageBitmap(bitmap3);

    }

    public void clickImage(View view) {
        if (flag) {
            fastScale();
        } else {
            slowScale();
        }
        flag = flag ^ true;
    }
}
