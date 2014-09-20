package ru.ifmo.md.lesson2;

import android.app.Activity;
import android.R;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.ImageView;

public class MyActivity extends Activity {

    private WhirlView whirlView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        whirlView = new WhirlView(this);
        setContentView(whirlView);
//        ImageView i = new ImageView(this);
//        Drawable d = getResources().getDrawable(R.drawable.source);
//        i.setImageDrawable(d);
//        setContentView(R.drawable.source);
//        setContentView(whirlView);
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
