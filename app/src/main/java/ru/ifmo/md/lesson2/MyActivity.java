package ru.ifmo.md.lesson2;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

public class MyActivity extends Activity {

    private enum StateType {
        QUALITY,
        SPEED
    };
    private StateType state = StateType.QUALITY;

    private final static int SOURCE_WIDTH = 700;
    private final static int SOURCE_HEIGHT = 750;
    private final static int TARGET_WIDTH = 405;
    private final static int TARGET_HEIGHT = 434;

    private Bitmap bitmap;
    private Bitmap qualityBitmap;
    private Bitmap speedBitmap;
    private ImageView imageView;
    private int[] initialPixels = new int[SOURCE_HEIGHT * SOURCE_WIDTH];
    private int[] qualityPixels = new int[TARGET_HEIGHT * TARGET_WIDTH];
    private int[] speedPixels = new int[TARGET_HEIGHT * TARGET_WIDTH];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);
        imageView = (ImageView) findViewById(R.id.imageView);
        imageView.setImageResource(R.drawable.source);
        preparePictures();
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Click", "Image view clicked");
                if (state == StateType.QUALITY) {
                    imageView.setImageBitmap(qualityBitmap);
                    state = StateType.SPEED;
                } else {
                    imageView.setImageBitmap(speedBitmap);
                    state = StateType.QUALITY;
                }
            }
        });
    }

    private void preparePictures() {
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.source);
        Log.d("bitmap", "created " + (bitmap != null));
        Log.d("size", "width " + (bitmap.getWidth()));
        Log.d("size", "height " + (bitmap.getHeight()));
        bitmap.getPixels(initialPixels, 0, SOURCE_WIDTH, 0, 0, SOURCE_WIDTH, SOURCE_HEIGHT);
        bitmap.recycle();
        increaseBrightness(initialPixels, SOURCE_WIDTH, SOURCE_HEIGHT);
        rotateMatrix(initialPixels, SOURCE_WIDTH, SOURCE_HEIGHT);
        bitmap = Bitmap.createBitmap(initialPixels, 0, SOURCE_WIDTH, SOURCE_WIDTH, SOURCE_HEIGHT, Bitmap.Config.ARGB_8888);
        speedPixels = dirtyScaling(initialPixels, SOURCE_WIDTH, SOURCE_HEIGHT, TARGET_WIDTH, TARGET_HEIGHT);
        qualityPixels = honestScaling(initialPixels, SOURCE_WIDTH, SOURCE_HEIGHT, TARGET_WIDTH, TARGET_HEIGHT);
        qualityBitmap = Bitmap.createBitmap(qualityPixels, 0, TARGET_WIDTH, TARGET_WIDTH, TARGET_HEIGHT, Bitmap.Config.ARGB_8888);
        speedBitmap = Bitmap.createBitmap(speedPixels, 0, TARGET_WIDTH, TARGET_WIDTH, TARGET_HEIGHT, Bitmap.Config.ARGB_8888);

        imageView.setImageBitmap(bitmap);
    }

    void increaseBrightness(int[] pixels, int width, int height) {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int pos = x + y * width;
                int oldColor = pixels[pos];
                int red = Math.min(Color.red(oldColor) * 2, 255);
                int green = Math.min(Color.green(oldColor) * 2, 255);
                int blue = Math.min(Color.blue(oldColor) * 2, 255);
                int newColor = Color.rgb(red, green, blue);
                pixels[pos] = newColor;
            }
        }
    }

    void rotateMatrix(int[] pixels, int width, int height) {
        int[] result = new int[width * height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int oldPos = x + y * width;
                int newPos = x * width + height - 1 - y;
                result[newPos] = pixels[oldPos];
            }
        }
        System.arraycopy(result, 0, pixels, 0, pixels.length);
    }

    int[] dirtyScaling(int[] source, int src_width, int src_height, int trg_width, int trg_height) {
        int[] target = new int[trg_height * trg_width];
        for (int x = 0; x < trg_width; x++) {
            for (int y = 0; y < trg_height; y++) {
                int oldX = (int) (x * 1f * src_width / trg_width);
                int oldY = (int) (y * 1f * src_height / trg_height);
                int oldPos = oldX + oldY * src_width;
                int newPos = x + y * trg_width;
                target[newPos] = source[oldPos];
            }
        }
        return target;
    }

    int[] honestScaling(int[] source, int src_width, int src_height, int trg_width, int trg_height) {
        int dx[] = {-1, -1, -1, -1, 0, 0, 0, 0, 1, 1, 1, 1, 2, 2, 2, 2};
        int dy[] = {-1,  0,  1,  2, 1, 0, 1, 2,-1, 0, 1, 2,-1, 0, 1, 2};
        int[] target = new int[trg_height * trg_width];
        for (int x = 0; x < trg_width; x++) {
            for (int y = 0; y < trg_height; y++) {
                int oldX = (int) (x * 1f * src_width / trg_width);
                int oldY = (int) (y * 1f * src_height / trg_height);
                int sumAlpha = 0;
                int sumRed = 0;
                int sumGreen = 0;
                int sumBlue = 0;
                int count = 0;  //number of correct neighbours
                for (int k = 0; k < dx.length; k++) {
                    int newX = oldX + dx[k];
                    int newY = oldY + dy[k];
                    if (0 <= newX && newX < src_width && 0 <= newY && newY < src_height) {
                        int pos = newX + newY * src_width;
                        int color = source[pos];
                        sumAlpha += Color.alpha(color);
                        sumRed += Color.red(color);
                        sumGreen += Color.green(color);
                        sumBlue += Color.blue(color);
                        count++;
                    }
                }
                int newPos = x + y * trg_width;
                target[newPos] = Color.argb(sumAlpha / count, sumRed / count, sumGreen / count, sumBlue / count);
            }
        }
        return target;
    }

}
