package ru.ifmo.md.lesson2;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

public class MyActivity extends Activity {

    private Bitmap cachedFast = null;
    private Bitmap cachedSlow = null;
    private static final int w = 434;
    private static final int h = 405;
    private double[] sqrt;
    ImageView imageButton;
    protected boolean mode = true;
    private int [] colors;
    private int ww, hh;

    @Override
    public void onTrimMemory(int level) {
        if (mode)
            cachedFast = null;
        else
            cachedSlow = null;
        if (level >= TRIM_MEMORY_RUNNING_CRITICAL) {
            cachedFast = null;
            cachedSlow = null;
        }
    }

    private int mixColor(int x, int y)
    {
        int c1 = colors[x + y * ww];

        int b = c1 & 255;
        int g = (c1 >> 8) & 255;
        int r = (c1 >> 16) & 255;

        b = (int) (sqrt[b] * sqrt[255]);
        g = (int) (sqrt[g] * sqrt[255]);
        r = (int) (sqrt[r] * sqrt[255]);

        return (0xff000000) | b | (g << 8) | (r << 16);
    }

    private int getColor(int x, int y, int c)
    {
        int c1 = colors[x + y * ww];

        if (c == 0)
            return (int) (sqrt[c1 & 255] * sqrt[255]);
        if (c == 1)
            return (int) (sqrt[(c1 >> 8) & 255] * sqrt[255]);
        return (int) (sqrt[(c1 >> 16) & 255] * sqrt[255]);
    }

    private void cacheFast()
    {
        if (cachedFast == null)
        {
            long start = System.nanoTime();
            Bitmap temp = BitmapFactory.decodeResource(this.getResources(), R.drawable.source);
            ww = temp.getWidth();
            hh = temp.getHeight();
            colors = new int[ww * hh];
            int[] fast_colors = new int[w * h];
            temp.getPixels(colors, 0, ww, 0, 0, ww, hh);

            int x1, y1;

            for (int i = 0; i < w; i++)
                for (int j = 0; j < h; j++) {
                    x1 = (int) ((ww - 1) * (float) j / (h - 1) + 0.5f);
                    y1 = (int) ((hh - 1) * (1.0 - (float) i / (w - 1)) + 0.5f);
                        fast_colors[i + j * w] = mixColor(x1, y1);
                }

            cachedFast = Bitmap.createBitmap(fast_colors, w, h, Bitmap.Config.ARGB_8888);

            colors = null;

            Log.v("Fast:", Long.toString((System.nanoTime() - start) / 1000000));
        }
    }

    private void cacheSlow()
    {
        if (cachedSlow == null)
        {
            long start = System.nanoTime();
            Bitmap temp = BitmapFactory.decodeResource(this.getResources(), R.drawable.source);
            ww = temp.getWidth();
            hh = temp.getHeight();
            colors = new int[ww * hh];
            temp.getPixels(colors, 0, ww, 0, 0, ww, hh);
            cachedSlow = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);

            short [] rs = new short [w * h];
            short [] gs = new short [w * h];
            short [] bs = new short [w * h];
            byte [] count = new byte [w * h];

            int x1, y1, c;

            for (int i = 0; i < ww; i++)
                for (int j = 0; j < hh; j++) {
                    x1 = (int) ( (w - 1) * ( - (float) j / (hh - 1) + 1.0f) + 0.5f);
                    y1 = (int) ( (h - 1) * (float) i / (ww - 1) + 0.5f);
                    c = x1 + y1 * w;
                    rs[c] += getColor(i, j, 2);
                    gs[c] += getColor(i, j, 1);
                    bs[c] += getColor(i, j, 0);
                    count[c]++;
                }

            colors = new int[w * h];

            for (int i = 0; i < w * h; i++)
            {
                colors[i] = (0xff000000 | (bs[i] / count[i]) | ((gs[i] / count[i]) << 8) | ((rs[i] / count[i]) << 16));
            }

            cachedSlow = Bitmap.createBitmap(colors, w, h, Bitmap.Config.ARGB_8888);

            colors = null;

            Log.v("Slow:", Long.toString((System.nanoTime() - start) / 1000000));
        }
    }

    private class cacheSlowTask extends AsyncTask<Integer, Integer, Integer> {
        protected Integer doInBackground(Integer... ints) {
            cacheSlow();
            return 1;
        }

        protected void onProgressUpdate(Integer... progress) {
        }

        protected void onPostExecute(Integer result) {
            if (mode)
                imageButton.setImageBitmap(cachedSlow);
        }
    }

    private class cacheFastTask extends AsyncTask<Integer, Integer, Integer> {
        protected Integer doInBackground(Integer... ints) {
            cacheFast();
            return 1;
        }

        protected void onProgressUpdate(Integer... progress) {
        }

        protected void onPostExecute(Integer result) {
            if (!mode)
                imageButton.setImageBitmap(cachedFast);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.layout1);

        sqrt = new double[256];
        for (int i = 0; i < 256; i++)
            sqrt[i] = Math.sqrt(i);

        new cacheFastTask().execute();
        new cacheSlowTask().execute();
        imageButton = (ImageView) findViewById(R.id.imageView);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mode) {
                    new cacheSlowTask().execute();
                } else {
                    new cacheFastTask().execute();
                }
                mode = !mode;
            }
        });
    }
}
