package ru.ifmo.md.lesson2;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;


public class MyActivity extends Activity {

    ImageView image;
    boolean setFast;
    public static final double RATIO = 1.0 / 1.84;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        setFast = true;

        image = (ImageView) findViewById(R.id.image);
        resizeImage(image, RATIO);
    }

    public void changeImage(View view) {
        if (setFast) {
            image.setImageResource(R.drawable.source2);
        } else {
            image.setImageResource(R.drawable.source);

        }
        resizeImage(image, RATIO);
        setFast = !setFast;
    }
    public Bitmap resizeBitmap(Bitmap source, double ratio) {
        int newWidth = (int) (source.getWidth() * ratio);
        int newHeight = (int) (source.getHeight() * ratio);
        return Bitmap.createScaledBitmap(source, newWidth, newHeight, false);
    }

    public void resizeImage(ImageView image, double ratio) {
        Bitmap current = ((BitmapDrawable) image.getDrawable()).getBitmap();
        Bitmap newBitmap = resizeBitmap(current, ratio);
        image.setImageBitmap(newBitmap);
    }

}