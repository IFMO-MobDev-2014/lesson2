package ru.ifmo.md.lesson2;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import static java.lang.Math.min;
import static java.lang.Math.round;


public class MyActivity extends Activity {
    boolean flag = false;
    final static int W = 405;
    final static int H = 434;
    Bitmap bitmap0, bitmap;
    ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.active);
        bitmap0 = BitmapFactory.decodeResource(getResources(), R.drawable.source);
        imageView = (ImageView)findViewById(R.id.ViewXXX);
        check();
    }
    void check() {
        if (flag)
            good();
        else
            bad();
        imageView.setImageBitmap(bitmap);
    }

    void good() {
        bitmap = rotate90(compression(bitmap0));
    }

    void bad() {
        bitmap = rotate90(compressionFast(bitmap0));
    }


    public Bitmap compressionFast(Bitmap bitmap) {
        float scale = bitmap.getWidth() * 1f / W;
        Bitmap newBitmap = Bitmap.createBitmap(W, H, Bitmap.Config.ARGB_8888);
        int [] data = new int[bitmap.getHeight() * bitmap.getWidth()];
        int [] newData = new int [W * H];
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        bitmap.getPixels(data, 0, width, 0, 0, width, height);
        for (int i = 0; i < W; i++)
            for (int j = 0; j < H; j++)
                newData[j * W + i] = data[round(i * scale) + round(j * scale) * width];
        for (int i = 0; i < W * H; i++) {
            for (int k = 0; k < 3; k++) {
                int cntBit = 8;
                int tmp = (newData[i] >> (k * cntBit)) & ((1 << cntBit) - 1);
                newData[i] ^= tmp << (k * cntBit);
                tmp = min((1 << cntBit) - 1, tmp * 2);
                newData[i] ^= tmp << (k * cntBit);
            }
        }
        newBitmap.setPixels(newData, 0, W, 0, 0, W, H);
        return newBitmap;
    }

    public Bitmap compression (Bitmap bitmap) {
        float scale = bitmap.getWidth() * 1f / W;
        Bitmap newBitmap = Bitmap.createBitmap(W, H, Bitmap.Config.ARGB_8888);
        int [] data = new int[bitmap.getHeight() * bitmap.getWidth()];
        int [] newData = new int [W * H];
        int [] cnt = new int [W * H];
        int [] r = new int[W * H];
        int [] g = new int[W * H];
        int [] b = new int[W * H];
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        bitmap.getPixels(data, 0, width, 0, 0, width, height);
        int cntBit = 8;
        for (int i = 0; i < W * H; i++)
            r[i] = g[i] = b[i] = 0xFF;
        for (int i = 0; i < width; i++)
            for (int j = 0; j < height; j++) {
                int from = i + j * width;
                int to  = round(j / scale) * W + round(i / scale);
                r[to] = min(r[to], data[from] & ((1 << cntBit) - 1));
                g[to] = min(g[to], (data[from] >> cntBit) & ((1 << cntBit) - 1));
                b[to] = min(b[to], (data[from] >> (cntBit * 2)) & ((1 << cntBit) - 1));
            }
        for (int i = 0; i < W * H; i++) {
            r[i] = min((1 << cntBit) - 1, r[i] * 2);
            g[i] = min((1 << cntBit) - 1, g[i] * 2);
            b[i] = min((1 << cntBit) - 1, b[i] * 2);
            newData[i] = r[i] | (g[i] << cntBit) | (b[i] << (cntBit + cntBit)) | (0xFF << (cntBit * 3));
        }
        newBitmap.setPixels(newData, 0, W, 0, 0, W, H);
        return newBitmap;
    }

    public Bitmap rotate90(Bitmap bitmap) {
        Bitmap newBitmap = Bitmap.createBitmap(H, W, Bitmap.Config.ARGB_8888);
        int [] data = new int[W * H];
        int [] newData = new int [W * H];
        bitmap.getPixels(data, 0, W, 0, 0, W, H);
        for (int i = 0; i < W; i++)
            for (int j = 0; j < H; j++)
                newData[(W - i - 1) * H + j] = data[i + j * W];
        newBitmap.setPixels(newData, 0, H, 0, 0, H, W);
        return newBitmap;
    }

    public void myClick(View view) {
        flag = !flag;
        check();
    }

}
