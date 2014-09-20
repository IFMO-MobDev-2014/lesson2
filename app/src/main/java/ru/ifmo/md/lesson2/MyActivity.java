package ru.ifmo.md.lesson2;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;


public class MyActivity extends Activity {

    ImageView image;
    boolean setFast;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        setFast = true;

        image = (ImageView) findViewById(R.id.image);
    }

    public void changeImage(View view) {
        if (setFast) {
            image.setImageResource(R.drawable.source2);
        } else {
            image.setImageResource(R.drawable.source);
        }
        setFast = !setFast;
    }

}