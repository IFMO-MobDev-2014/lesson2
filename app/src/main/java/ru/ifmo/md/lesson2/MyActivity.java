package ru.ifmo.md.lesson2;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ImageView;

public class MyActivity extends Activity {

    ImageProcessView view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bitmap initImg = BitmapFactory.decodeResource(getResources(), R.drawable.source2);
        view = new ImageProcessView(this, initImg);
        setContentView(view);
        view.processImage();
    }
}
