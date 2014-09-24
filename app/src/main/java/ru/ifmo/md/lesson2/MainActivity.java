package ru.ifmo.md.lesson2;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.LevelListDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;

public class MainActivity extends Activity {
    LevelListDrawable drawable = new LevelListDrawable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ImageView imageView = (ImageView) findViewById(R.id.image_view);
        imageView.setMinimumWidth(ImageLoader.WIDTH);
        imageView.setMinimumHeight(ImageLoader.HEIGHT);
        imageView.setImageDrawable(drawable);

        new LoaderThread(0, "LossyLoader", new LossyLoader()).start();
        new LoaderThread(1, "QualityLoader", new QualityLoader()).start();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN)
            drawable.setLevel(1 - drawable.getLevel());
        return true;
    }

    class LoaderThread extends Thread {
        final int level;
        final String tag;
        final ImageLoader loader;

        protected Bitmap bitmap;

        public LoaderThread(int level, String tag, ImageLoader loader) {
            setPriority(MAX_PRIORITY);
            this.level = level;
            this.tag = tag;
            this.loader = loader;
        }

        @Override
        public void run() {
            final long start = System.currentTimeMillis();
            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.source);
            Log.i(tag, "Decode time: " + (System.currentTimeMillis() - start));
            final BitmapDrawable result = new BitmapDrawable(getResources(), loader.transformBitmap(bitmap));
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    drawable.addLevel(level, level, result);
                    Log.i(tag, "Load time: " + (System.currentTimeMillis() - start));
                }
            });
        }
    }
}
