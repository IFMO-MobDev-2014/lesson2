package ru.ifmo.md.lesson2;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

public class MyActivity extends Activity implements View.OnTouchListener {
    private MyView view;

    public boolean onTouch(View v, MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN) {
            view.onDraw(view.canvas);
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = new MyView(this);
        view.setOnTouchListener(this);
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
