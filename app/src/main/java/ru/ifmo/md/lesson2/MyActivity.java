package ru.ifmo.md.lesson2;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;

public class MyActivity extends Activity {

    private final static int SOURCE_ROTATED_HEIGHT = 700;
    private final static int SOURCE_ROTATED_WIDTH = 750;
    private final static int SOURCE_HEIGHT = 750;
    private final static int SOURCE_WIDTH = 700;
    private final static int NEW_WIDTH = 434;
    private final static int NEW_HEIGHT = 405;
    private final static double scaleX = 1.73;
    private final static double scaleY = 1.73;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bitmap source = BitmapFactory.decodeResource(getResources(), R.drawable.source);
        int[] pixels = new int[SOURCE_HEIGHT * SOURCE_WIDTH];
        source.getPixels(pixels, 0, SOURCE_WIDTH, 0, 0, SOURCE_WIDTH, SOURCE_HEIGHT);
        preparePicture(pixels);
        MyView myview = new MyView(this);
        myview.setImages(fastScaling(pixels), qualityScaling(pixels));
        setContentView(myview);
    }

    public Bitmap fastScaling(int[] pixels) {
        int[] result = new int[NEW_WIDTH * NEW_HEIGHT];
        for (int i = 0; i < NEW_HEIGHT; i++) {
            for (int j = 0; j < NEW_WIDTH; j++) {
                result[i * NEW_WIDTH + j] = pixels[(int) (i * scaleX) * SOURCE_ROTATED_WIDTH + (int) (j * scaleY)];
            }
        }
        return Bitmap.createBitmap(result, NEW_WIDTH, NEW_HEIGHT, Bitmap.Config.ARGB_8888);
    }

    public Bitmap qualityScaling(int[] pixels) {
        int[] newPixels = new int[NEW_WIDTH * NEW_HEIGHT];
        double redD, alphaD, greenD, blueD;
        double s2 = scaleX * scaleY;
        double s1;
        for (int x = 0; x < NEW_HEIGHT; x++) {
            for (int y = 0; y < NEW_WIDTH; y++) {
                alphaD = 0.0D;
                redD = 0.0D;
                greenD = 0.0D;
                blueD = 0.0D;
                for (int i = (int) (x * scaleX); i < Math.min(1 + (int)((x + 1) * scaleX), SOURCE_ROTATED_HEIGHT); i++) {
                    int ind;
                    for (int j = (int) (y * scaleY); j < Math.min(1 + (int)((y + 1) * scaleY), SOURCE_ROTATED_WIDTH); j++) {
                        s1 = Math.min((x + 1) * scaleX, i + 1) - Math.max(x * scaleX, i);
                        s1 *= Math.max(Math.min((y + 1) * scaleY, j + 1) - Math.max(y * scaleY, j), 0.0D);
                        ind = i * SOURCE_ROTATED_WIDTH + j;
                        alphaD += Color.alpha(pixels[ind]) * s1;
                        redD += Color.red(pixels[ind]) * s1;
                        greenD += Color.green(pixels[ind]) * s1;
                        blueD += Color.blue(pixels[ind]) * s1;
                    }
                }
                int alpha = (int)(alphaD / s2);
                int red = (int)(redD / s2);
                int green = (int)(greenD / s2);
                int blue = (int)(blueD / s2);
                newPixels[x * NEW_WIDTH + y] = Color.argb(alpha, red, green, blue);
            }
        }
        return Bitmap.createBitmap(newPixels, NEW_WIDTH, NEW_HEIGHT, Bitmap.Config.ARGB_8888);
    }

    public void preparePicture(int[] pixels) {
        char alpha, red, blue, green;
        for (int i = 0; i < pixels.length; i++) {
            alpha = (char) (Color.alpha(pixels[i]));
            red = (char) Math.min(Color.red(pixels[i]) * 2, 255);
            green = (char) Math.min(Color.green(pixels[i]) * 2, 255);
            blue = (char) Math.min(Color.blue(pixels[i]) * 2, 255);
            pixels[i] = Color.argb(alpha, red, green, blue);
        }
        int[] rotated = new int[pixels.length];
        for (int i = 0; i < SOURCE_WIDTH; i++) {
            for (int j = 0; j < SOURCE_HEIGHT; j++) {
                rotated[i * SOURCE_HEIGHT + j] = pixels[(SOURCE_HEIGHT - j - 1) * SOURCE_WIDTH + i];
            }
        }
        System.arraycopy(rotated, 0, pixels, 0, pixels.length);
    }

}
