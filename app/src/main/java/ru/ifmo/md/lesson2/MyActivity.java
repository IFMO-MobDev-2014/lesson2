package ru.ifmo.md.lesson2;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class MyActivity extends Activity {

    public static final double splash = 0.5780346820809249; //1/1.73
    public static final double scale = 1.73;
    int oldWidth;
    int oldHeight;
    int newWidth;
    int newHeight;
    int[] oldPicture;
    int[] fastResizeArray;
    int[] resizeArray;
    ImageView output;
    boolean fastMod = true;
    protected Boolean slowModeOk = false;

    private Bitmap bitmap;


    private static void fastResize(int[] oldPicture, int oldWidth, int oldHeight, int[] newPicture, int newWidth, int newHeight) {
        for (int i = 0; i < newHeight; i++) {
            for (int t = 0; t < newWidth; t++) {
                newPicture[i * newWidth + t] = oldPicture[(int) (Math.floor(scale * i) * oldWidth + Math.floor(scale * t))];
            }
        }
    }

    protected static int[] rotate(int[] picture, final int width, final int height) {
        int[] temp = new int[width * height];
        for (int i = 0; i < height; i++) {
            for (int t = 0; t < width; t++) {
                temp[t * height + (height - i - 1)] = picture[i * width + t];
            }
        }
        return temp;
    }

    public void changeBrightness(int[] image) {

        int pixel = 0;
        int alpha = 0xFF;
        float[] hsv = new float[3];

        for (int i = 0; i < image.length; i++) {
            pixel = image[i];
            Color.colorToHSV(pixel, hsv);
            hsv[2] *= 2;

            image[i] = Color.HSVToColor(alpha, hsv);
        }
    }


    protected static void resize(int[] oldPicture, int oldWidth, int oldHeight, int[] newPicture, int newWidth, int newHeight) {
        int[] colors = new int[3];
        for (int i = 0; i < newHeight; i++) {
            for (int t = 0; t < newWidth; t++) {
                int x = (int) (i * scale);
                int y = (int) (t * scale);
                colors[0] = 0;
                colors[1] = 0;
                colors[2] = 0;
                for (int dx = -1; dx < 2; dx++) {
                    for (int dy = -1; dy < 2; dy++) {
                        if ((((x + dx) >= 0) && (x + dx) < oldHeight) && (((y + dy) >= 0) && (y + dy) < oldWidth)) {
                            colors[0] += ((oldPicture[(x + dx) * oldWidth + (y + dy)] & 0x00FF0000) >> 16);
                            colors[1] += ((oldPicture[(x + dx) * oldWidth + (y + dy)] & 0x0000FF00) >> 8);
                            colors[2] += ((oldPicture[(x + dx) * oldWidth + (y + dy)] & 0x000000FF));
                        } else {
                            colors[0] += ((oldPicture[(x) * oldWidth + (y)] & 0x00FF0000) >> 16);
                            colors[1] += ((oldPicture[(x) * oldWidth + (y)] & 0x0000FF00) >> 8);
                            colors[2] += ((oldPicture[(x) * oldWidth + (y)] & 0x000000FF));
                        }
                    }
                }
                newPicture[i * newWidth + t] = Math.min(((int) Math.floor(colors[0] / 9)), 0xFF) << 16 | Math.min(((int) Math.floor(colors[1] / 9)), 0xFF) << 8 | Math.min(((int) Math.floor(colors[2] / 9)), 0xFF);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout);
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.source);
        oldWidth = bitmap.getWidth();
        oldHeight = bitmap.getHeight();
        oldPicture = new int[oldHeight * oldWidth];
        bitmap.getPixels(oldPicture, 0, oldWidth, 0, 0, oldWidth, oldHeight);
        newHeight = (int) (oldHeight * splash);
        newWidth = (int) (oldWidth * splash);
        fastResizeArray = new int[newHeight * newWidth];
        resizeArray = new int[newHeight * newWidth];

        fastResize(oldPicture, oldWidth, oldHeight, fastResizeArray, newWidth, newHeight);
        resize(oldPicture, oldWidth, oldHeight, resizeArray, newWidth, newHeight);


        fastResizeArray = rotate(fastResizeArray, newWidth, newHeight);
        resizeArray = rotate(resizeArray, newWidth, newHeight);

        newHeight = (int) (oldWidth * splash);
        newWidth = (int) (oldHeight * splash);
        changeBrightness(fastResizeArray);
        changeBrightness(resizeArray);

        output = (ImageView) findViewById(R.id.imageView);
        bitmap = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.RGB_565);
        show();
    }

    protected void show() {
        if (fastMod) {
            bitmap.setPixels(fastResizeArray, 0, newWidth, 0, 0, newWidth, newHeight);
        } else {
            bitmap.setPixels(resizeArray, 0, newWidth, 0, 0, newWidth, newHeight);
        }
        output.setImageBitmap(bitmap);
    }


    public void onClick(View view)
    {
        fastMod =  !fastMod;
        show();
    }
}

