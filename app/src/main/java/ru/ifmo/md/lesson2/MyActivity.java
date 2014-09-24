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
}
