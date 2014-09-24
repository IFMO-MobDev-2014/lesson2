package ru.ifmo.md.lesson2;

import android.app.Activity;
import android.os.Bundle;

public class MyActivity extends Activity {

    private ShowPict showPict;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showPict = new ShowPict(this);
        setContentView(showPict);
    }

    @Override
    public void onResume() {
        super.onResume();
        showPict.resume();
    }

    @Override
    public void onPause() {
        super.onPause();
        showPict.pause();
    }
}
