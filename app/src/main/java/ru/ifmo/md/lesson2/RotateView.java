package ru.ifmo.md.lesson2;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;

/*
 * Created by pokrasko on 23.09.14.
 */
public class RotateView extends SurfaceView implements View.OnClickListener {
    Resources res;
    SurfaceHolder holder;
    Canvas canvas;
    Bitmap source;
    Bitmap scaled;

    int width;
    int height;
    int size;
    int newWidth;
    int newHeight;
    int newSize;

    int[] pixels;
    int[] scaledPixels;
    int[] newPixels;
    int[] red;
    int[] green;
    int[] blue;
    int[] count;

    boolean isFast = false;
    long scaleTime;
    long rotateTime;
    long brightenTime;
    long totalTime;

    Context context;
    String text;
    Toast toast;

    public RotateView(Context context) {
        super(context);
        this.context = context;
        holder = getHolder();

        res = getResources();
        source = BitmapFactory.decodeResource(res, R.drawable.source);
        width = source.getWidth();
        height = source.getHeight();
        size = width * height;
        newWidth = (int) (height / 1.73);
        newHeight = (int) (width / 1.73);
        newSize = newWidth * newHeight;
        scaled = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.RGB_565);

        pixels = new int[size];
        scaledPixels = new int[newSize];
        newPixels = new int[newSize];
        red = new int[newSize];
        green = new int[newSize];
        blue = new int[newSize];
        count = new int[newSize];
    }

    public void onClick(View view) {
        long start = System.currentTimeMillis();
        if (holder.getSurface().isValid()) {
            canvas = holder.lockCanvas();
            isFast = !isFast;

            source.getPixels(pixels, 0, width, 0, 0, width, height);
            scale();
            rotate();
            brighten();
            scaled.setPixels(newPixels, 0, newWidth, 0, 0, newWidth, newHeight);

            canvas.drawColor(0xFF000000);
            canvas.drawBitmap(scaled, 0, 0, null);
            holder.unlockCanvasAndPost(canvas);
        }

        long finish = System.currentTimeMillis();
        totalTime = finish - start;
        writeMessage();
    }

    private void scale() {
        long start = System.currentTimeMillis();

        double heightCoeff = (double) height / newWidth;
        double widthCoeff = (double) width / newHeight;

        if (isFast) {
            for (int y = 0; y < newWidth; y++) {
                for (int x = 0; x < newHeight; x++) {
                    scaledPixels[y * newHeight + x] = pixels[(int) (y * heightCoeff) * width + (int) (x * widthCoeff)];
                }
            }

        } else {
            for (int pix = 0; pix < newSize; pix++) {
                red[pix] = green[pix] = blue[pix] = count[pix] = 0;
            }
            double yStep = (double) newWidth / height;
            double xStep = (double) newHeight / width;
            double yScaled = 0;
            double xScaled;
            int xTrunc;
            int offset = 0;
            int thisPixel = 0;

            for (int y = 0; y < height; y++) {
                xScaled = 0;
                xTrunc = 0;
                for (int x = 0; x < width; x++) {
                    red[offset + xTrunc] += (pixels[thisPixel] >> 16) & 0xFF;
                    green[offset + xTrunc] += (pixels[thisPixel] >> 8) & 0xFF;
                    blue[offset + xTrunc] += pixels[thisPixel++] & 0xFF;
                    count[offset + xTrunc]++;
                    xScaled += xStep;
                    xTrunc = (int) xScaled;
                }
                yScaled += yStep;
                offset = (int) yScaled * newHeight;
            }

            int cnt;
            for (int pix = 0; pix < newSize; pix++) {
                cnt = Math.max(1, count[pix]);
                scaledPixels[pix] = 0xFF000000 | ((red[pix] / cnt) << 16) | ((green[pix] / cnt) << 8) | (blue[pix] / cnt);
            }
        }

        long finish = System.currentTimeMillis();
        scaleTime = finish - start;
    }

    private void rotate() {
        long start = System.currentTimeMillis();

        for (int y = 0; y < newHeight; y++) {
            for (int x = 0; x < newWidth; x++) {
                newPixels[y * newWidth + x] = scaledPixels[(newWidth - x - 1) * newHeight + y];
            }
        }

        long finish = System.currentTimeMillis();
        rotateTime = finish - start;
    }

    private void brighten() {
        long start = System.currentTimeMillis();

        int[] bright = new int[256];
        for (int i = 0; i < 256; i++) {
            bright[i] = (int) Math.sqrt((double) 255 * i);
        }

        int red;
        int green;
        int blue;
        for (int pix = 0; pix < newSize; pix++) {
            red = bright[(newPixels[pix] >> 16) & 0xFF];
            green = bright[(newPixels[pix] >> 8) & 0xFF];
            blue = bright[newPixels[pix] & 0xFF];
            newPixels[pix] = 0xFF000000 | (red << 16) | (green << 8) | blue;
        }

        long finish = System.currentTimeMillis();
        brightenTime = finish - start;
    }

    private void writeMessage() {
        if (isFast) {
            text = "Fast mode";
        } else {
            text = "Slow mode";
        }
        text += "\nScale time: " + scaleTime + " ms";
        text += "\nRotate time: " + rotateTime + " ms";
        text += "\nBrighten time: " + brightenTime + " ms";
        text += "\nTotal time: " + totalTime + " ms";

        int duration = Toast.LENGTH_SHORT;
        toast = Toast.makeText(context, text, duration);
        toast.show();
    }
}
