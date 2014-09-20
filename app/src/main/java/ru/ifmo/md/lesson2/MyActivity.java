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

        int b = c1 & 255;
        int g = (c1 >> 8) & 255;
        int r = (c1 >> 16) & 255;

        b = (int) (sqrt[b] * sqrt[255]);
        g = (int) (sqrt[g] * sqrt[255]);
        r = (int) (sqrt[r] * sqrt[255]);

        if (c == 0)
            return b;
        if (c == 1)
            return g;
        return r;
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

            for (int i = 0; i < w; i++)
                for (int j = 0; j < h; j++) {
                    int x1 = (int) ((ww - 1) * (double) j / (h - 1) + 0.5);
                    int y1 = (int) ((hh - 1) * (1.0 - (double) i / (w - 1)) + 0.5);
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

            int [] rs = new int [w * h];
            int [] gs = new int [w * h];
            int [] bs = new int [w * h];
            int [] count = new int [w * h];

            for (int i = 0; i < ww; i++)
                for (int j = 0; j < hh; j++) {
                    int x1 = (int) ( (w - 1) * ( - (double) j / (hh - 1) + 1.0) + 0.5);
                    int y1 = (int) ( (h - 1) * (double) i / (ww - 1) + 0.5);
                    rs[x1 + y1 * w] += getColor(i, j, 2);
                    gs[x1 + y1 * w] += getColor(i, j, 1);
                    bs[x1 + y1 * w] += getColor(i, j, 0);
                    count[x1 + y1 * w]++;
                }

            for (int i = 0; i < w * h; i++)
            {
                rs[i] = 0xff000000 | (bs[i] / count[i]) | ((gs[i] / count[i]) << 8) | ((rs[i] / count[i]) << 16);
            }

            cachedSlow = Bitmap.createBitmap(rs, w, h, Bitmap.Config.ARGB_8888);

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
            if (result == 1)
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
            if (result == 1)
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
