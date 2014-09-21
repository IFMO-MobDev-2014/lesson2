package ru.ifmo.md.lesson2;

import android.graphics.Bitmap;

/**
 * @author volhovm
 *         Created on 9/20/14
 */

@SuppressWarnings("UnusedDeclaration")
public class EditorFactory {
    private static final int THREAD_NUMBER = 1;
    Thread[] threads = new Thread[THREAD_NUMBER];
    int width, height;
    volatile int[] colors;

    public EditorFactory(Bitmap bitmap) {
        width = bitmap.getWidth();
        height = bitmap.getHeight();
        colors = new int[width * height];
        bitmap.getPixels(colors, 0, width, 0, 0, width, height);
    }

    public static enum Direction {
        ClockWise, CounterClockWise, UpsideDown
    }

    public EditorFactory fastShrink(double k) {
        int newH = (int) ((double) height / k);
        int newW = (int) ((double) width / k);
        int[] colorArray = new int[newW * newH];
        for (int i = 0; i < newW; i++) {
            for (int j = 0; j < newH; j++) {
                int a = (int) (i * k) + (int) (j * k) * width;
                colorArray[i + j * newW] = colors[a];
            }
        }
        width = newW;
        height = newH;
        colors = colorArray;
        return this;
    }


    private int average(int... colors){
        long a = 0;
        long r = 0;
        long g = 0;
        long b = 0;
        for (int color : colors) {
            a += (color >> 24) & 0xFF;
            r += (color >> 16) & 0xFF;
            g += (color >> 8) & 0xFF;
            b += color & 0xFF;
        }
        a /= colors.length;
        r /= colors.length;
        g /= colors.length;
        b /= colors.length;
        return (int) (a << 24 | r << 16 | g << 8 | b);
    }

    private volatile int[] colorArray;
    public EditorFactory niceShrink(double k) {
        int newH = (int) ((double) height / k);
        int newW = (int) ((double) width / k);
        colorArray = new int[newW * newH];

        // ------inner part-------
        for (int i = 0; i < THREAD_NUMBER; i++){
            threads[i] = new Thread(new Antialiaser(i, newW, newH, k));
            threads[i].start();
        }


        // ---edges and corners--- [x + y * width]
        colorArray[0] =
                average(colors[0],
                        colors[1],
                        colors[width]);
        colorArray[(newW - 1)] =
                average(colors[width - 1],
                        colors[width - 2],
                        colors[width * 2 - 1]);
        colorArray[((newH - 1) * newW)] =
                average(colors[(int) ((newH - 1) * k) * width],
                        colors[(int) ((newH - 1) * k) * width + 1],
                        colors[(int) ((newH - 1) * k) * width - width]);
        colorArray[newW - 1 + (newH - 1) * newW] =
                average(colors[(int) ((newH - 1) * k) * width + width - 1],
                        colors[(int) ((newH - 1) * k) * width + width - 2],
                        colors[(int) ((newH - 1) * k) * width - 1]);

        for (int i = 1; i < newW - 1; i++) {
            colorArray[i] = average(
                    colors[(int) (i * k)],
                    colors[(int) (i * k) - 1],
                    colors[(int) (i * k) + 1],
                    colors[(int) (i * k) + width],
                    colors[(int) (i * k) + width + 1],
                    colors[(int) (i * k) + width - 1]
            );
            colorArray[i + (newH - 1) * newW] = average(
                    colors[(int) ((i + ((newH - 1) * newW)) * k)],
                    colors[(int) ((i + ((newH - 1) * newW)) * k) + 1],
                    colors[(int) ((i + ((newH - 1) * newW)) * k) - 1],
                    colors[(int) ((i + ((newH - 1) * newW)) * k) - width],
                    colors[(int) ((i + ((newH - 1) * newW)) * k) - width + 1],
                    colors[(int) ((i + ((newH - 1) * newW)) * k) - width - 1]
            );
        }
        for (int i = 1; i < newH - 1; i++) {
            colorArray[i * newW] = average(
                    colors[(int) (i * k) * width],
                    colors[(int) (i * k) * width + 1],
                    colors[(int) ((i - 1) * k) * width],
                    colors[(int) ((i + 1) * k) * width],
                    colors[(int) ((i - 1) * k) * width + 1],
                    colors[(int) ((i + 1) * k) * width + 1]
            );
            colorArray[(newW - 1) + i * newW] = average(
                    colors[width - 1 + (int) (i * k) * width],
                    colors[width - 2 + (int) (i * k) * width],
                    colors[width - 1 + (int) ((i - 1) * k) * width],
                    colors[width - 2 + (int) ((i - 1) * k) * width],
                    colors[width - 1 + (int) ((i + 1) * k) * width],
                    colors[width - 2 + (int) ((i + 1) * k) * width]
            );
        }
        //------------------------

        for (int i = 0; i < THREAD_NUMBER; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        width = newW;
        height = newH;
        colors = colorArray;
        return this;
    }

    private class Antialiaser implements Runnable {
        private int num;
        private int newH;
        private int newW;
        private double k;

        private Antialiaser(int num, int newW, int newH, double k) {
            this.num = num;
            this.newW = newW;
            this.newH = newH;
            this.k = k;
        }

        @Override
        public void run() {
            int delta = ((newH - 2) / THREAD_NUMBER);
            for (int i = 1; i < newW - 1; i++) {
                for (int j = 1 + delta * num; j < 2 + delta * (num + 1); j++) {
                    int a = (int) (i * k) + (int) (j * k) * width;
                    colorArray[i + j * newW] = average(
                            colors[a],
                            colors[a - 1],
                            colors[a + 1],
                            colors[a - width],
                            colors[a - width + 1],
                            colors[a - width - 1],
                            colors[a + width],
                            colors[a + width + 1],
                            colors[a + width - 1]
                    );
                }
            }
        }
    }

    public EditorFactory setBrightness(double x) {
        int A, R, G, B;
        for (int i = 0; i < colors.length; i++) {
            A = (colors[i] >> 24) & 0xFF;
            R = (colors[i] >> 16) & 0xFF;
            G = (colors[i] >> 8) & 0xFF;
            B = colors[i] & 0xFF;
            R += x;
            G += x;
            B += x;
            if (R > 255) R = 255;
            if (G > 255) G = 255;
            if (B > 255) B = 255;
            if (R < 0) R = 0;
            if (G < 0) G = 0;
            if (B < 0) B = 0;
            colors[i] = A << 24 | R << 16 | G << 8 | B;
        }
        return this;
    }

    public EditorFactory turn(Direction direction) {
        int[] temp = new int[width * height];
        if (direction == Direction.ClockWise) {
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    temp[(height - 1 - j) + i * height] = colors[i + j * width];
                }
            }
            int c = width;
            width = height;
            height = c;
            colors = temp;
        }
        return this;
    }

    public Bitmap collect(){
        return Bitmap.createBitmap(colors, 0, width, width, height, Bitmap.Config.ARGB_8888);
    }
}
