package ru.ifmo.md.lesson2;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.MotionEvent;
import android.widget.ImageView;

/**
 * Created by flyingleafe on 20.09.14.
 * Listens for onTouch event and executes ProcessImageTask
 */
public class ImageProcessView extends ImageView {
    boolean hq = false;
    Bitmap initImg;

    public ImageProcessView(Context context, Bitmap img) {
        super(context);
        initImg = img;
    }

    public synchronized void processImage() {
        ProcessImageTask task = new ProcessImageTask(this, hq);
        task.execute(initImg);
        hq = !hq;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        processImage();
        return super.onTouchEvent(event);
    }
}
