package ru.ifmo.md.lesson2;

import android.app.Activity;
import android.os.Bundle;

public class MyActivity extends Activity {

    Image image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        image = new Image(this);
        setContentView(image);
    }

    @Override
    public void onResume() {
        super.onResume();
        image.resume();
    }

    @Override
    public void onPause() {
        super.onPause();
        image.pause();
    }
}