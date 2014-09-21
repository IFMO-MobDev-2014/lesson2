package ru.ifmo.md.lesson2;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

public class MyActivity extends Activity implements View.OnTouchListener {
    private ImageViewer imageViewer = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imageViewer = new ImageViewer(this);
        imageViewer.setOnTouchListener(this);
        setContentView(imageViewer);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        float x = motionEvent.getX();
        float y = motionEvent.getY();

        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (imageViewer.checkOnPicture(x, y)) {
                    imageViewer.changeCompress();
                }
                break;
        }
        return true;
    }
}
