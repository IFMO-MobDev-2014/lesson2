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
    private int initialWidth = 750;
    private int initialHeight = 700;
    private final int NEW_HEIGHT = 405;
    private final int NEW_WIDTH = 434;
    public final float scale = 1.73f;
    private int[] pixels = new int[initialWidth * initialHeight];
    private int[] rotated = new int[initialHeight * initialWidth];
    private int[] brightPixels = new int[initialHeight * initialWidth];
    private int[] fastShrink = new int[NEW_WIDTH * NEW_HEIGHT];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        reconfigureImage();
        setContentView(new ImageView(this));
    }

    private void reconfigureImage() {
        Bitmap image = BitmapFactory.decodeResource(this.getResources(), R.drawable.source);
        image.getPixels(pixels, 0, initialWidth, 0, 0, initialWidth, initialHeight);

        nearestNeighbourInterpolation();
        rotateAndBrighten();
    }

    private void nearestNeighbourInterpolation() {
        for (int i = 0; i < NEW_WIDTH; i++) {
            for (int j = 0; j < NEW_HEIGHT; j++) {
                int y = (int) (j * scale);
                int x = (int) (i * scale);
                fastShrink[i + j * NEW_WIDTH] = pixels[y * initialWidth + x];
            }
        }
    }

    private void rotateAndBrighten() {
        int index = 0;
        for (int i = 0; i < initialWidth; i++) {
            for (int j = initialHeight - 1; j >= 0; j--) {
                rotated[index++] = pixels[i + j * initialWidth];
                brightPixels[index - 1] = convertColor(pixels[i + j * initialWidth]);
            }
        }
        int tmp = initialHeight;
        initialHeight = initialWidth;
        initialWidth = tmp;
    }

    private int convertColor(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] = Math.min(1, hsv[2] * 2);
        return Color.HSVToColor(hsv);
    }

    private class ImageView extends View {

        private boolean isBright;

        public ImageView(Context context) {
            super(context);

            this.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    isBright = !isBright;
                    invalidate();
                }
            });
        }

        @Override
        protected void onDraw(Canvas canvas) {
            if (isBright)
                canvas.drawBitmap(fastShrink, 0, NEW_WIDTH, 0, 0, NEW_WIDTH, NEW_HEIGHT, false, null);
            else
                canvas.drawBitmap(pixels, 0, initialHeight, 0, 0, initialHeight, initialWidth, false, null);
        }
    }
}
