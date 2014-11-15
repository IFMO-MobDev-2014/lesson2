package ru.ifmo.md.lesson2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

/**
 * Created by timur on 15.10.14.
 */
public class MyView extends View {

    private Paint paint;
    private boolean fastState;
    private Bitmap fast;
    private Bitmap quality;

    public MyView(Context context) {
        super(context);
        paint = new Paint();
        fastState = false;
        this.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                invalidate();
            }
        });
    }

    public void setImages(Bitmap fast, Bitmap quality) {
        this.fast = fast;
        this.quality = quality;
    }

    @Override
    public void onDraw(Canvas canvas) {
        if (fastState) {
            canvas.drawBitmap(fast, 0, 0, paint);
            fastState = false;
        } else {
            canvas.drawBitmap(quality, 0, 0, paint);
            fastState = true;
        }
    }
}