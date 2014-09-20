package ru.ifmo.md.lesson2;

import android.app.Activity;
import android.os.Bundle;

public class MyActivity extends Activity {
    private RotateView rotateView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rotateView = new RotateView(this);
        setContentView(rotateView);
    }

    @Override
    public void onResume() {
        super.onResume();
        rotateView.resume();
    }

    @Override
    public void onPause() {
        super.onPause();
        rotateView.pause();
    }
}
