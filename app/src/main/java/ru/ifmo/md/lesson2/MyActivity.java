package ru.ifmo.md.lesson2;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class MyActivity extends Activity {

    public static final float SIZE_SCALE = 1.73F;
    public static final float COLOR_SCALE = 2.0F;

    Bitmap rotateAndBrighten(Bitmap bitmap) {
        int[] source = new int[bitmap.getWidth() * bitmap.getHeight()];
        int[] result = new int[bitmap.getWidth() * bitmap.getHeight()];
        bitmap.getPixels(source, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        for (int i = 0; i < bitmap.getWidth(); i++) {
            for (int j = 0; j < bitmap.getHeight(); j++) {
                int newColor = source[i + j * bitmap.getWidth()];

                int newRed = (newColor >> 16) & 0xFF;
                int newGreen = (newColor >> 8) & 0xFF;
                int newBlue = newColor & 0xFF;

                newRed = Math.min(0xFF, (int) (newRed * COLOR_SCALE));
                newGreen = Math.min(0xFF, (int) (newGreen * COLOR_SCALE));
                newBlue = Math.min(0xFF, (int) (newBlue * COLOR_SCALE));

                newColor = (newRed << 16) | (newGreen << 8) | newBlue;
                result[i * bitmap.getHeight() + (bitmap.getHeight() - j - 1)] = newColor;
            }
        }

        return Bitmap.createBitmap(result, bitmap.getHeight(), bitmap.getWidth(), Bitmap.Config.RGB_565);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final ImageView view = new ImageView(getApplicationContext());


        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.source);

        final Bitmap fastBitmap = rotateAndBrighten(new FastScaler().scale(bitmap, SIZE_SCALE));
        final Bitmap slowBitmap = rotateAndBrighten(new SlowScaler().scale(bitmap, SIZE_SCALE));

        view.setImageBitmap(fastBitmap);

        view.setOnClickListener(new View.OnClickListener() {
            boolean fast = true;

            @Override
            public void onClick(View v) {
                if (fast) {
                    view.setImageBitmap(slowBitmap);
                    fast = false;
                } else {
                    view.setImageBitmap(fastBitmap);
                    fast = true;
                }
            }
        });

        view.setScaleType(ImageView.ScaleType.CENTER);

        setContentView(view);
    }
}
