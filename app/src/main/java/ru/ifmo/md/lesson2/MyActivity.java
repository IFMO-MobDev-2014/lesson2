package ru.ifmo.md.lesson2;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class MyActivity extends Activity {

    public static final int WIDTH = 405;
    public static final int HEIGHT = 434;
    public static final int SOURCE_WIDTH = 700;
    public static final int SOURCE_HEIGHT = 750;
    public static final double SCALE = (double) SOURCE_WIDTH / (double) WIDTH;
    public static final double OPPOSITE_SCALE = (double) WIDTH / (double) SOURCE_WIDTH;
    public static final int BRIGHTNESS_CHANGE = 50;

    public static Bitmap resource;

    public static int[] r = new int[WIDTH * HEIGHT];
    public static int[] g = new int[WIDTH * HEIGHT];
    public static int[] b = new int[WIDTH * HEIGHT];
    public static int[] n = new int[WIDTH * HEIGHT];

    public static int[] matrix = new int[SOURCE_HEIGHT * SOURCE_WIDTH];
    public static int[] recolorMatrix = new int[SOURCE_HEIGHT * SOURCE_WIDTH];
    public static int[] slowMatrix = new int[HEIGHT * WIDTH];
    public static int[] fastMatrix = new int[HEIGHT * WIDTH];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ImageView img = new ImageView(getApplicationContext());
        img.setScaleType(ImageView.ScaleType.CENTER);

        resource = BitmapFactory.decodeResource(getResources(), R.drawable.source);
        resource.getPixels(matrix, 0, SOURCE_WIDTH, 0, 0, SOURCE_WIDTH, SOURCE_HEIGHT);
        lightAndRotate();
        fastCompressInit();
        slowCompressInit();

        final Bitmap compressFast = Bitmap.createBitmap(fastMatrix, WIDTH, HEIGHT, Bitmap.Config.RGB_565);
        final Bitmap compressSlow = Bitmap.createBitmap(slowMatrix, WIDTH, HEIGHT, Bitmap.Config.RGB_565);

        img.setImageBitmap(compressFast);
        View.OnClickListener l = new View.OnClickListener() {
            boolean cnt = false;

            @Override
            public void onClick(View v) {
                if (cnt)
                    img.setImageBitmap(compressFast);
                else
                    img.setImageBitmap(compressSlow);
                cnt = !cnt;
            }
        };
        img.setOnClickListener(l);
        setContentView(img);
    }

    public static void fastCompressInit() {
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                int x2 = (int) ((double) x * SCALE);
                int y2 = (int) ((double) y * SCALE);

                fastMatrix[x + WIDTH * y] = recolorMatrix[x2 + SOURCE_WIDTH * y2];
            }
        }
    }

    public static int rS = 0xFF;
    public static int gS = 0xFF;
    public static int bS = 0xFF;

    public static void slowCompressInit() {
        // in case of phone rotation process restarts, reinitialisation of arrays needed
        for (int i = 0; i < HEIGHT * WIDTH; i++) {
            r[i] = 0;
            g[i] = 0;
            b[i] = 0;
            n[i] = 0;

        }

        for (int i = 0; i < SOURCE_WIDTH; i++) {
            for (int j = 0; j < SOURCE_HEIGHT; j++) {
                int x = (int) ((double) i * OPPOSITE_SCALE);
                int y = (int) ((double) j * OPPOSITE_SCALE);

                color = recolorMatrix[i + j * SOURCE_WIDTH];
                rS = (color & 0xFF0000) >> 16;
                gS = (color & 0x00FF00) >> 8;
                bS = color & 0x0000FF;

                r[x + WIDTH * y] += rS;
                g[x + WIDTH * y] += gS;
                b[x + WIDTH * y] += bS;
                n[x + WIDTH * y]++;

            }
        }

        for (int i = 0; i < WIDTH * HEIGHT; i++) {
            r[i] /= n[i];
            g[i] /= n[i];
            b[i] /= n[i];
            slowMatrix[i] = (r[i] << 16) + (g[i] << 8) + b[i];
        }
    }

    public static int color;
    public static int rr;
    public static int gg;
    public static int bb;

    public static void lightAndRotate() {
        for (int x = 0; x < SOURCE_WIDTH; x++) {
            for (int y = 0; y < SOURCE_HEIGHT; y++) {

                color = matrix[x + SOURCE_WIDTH * y];
                rr = (color & 0xFF0000) >> 16;
                gg = (color & 0x00FF00) >> 8;
                bb = color & 0x0000FF;

                rr += Math.min(0xFF - rr, BRIGHTNESS_CHANGE);
                gg += Math.min(0xFF - gg, BRIGHTNESS_CHANGE);
                bb += Math.min(0xFF - bb, BRIGHTNESS_CHANGE);

                color = (rr << 16) + (gg << 8) + bb;
                recolorMatrix[(SOURCE_WIDTH - x - 1) + SOURCE_WIDTH * (SOURCE_HEIGHT - y - 1)] = color;
            }
        }
    }
}
