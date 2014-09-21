package ru.ifmo.md.lesson2;

import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;

/**
 * @author volhovm
 *         Created on 9/20/14
 */

public class MyActivity extends Activity {
    SimpleEditorView view;
    Bitmap origBitmap;
    Bitmap bitmap;
    boolean drawing = false;
    int mode = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.source);
        origBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.source);
        view = new SimpleEditorView(this);
        setContentView(view);
        view.setBitMap(bitmap);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!drawing && event.getAction() == MotionEvent.ACTION_DOWN) {
            drawing = true;
            if (mode <= 10 && mode > 5) {
                if (mode == 10) new AlertDialog.Builder(this)
                        .setMessage("Using fast resizing")
//                        .setPositiveButton("OK", null)
                        .show();
                bitmap = new EditorFactory(bitmap)
                        .fastShrink(1.73)
                        .setBrightness(30)
                        .turn(EditorFactory.Direction.ClockWise)
                        .collect();
                mode--;
            }
            if (mode <= 5 && mode > -1) {
                if (mode == 5) {
                    bitmap = origBitmap.copy(Bitmap.Config.ARGB_8888, true);
                    new AlertDialog.Builder(this)
                            .setMessage("Using quality resizing")
                            .show();
                }
                bitmap = new EditorFactory(bitmap)
                        .niceShrink(1.73)
                        .setBrightness(30)
                        .turn(EditorFactory.Direction.ClockWise)
                        .collect();
                mode--;
            }
            view.setBitMap(bitmap);
            if (mode == 0) {
                mode = 10;
                bitmap = origBitmap.copy(Bitmap.Config.ARGB_8888, true);
            }
            drawing = false;
        }
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        view.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        view.resume();
    }
}
