package ru.ifmo.md.lesson2;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class MyActivity extends Activity {

    ImageView image;
    boolean setFast;
    BitmapEditor sourceBitmap;
    Bitmap bitmap1;
    //bitmap1 - fast
    Bitmap bitmap2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout);
        setFast = true;

        image = (ImageView) findViewById(R.id.imageView);
        sourceBitmap = new BitmapEditor(BitmapFactory.decodeResource(getResources(), R.drawable.image));
        sourceBitmap.turnRight();
        sourceBitmap.changeBrightness(42);
        //42 - Answer to The Ultimate Question of Life, the Universe, and Everything
        bitmap1 = sourceBitmap.Neighbords();
        image.setImageBitmap(bitmap1);
        bitmap2 = sourceBitmap.bilinearInterpolation();
        image.setImageBitmap(bitmap1);
    }


    public void changeImage(View view) {
        if (setFast) {
            image.setImageBitmap(bitmap2);
        } else {
            image.setImageBitmap(bitmap1);

        }
        setFast = !setFast;
    }

    public class BitmapEditor {
        int width;
        int height;
        int[] pixels;
        double scale = 1.73;

        public BitmapEditor(Bitmap bitmap) {
            width = bitmap.getWidth();
            height = bitmap.getHeight();
            pixels = new int[width * height];
            bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        }


        public void turnRight() {
            int newWidth = height;
            int newHeight = width;
            int[] newPixels = new int[width * height];

            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    newPixels[x * height + (height - y - 1)] = pixels[y * width + x];
                }
            }

            width = newWidth;
            height = newHeight;
            pixels = newPixels;
        }

        public void changeBrightness(int x) {
            for (int i = 0; i < pixels.length; i++) {
                int alpha = (pixels[i] >> 24) & 0xFF;
                int red =(pixels[i] >> 16) & 0xFF;
                int green =(pixels[i] >> 8) & 0xFF;
                int blue =(pixels[i]) & 0xFF;
                red+=x;
                green+=x;
                blue+=x;
                if (red > 255) {
                    red = 255;
                }
                if (red < 0) {
                    red = 0;
                }

                if (green > 255) {
                    green = 255;
                }
                if (green < 0) {
                    green = 0;
                }

                if (blue > 255) {
                    blue = 255;
                }
                if (blue < 0) {
                    blue = 0;
                }

                pixels[i] = (alpha << 24) | (red << 16) | (green << 8) | (blue);
                //http://stackoverflow.com/questions/8246566/how-to-create-an-argb-8888-pixel-value
                //just accept it
            }
        }

        public Bitmap Neighbords() {
            int newWidth  = (int) ((double) width  / scale);
            int newHeight = (int) ((double) height / scale);
            int [] newPixels = new int[newWidth * newHeight];

            for (int x = 0; x < newWidth; x++) {
                for (int y = 0; y < newHeight; y++) {
                    int current = (int) (x * scale) + (int) (y * scale) * width;
                    newPixels[x + y * newWidth] = pixels[current];
                }
            }
            return Bitmap.createBitmap(newPixels, newWidth, newHeight, Bitmap.Config.ARGB_8888);
            //http://stackoverflow.com/questions/8246566/how-to-create-an-argb-8888-pixel-value
            //just accept it ONCE AGAIN
        }

        public Bitmap bilinearInterpolation() {
            int newWidth  = (int) ((double) width  / scale);
            int newHeight = (int) ((double) height / scale);
            int[] newPixels = new int[newWidth * newHeight];
            for (int x = 0; x < newWidth; x++) {
                for (int y = 0; y < newHeight; y++) {
                    double oldX = x * scale;
                    double oldY = y * scale;
                    int x1 = (int) oldX;
                    int x2 = Math.min((int) (oldX + 1), width);
                    int y1 = (int) oldY;
                    int y2 = Math.min((int) (oldY + 1), height);
                    //Here I began to hate my life
                    int alphaX1 = mathX(x, x1, x2, (((pixels[x1 + y1 * width] >> 24)) & 0xFF), (((pixels[x2 + y1 * width] >> 24)) & 0xFF));
                    int alphaX2 = mathX(x, x1, x2, (((pixels[x1 + y2 * width] >> 24)) & 0xFF), (((pixels[x2 + y2 * width] >> 24)) & 0xFF));
                    int redX1 = mathX(x, x1, x2, (((pixels[x1 + y1 * width] >> 16)) & 0xFF), (((pixels[x2 + y1 * width] >> 16)) & 0xFF));
                    int redX2 = mathX(x, x1, x2, (((pixels[x1 + y2 * width] >> 16)) & 0xFF), (((pixels[x2 + y2 * width] >> 16)) & 0xFF));
                    int greenX1 = mathX(x, x1, x2, (((pixels[x1 + y1 * width] >> 8)) & 0xFF), (((pixels[x2 + y1 * width] >> 8)) & 0xFF));
                    int greenX2 = mathX(x, x1, x2, (((pixels[x1 + y2 * width] >> 8)) & 0xFF), (((pixels[x2 + y2 * width] >> 8)) & 0xFF));
                    int blueX1 = mathX(x, x1, x2, (((pixels[x1 + y1 * width])) & 0xFF), (((pixels[x2 + y1 * width])) & 0xFF));
                    int blueX2 = mathX(x, x1, x2, (((pixels[x1 + y2 * width])) & 0xFF), (((pixels[x2 + y2 * width])) & 0xFF));
                    int alpha = (int) ((((y2 - y) / (y2 - y1)) * alphaX1) + ((y - y1) / (y2 - y1)) * alphaX2);
                    int red = (int) (((y2 - y) / (y2 - y1)) * redX1 + ((y - y1) / (y2 - y1)) * redX2);
                    int green = (int) (((y2 - y) / (y2 - y1)) * greenX1 + ((y - y1) / (y2 - y1)) * greenX2);
                    int blue = (int) (((y2 - y) / (y2 - y1)) * blueX1 + ((y - y1) / (y2 - y1)) * blueX2);
                    newPixels[x + y * newWidth] = ((alpha << 24) | (red << 16) | (green << 8) | (blue));
                    //kill me pls
                }
            }
            return Bitmap.createBitmap(newPixels, newWidth, newHeight, Bitmap.Config.ARGB_8888);
        }

        private int mathX(int x, int x1, int x2, int color1, int color2) {
            return (int) (((x2 - x) / (x2 - x1)) * color1 + ((x - x1) / (x2 - x1)) * color2);
        }
    }

}