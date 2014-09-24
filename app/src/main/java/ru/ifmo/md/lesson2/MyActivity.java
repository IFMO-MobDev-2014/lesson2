package ru.ifmo.md.lesson2;

import android.app.Activity;
import android.os.Bundle;


public class MyActivity extends Activity {

    private ImageTransformer imageTransformer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imageTransformer = new ImageTransformer(this);
        setContentView(imageTransformer);
    }

    @Override
    public void onResume() {
        super.onResume();
        imageTransformer.resume();
    }

    @Override
    public void onPause() {
        super.onPause();
        imageTransformer.pause();
    }
}