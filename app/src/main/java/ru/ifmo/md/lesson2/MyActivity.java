package ru.ifmo.md.lesson2;

import android.app.Activity;
import android.os.Bundle;

public class MyActivity extends Activity {
    private ImagePrinter printer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        printer = new ImagePrinter(this);
        setContentView(printer);
    }

    @Override
    public void onResume() {
        super.onResume();
        printer.resume();
    }

    @Override
    public void onPause() {
        super.onPause();
        printer.pause();
    }
}
