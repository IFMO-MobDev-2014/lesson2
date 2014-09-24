package ru.ifmo.md.lesson2;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.MotionEvent;

public class MyActivity extends Activity {

    private final String DEBUG_TAG = "Touch";
    private PicView picView;
    private ImageLoader imageLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Resources res = getResources();
        imageLoader = new ImageLoader(((BitmapDrawable)res.getDrawable(R.drawable.source)).getBitmap());
        Thread loader = new Thread(imageLoader);
        loader.start();
        picView = new PicView(this, imageLoader);
        setContentView(picView);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){

        int action = event.getActionMasked();

        switch(action) {
            case (MotionEvent.ACTION_UP) :
                picView.currentPic = 1 - picView.currentPic;
                return true;
            default :
                return super.onTouchEvent(event);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        picView.resume();
    }

    @Override
    public void onPause() {
        super.onPause();
        picView.pause();
    }
}
