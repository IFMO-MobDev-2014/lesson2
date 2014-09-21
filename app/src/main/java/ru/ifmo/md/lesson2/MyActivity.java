package ru.ifmo.md.lesson2;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

public class MyActivity extends Activity {


    private final static float SCALE = 1.728f;
    private static boolean quality = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainlay);
        ImageView imgV = (ImageView) findViewById(R.id.iV);
        Bitmap x = BitmapFactory.decodeResource(getResources(), R.drawable.source);
        x = beautyScale(x);
        imgV.setImageBitmap(x);
    }
    private static Bitmap fastScale(Bitmap x) {
        int oWidth = x.getWidth();
        int oHeight = x.getHeight();
        int width = (int) (oWidth / SCALE);
        int height = (int) (oHeight / SCALE);
        int [] newPixels = new int[width * height];
        int [] oldPixels = new int[oHeight * oWidth];
        x.getPixels(oldPixels, 0, oWidth, 0, 0, oWidth, oHeight);
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                newPixels[i * width + j] = oldPixels[((int) ((int) (i * SCALE) * oWidth + j * SCALE))];
            }
        }
        return Bitmap.createBitmap(newPixels, 0, width, width, height, Bitmap.Config.ARGB_8888);
    }
    private static Bitmap beautyScale(Bitmap x) {
        int oWidth = x.getWidth();
        int oHeight = x.getHeight();
        int width = (int) (oWidth / SCALE);
        int height = (int) (oHeight / SCALE);
        int[] newPixels = new int[width * height];
        int[] oldPixels = new int[oHeight * oWidth];
        short[] countOfPixels = new short[width * height];
        short[] redMid = new short[width * height];
        short[] greenMid = new short[width * height];
        short[] blueMid = new short[width * height];
        x.getPixels(oldPixels, 0, oWidth, 0, 0, oWidth, oHeight);
        int newPixPos;
        int oldPixPos;
        int pixel;
        for (int i = 0; i < oHeight; i++) {
            for (int j = 0; j < oWidth; j++) {
                oldPixPos = i * oWidth + j;
                pixel = oldPixels[oldPixPos];
                newPixPos = ((int)(i / SCALE) * width + (int)(j / SCALE));
                redMid[newPixPos] += (pixel & 0x000000FF);
                greenMid[newPixPos] += (pixel & 0x0000FF00) >> 8;
                blueMid[newPixPos] += (pixel & 0x00FF0000) >> 16;
                countOfPixels[newPixPos]++;
            }
        }
        for (int i = 0; i < newPixels.length; i++) {
            if (countOfPixels[i] != 0) {
                newPixels[i] |= (redMid[i] / countOfPixels[i]);
                newPixels[i] |= (greenMid[i] / countOfPixels[i]) << 8;
                newPixels[i] |= (blueMid[i] / countOfPixels[i]) << 16;
                newPixels[i] |= 0xFF000000;
            }
        }
        return Bitmap.createBitmap(newPixels, width, height, Bitmap.Config.ARGB_8888);
    }
    private static void increaseBrightnessOfArray(int [] a) {
        int tmpCol;
        int r, g, b;
        for (int i = 0; i < a.length; i++) {
            tmpCol = a[i];
            r = tmpCol & 0x000000FF;
            g = (tmpCol & 0x0000FF00) >> 8;
            b = (tmpCol & 0x00FF0000) >> 16;
            r = (int)(Math.sqrt((double)r / 255) * 255);
            g = (int)(Math.sqrt((double)g / 255) * 255);
            b = (int)(Math.sqrt((double)b / 255) * 255);
            a[i] = 0xFF000000 | r | (g << 8) | (b << 16);
        }
    }
    private static int [] rotateArrayOfPixels(int [] pixels, int oldWidth, int oldHeight) {
        int [] rotatedPixels;
        rotatedPixels = new int [pixels.length];
        for (int i = 0; i < oldWidth; i++) {
            for (int j = 0; j < oldHeight; j++) {
                rotatedPixels[i * oldHeight + j] = pixels[i + (oldHeight - j - 1) * oldWidth];
            }
        }
        return rotatedPixels;
    }
    private static Bitmap firstStepsOfTask(Bitmap x) {
        int oldWidth = x.getWidth();
        int oldHeight = x.getHeight();
        int [] pixels = new int [oldHeight * oldWidth];
        x.getPixels(pixels, 0, oldWidth, 0, 0, oldWidth, oldHeight);
        int [] rotatedPixels = rotateArrayOfPixels(pixels, oldWidth, oldHeight);
        increaseBrightnessOfArray(rotatedPixels);
        return Bitmap.createBitmap(rotatedPixels, oldHeight, oldWidth, Bitmap.Config.ARGB_8888);
    }
}
