package ru.ifmo.md.lesson2;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class MyActivity extends Activity {

    ImageView imageView;
    Bitmap fast,
            good,
            original;
    int[] pixels;
    int red,
            blue,
            green;
    public static final int WIDTH = 405,
            HEIGHT = 434,
            OLDWIDTH = 700,
            OLDHEIGHT = 750;
    public static final double SCALE = 1.73f;
    boolean current = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout);
        draw();
    }

    public void draw() {
        pixels = new int[700 * 750];
        original = BitmapFactory.decodeResource(this.getResources(), R.drawable.img);
        original = Bitmap.createScaledBitmap(original, OLDWIDTH, OLDHEIGHT, true);  //Scaling input img (Can you explain why 700x750 image is decoded as 1400x1500 image?)
        original.getPixels(pixels, 0, OLDWIDTH, 0, 0, OLDWIDTH, OLDHEIGHT);
        imageView = (ImageView) findViewById(R.id.imageView);
        fastDownscale(SCALE);
        goodDownscale(SCALE);
        imageView.setImageBitmap(fast);
    }

    public int makeBrighter(int colour) {
        blue = ((colour & 0xFF) *2);
        if (blue > 255)
            blue = 255;
        green = (((colour & 0xFF00) >> 8) *2 );
        if (green > 255)
            green = 255;
        red = (((colour & 0xFF0000) >> 16) *2 );
        if (red > 255)
            red = 255;
        return 0xFF000000 + (red << 16) + (green << 8) + blue;
    }

    public void fastDownscale(double scale) {
        int[] temp = new int[HEIGHT * WIDTH];
        double px, py;
        for (int i = 0; i < HEIGHT; i++) {
            for (int j = 1; j < WIDTH; j++) {
                px = Math.floor(j * scale);
                py = Math.floor(i * scale);
                temp[HEIGHT * j - i] = pixels[(int) ((py * OLDWIDTH) + px)];
                temp[HEIGHT * j - i] = makeBrighter(temp[HEIGHT * j - i]);
            }
        }
        fast = Bitmap.createBitmap(temp, 0, HEIGHT, HEIGHT, WIDTH, Bitmap.Config.ARGB_8888);
    }

    public void goodDownscale(double scale) {
        int[] temp = new int[HEIGHT * WIDTH];
        int px,
                py,
                left,
                right,
                top,
                bottom,
                red,
                green,
                blue,
                alpha;
        for (int i = 1; i < HEIGHT - 1; i++)
            for (int j = 1; j < WIDTH - 1; j++) {
                px = (int) Math.floor(j * scale);
                py = (int) Math.floor(i * scale);
                top = pixels[(py - 1) * OLDWIDTH + px];
                bottom = pixels[(py + 1) * OLDWIDTH + px];
                left = pixels[py * OLDWIDTH + px - 1];
                right = pixels[py * OLDWIDTH + px + 1];
                blue = ((top & 0xFF) + (bottom & 0xFF) + (left & 0xFF) + (right & 0xFF)) / 4;
                green = (((top & 0xFF00) + (bottom & 0xFF00) + (left & 0xFF00) + (right & 0xFF00)) >> 8) / 4;
                red = (((top & 0xFF0000) + (bottom & 0xFF0000) + (left & 0xFF0000) + (right & 0xFF0000)) >> 16) / 4;
                alpha = 0xFF;
                temp[HEIGHT * j - i] = (alpha << 24) + (red << 16) + (green << 8) + blue;
            }


        for (int i = 0; i < WIDTH * HEIGHT; i++)
            temp[i] = makeBrighter(temp[i]);
        good = Bitmap.createBitmap(temp, 0, HEIGHT, HEIGHT, WIDTH, Bitmap.Config.ARGB_8888);
    }

    public void replace(View view) {
        if (!current) {
            imageView.setImageBitmap(good);
        } else {
            imageView.setImageBitmap(fast);
        }
        current = !current;
    }
}
