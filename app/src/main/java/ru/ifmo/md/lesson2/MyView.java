package ru.ifmo.md.lesson2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class MyView extends SurfaceView implements SurfaceHolder.Callback {
    private DrawerThread drawerThread;
    private Bitmap newBitmap;
    private Bitmap newFastBitmap;
    private Bitmap oldBitmap;
    private Bitmap toDraw;
    private boolean tappedOnScreen = false;
    private boolean fast = true;
    private int[] oldColors;
    public static final double squeezing = 1.73D;

    public MyView(Context context) {
        super(context);
        getHolder().addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        Log.i("Drawing", "Surface created");
        drawerThread = new DrawerThread(R.drawable.source, getHolder());
        drawerThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        Log.d("Drawing", "Surface destroyed");
        boolean trying = true;
        while (trying) {
            try {
                drawerThread.join();
                trying = false;
            } catch (InterruptedException ignore) {

            }
        }
    }

    private class MyImage {
        private final int[] simpleColors;
        private final int width;
        private final int height;

        private MyImage(int[] colors, int width, int height) {
            simpleColors = colors;
            this.width = width;
            this.height = height;
            if (width * height != colors.length) {
                throw new IllegalArgumentException("Height and width doesn't match to colors array");
            }
        }

        public char getRed(int x, int y) {
            return (char) ((simpleColors[x * width + y] >> 16) & 255);
        }

        public char getGreen(int x, int y) {
            return (char) ((simpleColors[x * width + y] >> 8) & 255);
        }

        public char getBlue(int x, int y) {
            return (char) (simpleColors[x * width + y] & 255);
        }

        public char getAlpha(int x, int y) {
            return (char) ((simpleColors[x * width + y] >> 24) & 255);
        }

        public int getBilinearPixel(double u, double v) {
            u = u * height - 0.5;
            v = v * width - 0.5;
            int x = (int) Math.floor(u);
            int y = (int) Math.floor(v);
            double u_ratio = u - x;
            double v_ratio = v - y;
            double u_opposite = 1 - u_ratio;
            double v_opposite = 1 - v_ratio;
            char red = (char) (
                    (getRed(x, y) * u_opposite + getRed(x + 1, y) * u_ratio) * v_opposite +
                            (getRed(x, y + 1) * u_opposite + getRed(x + 1, y + 1) * u_ratio) *
                                    v_ratio);
            char green = (char) (
                    (getGreen(x, y) * u_opposite + getGreen(x + 1, y) * u_ratio) * v_opposite +
                            (getGreen(x, y + 1) * u_opposite + getGreen(x + 1, y + 1) * u_ratio) *
                                    v_ratio);
            char blue = (char) (
                    (getBlue(x, y) * u_opposite + getBlue(x + 1, y) * u_ratio) * v_opposite +
                            (getBlue(x, y + 1) * u_opposite + getBlue(x + 1, y + 1) * u_ratio) *
                                    v_ratio);
            char alpha = (char) (
                    (getAlpha(x, y) * u_opposite + getAlpha(x + 1, y) * u_ratio) * v_opposite +
                            (getAlpha(x, y + 1) * u_opposite + getAlpha(x + 1, y + 1) * u_ratio) *
                                    v_ratio);
            return alpha * (1 << 24) + red * (1 << 16) + green * (1 << 8) + blue;
        }

        public MyImage bilinearSqueeze(int newWidth, int newHeight) {
            int[] colors = new int[newWidth * newHeight];
            double pixelOffsetY = (1.0D / newWidth) / 2.0D;
            double pixelOffsetX = (1.0D / newHeight) / 2.0D;
            for (int i = 0; i < newHeight; i++) {
                for (int j = 0; j < newWidth; j++) {
                    colors[i * newWidth + j] = getBilinearPixel(
                            pixelOffsetX + (i * 1.0D) / newHeight,
                            pixelOffsetY + (j * 1.0D) / newWidth);
                }
            }
            return new MyImage(colors, newWidth, newHeight);
        }

        public MyImage fastSqueeze(int newWidth, int newHeight) {
            int[] newColors = new int[newWidth * newHeight];

            int YD = (height / newHeight) * width - width;
            int YR = height % newHeight;
            int XD = width / newWidth;
            int XR = width % newWidth;
            int outer = 0;
            int inner = 0;

            for (int y = newHeight, YE = 0; y > 0; y--) {
                for (int x = newWidth, XE = 0; x > 0; x--) {
                    newColors[outer++] = simpleColors[inner];
                    inner += XD;
                    XE += XR;
                    if (XE >= newWidth) {
                        XE -= newWidth;
                        inner++;
                    }
                }
                inner += YD;
                YE += YR;
                if (YE >= newHeight) {
                    YE -= newHeight;
                    inner += width;
                }
            }
            return new MyImage(newColors, newWidth, newHeight);
        }

        public MyImage rotate() {
            int[] newColors = new int[height * width];
            for (int n = 0; n < height * width; n++) {
                int i = n / height;
                int j = height - 1 - n % height;
                newColors[n] = simpleColors[width * j + i];
            }
            return new MyImage(newColors, height, width);
        }

        public Bitmap convertToBitmap() {
            return Bitmap.createBitmap(simpleColors, width, height, Bitmap.Config.RGB_565);
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        int x = (int) event.getX();
        int y = (int) event.getY();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (!tappedOnScreen) {
                    Log.d("Touch", "Down!");
                    tappedOnScreen = true;
                    if (x >= 0 && x < (toDraw.getWidth()) && y >= 800 &&
                            y < (800 + toDraw.getHeight())) {
                        fast = !fast;
                        drawerThread = new DrawerThread(R.drawable.source, getHolder());
                        drawerThread.start();
                        Log.d("Touch", "Yep!");
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                if (tappedOnScreen) {
                    tappedOnScreen = false;
                    Log.d("Touch", "Up!");
                }
                break;
        }
        return true;
    }

    private class DrawerThread extends Thread {
        private final int drawableId;
        private final SurfaceHolder holder;

        private DrawerThread(int drawableId, SurfaceHolder holder) {
            this.drawableId = drawableId;
            this.holder = holder;
        }

        @Override
        public void run() {
            long startTime = System.nanoTime();
            if (oldColors == null) {
                oldBitmap = BitmapFactory.decodeResource(getResources(), drawableId);
                oldColors = new int[oldBitmap.getWidth() * oldBitmap.getHeight()];
                oldBitmap.getPixels(oldColors, 0, oldBitmap.getWidth(), 0, 0, oldBitmap.getWidth(), oldBitmap.getHeight());
            }
            if (fast) {
                if (newFastBitmap == null) {
                    MyImage image = new MyImage(oldColors, oldBitmap.getWidth(), oldBitmap.getHeight());
                    MyImage fastConvertedImage = image.fastSqueeze((int) (oldBitmap.getWidth() /
                            squeezing), (int) (oldBitmap.getHeight() / squeezing));
                    newFastBitmap = fastConvertedImage.rotate().convertToBitmap();
                }
                toDraw = newFastBitmap;
            } else {
                if (newBitmap == null) {
                    MyImage image = new MyImage(oldColors, oldBitmap.getWidth(), oldBitmap.getHeight());
                    MyImage convertedImage = image.bilinearSqueeze((int) (oldBitmap.getWidth() /
                            squeezing), (int) (oldBitmap.getHeight() / squeezing));
                    newBitmap = convertedImage.rotate().convertToBitmap();
                }
                toDraw = newBitmap;
            }
            Canvas canvas = null;
            try {
                canvas = holder.lockCanvas(null);
                canvas.drawBitmap(oldBitmap, 0, 0, null);
                canvas.drawBitmap(toDraw, 0, 800, null);
            } catch (NullPointerException ignore) {

            } finally {
                if (canvas != null) {
                    holder.unlockCanvasAndPost(canvas);
                }
            }
            long finishTime = System.nanoTime();
            Log.d("TIME", Long.toString((finishTime - startTime) / 1000000L));
        }
    }
}
