package ru.ifmo.md.lesson2;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

/**
 * Created by SergeyBudkov on 19.09.2014.
 */
public class PictureView extends View {

    boolean pressed = true;
    Paint p = new Paint();

    public PictureView(Context context) {
        super(context);
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                pressed = !pressed;
                invalidate();
            }
        });
    }

    @Override
    public void onDraw(Canvas canvas) {
        if (pressed)
            canvas.drawBitmap(MyActivity.newFastPicture, 0, 0, p);
        else
            canvas.drawBitmap(MyActivity.newQualityPicture, 0, 0, p);
    }
}
