package ru.ifmo.md.lesson2;

import android.app.Activity;
import android.os.Bundle;


public class MyActivity extends Activity {

    private PictureView pictureView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pictureView = new PictureView(this);
        setContentView(pictureView);
    }

    @Override
    public void onResume() {
        super.onResume();
        pictureView.resume();
    }

    @Override
    public void onPause() {
        super.onPause();
        pictureView.pause();
    }
}
