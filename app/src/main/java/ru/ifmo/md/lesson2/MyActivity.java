package ru.ifmo.md.lesson2;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.graphics.Bitmap;
import android.widget.ImageView;

public class MyActivity extends Activity {

    ImageView picture;
    Bitmap bitmap;
    public static final double scale = 1.73;
    boolean flag = true;

    BitmapProcessor fastProcessor;
    BitmapProcessor qualityProcessor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        picture = (ImageView) findViewById(R.id.view_source);
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.source);

        fastProcessor = new BitmapProcessor(bitmap);
        fastProcessor.nearestNeighbourInterpolation(scale);
        fastProcessor.increaseBrightness(50);
        fastProcessor.rotate();

        qualityProcessor = new BitmapProcessor(bitmap);
        qualityProcessor.bilinearInterpolation(scale);
        qualityProcessor.increaseBrightness(50);
        qualityProcessor.rotate();

        picture.setImageBitmap(fastProcessor.getBitmap());


    }

    public void switchContent(View view) {
        if (flag) {
            picture.setImageBitmap(qualityProcessor.getBitmap());
        }
        else {
            picture.setImageBitmap(fastProcessor.getBitmap());
        }
        flag = !flag;
    }

}
