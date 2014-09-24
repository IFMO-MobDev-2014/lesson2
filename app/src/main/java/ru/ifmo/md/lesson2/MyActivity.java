package ru.ifmo.md.lesson2;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class MyActivity extends Activity {

    Bitmap bitmap, bitmap1, bitmap2;
    ImageView image;
    boolean flag = true;
    Edit new_bmp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.source);
        new_bmp = new Edit(bitmap);

        image = (ImageView) findViewById(R.id.test);
        image.setImageBitmap(new_bmp.getBitmap());
        bitmap1 = Bitmap.createBitmap(new_bmp.nearestNeighbor());
        bitmap2 = Bitmap.createBitmap(new_bmp.bilinearInterpolation());
    }

    public void clickImage(View view) {
        if (flag) {
            image.setImageBitmap(bitmap1);
        }
        else {
            image.setImageBitmap(bitmap2);
        }
        flag = !flag;

    }
}
