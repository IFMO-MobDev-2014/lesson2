package ru.ifmo.md.lesson2;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

public class MyActivity extends Activity {

    ImageView image;
    boolean setFast;
    BitmapEditor sourceBitmap;
    Bitmap fastMode;
    Bitmap slowMode;
    public static final double RATIO = 1.73;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        setFast = true;

        image = (ImageView) findViewById(R.id.image);
        sourceBitmap = new BitmapEditor(BitmapFactory.decodeResource(getResources(), R.drawable.source));
        sourceBitmap.rotateClockwise();
        long startTime = System.nanoTime();
        sourceBitmap.changeBrightness(50);
        long finishTime = System.nanoTime();
        Log.i("TIME", "Brightness:  " + (finishTime - startTime) / 1000000);
        startTime = System.nanoTime();
        fastMode = sourceBitmap.nearestNeighbor(RATIO);
        finishTime = System.nanoTime();
        Log.i("TIME", "Fast:        " + (finishTime - startTime) / 1000000);
        image.setImageBitmap(fastMode);
        startTime = System.nanoTime();
        slowMode = sourceBitmap.bilinearInterpolation(RATIO);
        finishTime = System.nanoTime();
        Log.i("TIME", "Slow:        " + (finishTime - startTime) / 1000000);
        image.setImageBitmap(fastMode);
    }


    public void changeImage(View view) {
        if (setFast) {
            image.setImageBitmap(slowMode);
        } else {
            image.setImageBitmap(fastMode);

        }
        setFast = !setFast;
    }
}