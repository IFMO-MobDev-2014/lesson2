package ru.ifmo.md.lesson2;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;

public class MyActivity extends Activity {
    private MyView view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = new MyView(this);
        setContentView(view);
    }

    @Override
    public void onResume() {
        super.onResume();
        view.resume();
    }

    @Override
    public void onPause() {
        super.onPause();
        view.pause();
    }
}
