package ru.ifmo.md.lesson2;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.app.Activity;
import android.widget.ImageView;
import java.lang.Override;

public class MyActivity extends Activity{



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ImageView rot;
        setContentView(R.layout.activity);
        rot = (ImageView)findViewById(R.id.RotatorAndShrinker);
        rot.setScaleType(ImageView.ScaleType.CENTER);
        Bitmap bit = BitmapFactory.decodeResource(this.getResources(), R.drawable.source, new BitmapFactory.Options());
        rot.setImageBitmap(bit);
    }

}
