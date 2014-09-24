package ru.ifmo.md.lesson2;

import android.app.Activity;
import android.os.Bundle;

public class MyActivity extends Activity {
    MyView mv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mv = new MyView(this);
        setContentView(mv);
    }

    @Override
    public void onResume() {
        super.onResume();
        mv.resume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}
