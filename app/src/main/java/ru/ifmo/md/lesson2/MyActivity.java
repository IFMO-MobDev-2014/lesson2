package ru.ifmo.md.lesson2;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public class MyActivity extends Activity {

    private PictureView picture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        picture = new PictureView(this);
        setContentView(picture);
        picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                picture.changeQuality();
            }
        });
    }



}
