package ru.ifmo.md.lesson2;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

public class MyActivity extends Activity {

    private ImageView imageView;
    private Bitmap bitmap;
    private Bitmap turnAndLightImage = null;
    private Bitmap lowQuality = null;
    private Bitmap highQuality = null;
    private boolean quality = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        imageView = (ImageView) findViewById(R.id.imageView);
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.source);

        new FastCompressionTask().execute();

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (quality) {
                    new FastCompressionTask().execute();
                } else {
                    new SlowCompressionTask().execute();
                }
                quality = !quality;
            }
        });
    }

    public class SlowCompressionTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            imageView.setImageBitmap(highQuality);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if (turnAndLightImage == null) {
                long start = System.nanoTime();
                turnAndLight();
                long finish = System.nanoTime();
                Log.i("TIME ", "time for turn and light = " + ((finish - start) / 1000000) + "ms");
            }
            if (highQuality == null) {
                long start = System.nanoTime();
                slowCompression();
                long finish = System.nanoTime();
                Log.i("TIME ", "time for slow compression = " + ((finish - start) / 1000000) + "ms");
            }
            return null;
        }
    }

    public class FastCompressionTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            imageView.setImageBitmap(lowQuality);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if (turnAndLightImage == null) {
                long start = System.nanoTime();
                turnAndLight();
                long finish = System.nanoTime();
                Log.i("TIME ", "time for turn and light = " + ((finish - start) / 1000000) + "ms");
            }
            if (lowQuality == null) {
                long start = System.nanoTime();
                fastCompression();
                long finish = System.nanoTime();
                Log.i("TIME ", "time for fast compression = " + ((finish - start) / 1000000) + "ms");
            }
            return null;
        }
    }

    public void turnAndLight() {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int[] pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);

        int newWidth = height;
        int newHeight = width;
        int[] newPixels = new int[newWidth * newHeight];
        turnAndLightImage = Bitmap.createBitmap(newWidth, newHeight, bitmap.getConfig());

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                newPixels[x * newWidth + newWidth - y - 1] = Color.argb(
                        Color.alpha(pixels[y * width + x]),
                        Math.min(Color.red(pixels[y * width + x]) * 2, 255),
                        Math.min(Color.green(pixels[y * width + x]) * 2, 255),
                        Math.min(Color.blue(pixels[y * width + x]) * 2, 255));
            }
        }
        turnAndLightImage.setPixels(newPixels, 0, newWidth, 0, 0, newWidth, newHeight);
    }

    public void fastCompression() {
        int width = turnAndLightImage.getWidth();
        int height = turnAndLightImage.getHeight();
        int[] pixels = new int[width * height];
        turnAndLightImage.getPixels(pixels, 0, width, 0, 0, width, height);

        int newWidth = width * 100 / 173;
        int newHeight = height * 100 / 173;
        int[] newPixels = new int[newWidth * newHeight];
        lowQuality = Bitmap.createBitmap(newWidth, newHeight, turnAndLightImage.getConfig());

        for (int y = 0; y < newHeight; y++) {
            for (int x = 0; x < newWidth; x++) {
                newPixels[y * newWidth + x] = pixels[y * 173 / 100 * width + x * 173 / 100];
            }
        }
        lowQuality.setPixels(newPixels, 0, newWidth, 0, 0, newWidth, newHeight);
    }

    public void slowCompression() {
        int width = turnAndLightImage.getWidth();
        int height = turnAndLightImage.getHeight();
        int[] pixels = new int[width * height];
        turnAndLightImage.getPixels(pixels, 0, width, 0, 0, width, height);

        int newWidth = width * 100 / 173;
        int newHeight = height * 100 / 173;
        int[] alphaPixels = new int[newWidth * newHeight];
        int[] redPixels = new int[newWidth * newHeight];
        int[] greenPixels = new int[newWidth * newHeight];
        int[] bluePixels = new int[newWidth * newHeight];
        int[] countPixels = new int[newWidth * newHeight];
        int[] newPixels = new int[newWidth * newHeight];
        highQuality = Bitmap.createBitmap(newWidth, newHeight, turnAndLightImage.getConfig());
        int nxy;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                nxy = (Math.min(y * 100 / 173, newHeight - 1)) * newWidth + Math.min(x * 100 / 173, newWidth - 1);
                countPixels[nxy]++;
                alphaPixels[nxy] += Color.alpha(pixels[y * width + x]);
                redPixels[nxy] += Color.red(pixels[y * width + x]);
                greenPixels[nxy] += Color.green(pixels[y * width + x]);
                bluePixels[nxy] += Color.blue(pixels[y * width + x]);
            }
        }
        for (int y = 0; y < newHeight; y++) {
            for (int x = 0; x < newWidth; x++) {
                nxy = y * newWidth + x;
                newPixels[nxy] = Color.argb(
                        alphaPixels[nxy] / countPixels[nxy],
                        redPixels[nxy] / countPixels[nxy],
                        greenPixels[nxy] / countPixels[nxy],
                        bluePixels[nxy] / countPixels[nxy]);
            }
        }
        highQuality.setPixels(newPixels, 0, newWidth, 0, 0, newWidth, newHeight);
    }
}
