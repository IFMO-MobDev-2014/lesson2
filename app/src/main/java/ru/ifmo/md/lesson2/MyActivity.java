package ru.ifmo.md.lesson2;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;

import java.io.File;

public class MyActivity extends Activity {
    private ImageMagic magic;
    public static final String path = "/mnt/ext_sd/Magic.png";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        File imgFile = new File(path);
        if (!imgFile.exists())
            Log.d("ERROR", "File not found");
        Bitmap img = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

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