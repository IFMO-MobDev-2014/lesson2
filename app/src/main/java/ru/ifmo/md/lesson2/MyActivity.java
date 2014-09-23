package ru.ifmo.md.lesson2;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

public class MyActivity extends Activity {
    RotateView rotateView;
    AlertDialog.Builder dialogBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rotateView = new RotateView(this);
        setContentView(rotateView);
        rotateView.setOnClickListener(rotateView);

        dialogBuilder = new AlertDialog.Builder(this);
    }
}
