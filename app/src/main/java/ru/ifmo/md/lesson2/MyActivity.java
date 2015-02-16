package ru.ifmo.md.lesson2;

import android.app.Activity;
import android.os.Bundle;

public class MyActivity extends Activity {

    private MyImage myImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myImage = new MyImage(this);
        setContentView(myImage);
    }

    @Override
    protected void onResume() {
        super.onResume();
        myImage.resume();
    }
}