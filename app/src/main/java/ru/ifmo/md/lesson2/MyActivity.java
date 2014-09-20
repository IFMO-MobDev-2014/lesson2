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
        int[] intRaw = new int[img.getWidth() * img.getHeight()];
        img.getPixels(intRaw, 0, img.getWidth(), 0, 0, img.getWidth(), img.getHeight());
        magic = new ImageMagic(this, intRaw, img.getWidth(), img.getHeight());
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