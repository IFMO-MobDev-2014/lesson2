package com.example.app;

import android.app.Activity;
import android.os.Bundle;

public class MyActivity extends Activity {
    private MyGanstaShitView mainView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainView = new MyGanstaShitView(this);
        setContentView(mainView);
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}