package ru.ifmo.md.lesson2;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;


public class MyActivity extends Activity {

    private WhirlView whirlView;
    public static boolean condition=true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        whirlView = new WhirlView(this);
        whirlView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                condition=!condition;
                return false;
            }
        });
        setContentView(whirlView);
    }

    @Override
    public void onResume() {
        super.onResume();
        whirlView.resume();
    }

    @Override
    public void onPause() {
        super.onPause();
        whirlView.pause();
    }
}
