package ru.ifmo.md.lesson2;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public class MyActivity extends Activity {

    private MyImageView myImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myImageView = new MyImageView(this);
        setContentView(myImageView);
        myImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myImageView.modeChange();
            }
        });
    }
}
