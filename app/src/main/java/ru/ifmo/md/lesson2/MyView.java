package ru.ifmo.md.lesson2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class MyView extends SurfaceView implements SurfaceHolder.Callback {
    private DrawerThread drawerThread;
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

    private static class Color {
        private final int color;

        private Color(int color) {
            this.color = color;
        }

        private Color(char red, char green, char blue, char alpha) {
            color = alpha * (1 << 24) + red * (1 << 16) + green * (1 << 8) + blue;
        }

        public char getRed() {
            return (char) ((color >> 16) & 255);
        }

        public char getGreen() {
            return (char) ((color >> 8) & 255);
        }

        public char getBlue() {
            return (char) (color & 255);
        }

        public char getAlpha() {
            return (char) ((color >> 24) & 255);
        }

        public int getColor() {
            return color;
        }

        public static char changeContrast(char source, double contrast) {
            double newSource = source;
            newSource /= 255.0;
            newSource -= 0.5;
            newSource *= contrast;
            newSource += 0.5;
            newSource *= 255;

            if (newSource < 0)
                newSource = 0;
            if (newSource > 255)
                newSource = 255;
            return (char) newSource;
        }

        public Color changeContrast(double contrast) {
            return new Color(changeContrast(getRed(), contrast), changeContrast(getGreen(), contrast), changeContrast(getBlue(), contrast), changeContrast(getAlpha(), contrast));
        }
    }

    private class Image {
        private final Color[] colors;
        private final int[] simpleColors;
        private final int width;
        private final int height;

        private Image(int[] colors, int width, int height) {
            simpleColors = colors;
            this.colors = new Color[colors.length];
            for (int i = 0; i < colors.length; i++) {
                this.colors[i] = new Color(colors[i]);
            }
            this.width = width;
            this.height = height;
            if (width * height != colors.length) {
                throw new IllegalArgumentException("Height and width doesn't match to colors array");
            }
        }

        public Color getColor(int x, int y) {
            return colors[x * width + y];
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
                    (getColor(x, y).getRed() * u_opposite + getColor(x + 1, y).getRed() * u_ratio) *
                            v_opposite + (getColor(x, y + 1).getRed() * u_opposite +
                            getColor(x + 1, y + 1).getRed() * u_ratio) * v_ratio);
            char green = (char) ((getColor(x, y).getGreen() * u_opposite +
                    getColor(x + 1, y).getGreen() * u_ratio) * v_opposite +
                    (getColor(x, y + 1).getGreen() * u_opposite +
                            getColor(x + 1, y + 1).getGreen() * u_ratio) * v_ratio);
            char blue = (char) ((getColor(x, y).getBlue() * u_opposite +
                    getColor(x + 1, y).getBlue() * u_ratio) * v_opposite +
                    (getColor(x, y + 1).getBlue() * u_opposite +
                            getColor(x + 1, y + 1).getBlue() * u_ratio) * v_ratio);
            char alpha = (char) ((getColor(x, y).getAlpha() * u_opposite +
                    getColor(x + 1, y).getAlpha() * u_ratio) * v_opposite +
                    (getColor(x, y + 1).getAlpha() * u_opposite +
                            getColor(x + 1, y + 1).getAlpha() * u_ratio) * v_ratio);
            return new Color(red, green, blue, alpha).getColor();
        }

        public Image bilinearSqueeze(int newWidth, int newHeight) {
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
            return new Image(colors, newWidth, newHeight);
        }

        public Image rotate() {
            int[] newColors = new int[height * width];
            for(int n = 0; n < height * width; n++) {
                int i = n / height;
                int j = height - 1 - n % height;
                newColors[n] = colors[width * j + i].getColor();
            }
            return new Image(newColors, height, width);
        }

        public Image changeContrast(double contrast) {
            int[] newColors = new int[height * width];

            contrast *= contrast;

            for(int n = 0; n < height * width; n++) {
                newColors[n] = colors[n].changeContrast(contrast).getColor();
            }

            return new Image(newColors, width, height);
        }

        public Bitmap convertToBitmap() {
            return Bitmap.createBitmap(simpleColors, width, height, Bitmap.Config.RGB_565);
        }
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
            Bitmap oldBitmap = BitmapFactory.decodeResource(getResources(), drawableId);
            int[] colors = new int[oldBitmap.getWidth() * oldBitmap.getHeight()];
            oldBitmap.getPixels(colors, 0, oldBitmap.getWidth(), 0, 0, oldBitmap.getWidth(), oldBitmap.getHeight());
            Image image = new Image(colors, oldBitmap.getWidth(), oldBitmap.getHeight());
            Image convertedImage = image.bilinearSqueeze((int) (oldBitmap.getWidth() /
                    squeezing), (int) (oldBitmap.getHeight() / squeezing));
            Bitmap newBitmap = convertedImage.rotate().changeContrast(0.81D).convertToBitmap();
            Canvas canvas = null;
            try {
                canvas = holder.lockCanvas(null);
                canvas.drawBitmap(oldBitmap, 0, 0, null);
                canvas.drawBitmap(newBitmap, 0, 800, null);
            } catch (NullPointerException ignore) {

            } finally {
                if (canvas != null) {
                    holder.unlockCanvasAndPost(canvas);
                }
            }
        }
    }
}
