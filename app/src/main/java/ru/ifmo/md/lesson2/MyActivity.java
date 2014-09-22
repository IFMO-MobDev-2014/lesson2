package ru.ifmo.md.lesson2;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
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
//                cnt[to]++;
//                r[to] += data[from] & ((1 << cntBit) - 1);
//                g[to] += (data[from] >> cntBit) & ((1 << cntBit) - 1);
//                b[to] += (data[from] >> (cntBit * 2)) & ((1 << cntBit) - 1);
                //newData[j * W + i] = data[round(i * scale) + round(j * scale) * width];
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


//    public Bitmap test() {
//        Bitmap r = Bitmap.createBitmap(W, H, Bitmap.Config.ARGB_8888);
//        for (int i = 0; i < W; i++)
//            for (int j = 0; j < H; j++)
//                r.setPixel(i, j, 0xFF008080);
//        return r;
//    }
//    public void run() {
//    myDraw(canvas, bitmap);
//   }
//
//    public void run2() {
//        while (running) {
//            if (holder.getSurface().isValid()) {
//                Canvas canvas = holder.lockCanvas();
//                long t1 = System.nanoTime();
//                Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.source);
//                long t2 = System.nanoTime();
//                bitmap = compression2(bitmap);
//                long t3 = System.nanoTime();
//                bitmap = rotate90_2(bitmap);
//                long t4 = System.nanoTime();
//                myDraw(canvas, bitmap);
//                long t5 = System.nanoTime();
//                holder.unlockCanvasAndPost(canvas);
//
//                Log.i("time decode: ", ": " + ((t2 - t1) / 1000000));
//                Log.i("time comp: ", ": " + ((t3 - t2) / 1000000));
//                Log.i("time rot: ", ": " + ((t4 - t3) / 1000000));
//                Log.i("time draw: ", ": " + ((t5 - t4) / 1000000));
//            }
//        }
//    }
//
//
//    public Bitmap compression(Bitmap bitmap) {
//        float scale = bitmap.getWidth() * 1f / W;
//        Bitmap newBitmap = Bitmap.createBitmap(W, H, Bitmap.Config.ARGB_8888);
//        for (int i = 0; i < W; i++)
//            for (int j = 0; j < H; j++) {
//                newBitmap.setPixel(i, j, bitmap.getPixel(round(i * scale), round(j * scale)));
//            }
//        return newBitmap;
//    }
//    public void myDraw(Canvas canvas, Bitmap bitmap) {
//        int W = bitmap.getWidth();
//        int H = bitmap.getHeight();
//        Rect rect = new Rect(0, 0, W, H);
//        canvas.drawBitmap(bitmap, null, rect, null);
//    }//
//    public Bitmap rotate90(Bitmap bitmap) {
//        Bitmap newBitmap = Bitmap.createBitmap(H, W, Bitmap.Config.ARGB_8888);
//        for (int i = 0; i < W; i++)
//            for (int j = 0; j < H; j++) {
//                int color = bitmap.getPixel(i, j);
//
//                for (int k = 0; k < 3; k++) {
//                    int cntBit = 8;
//                    int tmp = (color >> (k * cntBit)) & ((1 << cntBit) - 1);
//                    color ^= tmp << (k * cntBit);
//                    tmp = min((1 << cntBit) - 1, tmp * 2);
//                    color ^= tmp << (k * cntBit);
//                }
//                newBitmap.setPixel(j, W - 1 - i, color);
//
//            }
//        return newBitmap;
//    }
//    @Override
//    public void onSizeChanged(int w, int h, int oldW, int oldH) {
//        //W = w;
//        //H = h;
//        //width = 240;
//        //height = 320;
////        if (w > h) {
////            int x = width;
////            width = height;
////            height = x;
////        }
//    }

}
