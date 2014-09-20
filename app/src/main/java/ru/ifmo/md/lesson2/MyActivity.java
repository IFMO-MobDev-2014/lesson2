package ru.ifmo.md.lesson2;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

public class MyActivity extends Activity {
    private ImageView view;
    private final int INITIAL_WIDTH = 750;
    private final int INITIAL_HEIGHT = 700;
    private final int NEW_HEIGHT = 405;
    private final int NEW_WIDTH = 434;
    public final float scale = INITIAL_WIDTH / NEW_WIDTH;
    private int[] pixels = new int[INITIAL_WIDTH * INITIAL_HEIGHT];
    private int[] rotated = new int[INITIAL_HEIGHT * INITIAL_WIDTH];
    private int[] brightPixels = new int[INITIAL_HEIGHT * INITIAL_WIDTH];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        reconfigureImage();
        view = new ImageView(this);
        setContentView(view);
    }

    private void reconfigureImage() {
        Bitmap image = BitmapFactory.decodeResource(this.getResources(), R.drawable.source);
        image.getPixels(pixels, 0, INITIAL_WIDTH, 0, 0, INITIAL_WIDTH, INITIAL_HEIGHT);
        shrinkAndRotate();
        brighten();
    }


    private void shrinkAndRotate() {
        int index = 0;
        for (int i = 0; i < INITIAL_WIDTH; i++) {
            for (int j = INITIAL_HEIGHT - 1; j >= 0; j--) {
                rotated[index++] = pixels[i + j * INITIAL_WIDTH];
            }
        }
    }

    private void brighten() {
        for (int i = 0; i < INITIAL_WIDTH * INITIAL_HEIGHT; i++) {
            int pixel = rotated[i];
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
            if (isFast)
                canvas.drawBitmap(rotated, 0, INITIAL_HEIGHT, 0, 0, INITIAL_HEIGHT, INITIAL_WIDTH, false, null);
            else
                canvas.drawBitmap(brightPixels, 0, INITIAL_HEIGHT, 0, 0, INITIAL_HEIGHT, INITIAL_WIDTH, false, null);
        }
    }
}
