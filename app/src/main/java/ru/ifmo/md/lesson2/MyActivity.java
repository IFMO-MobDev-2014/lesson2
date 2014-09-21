package ru.ifmo.md.lesson2;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

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
//        long timeStart = System.currentTimeMillis();
        img = reduceImageFast(img, 405, 434);
//        long timeEnd = System.currentTimeMillis();
//        Toast t = Toast.makeText(this, Integer.toString((int)(timeEnd - timeStart)), Toast.LENGTH_SHORT);
//        t.show();
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

    private Bitmap reduceImageFast(Bitmap source, int newWidth, int newHeight) {
        int width = source.getWidth();
        int height = source.getHeight();
        int[] newRaw = new int[newHeight * newWidth],
              raw = new int[width * height];
        source.getPixels(raw, 0, width, 0, 0, width, height);

        int oldX, offset;
        for(int y = 0; y < newHeight; y++) {
            offset = (int)Math.ceil((height - 1) * y / newHeight) * width;
            for (int x = 0; x < newWidth; x++) {
                oldX = (int)Math.ceil((width - 1) * x / newWidth);
                newRaw[y * newWidth + x] = raw[offset + oldX];
            }
        }

        return Bitmap.createBitmap(newRaw, newWidth, newHeight, Bitmap.Config.ARGB_8888);
    }
}