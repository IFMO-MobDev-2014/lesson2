package ru.ifmo.md.lesson2;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;

public class MyActivity extends Activity {

    private PhotoView photoView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        photoView = new PhotoView(this);
        setContentView(photoView);
    }

    @Override
    public void onResume() {
        super.onResume();
        photoView.resume();
    }

    @Override
    public void onPause() {
        super.onPause();
        photoView.pause();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN) {
            photoView.changeQuality();
        }
        return true;
    }
}