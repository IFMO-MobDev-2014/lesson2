package ru.ifmo.md.lesson2;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import java.util.Arrays;

public class MyActivity extends Activity {

    private ImageView view;

    private final static int TARGET_WIDTH = 434;
    private final static int TARGET_HEIGHT = 405;

    boolean useFast = false;

    static int[] pixelTransformTable = new int[256];
    static {
        for (int i = 0; i < 256; i++) { // offers subjectively better quality
            pixelTransformTable[i] = (int)(Math.sqrt(((float)i)/255.0f)*255.0f);
        }
    }

    Bitmap sourceBitmap = null;
    int[] oldPixels = null;
    int[] newPixels = null;
    int[] scratchpad = null;

    private Bitmap getAdjustedImage(int w, int h, boolean fast) {
        if(sourceBitmap == null)
            sourceBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.source);
        int oldW = sourceBitmap.getWidth();
        int oldH = sourceBitmap.getHeight();
        if(oldPixels == null)
            oldPixels = new int[oldW * oldH];

        sourceBitmap.getPixels(oldPixels, 0, oldW, 0, 0, oldW, oldH);
        if(newPixels == null)
            newPixels = new int[w * h];

        int[] oldPixels = this.oldPixels;
        int[] newPixels = this.newPixels;

        long startTime = System.nanoTime();
        if(fast) { // fast method: nearest
            for(int y = 0; y < h; y++) {
                for(int x = 0; x < w; x++) {
                    newPixels[x + y * w] = oldPixels[y * oldW / h + x * oldH / w * oldW];
                }
            }
        } else { // slow method: plain 'supersampling'
            if(scratchpad == null)
                scratchpad = new int[w * h];
            else
                Arrays.fill(scratchpad, 0);

            int[] scratchpad = this.scratchpad;

            for(int x = 0; x < oldW; x++)
                for(int y = 0; y < oldH; y++) {
                    int targetX = y * w / oldH;
                    int targetY = x * h / oldW;
                    int targetIdx = targetX + targetY * w;

                    int cpv = newPixels[targetIdx];
                    int csv = oldPixels[x + y * oldW];
                    int r = (cpv & 0xff) * scratchpad[targetIdx] + (csv & 0xff);
                    int g = ((cpv & 0xff00) >> 8) * scratchpad[targetIdx] + ((csv & 0xff00) >> 8);
                    int b = ((cpv & 0xff0000) >> 16) * scratchpad[targetIdx] + ((csv & 0xff0000) >> 16);

                    scratchpad[targetIdx]++;

                    r /= scratchpad[targetIdx];
                    g /= scratchpad[targetIdx];
                    b /= scratchpad[targetIdx];

                    newPixels[targetIdx] = 0xff000000 | (b << 16) | (g << 8) | r;
                }
        }
        Log.d("TIME", "Rescale: " + (System.nanoTime() - startTime) / 1000000 );
        startTime = System.nanoTime();
        int[] pixelTransformTable = MyActivity.pixelTransformTable;
        for(int i = 0; i < newPixels.length; i++) {
            int r = newPixels[i] & 0xff;
            int g = (newPixels[i] & 0xff00) >> 8;
            int b = (newPixels[i] & 0xff0000) >> 16;
            r = pixelTransformTable[r];
            g = pixelTransformTable[g];
            b = pixelTransformTable[b];
            newPixels[i] = 0xff000000 | (b << 16) | (g << 8) | r;
        }
        Log.d("TIME", "Recolor: " + (System.nanoTime() - startTime) / 1000000 );
        return Bitmap.createBitmap(newPixels, w, h, Bitmap.Config.ARGB_8888);
    }

    @Override
    public void onTrimMemory(int level) {
        scratchpad = null;
        newPixels = null;
        oldPixels = null;
        if(level >= TRIM_MEMORY_UI_HIDDEN)
            sourceBitmap = null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.layout);
        view = (ImageView) findViewById(R.id.imageView);
        view.setImageBitmap(getAdjustedImage(TARGET_WIDTH, TARGET_HEIGHT, useFast));
    }

    public void onClick(View v) {
        useFast = !useFast;
        view.setImageBitmap(getAdjustedImage(TARGET_WIDTH, TARGET_HEIGHT, useFast));
    }
}
