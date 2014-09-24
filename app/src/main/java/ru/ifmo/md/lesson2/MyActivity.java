package ru.ifmo.md.lesson2;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;


public class MyActivity extends Activity {
    ImageMagic magicForPoor; //faster, but poor quality version
    ImageMagic qualityMagic; //slower, but better quality version
    Bitmap bmp;
    boolean changedSampling = false;
    ImageView img;
    public static final double scale = 1.73;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        img = (ImageView) findViewById(R.id.view);
        bmp = BitmapFactory.decodeResource(getResources(), R.drawable.source);

        magicForPoor = new ImageMagic(bmp);
        magicForPoor.nearestNeighborInterpolation(scale);
        magicForPoor.increaseBrightness(80);
        magicForPoor.rotate();

        qualityMagic = new ImageMagic(bmp);
        qualityMagic.bilinearInterpolation(scale);
        qualityMagic.increaseBrightness(80);
        qualityMagic.rotate();
    }

    public void changeSampling(View view) {
        if (!changedSampling) {
            img.setImageBitmap(magicForPoor.createBitmap());
        } else {
            img.setImageBitmap(qualityMagic.createBitmap());
        }
        changedSampling = !changedSampling;
    }

}
