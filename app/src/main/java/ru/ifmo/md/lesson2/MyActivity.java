package ru.ifmo.md.lesson2;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.graphics.Paint;

public class MyActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new ImageView(this));
    }

    class ImageView extends View {
        private Paint paint = new Paint();
        private Bitmap bitmap;
        private int[] pixels;
        private int curId;
        private MyImage[] pictures = new MyImage[2];

        public ImageView(Context context) {
            super(context);
            paint.setColor(Color.WHITE);
            paint.setTextSize(50);
            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.source);
            pixels = new int[bitmap.getWidth() * bitmap.getHeight()];
            bitmap.getPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
            MyImage buf = new MyImage(pixels, bitmap.getWidth(), bitmap.getHeight());
            buf = buf.incBrightness();
            buf = buf.turn90();
            pictures[0] = buf.fastScale(405, 434);
            pictures[1] = buf.qualityScale(405, 434);
            setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    curId = 1 - curId;
                    invalidate();
                }
            });
            invalidate();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawText(curId == 0 ? "FAST" : "QUALITY", 500, 100, paint);
            canvas.drawBitmap(pictures[curId].getPixels(), 0, pictures[curId].getWidth(), 0, 0, pictures[curId].getWidth(), pictures[curId].getHeight(), false, null);
        }
    }
}