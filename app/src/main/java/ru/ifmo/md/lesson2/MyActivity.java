package ru.ifmo.md.lesson2;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

public class MyActivity extends Activity {
    private ImageView view;
    public static final float scale = 1.73f;
    private int INITIAL_WIDTH = 750;
    private int INITIAL_HEIGHT = 700;
    private int[] pixels = null;
    private int[] brightPixels = null;
    private int NEW_WIDTH = 434;
    private int NEW_HEIGHT = 405;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        reconfigureImage();
        view = new ImageView(this);
        setContentView(view);
    }

    private void reconfigureImage() {
        Bitmap image = BitmapFactory.decodeResource(this.getResources(), R.drawable.source);

        pixels = new int[INITIAL_WIDTH * INITIAL_HEIGHT];
        brightPixels = new int[INITIAL_WIDTH * INITIAL_HEIGHT];

        image.getPixels(pixels, 0, INITIAL_WIDTH, 0, 0, INITIAL_WIDTH, INITIAL_HEIGHT);

        brighten();
    }

    private void brighten() {
        for (int i = 0; i < INITIAL_WIDTH * INITIAL_HEIGHT; i++) {
            int pixel = pixels[i];
            brightPixels[i] = convertColor(pixel);
        }
    }

    private int convertColor(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] = Math.min(1, hsv[2] * 2);
        return Color.HSVToColor(hsv);
    }

    private class ImageView extends View {
        private boolean isFast;


        public ImageView(Context context) {
            super(context);

            this.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    isFast = !isFast;
                    invalidate();
                }
            });
        }

        @Override
        protected void onDraw(Canvas canvas) {
            canvas.save();
            if (isFast)
                canvas.drawBitmap(pixels, 0, INITIAL_WIDTH, 0, 0, INITIAL_WIDTH, INITIAL_HEIGHT, false, null);
            else
                canvas.drawBitmap(brightPixels,0, INITIAL_WIDTH, 0, 0, INITIAL_WIDTH, INITIAL_HEIGHT, false, null );
            canvas.restore();
        }
    }
}
