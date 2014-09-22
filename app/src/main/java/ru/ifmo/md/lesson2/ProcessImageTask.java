package ru.ifmo.md.lesson2;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

/**
 * Created by flyingleafe on 20.09.14.
 */
class ProcessImageTask extends AsyncTask<Bitmap, Void, Void> {
    final ImageView view;
    final boolean hq;
    int iwidth;
    int iheight;
    static final int rwidth = 405;
    static final int rheight = 434;
    static final double resizeRate = 1.73;
    Bitmap resultImg;
    int[] bigPixels;
    int[] pixels = new int[rwidth * rheight];
    int[] pixelsRotated = new int[rwidth * rheight];
    int[] edgeMask;
    float[] hsv = new float[3];

    ProcessImageTask(ImageView view, boolean hq) {
        super();
        this.view = view;
        this.hq = hq;
    }

    private void resizeLQ() {
        double rate = (double) iwidth / (double) rwidth;
        for (int x = 0; x < rwidth; x++) {
            for (int y = 0; y < rheight; y++) {
                int ix = (int) Math.floor(rate * x);
                int iy = (int) Math.floor(rate * y);
                pixels[x + y * rwidth] = bigPixels[ix + iy * iwidth];
            }
        }
    }

    private void resizeHQ() {
        double rate = (double) iwidth / (double) rwidth;
        for (int x = 0; x < rwidth; x++) {
            for (int y = 0; y < rheight; y++) {
                int lowX = (int) Math.floor(rate * x);
                int highX = (int) Math.floor(rate * (x + 1));
                int lowY = (int) Math.floor(rate * y);
                int highY = (int) Math.floor(rate * (y + 1));
                if (highX > iwidth) highX = iwidth;
                if (highY > iheight) highY = iheight;
                int aR = 0;
                int aG = 0;
                int aB = 0;
                int count = 0;
                for (int i = lowX; i < highX; i++) {
                    for (int j = lowY; j < highY; j++) {
                        int index = i + j * iwidth;
                        int pixel = bigPixels[index];
                        aR += Color.red(pixel);
                        aB += Color.blue(pixel);
                        aG += Color.green(pixel);
                        count++;
                    }
                }
                aR /= count;
                aG /= count;
                aB /= count;
                pixels[x + y * rwidth] = Color.rgb(aR, aG, aB);
            }
        }
    }

    private void rotate() {
        for (int x = 0; x < rwidth; x++) {
            for (int y = 0; y < rheight; y++) {
                pixelsRotated[(rheight - 1 - y) + x * rheight] = pixels[x + y * rwidth];
            }
        }
    }

    private void brighten(int rate) {
        for (int i = 0; i < rheight * rwidth; ++i) {
            int pixel = pixelsRotated[i];
            Color.colorToHSV(pixel, hsv);
            hsv[2] *= rate;
            pixelsRotated[i] = Color.HSVToColor(hsv);
        }
    }

    @Override
    protected Void doInBackground(Bitmap... bp) {
        iwidth = bp[0].getWidth();
        iheight = bp[0].getHeight();
        bigPixels = new int[iwidth * iheight];
        bp[0].getPixels(bigPixels, 0, iwidth, 0, 0, iwidth, iheight);
        long startTime = System.currentTimeMillis();
        if (hq) {
            edgeMask = new int[iwidth * iheight];
            resizeHQ();
            Log.i("QUALITY", "High");
        } else {
            resizeLQ();
            Log.i("QUALITY", "Low");
        }
        rotate();
        brighten(2);
        resultImg = Bitmap.createBitmap(pixelsRotated, rheight, rwidth, Bitmap.Config.RGB_565);
        long endTime = System.currentTimeMillis();
        Log.i("TIME", (endTime - startTime) + "");
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        view.setImageBitmap(resultImg);
    }
}
