package ru.ifmo.md.lesson2;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.graphics.Bitmap;
import android.view.View;
import android.util.Log;
import android.graphics.Color;
import android.widget.ImageView;

public class MyActivity extends Activity {
    final double scale = 1.73;

    private ImageView viewer;
    private Bitmap maps[] = new Bitmap[2];
    private int nowMap;

    //rotate array of colors by pi / 2. Rotating is clockwise
    private int[] rotate(int[] colors, int w, int h) {
        int[] answer = new int[w * h];
        for (int i = 0; i < colors.length; i++) {
            int row = i / w;
            int column = i % w;
            answer[column * h + (h - row - 1)] = colors[i];
        }
        return answer;
    }

    int averageInRectangle(int[] colors, int x1, int x2, int y1, int y2, int w, int h) {
        int sumAlpha = 0;
        int sumRed = 0;
        int sumGreen = 0;
        int sumBlue = 0;
        for (int i = x1; i < x2; i++)
            for (int j = y1; j < y2; j++) {
                sumAlpha += Color.alpha(colors[i * w + j]);
                sumRed += Color.red(colors[i * w + j]);
                sumGreen += Color.green(colors[i * w + j]);
                sumBlue += Color.blue(colors[i * w + j]);
        }
        int count = (x2 - x1) * (y2 - y1);
        return Color.argb(
                (int)(sumAlpha * 1. / count + .5),
                (int)(sumRed * 1. / count + .5),
                (int)(sumGreen * 1. / count + .5),
                (int)(sumBlue * 1. / count + .5)
        );
    }

    //slow, but good compress
    private int[] goodCompress(int[] colors, int w, int h, int needW, int needH) {
        int[] answer = new int[needW * needH];
        for (int i = 0; i < needH; i++)
            for (int j = 0; j < needW; j++) {
                answer[i * needW + j] = averageInRectangle(
                    colors,
                    (int)((i - 0.5) * scale),
                    (int)((i + 0.5) * scale),
                    (int)((j - 0.5) * scale),
                    (int)((j + 0.5) * scale),
                    w,
                    h
                );
            }
        return answer;
    }

    //fast, but bad compress
    private int[] badCompress(int[] colors, int w, int h, int needW, int needH) {
        int[] answer = new int[needW * needH];
        for (int i = 0; i < h; i++)
            for (int j = 0; j < w; j++) {
                int newRow = (int)(i / scale);
                int newColumn = (int)(j / scale);
                answer[newRow * needW + newColumn] = colors[i * w + j];
            }
        return answer;
    }

    //multiplies red, green and blue parameteres by two
    private void addBrightness(int[] colors) {
        for (int i = 0; i < colors.length; i++) {
            int color = colors[i];
            colors[i] = Color.argb(
                Color.alpha(color),
                Math.min(255, Color.red(color) << 1),
                Math.min(255, Color.green(color) << 1),
                Math.min(255, Color.blue(color) << 1)
            );
        }
    }

    void createMapsAndCompressAll(Bitmap map) {
        int w = map.getWidth();
        int h = map.getHeight();
        int[] colors = new int[w * h];

        map.getPixels(colors, 0, w, 0, 0, w, h);
        colors = rotate(colors, w, h);
        //swap w, h after rotate
        int tmp = w;
        w = h;
        h = tmp;
        addBrightness(colors);

        int needW = (int)((w - 1) / scale + 1);
        int needH = (int)((h - 1) / scale + 1);
        int newColors[] = goodCompress(colors, w, h, needW, needH);
        maps[0] = Bitmap.createBitmap(newColors, 0, needW, needW, needH, Bitmap.Config.ARGB_8888);
        newColors = badCompress(colors, w, h, needW, needH);
        maps[1] = Bitmap.createBitmap(newColors, 0, needW, needW, needH, Bitmap.Config.ARGB_8888);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        long start = System.currentTimeMillis();
        super.onCreate(savedInstanceState);

        viewer = new ImageView(getApplicationContext());
        viewer.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                nowMap = 1 - nowMap;
                viewer.setImageBitmap(maps[nowMap]);
                setContentView(view);
            }
        });


        createMapsAndCompressAll(BitmapFactory.decodeResource(this.getResources(), R.drawable.sourcepng));

        viewer.setScaleType(ImageView.ScaleType.CENTER);
        viewer.setImageBitmap(maps[nowMap]);
        setContentView(viewer);
        long finish = System.currentTimeMillis();
        Log.i("MyActivity", "OnCreate complete in " + Double.toString((finish - start) / 1000.0));
    }
}
