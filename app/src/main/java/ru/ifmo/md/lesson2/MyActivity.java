package ru.ifmo.md.lesson2;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;


public class MyActivity extends Activity {

    private MySurfaceView mySurfaceView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mySurfaceView = new MySurfaceView(this);
        setContentView(mySurfaceView);
        mySurfaceView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                mySurfaceView.Change();
                return false;
            }
        });
    }
    @Override
    public void onResume() {
        super.onResume();
        mySurfaceView.resume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mySurfaceView.pause();
    }
}
