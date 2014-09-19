package ru.ifmo.md.lesson2;

import android.app.Activity;
import android.os.Bundle;

public class MyActivity extends Activity {
    private MyView myView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myView = new MyView(this);
        setContentView(myView);
    }

    @Override
    public void onResume() {
        super.onResume();
        myView.resume();
    }

    @Override
    public void onPause() {
        super.onPause();
        myView.pause();
    }
}
