package ru.ifmo.md.lesson2;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;

import java.io.File;

public class MyActivity extends Activity {
    private ImageMagic magic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bitmap img = BitmapFactory.decodeResource(getResources(), R.drawable.source);

        magic = new ImageMagic(this, img);
        setContentView(magic);
    }

    @Override
    public void onResume() {
        super.onResume();
        magic.resume();
    }

    @Override
    public void onPause() {
        super.onPause();
        magic.pause();
    }
}