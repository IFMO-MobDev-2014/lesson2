package ru.ifmo.md.lesson2;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class MyActivity extends Activity {

    private enum Compression {
        FAST,
        SLOW
    }

    private static int sourceWidth = 700;
    private static int sourceHeight = 750;
    private static int targetWidth = 405;
    private static int targetHeight = 434;
    private static double multX = (1f * sourceWidth / targetWidth);
    private static double multY = (1f * sourceHeight / targetHeight);


    private  Compression comp = Compression.SLOW;
    int[] initPixels = new int[sourceHeight * sourceWidth];
    int[] fastPixels;
    int[] slowPixels;
    ImageView imageView = null;
    Bitmap bitmap = null;
    Bitmap initBitmap = null;
    Bitmap fastBitmap = null;
    Bitmap slowBitmap = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);
        imageView = (ImageView) findViewById(R.id.imageView);
        imageView.setImageResource(R.drawable.source);
        makeRightPicture();
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(comp == Compression.SLOW) {
                    imageView.setImageBitmap(bitmap);
                    comp = Compression.FAST;
                } else {
                    imageView.setImageBitmap(fastBitmap);
                    comp = Compression.SLOW;
                }
            }
        });
    }

    void makeRightPicture() {
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.source);
        bitmap.getPixels(initPixels, 0, sourceWidth, 0, 0, sourceWidth, sourceHeight);
        initBitmap = Bitmap.createBitmap(initPixels, sourceWidth, sourceHeight, Bitmap.Config.RGB_565);
        increaseBrightness(initPixels, sourceWidth, sourceHeight);
        rotatePicture(initPixels, sourceWidth, sourceHeight);
        bitmap.recycle();
        bitmap = Bitmap.createBitmap(initPixels, 0, sourceWidth, sourceWidth, sourceHeight, Bitmap.Config.ARGB_8888);
        fastPixels = fastCompression(initPixels, sourceWidth, sourceHeight, targetWidth, targetHeight);
        fastBitmap = Bitmap.createBitmap(fastPixels, 0, targetWidth, targetWidth, targetHeight, Bitmap.Config.ARGB_8888);
        slowPixels = slowCompression(initPixels, sourceWidth, sourceHeight, targetWidth, targetHeight);
    }

    void increaseBrightness(int[] pixels, int width, int height) {
        for(int i = 0; i < width; i++) {
            for(int j = 0; j < height; j++) {
                int position = i + j * width;
                int curColor = pixels[position];
                int red = Math.min(2 * Color.red(curColor), 255);
                int blue = Math.min(2 * Color.blue(curColor), 255);
                int green = Math.min(2 * Color.green(curColor), 255);
                int nextColor = Color.rgb(red, green, blue);
                pixels[position] = nextColor;
            }
        }
    }

    void rotatePicture(int pixels[], int width, int height) {
        int[] result = new int[width * height];
        for(int i = 0; i < width; i++) {
            for(int j = 0; j < height; j++) {
                int curPos = i + j * width;
                int nextPos = i * width + height - 1 - j;
                result[nextPos] = pixels[curPos];
            }
        }

        for(int i = 0; i < width * height; i++) {
            pixels[i] = result[i];
        }
    }

    int[] fastCompression(int[] pixels, int width, int height, int needWidth, int needHeight) {
        int[] modernPicture = new int[needWidth * needHeight];

        for(int i = 0; i < needWidth; i++) {
            for(int j = 0; j < needHeight; j++) {
                int x = (int) (i * multX);
                int y = (int) (j * multY);
                int curPos = x + y * width;
                int nextPos = i + j * needWidth;
                modernPicture[nextPos] = pixels[curPos];
            }
        }

        return modernPicture;
    }

    int[] slowCompression(int[] pixels, int width, int height, int needWidth, int needHeight) {
        int[] modernPicture = new int[needWidth * needHeight];

        return modernPicture;
    }
}