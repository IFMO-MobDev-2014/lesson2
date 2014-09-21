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
    private MyImage sourceImage;
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
        drawerThread = new DrawerThread(R.drawable.source, getHolder());
        drawerThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
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
        private final int[] colors;
        private final int width;
        private final int height;

        private MyImage(int[] colors, int width, int height) {
            this.colors = colors;
            this.width = width;
            this.height = height;
            if (width * height != colors.length) {
                throw new IllegalArgumentException("Height and width doesn't match to colors array");
            }
        }

        public char getRed(int x, int y) {
            return (char) ((colors[x * width + y] >> 16) & 255);
        }

        public char getGreen(int x, int y) {
            return (char) ((colors[x * width + y] >> 8) & 255);
        }

        public char getBlue(int x, int y) {
            return (char) (colors[x * width + y] & 255);
        }

        public char getAlpha(int x, int y) {
            return (char) ((colors[x * width + y] >> 24) & 255);
        }

        public int getBilinearPixel(double heightRatio, double widthRatio) {
            heightRatio = heightRatio * height - 0.5;
            widthRatio = widthRatio * width - 0.5;
            int x = (int) Math.floor(heightRatio);
            int y = (int) Math.floor(widthRatio);
            double uRatio = heightRatio - x;
            double vRatio = widthRatio - y;
            double uOpposite = 1 - uRatio;
            double vOpposite = 1 - vRatio;
            char red = (char) ((getRed(x, y) * uOpposite + getRed(x + 1, y) * uRatio) * vOpposite +
                    (getRed(x, y + 1) * uOpposite + getRed(x + 1, y + 1) * uRatio) * vRatio);
            char green = (char) (
                    (getGreen(x, y) * uOpposite + getGreen(x + 1, y) * uRatio) * vOpposite +
                            (getGreen(x, y + 1) * uOpposite + getGreen(x + 1, y + 1) * uRatio) *
                                    vRatio);
            char blue = (char) (
                    (getBlue(x, y) * uOpposite + getBlue(x + 1, y) * uRatio) * vOpposite +
                            (getBlue(x, y + 1) * uOpposite + getBlue(x + 1, y + 1) * uRatio) *
                                    vRatio);
            char alpha = (char) (
                    (getAlpha(x, y) * uOpposite + getAlpha(x + 1, y) * uRatio) * vOpposite +
                            (getAlpha(x, y + 1) * uOpposite + getAlpha(x + 1, y + 1) * uRatio) *
                                    vRatio);
            return alpha * (1 << 24) + red * (1 << 16) + green * (1 << 8) + blue;
        }

        public void increaseBrightness() {
            for (int i = 0; i < colors.length; i++) {
                char alpha = (char) ((colors[i] >> 24) & 255);
                char red = (char) ((colors[i] >> 16) & 255);
                char green = (char) ((colors[i] >> 8) & 255);
                char blue = (char) ((colors[i]) & 255);
                red = (char) Math.min(red * 2, 255);
                green = (char) Math.min(green * 2, 255);
                blue = (char) Math.min(blue * 2, 255);

                colors[i] = alpha * (1 << 24) + red * (1 << 16) + green * (1 << 8) + blue;
            }
        }

        public MyImage bilinearSqueeze(int newWidth, int newHeight) {
            int[] newColors = new int[newWidth * newHeight];
            double pixelOffsetY = (1.0D / newWidth) / 2.0D;
            double pixelOffsetX = (1.0D / newHeight) / 2.0D;
            for (int i = 0; i < newHeight; i++) {
                for (int j = 0; j < newWidth; j++) {
                    newColors[i * newWidth + j] = getBilinearPixel(
                            pixelOffsetX + (i * 1.0D) / newHeight,
                            pixelOffsetY + (j * 1.0D) / newWidth);
                }
            }

            return new MyImage(newColors, newWidth, newHeight);
        }

        public MyImage naiveSqueeze(int newWidth, int newHeight) {
            int[] newColors = new int[newWidth * newHeight];
            for (int i = 0; i < newWidth; i++) {
                for (int j = 0; j < newHeight; j++) {
                    int x = (int) (i * width * 1.0F / newWidth);
                    int y = (int) (j * height * 1.0F / newHeight);
                    newColors[i + j * newWidth] = colors[x + y * width];
                }
            }
            return new MyImage(newColors, newWidth, newHeight);
        }

        public MyImage rotate() {
            int[] newColors = new int[height * width];
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    newColors[i * height + j] = colors[width * (height - 1 - j) + i];
                }
            }
            return new MyImage(newColors, height, width);
        }

        public Bitmap convertToBitmap() {
            return Bitmap.createBitmap(colors, width, height, Bitmap.Config.RGB_565);
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
                    tappedOnScreen = true;
                    if (x < toDraw.getWidth() && y < toDraw.getHeight()) {
                        fast = !fast;
                        drawerThread = new DrawerThread(R.drawable.source, getHolder());
                        drawerThread.start();
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                if (tappedOnScreen) {
                    tappedOnScreen = false;
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
                sourceImage = new MyImage(oldColors, oldBitmap.getWidth(), oldBitmap.getHeight()).rotate();
                sourceImage.increaseBrightness();
            }
            if (fast) {
                if (newFastBitmap == null) {
                    MyImage fastConvertedImage = sourceImage.naiveSqueeze((int) (
                            oldBitmap.getWidth() / squeezing), (int) (oldBitmap.getHeight() /
                            squeezing));
                    newFastBitmap = fastConvertedImage.convertToBitmap();
                }
                toDraw = newFastBitmap;
            } else {
                if (newBitmap == null) {
                    MyImage convertedImage = sourceImage.bilinearSqueeze((int) (
                            oldBitmap.getWidth() / squeezing), (int) (oldBitmap.getHeight() /
                            squeezing));
                    newBitmap = convertedImage.convertToBitmap();
                }
                toDraw = newBitmap;
            }
            Canvas canvas = null;
            try {
                canvas = holder.lockCanvas(null);
                canvas.drawBitmap(toDraw, 0, 0, null);
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