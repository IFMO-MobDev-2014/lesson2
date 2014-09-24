package com.dmitry2537.rotator;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import java.lang.Override;

public class MyActivity extends Activity{



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity);
        final Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.source);
        final RotatorAndShrinker rotatorAndShrinker = new RotatorAndShrinker(bitmap);
        final ImageView rot = (ImageView)findViewById(R.id.imageView);
       // rot.setScaleType(ImageView.ScaleType.CENTER);
        rot.setImageBitmap(bitmap);
        rot.setClickable(true);
        rot.invalidate();
        rot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bitmap tmp = rotatorAndShrinker.next();
                rot.setImageBitmap(tmp);
                rot.invalidate();
            }
        });

    }

}
