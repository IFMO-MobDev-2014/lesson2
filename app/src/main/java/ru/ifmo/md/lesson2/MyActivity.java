package ru.ifmo.md.lesson2;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.io.InputStream;

public class MyActivity extends Activity {

    ImageView image;
    Bitmap bitmap = Bitmap.createBitmap(434, 405, Bitmap.Config.ARGB_8888);
    private static final int ORIGINAL_WIDTH = 700;
    private static final int ORIGINAL_HEIGHT = 750;
    private static final int WIDTH = 434;
    private static final int HEIGHT = 405;
    private static final double RATIO = 1.73;
    int[][][] source = new int[ORIGINAL_WIDTH][ORIGINAL_HEIGHT][3];
    boolean fast = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        image = (ImageView)findViewById(R.id.imageView);
        loadSource();
        updateImage();
    }

    private void loadSource() {
        InputStream is = getResources().openRawResource(R.drawable.source);
        Bitmap original = BitmapFactory.decodeStream(is);
        for (int x=0; x<ORIGINAL_WIDTH; x++) {
            for (int y = 0; y<ORIGINAL_HEIGHT; y++) {
                int color = original.getPixel(x, y);
                source[x][y][0] = Color.red(color);
                source[x][y][1] = Color.green(color);
                source[x][y][2] = Color.blue(color);
            }
        }
    }

    private void updateImage() {
        for (int x=0; x<WIDTH; x++) {
            int oy = (int)(ORIGINAL_HEIGHT - x*RATIO - 1);
            for (int y=0; y<HEIGHT; y++) {
                int ox = (int)(y*RATIO);
                int[] colors = new int[3];

                for (int i=0; i<3; i++) {
                    if (fast) {
                        colors[i] = source[ox][oy][i];
                    } else {
                        int sum = 0;
                        int count = 0;
                        for (int x1 = ox; x1 < Math.min(ORIGINAL_WIDTH, ox + 2); x1++) {
                            for (int y1 = oy; y1 < Math.min(ORIGINAL_HEIGHT, oy + 2); y1++) {
                                sum += source[x1][y1][i];
                                count++;
                            }
                        }
                        colors[i] = sum / count;
                    }
                }

                final int OFFSET = 50;
                for (int i=0; i<3; i++) {
                    colors[i] = Math.min(colors[i] + OFFSET, 0xff);
                }

                int newColor = Color.rgb(colors[0], colors[1], colors[2]);
                bitmap.setPixel(x, y, newColor);
            }
        }
        image.setImageBitmap(bitmap);
    }

    public void onClick(View view) {
        fast = !fast;
        updateImage();
    }
}
