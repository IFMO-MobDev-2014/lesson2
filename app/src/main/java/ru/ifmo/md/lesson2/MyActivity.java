package ru.ifmo.md.lesson2;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class MyActivity extends Activity {

    Bitmap bitmap, bitmap2;
    ImageView image;
    boolean flag = false;
    Edit new_bmp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.source);
        new_bmp = new Edit(bitmap);

        image = (ImageView) findViewById(R.id.test);

        //image.setImageBitmap(new_bmp.getBitmap());

    }

    public void clickImage(View view) {
        if (flag) {
            image.setImageBitmap(bitmap);
        }
        else {
            image.setImageBitmap(new_bmp.getBitmap());
        }
        flag = !flag;
    }
}
