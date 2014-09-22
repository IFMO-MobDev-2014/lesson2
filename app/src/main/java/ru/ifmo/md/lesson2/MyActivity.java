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
    private boolean lowQuality = true;

    private static final int initialWidth = 750;
    private static final int initialHeight = 700;
    private int newWidth = 434;
    private int newHeight = 405;
    private static final float scaleFactor = 1.73f;
    private int[] pixels = new int[initialHeight * initialWidth];
    private int[] lowQualityPixels = new int[newWidth * newHeight];
    private int[] highQualityPixels = new int[newWidth * newHeight];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        reconfigureImage();
        setContentView(new ImageView(this));
    }

    private void reconfigureImage() {
        Bitmap image = BitmapFactory.decodeResource(this.getResources(), R.drawable.source);
        image = Bitmap.createScaledBitmap(image, initialWidth, initialHeight, true);
        image.getPixels(pixels, 0, initialWidth, 0, 0, initialWidth, initialHeight);
        image.recycle();
        nearestNeighbourInterpolation();
        bilinearInterpolation();
        rotateAndBrighten();
    }

    private void bilinearInterpolation() {
        int[] tmp = new int[newWidth * newHeight];
        int a, b, c, d, x, y, index;
        float scaleX = ((initialWidth * 1f - 1)) / newWidth;
        float scaleY = ((initialHeight * 1f - 1)) / newHeight;
        float xDist, yDist, blue, red, green;
        int offset = 0;
        for (int i = 0; i < newHeight; i++) {
            for (int j = 0; j < newWidth; j++) {
                x = (int) (scaleX * j);
                y = (int) (scaleY * i);
                xDist = (scaleX * j) - x;
                yDist = (scaleY * i) - y;
                index = (y * initialWidth + x);
                a = pixels[index];
                b = pixels[index + 1];
                c = pixels[index + initialWidth];
                d = pixels[index + initialWidth + 1];

                blue = Color.blue(a) * (1 - xDist) * (1 - yDist) + Color.blue(b) * (xDist) * (1 - yDist) +
                        Color.blue(c) * (yDist) * (1 - xDist) + Color.blue(d) * (xDist * yDist);

                green = Color.green(a) * (1 - xDist) * (1 - yDist) + Color.green(b) * (xDist) * (1 - yDist) +
                        Color.green(c) * (yDist) * (1 - xDist) + Color.green(d) * (xDist * yDist);

                red = Color.red(a) * (1 - xDist) * (1 - yDist) + Color.red(b) * (xDist) * (1 - yDist) +
                        Color.red(c) * (yDist) * (1 - xDist) + Color.red(d) * (xDist * yDist);

                tmp[offset++] = Color.rgb((int) red, (int) green, (int) blue);
            }
        }
        highQualityPixels = tmp;
    }

    private void nearestNeighbourInterpolation() {
        for (int i = 0; i < newWidth; i++) {
            for (int j = 0; j < newHeight; j++) {
                int y = (int) (j * scaleFactor);
                int x = (int) (i * scaleFactor);
                lowQualityPixels[i + j * newWidth] = pixels[y * initialWidth + x];
            }
        }
    }

    private void rotateAndBrighten() {
        int index = 0;
        int[] brightPixels = new int[newWidth * newHeight];
        int[] brightPixels2 = new int[newWidth * newHeight];
        for (int i = 0; i < newWidth; i++) {
            for (int j = newHeight - 1; j >= 0; j--) {
                brightPixels[index++] = convertColor(lowQualityPixels[i + j * newWidth]);
                brightPixels2[index - 1] = convertColor(highQualityPixels[i + j * newWidth]);
            }
        }
        int tmp = newHeight;
        newHeight = newWidth;
        newWidth = tmp;

        lowQualityPixels = brightPixels;
        highQualityPixels = brightPixels2;
    }

    private int convertColor(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] = Math.min(1, hsv[2] * 2);
        return Color.HSVToColor(hsv);
    }

    private class ImageView extends View {

        public ImageView(Context context) {
            super(context);

            this.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    lowQuality = !lowQuality;
                    invalidate();
                }
            });
        }

        @Override
        protected void onDraw(Canvas canvas) {
            if (lowQuality)
                canvas.drawBitmap(lowQualityPixels, 0, newWidth, 0, 0, newWidth, newHeight, false, null);
            else
                canvas.drawBitmap(highQualityPixels, 0, newWidth, 0, 0, newWidth, newHeight, false, null);
        }
    }
}
