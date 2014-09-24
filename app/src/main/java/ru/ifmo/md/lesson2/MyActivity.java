package ru.ifmo.md.lesson2;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public class MyActivity extends Activity {
    MyView mv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mv = new MyView(this);
        setContentView(mv);
        mv.setOnClickListener(new View.OnClickListener()  {
            @Override
            public void onClick(View view) {
                 mv.changeMode();
            }
         });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mv.resume();
    }
}
