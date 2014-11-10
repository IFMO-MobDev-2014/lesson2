package ru.ifmo.md.lesson2;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

public class MyActivity extends Activity {
    public  static final float SIZE_SCALE = 1.0f / 1.73f;
    public  static final float BRIGHTNESS_RATIO = 2.0f;
    public  static final int   IMG_TURNS = 1;
    private static final boolean caching = false;

    private ImageView imageView;
    private boolean isGood = true;
    private Bitmap goodImage = null, fastImage = null;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        imageView = new ImageView(getApplicationContext());
        imageView.setScaleType(ImageView.ScaleType.CENTER);
        imageView.setOnClickListener(new ImageView.OnClickListener() {
            public void onClick(View view) {
                swapPictures();
            }
        });
        createPictures();
        drawPicture();
        setContentView(imageView);
    }
    private ArrayImage convert(ArrayImage image, ImageEditor.ScaleMode scaleMode) {
        long t;

        t = System.nanoTime();
        image = ImageEditor.changeBrightness(image, BRIGHTNESS_RATIO);
        t = System.nanoTime() - t;
        Log.i("TIME", "changing brightness... " + (t / 1000000) + "ms");

        t = System.nanoTime();
        image = ImageEditor.rotate(image, IMG_TURNS);
        t = System.nanoTime() - t;
        Log.i("TIME", "rotating... " + (t / 1000000) + "ms");

        t = System.nanoTime();
        image = ImageEditor.scale (image, SIZE_SCALE, scaleMode);
        t = System.nanoTime() - t;
        Log.i("TIME", (scaleMode == ImageEditor.ScaleMode.FAST ? "fast" : "slow") + " scaling image... " + (t / 1000000) + "ms");

        return image;
    }
    public void swapPictures() {
        isGood = !isGood;
        drawPicture();
    }
    public void createPictures() {
        if (caching) {
            ArrayImage image = new ArrayImage(BitmapFactory.decodeResource(getResources(), R.drawable.source));
            fastImage = convert(image, ImageEditor.ScaleMode.FAST).toBitmap();
            goodImage = convert(image, ImageEditor.ScaleMode.SLOW).toBitmap();
        }
    }
    public void drawPicture() {
        Bitmap cur;
        if (caching) {
            cur = isGood ? goodImage : fastImage;
        } else {
            cur = convert(
                    new ArrayImage(BitmapFactory.decodeResource(getResources(), R.drawable.source)),
                    isGood ? ImageEditor.ScaleMode.SLOW : ImageEditor.ScaleMode.FAST
            ).toBitmap();
        }

        if (cur != null) {
            imageView.setImageBitmap(cur);
        }
    }
}
