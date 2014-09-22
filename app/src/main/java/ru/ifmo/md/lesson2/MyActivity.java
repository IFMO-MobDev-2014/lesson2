package ru.ifmo.md.lesson2;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

public class MyActivity extends Activity {
    private final int HEIGHT = 405;
    private final int WIDTH = 434;
    private ImageView imageView;
    private Bitmap srcBitmap;
    private Bitmap fastScaledBitmap = Bitmap.createBitmap(WIDTH, HEIGHT, Bitmap.Config.RGB_565);
    private Bitmap niceScaledBitmap = Bitmap.createBitmap(WIDTH, HEIGHT, Bitmap.Config.RGB_565);
    private int [] pixels = new int[1400 * 1500];
    private int [] srcPixels = new int[1400 * 1500];
    private enum CurrentImage { FAST, NICE }
    CurrentImage currentImage = CurrentImage.FAST;

    public void changeImage(View v) {
        switch (currentImage) {
            case FAST:
                currentImage = CurrentImage.NICE;
                imageView.setImageBitmap(niceScaledBitmap);
                Log.i("Scale method", "Interpolated");
                break;
            case NICE:
                currentImage = CurrentImage.FAST;
                imageView.setImageBitmap(fastScaledBitmap);
                Log.i("Scale method", "Nearest neighbour");
                break;
        }
    }

    private void increaseBrightness(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        bitmap.getPixels(srcPixels, 0, width, 0, 0, width, height);
        for(int x = 0; x < width; x++) {
            for(int y = 0; y < height; y++) {
                int color = srcPixels[x + y * width];
                int red = Color.red(color);
                int green = Color.green(color);
                int blue = Color.blue(color);
                red = Math.min(red * 2, 0xFF);
                green = Math.min(green * 2, 0xFF);
                blue = Math.min(blue * 2, 0xFF);
                srcPixels[x + y * width] =  Color.rgb(red, green, blue);
            }
        }
        bitmap.setPixels(srcPixels, 0, width, 0, 0, width, height);
    }

    private void fastScale(Bitmap bitmap, int w, int h) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        double wScale = (double)width / w;
        double hScale = (double)height / h;
        bitmap.getPixels(srcPixels, 0, width, 0, 0, width, height);

        for(int x = 0; x < w; x++) {
            for(int y = 0; y < h; y++) {
                pixels[x + y * w] = srcPixels[(int) Math.round(x * wScale) + width * ((int) Math.round(y * hScale))];
            }
        }
        fastScaledBitmap.setPixels(pixels, 0, w, 0, 0, w, h);
    }

    private int midColor(int nw, int ne, int sw, int se, double north, double west) {
        int red = (int)Math.round(north*west * Color.red(nw) + north*(1f - west) * Color.red(ne) + (1f-north)*west * Color.red(sw) + (1f-north)*(1f-west)*Color.red(se));
        int green = (int)Math.round(north*west * Color.green(nw) + north*(1f - west) * Color.green(ne) + (1f-north)*west * Color.green(sw) + (1f-north)*(1f-west)*Color.green(se));
        int blue = (int)Math.round(north*west * Color.blue(nw) + north*(1f - west) * Color.blue(ne) + (1f-north)*west * Color.blue(sw) + (1f-north)*(1f-west)*Color.blue(se));
        return Color.rgb(red, green, blue);
    }

    private void niceScale(Bitmap bitmap, int w, int h) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float wScale = (float)width / w;
        float hScale = (float)height / h;
        bitmap.getPixels(srcPixels, 0, width, 0, 0, width, height);
        for(int x = 0; x < w; x++) {
            for(int y = 0; y < h; y++) {
                int nw = srcPixels[(int)Math.floor(x * wScale) + width * ((int)Math.floor(y * hScale))];
                int ne = srcPixels[(int) Math.ceil(x * wScale) + width * ((int)Math.floor(y * hScale))];
                int sw = srcPixels[(int)Math.floor(x * wScale) + width * ((int) Math.ceil(y * hScale))];
                int se = srcPixels[(int) Math.ceil(x * wScale) + width * ((int) Math.ceil(y * hScale))];
                double north = y * hScale - Math.floor(y * hScale);
                double west  = x * wScale - Math.floor(x * wScale);
                pixels[x + y * w] = midColor(nw, ne, sw, se, north, west);
            }
        }
        niceScaledBitmap.setPixels(pixels, 0, w, 0, 0, w, h);
    }

    private Bitmap rotateCW(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        bitmap.getPixels(srcPixels, 0, width, 0, 0, width, height);
        for(int x = 0; x < width; x++) {
            for(int y = 0; y < height; y++) {
                pixels[y + x * height] = srcPixels[x + (height - y - 1) * width];
            }
        }
        return Bitmap.createBitmap(pixels, height, width, Bitmap.Config.RGB_565);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = (ImageView) findViewById(R.id.imageView);
        srcBitmap = rotateCW(BitmapFactory.decodeResource(getResources(), R.drawable.source)).copy(Bitmap.Config.RGB_565, true);
        increaseBrightness(srcBitmap);
        fastScale(srcBitmap, WIDTH, HEIGHT);
        imageView.setImageBitmap(fastScaledBitmap);
        niceScale(srcBitmap, WIDTH, HEIGHT);
    }
}
