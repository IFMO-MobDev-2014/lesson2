package ru.ifmo.md.lesson2;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

public class MyActivity extends Activity {

    ImageProcessView view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bitmap initImg = BitmapFactory.decodeResource(getResources(), R.drawable.source);
        view = new ImageProcessView(this, initImg);
        setContentView(view);
        view.processImage();
    }
}
